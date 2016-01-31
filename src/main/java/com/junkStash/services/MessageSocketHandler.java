package com.junkStash.services;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.util.PropertyUtil;


@WebSocket
@Component
public class MessageSocketHandler implements ApplicationContextAware {
	
    private static BiMap<String, Session> userSessionMap = HashBiMap.create();
    
    private static UserService userServcie;
    
    private static final String EVENT = "event";
    private static final String DATA = "data";
    private static final String RECIPIENT = "recipient";
    private static final String SENDER = "sender";
    private static final String MESSAGE = "message";
    private static final String BROADCAST = "broadcast";
    private static final String STATUS = "status";
    private static enum Status {ONLINE, OFFLINE};
    
    public MessageSocketHandler() {
		System.out.println("Websockets Initialized URL @ "+PropertyUtil.getWebSocketUrl());
	}
    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		userServcie = applicationContext.getBean(UserService.class);
	}
    
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	
    	URI uri = session.getUpgradeRequest().getRequestURI();
    	String userKey = uri.getQuery().split("=")[1];
    	String userId = userServcie.getUserId(userKey);
    	
    	if(StringUtils.isNotEmpty(userId)){
    		
    		userSessionMap.put(userId, session);
            broadcastMessage(session, Status.ONLINE);
            System.out.println("Websocket Connection Established [USER : ("+userId+") CONNECTED]");
    	}
    	else{
    		
			System.out.println("Unknown User : Rejecting Connection");
			session.close();
    	}
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        
    	String userId = userSessionMap.inverse().get(session);
        
    	userSessionMap.remove(userId);
        broadcastMessage(session, Status.OFFLINE);
        
        System.out.println("Websocket Connection Terminated [USER : ("+userId+") DISCONNECTED]");
    }

    @OnWebSocketMessage
    public void onMessage(Session userSession, String payload) {
        
    	String fromUserId = userSessionMap.inverse().get(userSession);
    	
    	JsonParser jsonParser = new JsonParser();
    	
    	try{
			
    		JsonObject json = jsonParser.parse(payload).getAsJsonObject();
			JsonObject data = json.get(DATA).getAsJsonObject();
			String message = data.get(MESSAGE).getAsString();
			String toUser = data.get(RECIPIENT).getAsString();
			
        	data = new JsonObject();
        	data.add(MESSAGE,new JsonPrimitive(message));
        	data.add(SENDER, new JsonPrimitive(fromUserId));
			
			JsonObject conversation = new JsonObject();
	        conversation.add(EVENT, new JsonPrimitive(MESSAGE));
	        conversation.add(DATA, data);
	        
	        System.out.println("Websocket Recieved Message [FROM : ("+fromUserId+") TO : ("+toUser+") MESSAGE : ("+message+")]");
	        
	        if(!userSessionMap.containsKey(toUser)){
	        	System.out.println("User "+toUser+" Not Online");
	        	return;
	        }
	        
	        privateMessage(toUser, conversation.toString());
    	}
    	catch(Exception e){
    		
        	JsonObject data = new JsonObject();
        	data.add(MESSAGE, new JsonPrimitive("Unble To Send Message"));
        	data.add(SENDER, new JsonPrimitive("Server"));
    		
			JsonObject conversation = new JsonObject();
	        conversation.add(EVENT, new JsonPrimitive(MESSAGE));
	        conversation.add(DATA, data);
    		
    		privateMessage(fromUserId, conversation.toString());
    	}
    }
    
    public static void privateMessage(String recipient, String message){
    	
    	Session toUserSession = userSessionMap.get(recipient);
    	
    	try {
    		if(toUserSession.isOpen()){
    			toUserSession.getRemote().sendString(message);
    			
    			System.out.println("Websocket Recieved Message [TO : ("+recipient+") MESSAGE : ("+message+")]");
    		}
    		else
    			userSessionMap.remove(toUserSession);
		} 
    	catch (IOException e) {
			System.out.println("Failure Sending Messsage : "+e.getMessage());
		}
    }
    
    //Notify other users are available/unavailable for chat
    public static void broadcastMessage(Session session, Status status) {
    	
    	String sender = userSessionMap.inverse().get(session);
    	
    	JsonObject data = new JsonObject();
    	data.add(STATUS, new JsonPrimitive(status.name()));
    	data.add(SENDER, new JsonPrimitive(sender));
    	
    	JsonObject json = new JsonObject();
    	json.add(EVENT, new JsonPrimitive(BROADCAST));
    	json.add(DATA, data);
    	
    	for(Session ses : userSessionMap.inverse().keySet()){
    	
	    	try {
	    		
	    		if(ses.isOpen()){
	    			ses.getRemote().sendString(json.toString());
	    			
	    			System.out.println("Websocket Recieved Message [USER : ("+sender+") >>BROADCAST<< STATUS :: ("+status.name()+")]");
	    		}
	    		else
	    			userSessionMap.inverse().remove(ses);
			} 
	    	catch (IOException e) {
				System.out.println("Failure Broadcast Messsage : "+e.getMessage());
			}
    	}
    }
}
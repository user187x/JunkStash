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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.util.PropertyUtil;

@WebSocket
@Component
public class MessageSocketHandler implements ApplicationContextAware {
	
    private static BiMap<String, Session> userSessionMap = HashBiMap.create();
    
    private static UserService userServcie;
    private static MailService mailService;
    
    private static final String EVENT = "event";
    private static final String DATA = "data";
    private static final String RECIPIENT = "recipient";
    private static final String SENDER = "sender";
    private static final String MESSAGE = "message";
    private static final String BROADCAST = "broadcast";
    private static final String USERS = "users";
    private static final String NOTIFICATION = "notification";
    private static final String TYPE = "type";
    private static final String COUNT = "count";
    private static final String FILE_UPDATE = "fileUpdate";
    
    public MessageSocketHandler() {
		System.out.println("Websockets Initialized URL @ "+PropertyUtil.getWebSocketUrl());
	}
    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		userServcie = applicationContext.getBean(UserService.class);
		mailService = applicationContext.getBean(MailService.class);
	}
    
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	
    	URI uri = session.getUpgradeRequest().getRequestURI();
    	String userKey = uri.getQuery().split("=")[1];
    	String userId = userServcie.getUserId(userKey);
    	
    	if(StringUtils.isNotEmpty(userId)){
    		
    		userSessionMap.put(userId, session);
    		broadcastMessage();
            
    		checkMailAndNotify(userId);
    		
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
    	
        broadcastMessage();
        
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
    
    public static void fileUpdate(String user){
    	
       	if(!userSessionMap.containsKey(user))
    		return;
    	
    	Session toUserSession = userSessionMap.get(user);
    	
    	try {
    		if(toUserSession.isOpen()){
    			
    	    	JsonObject payload = new JsonObject();
    	    	payload.add(EVENT, new JsonPrimitive(FILE_UPDATE));
    			
    			toUserSession.getRemote().sendString(payload.toString());
    			
    			//Notify Administrators As Well
    	    	for(String userSession : userSessionMap.keySet()){
    	    		
    	    		boolean isAdmin = userServcie.isUserAdmin(userSession);
    	    		
    	    		if(isAdmin==false)
    	    			continue;
    	    		
    	    		Session session = userSessionMap.get(userSession);
    	    		
    	    		if(session.isOpen()){
    	    			try{
    	    				
    	    				session.getRemote().sendString(payload.toString());
    	    			}
    	    			catch(Exception e){}
    	    		}
    	    	}
    			
    			
    			System.out.println("Notifying User For File Update ("+user+")");
    		}
    		else
    			userSessionMap.remove(toUserSession);
		} 
    	catch (IOException e) {
			
    		System.out.println("Failure Sending User File Update : "+e.getMessage());
		}
    }
    
    public static void checkMailAndNotify(String user){
    	
    	if(!userSessionMap.containsKey(user))
    		return;
    	
    	Session toUserSession = userSessionMap.get(user);
    	
    	try {
    		if(toUserSession.isOpen()){
    			
    			JsonObject data = new JsonObject();
    			data.add(TYPE, new JsonPrimitive("Mail Messages"));
    			
    			if(!mailService.hasUnAcknowledgedMail(user))
        			data.add(COUNT, new JsonPrimitive(0));
    			else
    				data.add(COUNT, new JsonPrimitive(mailService.getUnreadMail(user).size()));
    			
    	    	JsonObject payload = new JsonObject();
    	    	payload.add(EVENT, new JsonPrimitive(NOTIFICATION));
    	    	payload.add(DATA, data);
    			
    			toUserSession.getRemote().sendString(payload.toString());
    			int count = payload.get("data").getAsJsonObject().get("count").getAsInt();
    			
    			System.out.println("Notifying Mail Alert To User ("+user+") of "+count+" Notifications");
    		}
    		else
    			userSessionMap.remove(toUserSession);
		} 
    	catch (IOException e) {
			
    		System.out.println("Failure Sending User Mail Alert : "+e.getMessage());
		}
    }
    
    //Notify other users are available/unavailable for chat
    public static void broadcastMessage() {
    	
    	JsonArray users = new JsonArray();
    	
    	JsonObject data = new JsonObject();
    	data.add(USERS, users);
    	
    	JsonObject payload = new JsonObject();
    	payload.add(EVENT, new JsonPrimitive(BROADCAST));
    	payload.add(DATA, data);
    	
    	if(userSessionMap.isEmpty())
    		return;
    	
    	for(String user : userSessionMap.keySet())
	    	users.add(new JsonPrimitive(user));
    	
    	for(Session session : userSessionMap.inverse().keySet()){
    		
    		if(session.isOpen()){
    			try{
    				
    				session.getRemote().sendString(payload.toString());
    			}
    			catch(Exception e){}
    		}
    	}
    	
    	System.out.println("Websocket Broadcast : "+payload.toString());
    }
}
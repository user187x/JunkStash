package com.junkStash.services;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.util.PropertyUtil;


@WebSocket
@Component
public class MessageSocketHandler implements ApplicationContextAware {
	
    private static Map<Session, String> userSessionMap = new HashMap<>();
    private static UserService userServcie;
    
    public MessageSocketHandler() {
		System.out.println("Junkstash Websockets Initialized URL @ "+PropertyUtil.getWebSocketUrl());
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
    	
    	if(StringUtils.isEmpty(userId)){
    		
    		System.out.println("This User ID Is Not Known : Rejecting Connection");
    		session.close();
    		
    		return;
    	}
    	
        broadcastMessage(session, userId);
        userSessionMap.put(session, userId);
        
        System.out.println("JunkStash Websocket Connection Created ");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        
    	String userId = userSessionMap.get(session);
        userSessionMap.remove(userId);
        
        System.out.println("JunkStash Websocket Closed");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String payload) {
        
    	String userId = userSessionMap.get(session);
    	
    	JsonParser jsonParser = new JsonParser();
    	String recipient = null;
		String message = null;
    	
    	try{
    		
    		JsonObject json = jsonParser.parse(payload).getAsJsonObject();
    		JsonObject data = json.get("data").getAsJsonObject();
    	}
    	catch(Exception e){
    		
    		session.close();
    		return;
    	}
    	
    	try{
    		
    		JsonObject json = jsonParser.parse(payload).getAsJsonObject();
    		JsonObject data = json.get("data").getAsJsonObject();
    		message = data.get("message").getAsString();
    		recipient = data.get("recipient").getAsString();
    	}
    	catch(Exception e){
    		
    		message = null;
    	}
    	
        System.out.println("JunkStash Websocket Recieved Message [FROM : "+userId+" TO : "+recipient+" :: "+message+"]");
        
        broadcastMessage(session, userId);
    }
    
    //Sends Client Back MessageKey from the Server
    public static void broadcastMessage(Session session, String message) {
    	
    	JsonObject json = new JsonObject();
    	json.add("event", new JsonPrimitive("broadcast"));
    	json.add("data", new JsonPrimitive(message));
    	
    	try {
    		if(session.isOpen())
    			session.getRemote().sendString(json.toString());
    		else
    			userSessionMap.remove(session);
		} 
    	catch (IOException e) {
			System.out.println("Failure Broadcast Messsage : "+e.getMessage());
		}
    }
}
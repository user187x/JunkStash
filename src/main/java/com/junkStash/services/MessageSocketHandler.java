package com.junkStash.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.util.UserUtils;


@WebSocket
public class MessageSocketHandler {
	
    private static Map<Session, UserMessage> userSessionMap = new HashMap<>();
    
    public MessageSocketHandler() {
		System.out.println("Junkstash Websockets Initialized...");
	}
    
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        
    	String messengerKey = UserUtils.createSecureIdentifier();
        broadcastMessage(session, messengerKey);
        userSessionMap.put(session, new UserMessage(messengerKey));
        
        System.out.println("JunkStash Websocket Connection Created ");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        
    	String messengerKey = userSessionMap.get(session).getMessageKey();
        userSessionMap.remove(messengerKey);
        
        System.out.println("JunkStash Websocket Closed");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String payload) {
        
    	String messengerKey = userSessionMap.get(session).getMessageKey();
    	
    	JsonParser jsonParser = new JsonParser();
    	String userKey = null;
    	String recipient = null;
		String message = null;
    	
    	try{
    		
    		JsonObject json = jsonParser.parse(payload).getAsJsonObject();
    		JsonObject data = json.get("data").getAsJsonObject();
    		userKey = data.get("user").getAsString();
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
    	
        System.out.println("JunkStash Websocket Recieved Message : "+message+" From User "+userKey+" To User : "+recipient);
        
        broadcastMessage(session, messengerKey);
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
    
    private class UserMessage
    {
    	public String messageKey;
    	public String user;
    	
    	public UserMessage(String messageKey){
    		this.messageKey = messageKey;
    		this.user = null;
    	}

		public String getMessageKey() {
			return messageKey;
		}

		public void setMessageToken(String messageKey) {
			this.messageKey = messageKey;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}
    }
}
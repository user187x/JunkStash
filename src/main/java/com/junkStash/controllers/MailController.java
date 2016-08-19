package com.junkStash.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.services.MailService;
import com.junkStash.services.MessageSocketHandler;
import com.junkStash.services.UserService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class MailController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private MailService mailService;
	
	public MailController() {	
		setUpRoutes();
	}
	
	private void setUpRoutes(){
	
		Spark.get("/getNotifications/:userKey", new Route() {
		
			@Override
	        public Object handle(Request request, Response response) {
				
				JsonObject payload = new JsonObject();
				
				String userKey = request.params(":userKey");
	    		String userId = userService.getUserId(userKey);
	    		
	    		if(StringUtils.isEmpty(userId)){
	    			
	    			payload.add("message", new JsonPrimitive("Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	    		}
	    		
	    		JsonArray mail = mailService.getUnreadMail(userId);
    			payload.add("message", new JsonPrimitive("Messages Total : "+mail.size()));
	         	payload.add("success", new JsonPrimitive(true));
	    		payload.add("payload", mail);
	    		
	    		return payload;
			}
		
		});
		
		Spark.get("/hasUnAcknowledged/:userKey", new Route() {
			
			@Override
	        public Object handle(Request request, Response response) {
				
				JsonObject payload = new JsonObject();
				
				String userKey = request.params(":userKey");
	    		String userId = userService.getUserId(userKey);
	    		
	    		if(StringUtils.isEmpty(userId)){
	    			
	    			payload.add("message", new JsonPrimitive("Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	    		}
	    		
	    		boolean unacknowledged = mailService.hasUnAcknowledgedMail(userId);
	    		
    			payload.add("message", new JsonPrimitive("Has Unacknowledged : "+unacknowledged));
	         	payload.add("success", new JsonPrimitive(true));
	    		payload.add("payload", new JsonPrimitive(unacknowledged));
	    		
	    		
	    		
	    		return payload;
			}
		
		});
		
		Spark.get("/markAcknowledged/:userKey/:mailId", new Route() {
			
			@Override
	        public Object handle(Request request, Response response) {
				
				JsonObject payload = new JsonObject();
				
				String userKey = request.params(":userKey");
				String mailId = request.params(":mailId");
				
	    		String userId = userService.getUserId(userKey);
	    		
	    		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(mailId)){
	    			
	    			payload.add("message", new JsonPrimitive("Unable to find owner/mail"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	    		}
	    		
	    		boolean success = mailService.acknowledgeMail(mailId);
	         	payload.add("success", new JsonPrimitive(success));
	         	
	         	if(success)
	         		MessageSocketHandler.checkMailAndNotify(userId);
	    		
	    		return payload;
			}
		
		});
		
		Spark.post("/sendMail/:userKey", new Route() {
	     	
	    	@Override
	        public Object handle(Request request, Response response) {
	            
	    		JsonObject payload = new JsonObject();
	    		
	    		String userKey = request.params(":userKey");
	    		String actionUserId = userService.getUserId(userKey);
	    		
	    		if(StringUtils.isEmpty(actionUserId)){
	    			
	    			payload.add("message", new JsonPrimitive("Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	    		}
	    		
	    		String data = request.body();
	        	JsonParser jsonParser = new JsonParser();
	        	JsonObject json = jsonParser.parse(data).getAsJsonObject();
	        	
	        	String recipient = json.get("recipient").getAsString();
	        	String message = json.get("message").getAsString();
	        	
	        	if(recipient.isEmpty() || message.isEmpty()){
		         	
	        		payload.add("message", new JsonPrimitive("Parameters Missing from Payload"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	        	}
	        			
	        	boolean sendMail = mailService.sendMail(actionUserId, recipient, message);
	        	
	        	if(sendMail){
	        		
	        		payload.add("message", new JsonPrimitive("Successfully Sent Message"));
		         	payload.add("success", new JsonPrimitive(true));
	        		
	        		return payload;
	        	}
	        	else{
		         	
	        		payload.add("message", new JsonPrimitive("Failure Sending Message"));
		         	payload.add("success", new JsonPrimitive(false));
	        		
	        		return payload;
	        	}
	         }
	    });
	}
}

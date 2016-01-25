package com.junkStash.controllers;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.junkStash.services.FileService;
import com.junkStash.services.UserService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class UserAccessController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	
	public UserAccessController() {	
		setUpRoutes();
	}
	
	private void setUpRoutes(){
	
		 Spark.get("/getUsers/:userKey", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userKey = request.params(":userKey");
				String userId = userService.getUserId(userKey);
				
				if(userKey == null || userKey.isEmpty()){
					
					payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				boolean isUserAdmin = userService.isUserAdmin(userId);
				
				if(isUserAdmin==false){
					
					payload.add("message", new JsonPrimitive("Only admins can access this service"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				JsonArray jsonArray = userService.getAllUsers(userId);
				
				payload.add("message", new JsonPrimitive("Successfully Retrieved Users : Total "+jsonArray.size()));
	         	payload.add("success", new JsonPrimitive(true));
	         	payload.add("payload", jsonArray);
	         	
	         	return payload;
				
			}
	     });

		 Spark.get("/whoIs/:userId", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userId = request.params(":userId");
				
				if(userId == null || userId.isEmpty()){
					
					payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				boolean userExists = userService.userExists(userId);
				
				payload.add("message", new JsonPrimitive("Successfully Found Users "+userId));
	         	payload.add("success", new JsonPrimitive(true));
	         	payload.add("payload", new JsonPrimitive(userExists));
	         	
	         	return payload;
				
			}
	     });
		 
		 Spark.get("/findUsers/:userKey/:searchUser", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				String userKey = request.params(":userKey");
				String searchUserId = request.params(":searchUser");
				
				if(searchUserId == null || searchUserId.isEmpty() || userKey == null || userKey.isEmpty()){
					
					payload.add("message", new JsonPrimitive("Request Parameters Were Missing"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				String actionUser = userService.getUserId(userKey);
				boolean userExists = userService.userExists(actionUser);
				
				if(!userExists){
					
					payload.add("message", new JsonPrimitive("Unknown User"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				JsonArray jsonArray = userService.findUsersLike(searchUserId);
				
				payload.add("message", new JsonPrimitive("Successful Search Results"));
	         	payload.add("success", new JsonPrimitive(true));
	         	payload.add("payload", jsonArray);
	         	
	         	return payload;
				
			}
	     });
		 
		 Spark.get("/userExists/:userId", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userId = request.params(":userId");
				
				if(userId == null || userId.isEmpty()){
					
					payload.add("message", new JsonPrimitive("Request Was Empty"));
	         		
	         		return payload;
				}
				
				boolean userExists = userService.userExists(userId);
				
				if(userExists){
					payload.add("message", new JsonPrimitive("User Account Already Exists"));
					payload.add("success", new JsonPrimitive(false));
				}
				else{
					payload.add("message", new JsonPrimitive("User Account Doesn't Exist"));
					payload.add("success", new JsonPrimitive(true));
				}
				
	         	payload.add("exists", new JsonPrimitive(userExists));
	         	
	         	return payload;
				
			}
	     });
		 
		 Spark.post("/approveUser/:userKey", new Route() {
		     	
	     	 @Override
	         public Object handle(Request request, Response response) {
	             
	     		JsonObject payload = new JsonObject();
	     		
	     		String userKey = request.params(":userKey");
	     		String userId = userService.getUserId(userKey);
	     		
	     		if(StringUtils.isEmpty(userId)){
	     			
	     			payload.add("message", new JsonPrimitive("Failure Remove File : Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	     		
	         	String userToApprove = request.body();
	         	
	         	if(userToApprove.isEmpty()){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	System.out.println("Server Attempting Approve : "+userToApprove);
	         	
	         	
	         	boolean isAdmin = userService.isUserAdmin(userId);
	         	
	         	if(isAdmin==false){
	         		
	         		payload.add("message", new JsonPrimitive("Only Admins Can Take Action"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	boolean userApproved = userService.approveUser(userToApprove);
	         	
	         	if(userApproved){
	         		
	         		payload.add("message", new JsonPrimitive("User Has Been Successfully Approved"));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("Failure Approving User"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	          }
	     });
		 
		 Spark.post("/login", new Route() {
		     	
	     	 @Override
	         public Object handle(Request request, Response response) {
	     		 
	     		JsonObject payload = new JsonObject();
	     		
	         	String data = request.body();
	         	
	         	if(data.isEmpty() || data.equals("{}")){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	System.out.println("Server Recieved Payload : "+data);
	         	
	         	JsonParser jsonParser = new JsonParser();
	         	JsonObject json = jsonParser.parse(data).getAsJsonObject();
	         	
	         	System.out.println("Server Looking Up User "+json.get("user").getAsString());
	         	
	         	String userName = json.get("user").getAsString();
	         	String userPassword = json.get("password").getAsString();
	         	
	         	boolean userAccountExists = userService.userExists(userName);
	         	
	         	if(!userAccountExists){
	         		
	         		payload.add("message", new JsonPrimitive("User Not Found"));
		         	payload.add("success", new JsonPrimitive(false));
		         	
		         	return payload;
	         	}
	         	
         		boolean exceededLoginAttempts = userService.hasExhaustedLoginAttempts(userName);
         		
	         	if(exceededLoginAttempts){
	         		
	         		payload.add("message", new JsonPrimitive("Account Has Been Locked For 24 Hours"));
		         	payload.add("success", new JsonPrimitive(false));
		         	
		         	return payload;
	         	}
	         	
	         	boolean authenticated = userService.isAuthenticated(userName, userPassword);
	         	
	         	if(!authenticated){
	         		
	         		payload.add("message", new JsonPrimitive("Wrong Password"));
		         	payload.add("success", new JsonPrimitive(false));
		         	
		         	return payload;
	         	}

	         	userService.removeLoginAttempts(userName);
	         	
	         	String userKey = userService.getUserKey(userName, userPassword);
	         	boolean userFound = StringUtils.isNotEmpty(userKey);
	         	boolean isAdmin = userService.isUserAdmin(userName);
	         	
	         	if(userFound){
	         		
	         		payload.add("message", new JsonPrimitive("User Found"));
	         		payload.add("userKey", new JsonPrimitive(userKey));
		         	payload.add("success", new JsonPrimitive(true));
		         	payload.add("admin", new JsonPrimitive(isAdmin));
	         		
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("User Not Found"));
	         		payload.add("userKey", new JsonObject());
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}	
	          }
	     });
		 
		 Spark.post("/logout", new Route() {
		     	
	     	 @Override
	         public Object handle(Request request, Response response) {
	            
	     		JsonObject payload = new JsonObject();
	     		
	         	String data = request.body();
	         	
	         	if(data.isEmpty()){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	System.out.println("Server Recieved Payload : "+data);
	         	
	         	JsonParser jsonParser = new JsonParser();
	         	JsonObject json = jsonParser.parse(data).getAsJsonObject();
	         	
	         	System.out.println("Server Looking Up User "+json.get("user").getAsString());
	         	
	         	String user = json.get("user").getAsString();
	         	String userKey = json.get("userKey").getAsString();
	        
	         	boolean keyRemoved = userService.removeUserIdentifier(user, userKey);
	         	
	         	if(keyRemoved){
	         		
	         		payload.add("message", new JsonPrimitive("User Successfuly Logged Out"));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("User Failed Loggin Out"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}	
	          }
	     });
		 
		 Spark.post("/register", new Route() {
		     	
	     	 @Override
	         public Object handle(Request request, Response response) {
	            
	     		JsonObject payload = new JsonObject();
	     		
	     		boolean canServerAllocateSpace = fileService.canServerAcceptSize(FileService.USER_SPACE_SIZE);
	     		
	     		if(!canServerAllocateSpace){
		         	
	         		payload.add("message", new JsonPrimitive("JunkStash Can No Longer Accept New Users"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	     		
	         	String data = request.body();
	         	
	         	if(data.isEmpty()){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	System.out.println("Server Recieved Payload : "+data);
	         	
	         	JsonParser jsonParser = new JsonParser();
	         	JsonObject json = jsonParser.parse(data).getAsJsonObject();
	         	
	         	System.out.println("Server Creating Account For User "+json.get("user").getAsString());
	         	
	         	String user = json.get("user").getAsString();
	         	String password1 = json.get("password1").getAsString();
	         	String password2 = json.get("password2").getAsString();
	         	
	         	if(!password1.equals(password2)){
	         		
	         		payload.add("message", new JsonPrimitive("Failure Creating User Account : Passwords Don't Match"));
	         		payload.add("userKey", new JsonObject());
		         	payload.add("success", new JsonPrimitive(false));
	         			         		
	         		return payload;
	         	}
	         	
	         	boolean userFound = userService.userExists(user);
	         	
	         	if(userFound){
	         		
	         		payload.add("message", new JsonPrimitive("Failure Creating User Account : User Already Exists"));
	         		payload.add("userKey", new JsonObject());
		         	payload.add("success", new JsonPrimitive(false));
	         			         		
	         		return payload;
	         	}
	         	
	         	if(userService.createUser(user, password1)){

	         		String userKey = userService.getUserKey(user, password1);
	         		
	         		payload.add("message", new JsonPrimitive("Account Created"));
	         		payload.add("userKey", new JsonPrimitive(userKey));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         	}
	         	else{
	         		
	         		payload.add("message", new JsonPrimitive("Failure Creating User Account"));
	         		payload.add("userKey", new JsonObject());
		         	payload.add("success", new JsonPrimitive(false));
		         	
		         	return payload;
	         	}
	          }
	     });
		 
		 Spark.post("/removeUser/:userKey", new Route() {
		     	
	     	 @Override
	         public Object handle(Request request, Response response) {
	             
	     		JsonObject payload = new JsonObject();
	     		
	     		String userKey = request.params(":userKey");
	     		String actionUserId = userService.getUserId(userKey);
	     		
	     		if(StringUtils.isEmpty(actionUserId)){
	     			
	     			payload.add("message", new JsonPrimitive("Failure Remove User : Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	     		
	         	String targetUser = request.body();
	         	
	         	if(targetUser.isEmpty()){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	if(!userService.isUserAdmin(actionUserId)){
	         		
	         		payload.add("message", new JsonPrimitive("Failure Remove User : Admin Priveledge Only"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         			
	         	boolean removed = userService.removeUser(actionUserId, targetUser);
	         	
	         	if(removed){
	         		
	         		payload.add("message", new JsonPrimitive("User Successfully Removed"));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("User Failed To Remove"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	          }
	     });
	}
}

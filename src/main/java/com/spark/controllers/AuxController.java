package com.spark.controllers;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.spark.services.FileService;
import com.spark.services.UserService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class AuxController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	public AuxController() {	
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

		 Spark.post("/approve/:userKey", new Route() {
		     	
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
		 
		 Spark.get("/getFiles/:userKey", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userKey = request.params(":userKey");
				String userId = userService.getUserId(userKey);
				
				JsonArray jsonArray = fileService.getAllFiles(userId);
				
				if(jsonArray.isJsonNull() || !jsonArray.iterator().hasNext()){
					
					payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				else{
					
					payload.add("message", new JsonPrimitive("Successfully Found "+jsonArray.size()));
		         	payload.add("success", new JsonPrimitive(true));
		         	payload.add("payload", jsonArray);
		         	
		         	return payload;
				}
			}
	     });
		 
		 Spark.get("/getTotalDiskSpace/:userKey", new Route() {
			
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userKey = request.params(":userKey");
				
				JsonObject totalSize = fileService.getTotalDiskSpace(userKey);
				
				if(totalSize == null || totalSize.isJsonNull()){
					
					payload.add("message", new JsonPrimitive("Space Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				else{
					
					payload.add("message", new JsonPrimitive("Total Space Used "+totalSize.get("normalized")));
		         	payload.add("success", new JsonPrimitive(true));
		         	payload.add("payload", totalSize);
		         	
		         	return payload;
				}
			}
	     });
		 
		 Spark.get("/download/:fileId/:userKey", new Route() {
			
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				String fileId = request.params(":fileId");
				String userKey = request.params(":userKey");
				
				if(fileId == null || fileId.isEmpty() || userKey == null || userKey.isEmpty()){
					
					System.out.println("User Request To Download File With ID : "+fileId);
					
					JsonObject payload = new JsonObject();
					payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				else{
					
					ObjectId objectId = new ObjectId(fileId);
					fileService.getGridFSBucket().downloadToStream(objectId, response.raw().getOutputStream());
		         	
		         	return response.raw();
				}
			}
	     });
		 
		 Spark.post("/login", new Route() {
		     	
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
	         	
	         	String userName = json.get("user").getAsString();
	         	String userPassword = json.get("password").getAsString();
	         	
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
	         	String password = json.get("password").getAsString();
	         	
	         	boolean userFound = userService.userExists(user);
	         	
	         	if(userFound){
	         		
	         		payload.add("message", new JsonPrimitive("Failure Creating User Account : User Already Exists"));
	         		payload.add("userKey", new JsonObject());
		         	payload.add("success", new JsonPrimitive(false));
	         			         		
	         		return payload;
	         	}
	         	
	         	if(userService.createUser(user, password)){

	         		String userKey = userService.getUserKey(user, password);
	         		
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
		 
		 Spark.post("/removeFile/:userKey", new Route() {
		     	
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
	     		
	         	String data = request.body();
	         	
	         	if(data.isEmpty()){
		         	
	         		payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	
	         	JsonParser jsonParser = new JsonParser();
	         	JsonObject json = jsonParser.parse(data).getAsJsonObject();
	         	
	         	System.out.println("Server Attempting To Remove File : "+json.get("name").getAsString());
	         	
	         	String fileId = json.get("id").getAsString();
	         			
	         	boolean removed = fileService.remove(fileId, userId);
	         	
	         	if(removed){
	         		
	         		payload.add("message", new JsonPrimitive("Entry Successfully Removed"));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("Entry Failed To Remove"));
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
		 
		 Spark.post("/upload/:userKey", new Route() {
		     
		 	 JsonObject payload = new JsonObject();
		 
	     	 @Override
	         public Object handle(Request request, Response response) {
	            
	     		String userKey = request.params(":userKey");
	     		String fileName = null;
	     		
	     		if(StringUtils.isEmpty(userService.getUserId(userKey))){
	     			
	     			payload.add("message", new JsonPrimitive("Failure Uploading File : Unable to find owner"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	     		
	     		String userId = userService.getUserId(userKey);
	     		
	     		if(!userService.isUserApproved(userId)){
	     			
	     			payload.add("message", new JsonPrimitive("Account Approval Is Needed"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	     		
	     		JsonObject diskSpace = fileService.getTotalDiskSpace(userKey);
	     		long spaceUsed = diskSpace.get("size").getAsLong();
	     		long totalSpace = diskSpace.get("maxSpace").getAsLong();
	     		long totalRemaining = (totalSpace - spaceUsed);
	     		
	     		try{
	     			
		            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
		            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
		            
		            Part filePart = request.raw().getPart("file");
		           
		            fileName = filePart.getSubmittedFileName();
		            String fileType = filePart.getContentType();
		            
		            long fileSize = filePart.getSize();
		            
		            if(fileSize>totalRemaining){
		            	
		            	payload.add("message", new JsonPrimitive("File Size Exceeds The Account Space Available"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
		            }
		            
		            System.out.println("Upload FileType : "+fileType);
		            
		            InputStream fileStream = filePart.getInputStream();
		            ObjectId fileId = fileService.getGridFSBucket().uploadFromStream(fileName, fileStream);
		            
		            fileService.setFileType(fileId.toString(), fileType);
		            fileService.setFileOwner(fileId.toString(), userId);
	     		}
	     		
	     		catch(Exception e){
	     			
	     			payload.add("message", new JsonPrimitive("Failed Uploading File "+e.getMessage()));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	         	
	     		System.out.println("Successfully File Upload : "+fileName);
	     		
	     		payload.add("message", new JsonPrimitive("Successfully Uploaded File"));
	         	payload.add("success", new JsonPrimitive(true));
	         	
	         	return payload;
	     	} 	
	     });
	}
}

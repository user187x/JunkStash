package com.spark.controllers;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.spark.services.DatabaseService;
import com.spark.services.UserService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class AuxController {

	@Autowired
	private DatabaseService databaseService;
	
	@Autowired
	private UserService userService;
	
	public AuxController() {	
		setUpRoutes();
	}
	
	private void setUpRoutes(){
	
		 Spark.get("/getAll", new Route() {
	
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					JsonObject payload = new JsonObject();
					
					System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
					
					JsonArray jsonArray = databaseService.getAllDocuments();
					
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
		 
		 Spark.get("/download/:fileId", new Route() {
				
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String fileId = request.params(":fileId");
					
					if(fileId == null || fileId.isEmpty()){
						
						System.out.println("User Request To Download File With ID : "+fileId);
						
						JsonObject payload = new JsonObject();
						payload.add("message", new JsonPrimitive("Request Was Empty"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
					}
					else{
						
						ObjectId objectId = new ObjectId(fileId);
						databaseService.getGridFSBucket().downloadToStream(objectId, response.raw().getOutputStream());
			         	
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
		         	
		         	boolean userFound = userService.getUser(userName, userPassword);
		         	
		         	if(userFound){
		         		
		         		payload.add("message", new JsonPrimitive("User Found"));
			         	payload.add("success", new JsonPrimitive(true));
		         		
		         		return payload;
		         	}
		         	else{
			         	
		         		payload.add("message", new JsonPrimitive("User Not Found"));
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
		         	
		         	String userName = json.get("user").getAsString();
		         	String userPassword = json.get("password").getAsString();
		         	
		         	boolean userFound = userService.register(userName, userPassword);
		         	
		         	if(userFound){
		         		
		         		payload.add("message", new JsonPrimitive("Account Created"));
			         	payload.add("success", new JsonPrimitive(true));
		         		
		         		return payload;
		         	}
		         	else{
			         	
		         		payload.add("message", new JsonPrimitive("Failure Creating User Account"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
		         	}	
		          }
	     });
		 
		 Spark.post("/remove", new Route() {
		     	
		     	@Override
		         public Object handle(Request request, Response response) {
		             
		     		JsonObject payload = new JsonObject();
		     		
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
		         			
		         	boolean removed = databaseService.remove(fileId);
		         	
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
		 
		 Spark.post("/upload", new Route() {
		     	
			 	JsonObject payload = new JsonObject();
			 
		     	@Override
		         public Object handle(Request request, Response response) {
		            
		     		String fileName = null;
		     		
		     		try{
		     			
			            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
			            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
			            
			            Part filePart = request.raw().getPart("file");
			           
			            fileName = filePart.getSubmittedFileName();
			            String fileType = filePart.getContentType();
			            
			            System.out.println("Upload FileType : "+fileType);
			            
			            InputStream fileStream = filePart.getInputStream();
			            ObjectId fileId = databaseService.getGridFSBucket().uploadFromStream(fileName, fileStream);
			            
			            databaseService.setFileType(fileId.toString(), fileType);
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
	     
		 Spark.post("/search", new Route() {
	     	
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
	         	
	         	Document document = databaseService.find(data);
	      	
	         	if(document.isEmpty()){
	         		
	         		payload.add("message", new JsonPrimitive("No Entry Found"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	         	}
	         	else{
	         		
	         		payload.add("message", new JsonPrimitive("Entry Found : "+document.getDate("time")));
		         	payload.add("success", new JsonPrimitive(true));
	         		
	         		return payload;
	         		
	         	}
	          }
	     });
	}
}

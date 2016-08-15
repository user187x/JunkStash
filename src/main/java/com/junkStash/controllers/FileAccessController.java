package com.junkStash.controllers;

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
import com.junkStash.services.FileService;
import com.junkStash.services.MessageSocketHandler;
import com.junkStash.services.UserService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class FileAccessController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	public FileAccessController() {	
		setUpRoutes();
	}
	
	private void setUpRoutes(){
		
		 Spark.get("/getFiles/:userKey", new Route() {
			 
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				JsonObject payload = new JsonObject();
				
				System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
				
				String userKey = request.params(":userKey");
				String userId = userService.getUserId(userKey);
				
				JsonArray jsonArray = fileService.getFiles(userId);
				
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
			 
		 Spark.get("/download/:userKey/:fileId/:fileName", new Route() {
			
			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				String fileId = request.params(":fileId");
	         	String userKey = request.params(":userKey");
	         	String fileName = request.params(":fileName");
				
				if(fileId == null || fileId.isEmpty() 
					|| userKey == null || userKey.isEmpty()
					|| fileName == null || fileName.isEmpty()){
					
					JsonObject payload = new JsonObject();
					payload.add("message", new JsonPrimitive("Request Was Empty"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
			
				boolean fileExists = fileService.exists(fileId);
				
				if(!fileExists){
					
					JsonObject payload = new JsonObject();
					payload.add("message", new JsonPrimitive("File No Longer Exists"));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
				}
				
				System.out.println("Request To Download File : "+fileName);
				
				ObjectId objectId = new ObjectId(fileId);
				fileService.getGridFSBucket().downloadToStream(objectId, response.raw().getOutputStream());
	         	
				String mimeType = fileService.getFile(fileId, null).get("type").getAsString();
				String[] mimeTypeParts = mimeType.split("/");
				String fileExtension = mimeTypeParts[1];

				response.header("Content-Type", "application/"+fileExtension);
				response.header("Content-Transfer-Encoding", "binary");
				response.header("Content-Disposition", "attachment; filename=" + fileName);
				response.type("application/force-download");
				response.type(mimeType);
				
				fileService.incrementDownloadCount(fileId);
				
	         	return response.raw();
			}
	     });
		 
		 Spark.post("/shareFile/:userKey", new Route() {
					
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String data = request.body();
					
		         	JsonParser jsonParser = new JsonParser();
		         	JsonObject json = jsonParser.parse(data).getAsJsonObject();
		         	
		         	String userId = json.get("user").getAsString();
		         	String fileId = json.get("fileId").getAsString();
		         	
		        	String actionUserKey = request.params(":userKey");
					String actionUserId = userService.getUserId(actionUserKey);
					
					if(fileId == null || fileId.isEmpty() || userId == null || userId.isEmpty()){
						
						System.out.println("User Request To Download File With ID : "+fileId);
						
						JsonObject payload = new JsonObject();
						
						payload.add("message", new JsonPrimitive("Request Was Empty"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
					}
					
					JsonObject payload = new JsonObject();
					
					if(!userService.userExists(userId)){
						
						payload.add("message", new JsonPrimitive("Unknown System User"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
					}
					
					boolean isActionUserOwner = fileService.isUserOwner(actionUserId, fileId);
					boolean isActionUserAdmin = userService.isUserAdmin(actionUserId);
					boolean isFileSharedWith = userService.userHasFileAccess(userId, fileId);
					
					if(isFileSharedWith){
						
						payload.add("message", new JsonPrimitive("User Already Has Access To This File"));
			         	payload.add("success", new JsonPrimitive(false));
		         		
		         		return payload;
					}
					
					if(isActionUserOwner || isActionUserAdmin){
						
						boolean shareSuccess = userService.shareFile(userId, actionUserId, fileId);
						
			         	if(shareSuccess){
			         		
			         		payload.add("message", new JsonPrimitive("Successfully Shared File : "+fileId));
				         	payload.add("success", new JsonPrimitive(true));
				         	
				         	MessageSocketHandler.fileUpdate(userId);
			         		
			         		return payload;
			         	}
			         	else{
				         	
			         		payload.add("message", new JsonPrimitive("Failure Sharing File : "+fileId));
			         		payload.add("userKey", new JsonObject());
				         	payload.add("success", new JsonPrimitive(false));
			         		
			         		return payload;
			         	}
					}
					else{
						
						payload.add("message", new JsonPrimitive("Only file owners can share this file"));
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
	         	
	         	boolean isSharedFile = fileService.isFileShared(userId, fileId);
	         	boolean removed;
	         	
	         	if(isSharedFile)
	         		removed = fileService.removeShared(userId, fileId);
	         	else
	         		removed = fileService.remove(fileId, userId);
	         	
	         	if(removed){
	         		
	         		payload.add("message", new JsonPrimitive("Entry Successfully Removed"));
		         	payload.add("success", new JsonPrimitive(true));
	         		
		         	MessageSocketHandler.fileUpdate(userId);
		         	
	         		return payload;
	         	}
	         	else{
		         	
	         		payload.add("message", new JsonPrimitive("Entry Failed To Remove"));
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
		            fileService.initFileCounter(fileId.toString());
	     		}
	     		
	     		catch(Exception e){
	     			
	     			payload.add("message", new JsonPrimitive("Failed Uploading File "+e.getMessage()));
		         	payload.add("success", new JsonPrimitive(false));
	         		
	         		return payload;
	     		}
	         	
	     		System.out.println("Successfully File Upload : "+fileName);
	     		
	     		payload.add("message", new JsonPrimitive("Successfully Uploaded File"));
	         	payload.add("success", new JsonPrimitive(true));
	         	
	         	MessageSocketHandler.fileUpdate(userId);
	         	
	         	return payload;
	     	} 	
	     });
	}
}

package com.spark.services;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.spark.config.DatabaseConfig;
import com.spark.util.UserUtils;

@Service
public class UserService {

	@Autowired
	private DatabaseConfig databaseService;
	
	@Autowired
	private FileService fileService;
	
	public boolean userExists(String user){
		
		Document query = new Document();
		query.append("user", user);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return true;
		else
			return false;
	}
	
	public JsonArray getAllUsers(String userId){
		
		MongoCursor<Document> cursor = databaseService.getUserCollection().find().iterator();
		
		JsonArray jsonArray = new JsonArray();
		
		while(cursor.hasNext()){
			
			Document result = cursor.next();
			
			String user = result.getString("user");
			Date created = result.getDate("created");
			String status = result.getString("status");
			
			JsonObject json = new JsonObject();
			
			if(StringUtils.isNotEmpty(user))
				json.add("user", new JsonPrimitive(user));
			
			if(StringUtils.isNotEmpty(created.toString()))
				json.add("added", new JsonPrimitive(created.toString()));
			
			if(StringUtils.isNotEmpty(status))
				json.add("status", new JsonPrimitive(status));
			else
				json.add("status", new JsonPrimitive("Pending"));
				
			JsonObject diskSpace = fileService.getUserDiskSpace(user);
			
			json.add("space", new JsonPrimitive(diskSpace.get("normalized").getAsString()));
			
			jsonArray.add(json);
		}
		
		return jsonArray;
	}
	
	public String getUserId (String userKey){
		
		Document query = new Document();
		query.append("userKey", userKey);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next().getString("user");
		else
			return null;
	}
	
	public boolean createUser(String userId, String password){
		
		Document document = new Document();
		document.append("user", userId);
		document.append("password", password);
		document.append("created", new Date());
		document.append("status", "Pending");
		
		databaseService.getUserCollection().insertOne(document);
		addUserIdentifier(userId, password);
		
		return userExists(userId);
	}
	
	public String getUserKey(String user, String password){
		
		if(!userExists(user))
			return null;
		
		return addUserIdentifier(user, password);
	}
	
	public boolean isUserAdmin(String user){
		
		Document query = new Document();
		query.append("user", user);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next().getBoolean("admin", false);
		else
			return false;
	}
	
	private String addUserIdentifier(String user, String password){
		
		Document match = new Document();
		match.append("user", user);
		match.append("password", password);;
		
		String passwordHash = UserUtils.createSecureIdentifier();
		
		Document update = new Document();
		update.append("$set", new Document("userKey", passwordHash));
		
		databaseService.getUserCollection().updateOne(match, update);
		
		return passwordHash;
	}
	
	public boolean approveUser(String user){
		
		Document match = new Document();
		match.append("user", user);
		
		Document update = new Document();
		update.append("$set", new Document("status", "Approved"));
		
		databaseService.getUserCollection().updateOne(match, update);
		
		return isUserApproved(user);
	}
	
	public boolean isUserApproved(String user){
		
		Document query = new Document();
		query.append("user", user);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext()){
			
			Document document = results.iterator().next();
			String status = document.getString("status");
			
			if(status == null || status.isEmpty() || !status.equalsIgnoreCase("Approved"))
				return false;
			else
				return true;
		}
		else
			return false;
	}
	
	private boolean userKeyExists(String user, String userKey){
		
		Document query = new Document();
		query.append("user", user);
		query.append("userKey", userKey);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return true;
		else
			return false;
	}
	
	public boolean removeUserIdentifier(String user, String userKey){
		
		if(!userKeyExists(user, userKey))
			return false;
		
		Document match = new Document();
		match.append("user", user);
		match.append("userKey", userKey);;
		
		Document update = new Document();
		update.append("$unset", new Document("userKey", 1));
		
		databaseService.getUserCollection().updateOne(match, update);
		
		if(!userKeyExists(user, userKey))
			return true;
		else
			return false;
	}
}

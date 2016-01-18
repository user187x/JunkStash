package com.spark.services;

import java.util.Date;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.spark.config.DatabaseConfig;
import com.spark.util.UserUtils;

@Service
public class UserService {

	@Autowired
	private DatabaseConfig databaseService;
	
	public boolean userExists(String user, String password){
		
		Document query = new Document();
		query.append("user", user);
		query.append("password", password);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return true;
		else
			return false;
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
		
		databaseService.getUserCollection().insertOne(document);
		addUserIdentifier(userId, password);
		
		return userExists(userId, password);
	}
	
	public String getUserKey(String user, String password){
		
		if(!userExists(user, password))
			return null;
		
		return addUserIdentifier(user, password);
	}
	
	public String register(String user, String password){
		
		Document document = new Document();
		document.append("user", user);
		document.append("password", password);
		
		String key = UserUtils.createSecureIdentifier();
		document.append("userKey", key);
		
		databaseService.getUserCollection().insertOne(document);
		
		if(!userExists(user, password))
			return null;
		
		return key;
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

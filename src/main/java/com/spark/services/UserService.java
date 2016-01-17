package com.spark.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.spark.config.DatabaseConfig;

@Service
public class UserService {

	@Autowired
	private DatabaseConfig databaseService;
	
	public boolean getUser(String user, String password){
		
		Document query = new Document();
		query.append("user", user);
		query.append("password", password);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		if(results.iterator().hasNext())
			return true;
		
		else
			return false;
	}
}

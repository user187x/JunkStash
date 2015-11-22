package com.spark.config;

import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Component
public class DatabaseConfig {

	private static MongoClient mongoClient;
    
	public DatabaseConfig() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		
		System.out.println("Started Database Service...");
	}
	
     public MongoDatabase getMongoDatabase() {
          return mongoClient.getDatabase("testDb");
     }
}

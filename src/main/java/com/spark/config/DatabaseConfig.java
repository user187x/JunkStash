package com.spark.config;

import java.net.UnknownHostException;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class DatabaseConfig {

	public static final String COLLECTION = "users";
	public static final String DATABASE = "webapp";
	
	private static MongoClient mongoClient;
    
	public DatabaseConfig() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		System.out.println("Database Service Initialized...");
	}
	
     public MongoDatabase getMongoDatabase() {
          return mongoClient.getDatabase(DATABASE);
     }
     
     public MongoCollection<Document> getCollection(){
    	 return getMongoDatabase().getCollection(COLLECTION);
     }
}

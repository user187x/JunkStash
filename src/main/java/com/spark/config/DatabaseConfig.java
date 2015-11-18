package com.spark.config;

import java.net.UnknownHostException;

import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Configuration
public class DatabaseConfig {

	private static MongoClient mongoClient;
    
	public DatabaseConfig() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		
		System.out.println("Started Database Service...");
	}
	
	 @Bean 
     public MongoDatabase getMongoDatabase() {
          return mongoClient.getDatabase("testDb");
     }
	
	 @Bean 
     public MongoCollection<Document> getMongoCollection() {
          return mongoClient.getDatabase("testDb").getCollection("stuff");
     }
}

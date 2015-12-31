package com.spark.config;

import java.net.UnknownHostException;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@Component
public class DatabaseConfig {

	public static final String GRID_FS = "fileStore";
	
	//public static final String USER_COLLECTION = "users";
	public static final String FILE_COLLECTION = "fileStore.files";
	public static final String DATABASE = "webapp";
	
	private static MongoClient mongoClient;
    
	public DatabaseConfig() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		System.out.println("Database Service Initialized...");
	}
	
     public MongoDatabase getMongoDatabase() {
          return mongoClient.getDatabase(DATABASE);
     }
     
     public GridFSBucket getGridFSBucket(){
 		return GridFSBuckets.create(getMongoDatabase(), DatabaseConfig.GRID_FS);
 	}
     
     public MongoClient getMongoClient(){
    	 return mongoClient;
     }
     
//     public MongoCollection<Document> getUserCollection(){
//    	 return getMongoDatabase().getCollection(USER_COLLECTION);
//     }
     
     public MongoCollection<Document> getFileCollection(){
    	 return getMongoDatabase().getCollection(FILE_COLLECTION);
     }
}

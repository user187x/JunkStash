package com.junkStash.config;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@Component
public class DatabaseConfig {

	public static final Date systemStart = new Date();
	
	public static final String DATABASE = "junkstash";
	
	public static final String GRID_FS = "fileStore";	
	public static final String USER_COLLECTION = "users";
	public static final String MAIL_COLLECTION = "mail";
	public static final String FILE_COLLECTION = "fileStore.files";
	
	private static MongoClient mongoClient;
    
	public DatabaseConfig() throws UnknownHostException {
		
		mongoClient = new MongoClient("localhost", 27017);
		
		if(mongoClient==null){
			
			System.out.println("Unalbe to connect to database. System terminating...");
			System.exit(1);
		}
		else{
			
			System.out.println("Database Service Initialized...");
			System.out.println("JunkStash Started "+systemStart);
		}
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
     
     public MongoCollection<Document> getUserCollection(){
    	 return getMongoDatabase().getCollection(USER_COLLECTION);
     }
     
     public MongoCollection<Document> getMailCollection(){
    	 return getMongoDatabase().getCollection(MAIL_COLLECTION);
     }
     
     public MongoCollection<Document> getFileCollection(){
    	 return getMongoDatabase().getCollection(FILE_COLLECTION);
     }
}

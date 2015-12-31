package com.spark.services;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.result.DeleteResult;
import com.spark.config.DatabaseConfig;

@Service
public class DatabaseService {
	
	@Autowired
	private DatabaseConfig databaseService;
	
	private static String cachedIndex;
	
	public DatabaseService() throws Exception{
		cacheResource();
	}
	
	public boolean save(String message) {
		
		if(exists(message))
			return false;
		
		databaseService.getCollection().insertOne(new Document("message", message).append("time", new Date()));
		
		return true;
	}
	
	public MongoDatabase getMongoDatabase(){
		return databaseService.getMongoDatabase();
	}
	
	public MongoClient getMongoClient(){
		return databaseService.getMongoClient();
	}
	
	public GridFSBucket getGridFSBucket(){
		return databaseService.getGridFSBucket();
	}
	
	public boolean exists(String message){
		
		FindIterable<Document> results = databaseService.getCollection().find(new Document("message", message));
		
		if(results.iterator().hasNext())
			return true;
		else
			return false;
	}
	
	public boolean remove(String message){
		
		Document document = new Document();
		document.append("message", message);
		
		DeleteResult result = databaseService.getCollection().deleteOne(document);
		
		return result.getDeletedCount()>0;
	}
	
	public JsonArray getAllDocuments(){
		
		MongoCursor<Document> cursor = databaseService.getCollection().find().iterator();
		
		JsonArray jsonArray = new JsonArray();
		
		while(cursor.hasNext()){
			
			Document result = cursor.next();
			String messsage = result.getString("message");
			String time = result.getDate("time").toString();
			
			JsonObject json = new JsonObject();
			json.add("message", new JsonPrimitive(messsage));
			json.add("time", new JsonPrimitive(time));
			
			jsonArray.add(json);
		}
		
		return jsonArray;
	}
	
	public Document find(String query){
		
		FindIterable<Document> results = databaseService.getCollection().find(new Document("message", query));
		
		if(results.iterator().hasNext())
			return results.iterator().next();
		
		else
			return new Document();
	}
	
	public String getFileName(String fileId){
		
		ObjectId objectId = new ObjectId(fileId);
		
		Document query = new Document();
		query.append("_id", objectId);
		
		FindIterable<Document> results = databaseService.getFileCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next().getString("filename");
		
		else
			return null;
	}
	
	public boolean deleteFile(String fileId){
		
		try{
			getGridFSBucket().delete(new ObjectId(fileId));
		}
		catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	private void cacheResource() throws Exception{
		
		URL indexFile = getClass().getClassLoader().getResource("index.html");
		cachedIndex = FileUtils.readFileToString(new File(indexFile.toURI()));
		
		System.out.println("Resource Cached : "+indexFile);
	}
	
	public String getIndex(){
		return cachedIndex;
	}
}

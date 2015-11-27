package com.spark.services;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.spark.config.DatabaseConfig;

@Service
public class DatabaseService {
	
	@Autowired
	private DatabaseConfig databaseService;
	
	private static String cachedIndex;
	
	public DatabaseService() throws Exception{
		cacheResource();
	}
	
	public void save(String message) {
		databaseService.getCollection().insertOne(new Document("message", message));
	}
	
	public Iterable<Document> getAllDocuments(){
		return databaseService.getCollection().find();
	}
	
	public Document find(String query){
		
		FindIterable<Document> results = databaseService.getCollection().find(new Document("message", query));
		
		if(results.iterator().hasNext())
			return results.iterator().next();
		
		else
			return new Document();
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

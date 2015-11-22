package com.spark.services;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.config.DatabaseConfig;

@Service
public class DatabaseService {
	
	@Autowired
	private DatabaseConfig databaseService;
	
	private static String cachedIndex;
	
	public DatabaseService() throws Exception{
		cacheResource();
	}
	
	public void saveMessage(String message) {
		databaseService.getMongoCollection().insertOne(new Document("message", message));
	}
	
	public Iterable<Document> getAllDocuments(){
		return databaseService.getMongoCollection().find();
	}
	
	private void cacheResource() throws Exception{
		
		URL indexFile = getClass().getClassLoader().getResource("index.html");
		cachedIndex = FileUtils.readFileToString(new File(indexFile.toURI()));
		
		System.out.println("Caching Resource : "+indexFile);
	}
	
	public String getIndex(){
		return cachedIndex;
	}
}

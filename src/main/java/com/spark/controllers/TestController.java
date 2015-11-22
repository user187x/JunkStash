package com.spark.controllers;

import java.util.Date;

import org.bson.Document;

import com.spark.services.DatabaseService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class TestController {

	private DatabaseService databaseService;
	
	public TestController(final DatabaseService databaseService) {
		this.databaseService = databaseService;
		
		setUpRoutes();
	}
	
	private void setUpRoutes(){
	
		 Spark.get("/testGet", new Route() {
	
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
					
					databaseService.saveMessage(message);
					System.out.println(message);
					
					return "Working!";
				}
	     });
	     
		 Spark.get("/testGetAll", new Route() {
	
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
					System.out.println(message);
				
					Iterable<Document> documents = databaseService.getAllDocuments();
					
					return documents.toString();
				}
	     });
	     
		 Spark.post("/testPost", new Route() {
	     	
	     	@Override
	         public Object handle(Request request, Response response) {
	             
	         	String payload = request.body();
	         	System.out.println("Server Recieved Payload : "+payload);
	         	
	         	databaseService.saveMessage(payload);
	         	
	         	String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
	         	System.out.println(message);
	      	
	             return "Input Recieved : "+payload;
	          }
	     });
	}
}

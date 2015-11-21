package com.spark.config;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.staticFileLocation;

import java.util.Date;

import com.spark.service.DatabaseService;

import spark.Request;
import spark.Response;
import spark.Route;

public class WebConfig {
	
	private DatabaseService databaseService;

	public WebConfig(final DatabaseService databaseService) {
		
		this.databaseService = databaseService;
		
		staticFileLocation("/public");
		
		setupRoutes();
	}
	
	private void setupRoutes() {
		
        get("/", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
				
				databaseService.saveMessage(message);
				System.out.println(message);
				
				return databaseService.getIndex();
			}
        });
        
        get("/testGet", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
				
				databaseService.saveMessage(message);
				System.out.println(message);
				
				return "Working!";
			}
        });
        
        post("/testPost", new Route() {
        	
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
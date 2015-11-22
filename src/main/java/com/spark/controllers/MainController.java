package com.spark.controllers;

import java.util.Date;

import com.spark.services.DatabaseService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class MainController {
	
	private DatabaseService databaseService;
	
	public MainController(final DatabaseService databaseService){
		this.databaseService = databaseService;
		
		setUpRoutes();
	}
	
	public void setUpRoutes(){
		
		Spark.get("/users", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception {
				
				String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
				
				databaseService.saveMessage(message);
				System.out.println(message);
				
				return "Working!";
			}
        });
	}
}

package com.spark.controllers;

import java.util.Date;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.spark.services.DatabaseService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class AuxController {

	@Autowired
	private DatabaseService databaseService;
	
	public AuxController() {	
		setUpRoutes();
	}
	
	private void setUpRoutes(){
	
		 Spark.get("/getAll", new Route() {
	
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					System.out.println("User Request at Path : ("+request.pathInfo()+") "+new Date());
					
					return databaseService.getAllDocuments();
				}
	     });
	     
		 Spark.post("/search", new Route() {
	     	
	     	@Override
	         public Object handle(Request request, Response response) {
	             
	         	String payload = request.body();
	         	System.out.println("Server Recieved Payload : "+payload);
	         	
	         	Document document = databaseService.find(payload);
	      	
	         	if(document.isEmpty())
	         		return "No Result";
	         	else
	         		return "Found : "+document.getString("message");
	          }
	     });
	}
}

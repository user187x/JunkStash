package com.junkStash.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Controller;

import com.junkStash.services.FileService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

@Controller
@Configurable
public class MainController {
	
	@Autowired
	private FileService databaseService;
	
	public MainController(){
		setUpRoutes();
	}
	
	public void setUpRoutes(){
		
		 Spark.get("/", new Route() {
				
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
					System.out.println(message);
					
					return databaseService.getIndex();
				}
	     });
	}
}

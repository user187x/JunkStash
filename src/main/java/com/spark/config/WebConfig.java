package com.spark.config;

import java.util.Date;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.spark.WebApp;
import com.spark.controllers.MainController;
import com.spark.controllers.TestController;
import com.spark.services.DatabaseService;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class WebConfig {
	
	private DatabaseService databaseService;

	public WebConfig() {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebApp.class);	
		ctx.registerShutdownHook();
		
		this.databaseService = ctx.getBean(DatabaseService.class);
		ctx.close();
		
		Spark.staticFileLocation("/public");
		Spark.port(4567);
		
		serveIndexPage();
		addControllers();
	}
	
	public void serveIndexPage(){
		
		 Spark.get("/", new Route() {
				
				@Override
				public Object handle(Request request, Response response) throws Exception {
					
					String message = "User Request at Path : ("+request.pathInfo()+") "+new Date();
					System.out.println(message);
					
					return databaseService.getIndex();
				}
	     });
	}
	
	private void addControllers() {
		
		new MainController(databaseService);
		new TestController(databaseService);
    }
}
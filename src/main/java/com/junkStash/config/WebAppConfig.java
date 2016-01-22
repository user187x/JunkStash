package com.junkStash.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.junkStash.WebApp;
import com.junkStash.controllers.FileAccessController;
import com.junkStash.controllers.IndexController;
import com.junkStash.controllers.UserAccessController;

import spark.Spark;

public class WebAppConfig {

	public WebAppConfig(int port) {
		
		Spark.staticFileLocation("/webapp");
		Spark.port(port);
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebApp.class);	
		ctx.registerShutdownHook();
		ctx.close();
		
		initializeControllers();
	}
	
	private void initializeControllers() {
		
		new IndexController();
		new UserAccessController();
		new FileAccessController();
    }
}
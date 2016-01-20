package com.junkStash.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.junkStash.WebApp;
import com.junkStash.controllers.AuxController;
import com.junkStash.controllers.MainController;

import spark.Spark;

public class WebAppConfig {

	public WebAppConfig(int port) {
		
		Spark.staticFileLocation("/public");
		Spark.port(port);
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebApp.class);	
		ctx.registerShutdownHook();
		ctx.close();
		
		RegisterControllers();
	}
	
	private void RegisterControllers() {
		
		new MainController();
		new AuxController();
    }
}
package com.spark.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.spark.WebApp;
import com.spark.controllers.AuxController;
import com.spark.controllers.MainController;

import spark.Spark;

public class WebAppConfig {

	public WebAppConfig() {
		
		Spark.staticFileLocation("/public");
		Spark.port(4567);
		
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
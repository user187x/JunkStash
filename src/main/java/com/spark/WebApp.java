package com.spark;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.spark.config.WebConfig;
import com.spark.service.DatabaseService;

@Configuration
@ComponentScan({"com.spark"})
@SuppressWarnings("resource")
public class WebApp {
	
	public static void main(String[] args) {
    	
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebApp.class);	
		new WebConfig(ctx.getBean(DatabaseService.class));
		ctx.registerShutdownHook();
    }   
}

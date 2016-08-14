package com.junkStash.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.junkStash.controllers.FileAccessController;
import com.junkStash.controllers.IndexController;
import com.junkStash.controllers.UserAccessController;
import com.junkStash.services.MailService;
import com.junkStash.services.MessageSocketHandler;
import com.junkStash.util.CacheUtil;
import com.junkStash.util.PropertyUtil;

import spark.Spark;

@ComponentScan({"com.junkStash"})
public class WebAppConfig {
	
	public WebAppConfig(){}
	
	public static void intialized(){
		
		Spark.port(PropertyUtil.getPort());
		
		CacheUtil.cacheResource();
		
		Spark.staticFileLocation(PropertyUtil.getPublicDirectory());
		Spark.webSocket(PropertyUtil.getSocketPath(), MessageSocketHandler.class);
		Spark.init();
		
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebAppConfig.class);	
		ctx.registerShutdownHook();
		
		initializeControllers();
	}
	
	private static void initializeControllers() {
		
		new IndexController();
		new UserAccessController();
		new FileAccessController();
		new MailService();
    }
}
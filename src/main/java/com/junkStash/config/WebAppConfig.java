package com.junkStash.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.junkStash.controllers.FileAccessController;
import com.junkStash.controllers.IndexController;
import com.junkStash.controllers.UserAccessController;
import com.junkStash.services.MessageSocketHandler;
import com.junkStash.util.CacheUtil;

import spark.Spark;

@ComponentScan({"com.junkStash"})
public class WebAppConfig {

	private WebAppConfig(){}
	
	public WebAppConfig(String port) {
		
		if(StringUtils.isNotEmpty(port) && StringUtils.isNumeric(port))
			Spark.port(Integer.parseInt(port));
		
		CacheUtil.cacheResource();
		
		Spark.staticFileLocation("/webapp");
		Spark.webSocket("/chat", MessageSocketHandler.class);
		Spark.init();
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebAppConfig.class);	
		ctx.registerShutdownHook();
		
		initializeControllers();
	}
	
	private void initializeControllers() {
		
		new IndexController();
		new UserAccessController();
		new FileAccessController();
    }
}
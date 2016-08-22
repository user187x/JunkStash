package com.junkStash;

import com.junkStash.config.WebAppConfig;
import com.junkStash.util.PropertyUtil;


public class WebApp {
	
	public static void main(String[] args) {
		
		System.out.println("Starting JunkStash.... ");
		
		if(args.length!=0)
			PropertyUtil.setPort(args[0]);
		
		String testMode = System.getProperty("testMode");
		if(Boolean.parseBoolean(testMode))
			PropertyUtil.setTestMode();
		
		WebAppConfig.intialized();
    }   
}

package com.junkStash;

import org.springframework.context.annotation.ComponentScan;

import com.junkStash.config.WebAppConfig;

@ComponentScan({"com.spark"})
public class WebApp {
	
	public static final int DEFAULT_PORT = 4567;
	
	public static void main(String[] args) {
		
		try{
			
			new WebAppConfig(Integer.parseInt(args[0]));
		}
		catch(Exception e){
			
			System.out.println("Using Default Port "+DEFAULT_PORT);
			new WebAppConfig(DEFAULT_PORT);
		}
    }   
}

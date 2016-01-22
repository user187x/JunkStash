package com.junkStash;

import org.springframework.context.annotation.ComponentScan;

import com.junkStash.config.WebAppConfig;

@ComponentScan({"com.junkStash"})
public class WebApp {
	
	public static final int DEFAULT_PORT = 4567;
	
	public static void main(String[] args) {
		
		System.out.println("Starting JunkStash.... ");
		
		try{
			new WebAppConfig(Integer.parseInt(args[0]));
		}
		catch(Exception e){
			new WebAppConfig(DEFAULT_PORT);
		}
    }   
}

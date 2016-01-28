package com.junkStash;

import org.apache.commons.lang3.StringUtils;

import com.junkStash.config.WebAppConfig;


public class WebApp {
	
	public static void main(String[] args) {
		
		System.out.println("Starting JunkStash.... ");
		
		if(args.length==0)
			new WebAppConfig(StringUtils.EMPTY);
		else
			new WebAppConfig(args[0]);
    }   
}

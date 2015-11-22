package com.spark;

import org.springframework.context.annotation.ComponentScan;

import com.spark.config.WebConfig;

@ComponentScan({"com.spark"})
public class WebApp {
	
	public static void main(String[] args) {
		new WebConfig();
    }   
}

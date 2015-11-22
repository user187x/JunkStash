package com.spark;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.spark.config.WebConfig;

@Configuration
@ComponentScan({"com.spark"})
public class WebApp {
	
	public static void main(String[] args) {
		new WebConfig();
    }   
}

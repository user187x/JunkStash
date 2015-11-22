package com.spark.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
	
	private static String database;
	private static String databaseIp;
	private static int databasePort;
	private static String serverIp;
	private static int serverPort;

	public static void loadProperties(){
		
		InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("system.properties");
		Properties config = new Properties();	  
		
		try {	
			config.load(in);
	      
			serverIp = (String) config.get("serverIp");
			serverPort = Integer.parseInt(config.getProperty("serverPort"));
			database = (String) config.get("database");
			databaseIp = (String) config.get("databaseIp");
			databasePort = Integer.parseInt(config.getProperty("databasePort"));
		} 
		catch (IOException e) {
		  System.exit(1);
		}
	}
	
   public static String getDatabase(){
	   return database;
   }
   
   public static String getDatabaseIp(){
	   return databaseIp;
   }
   
   public static int getDatabasePort(){
	   return databasePort;
   }
   
   public static int getServerPort(){
	   return serverPort;
   }
   
   public static String getServerIp(){
	   return serverIp;
   }
}


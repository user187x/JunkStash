package com.junkStash.util;

import org.apache.commons.lang3.StringUtils;

public class PropertyUtil {

	public static String DEFAULT_SOCKET_HOST = "junkstash.com";
	public static String DEFAULT_SOCKET_PATH = "/chat";
	public static int DEFAULT_APP_PORT = 8080;
	public static String DEFAULT_PUBLIC_DIR = "/webapp";
	
	public static String getSocketHost() {
		return DEFAULT_SOCKET_HOST;
	}
	
	public static void setSocketHost(String host) {
		DEFAULT_SOCKET_HOST = host;
	}
	
	public static String getSocketPath() {
		return DEFAULT_SOCKET_PATH;
	}
	
	public static void setSocketPath(String path) {
		DEFAULT_SOCKET_PATH = path;
	}
	
	public static int getPort() {
		return DEFAULT_APP_PORT;
	}
	
	public static void setPort(String port) {
		
		if(StringUtils.isNotEmpty(port) && StringUtils.isNumeric(port))
			DEFAULT_APP_PORT = Integer.parseInt(port);
	}
	
	public static void setPublicDirectory(String publicDirectory){
		DEFAULT_PUBLIC_DIR = publicDirectory;
	}
	
	public static String getPublicDirectory(){
		return DEFAULT_PUBLIC_DIR;
	}
	
	public static String getWebSocketUrl(){
		return "ws://"+PropertyUtil.getSocketHost()+PropertyUtil.getSocketPath();
	}
}

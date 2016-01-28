package com.junkStash.util;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.junkStash.services.FileService;

public class CacheUtil {

	private static String cachedIndex;
	
	public static void cacheResource(){
		
		InputStream inputStream = FileService.class.getResourceAsStream("/index.html");
		
		StringWriter writer = new StringWriter();
		
		try{
			IOUtils.copy(inputStream, writer);
			cachedIndex = writer.toString();
		}
		catch(Exception e){
			System.out.println("Failure Caching Resource File : index.html");
			System.exit(1);
		}
		
		System.out.println("Cached Resource File : "+FileService.class.getResource("/index.html").getFile());
		System.out.println("Cached Resource Size : "+FileUtils.byteCountToDisplaySize(cachedIndex.getBytes().length));
	}
	
	public static String getIndex(){
		return cachedIndex;
	}
}

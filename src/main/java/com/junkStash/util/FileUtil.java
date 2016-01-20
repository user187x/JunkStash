package com.junkStash.util;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileUtil {

	public static boolean saveLocal(String fileName, InputStream fileStream, String localDir){
		
		try{
			
	        File uploadFile = new File(localDir+"/"+fileName);
	        FileUtils.copyInputStreamToFile(fileStream, uploadFile);
	        IOUtils.closeQuietly(fileStream);
		}
		catch(Exception e){
			return false;
		}
		
		return true;
	}
}

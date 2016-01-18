package com.spark.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class UserUtils {
    
	static SecureRandom random = new SecureRandom();
	
	public static String createSecureIdentifier(){
		return new BigInteger(130, random).toString(32);
	}
}

package com.junkStash.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class UserUtils {
    
	public static String createSecureIdentifier(){
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
}

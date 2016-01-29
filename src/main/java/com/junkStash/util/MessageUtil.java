package com.junkStash.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MessageUtil {

	private static Cipher cipher;

	public static void main(String[] args) throws Exception {
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		SecretKey secretKey = keyGenerator.generateKey();
		cipher = Cipher.getInstance("AES");

		String plainText = "Blah Blah Blah Blah Blah";
		String encryptedText;
		String decryptedText;
		
		System.out.println("Plain : " + plainText);

		encryptedText = encrypt(plainText, secretKey);
		System.out.println("Encryption : " + encryptedText);

		decryptedText = decrypt(encryptedText, secretKey);
		System.out.println("Decryption : " + decryptedText);
		
	}

	public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
		
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		
		return encryptedText;
	}

	public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
		
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		
		return decryptedText;
	}
}

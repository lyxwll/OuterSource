package com.fourteen.outersource.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * DES对称加密算法
 * @author Administrator
 *
 */
public class Security {
	public static final String KEY = "ft14outersource1";
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(crypted, 0);
		//return new String(Base64.encodeBase64(crypted));
	}

	public static String decrypt(String input, String key) {
		//加密
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decode(input, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(output);
	}

	/*public static void main(String[] args) {
		String key = "1234567891234567";
		String data = "example";
		System.out.println(Security.encrypt(data, key));
		System.out.println(Security.decrypt(Security.encrypt(data, key), key));
	}*/
}
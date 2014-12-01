package com.htyd.fan.om.util.base;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

public class DES {
	private static byte[] iv = new byte[8];
	
	static{
		PRNGFixes.apply();
	}
	
	@SuppressLint("TrulyRandom")
	public static String encryptDES(String encryptString, String encryptKey) {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
			return new String(Base64.encode(encryptedData, Base64.DEFAULT));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			return "";
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return "";
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return "";
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return "";
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String decryptDES(String decryptString, String decryptKey) {
		byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte[] decryptedData = cipher.doFinal(byteMi);
			return new String(decryptedData);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			return "";
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return "";
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return "";
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return "";
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return "";
		}
	}
}

package de.axxepta.tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptAES {

	private Cipher cipher;
	
	public EncryptAES(String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		Key aesKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	}
	
	public String encrypt(String s) throws IllegalBlockSizeException, BadPaddingException {
		return new String(cipher.doFinal(s.getBytes()));
	}
}

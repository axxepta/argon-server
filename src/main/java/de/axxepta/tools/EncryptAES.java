package de.axxepta.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
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

	public byte [] encrypt (byte [] array) throws IllegalBlockSizeException, BadPaddingException {
		return cipher.doFinal(array);
	}
	
	public String encrypt(String s) throws IllegalBlockSizeException, BadPaddingException {
		return new String(encrypt (s.getBytes()));
		
	}

	public byte [] decrypt(byte [] array) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ByteArrayInputStream in = new ByteArrayInputStream(array);
	    CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
	    byte[] buffer = new byte[1024];
	    int readLength;
	    while ((readLength = cipherInputStream.read(buffer)) >= 0) {
	        out.write(buffer, 0, readLength);
	    }
	    cipherInputStream.close();
	    return out.toByteArray();
	}
	
	public String decrypt(String s) throws IOException {
		return new String(decrypt(s.getBytes()));
	}
}

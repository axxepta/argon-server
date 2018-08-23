package de.axxepta.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SerializeObjectUtil {

	public static byte[] serialize(Object obj, String key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		EncryptAES encrypt = new EncryptAES(key);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return encrypt.encrypt(out.toByteArray());
	}

	public static Object deserialize(byte[] data, String key) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		EncryptAES encrypt = new EncryptAES(key);
		ByteArrayInputStream in = new ByteArrayInputStream(encrypt.encrypt(data));
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
}

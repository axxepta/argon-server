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

import org.apache.log4j.Logger;

public class SerializeObjectUtil {

	private static final Logger LOG = Logger.getLogger(SerializeObjectUtil .class);
	
	public static byte[] serialize(Object obj, String key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		EncryptAES encrypt = new EncryptAES(key);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		LOG.info("Serialize object with key " + key);
		return encrypt.encrypt(out.toByteArray());
	}

	public static Object deserialize(byte[] data, String key) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		EncryptAES encrypt = new EncryptAES(key);
		ByteArrayInputStream in = new ByteArrayInputStream(encrypt.encrypt(data));
		ObjectInputStream is = new ObjectInputStream(in);
		LOG.info("Deserialize object with key " + key);
		return is.readObject();
	}
}

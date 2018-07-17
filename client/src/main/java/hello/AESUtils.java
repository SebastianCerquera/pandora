package hello;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;

public class AESUtils {

	// it won't work with a key larger than 32 bits
	static int keyLength = 32;

	static SecretKey key = null;

	private static final String ALGO = "AES/ECB/PKCS5Padding";

	public static byte[] padKey(byte[] key) {
		byte[] padded = new byte[keyLength];

		for (int i = 0; i < key.length; i++)
			padded[i] = key[i];

		for (int i = key.length; i < keyLength; i++)
			padded[i] = 0;

		return padded;
	}

	public static byte[] encrypt(byte[] decrypted, byte[] rawKey) throws Exception {
		Key key = generateKey(padKey(rawKey));
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(decrypted);
	}

	public static byte[] decrypt(byte[] encrypted, byte[] rawKey) throws Exception {
		Key key = generateKey(padKey(rawKey));
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(encrypted);
	}

	public static void encryptFile(String source, String target, String key, Logger log) {
		FileInputStream in;
		try {
			in = new FileInputStream(new File(source));
		} catch (FileNotFoundException e) {
			log.error("The source file should exists");
			return;
		}

		ByteArrayOutputStream out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(), in, log);

		byte[] encrypted;
		try {
			encrypted = encrypt(out.toByteArray(), key.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("this shouldn't happen, the charset should exists");
			return;
		} catch (Exception e) {
			log.error("how can getBytes fail?");
			return;
		}

		FileUtils.writeFile(target, encrypted, log);
	}

	public static void decryptFile(String source, String target, String key, Logger log) {
		FileInputStream in;
		try {
			in = new FileInputStream(new File(source));
		} catch (FileNotFoundException e) {
			log.error("The source file should exists");
			return;
		}

		ByteArrayOutputStream out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(), in, log);

		byte[] decrypted;
		try {
			decrypted = decrypt(out.toByteArray(), key.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("this shouldn't happen, the charset should exists");
			return;
		} catch (Exception e) {
			log.error("how can getBytes fail?");
			return;
		}

		FileUtils.writeFile(target, decrypted, log);

	}

	private static Key generateKey(byte[] key) throws Exception {
		/*
		 * The prperty is set to unlimited and the maxKeyLen is 2147483647 yet i can
		 * only use keys up to 32 bits.
		 * 
		 * System.out.println(Security.getProperty("crypto.policy"));
		 * 
		 * int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
		 * System.out.println(maxKeyLen);
		 */

		/*
		 * Even the KeyGenerator fails to generate a 128 bit key.
		 * 
		 * KeyGenerator gen = KeyGenerator.getInstance(ALGO);
		 * gen.init(128);
		 * SecretKey keySpec = gen.generateKey();
		 */

		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		return keySpec;
	}
}

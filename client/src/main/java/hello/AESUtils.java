package hello;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;

public class AESUtils {

	private static final String ALGO = "AES/ECB/PKCS5Padding";

	public static byte[] encrypt(byte[] decrypted, String password) throws Exception {
		Key key = generateKey(password);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(decrypted);
	}

	public static byte[] decrypt(byte[] encrypted, String password) throws Exception {
		Key key = generateKey(password);
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
			encrypted = encrypt(out.toByteArray(), key);
		} catch (UnsupportedEncodingException e) {
			log.error("this shouldn't happen, the charset should exists");
			return;
		} catch (Exception e) {
			log.error("it failed during the encryption");
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
			decrypted = decrypt(out.toByteArray(), key);
		} catch (UnsupportedEncodingException e) {
			log.error("this shouldn't happen, the charset should exists");
			return;
		} catch (Exception e) {
			log.error("how can getBytes fail?");
			return;
		}

		FileUtils.writeFile(target, decrypted, log);
	}

	private static byte[] salt = null;

	private static byte[] getSalt() {
		if (salt == null) {
			SecureRandom random = new SecureRandom();
			salt = new byte[16];
			random.nextBytes(salt);
		}
		return salt;
	}

	/*
	 * it works but it keeps creating 16bit keys
	 * 
	 * i can use a password longer than 16 bit it will generate the key anyways.
	 */
	private static Key generateKey(String password) throws Exception {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), getSalt(), 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}
}

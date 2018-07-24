package pandora.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

	private static final String KEY_ALGO = "AES";
	
	private static final String ALGO = "AES/ECB/PKCS5Padding";
	
	private static final String SECRET_ALGO = "PBKDF2WithHmacSHA1";

	public static byte[] encrypt(byte[] decrypted, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Key key = generateKey(password);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(decrypted);
	}

	public static byte[] decrypt(byte[] encrypted, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Key key = generateKey(password);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(encrypted);
	}

	public static void encryptFile(String source, String target, String key)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		FileInputStream in = new FileInputStream(new File(source));
		ByteArrayOutputStream out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(), in);
		byte[] encrypted = encrypt(out.toByteArray(), key);
		FileUtils.writeFile(target, encrypted);
	}

	public static void decryptFile(String source, String target, String key)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		decryptFile(new File(source), new File(target), key);
	}
	
	public static void decryptFile(File source, File target, String key)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		FileInputStream in = new FileInputStream(source);
		ByteArrayOutputStream out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(), in);
		byte[] decrypted = decrypt(out.toByteArray(), key);
		FileUtils.writeFile(target, decrypted);
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
	private static Key generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_ALGO);
		KeySpec spec = new PBEKeySpec(password.toCharArray(), getSalt(), 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), KEY_ALGO);
	}
}

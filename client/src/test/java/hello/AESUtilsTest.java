package hello;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AESUtilsTest {

	private static String password = "12345678910111213141516171819202122232425262728293031323334353637383940";

	@Test
	public void encryptFile() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		
		// TODO use relative paths
		AESUtils.encryptFile("/home/beto/workspace/pandora/test/test.jpg",
				"/home/beto/workspace/pandora/test/encrypted.jpg", password);
		
		// TODO use relative paths
		AESUtils.decryptFile("/home/beto/workspace/pandora/test/encrypted.jpg",
				"/home/beto/workspace/pandora/test/decrypted.jpg", password);
	}

}

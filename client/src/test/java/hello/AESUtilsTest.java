package hello;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AESUtilsTest {
    
	private static String password = "12345678910111213141516171819202122232425262728293031323334353637383940";
	
	@Autowired
	Logger log;
	
	@Test
    public void encryptFile() {
		AESUtils.encryptFile("/home/beto/workspace/pandora/test/test.jpg", "/home/beto/workspace/pandora/test/encrypted.jpg", password, log);
		AESUtils.decryptFile("/home/beto/workspace/pandora/test/encrypted.jpg", "/home/beto/workspace/pandora/test/decrypted.jpg", password, log);
    }
	
}

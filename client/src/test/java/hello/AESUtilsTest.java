package hello;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AESUtilsTest {
    
	@Autowired
	Logger log;
	
	@Test
    public void encryptFile() {
		AESUtils.encryptFile("/home/beto/workspace/pandora/test/test.jpg", "/home/beto/workspace/pandora/test/encrypted.jpg", "123456789", log);
		AESUtils.decryptFile("/home/beto/workspace/pandora/test/encrypted.jpg", "/home/beto/workspace/pandora/test/decrypted.jpg", "123456789", log);
    }
	
}

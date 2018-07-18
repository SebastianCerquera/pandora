package hello;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private static String FILE_URL = "http://qanomads.com:48080/";

	private static String TARGET_DIR = "/opt/pandora";

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private String downloadProblems(String fileUrl) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(fileUrl);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed");
			return null;
		} catch (IOException e) {
			log.error("It fails to read from the stream");
			return null;
		}

		return new String(payload, Charset.forName("UTF-8"));
	}
	
	private void downloadAndEncrypt(String source, String target, String key) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(source);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed");
			return;
		} catch (IOException e) {
			log.error("It fails to download the payload");
			return;
		}

		byte[] encrypted = null;
		try {
			encrypted = AESUtils.encrypt(payload, key);
		} catch (InvalidKeyException e) {
			log.error("it was unable to generate a proper key");
			return;
		} catch (NoSuchAlgorithmException e) {
			log.error("AES is not available");
			return;
		} catch (InvalidKeySpecException e) {
			log.error("it was unable to generate a proper key");
			return;
		} catch (NoSuchPaddingException e) {
			log.error("there is an error with the padding");
			return;
		} catch (IllegalBlockSizeException e) {
			log.error("there is an error with the block size (?)");
			return;
		} catch (BadPaddingException e) {
			log.error("there is an error with the padding");
			return;
		}
		
		try {
			FileUtils.writeFile(FileUtils.createFile(target), encrypted);
		} catch (FileNotFoundException e) {
			log.error("It was unable to find the encrypted file");
			return;
		} catch (IOException e) {
			log.error("It was unable to create the encrypted file");
			return;
		}
	}

	private String[] getProblems(String fileUrl) {
		String raw = downloadProblems(fileUrl);

		if (raw == null) {
			log.info("There are no available problems");
			return null;
		}

		return raw.split("\n");
	}

	private void startProblems() {
		String[] problems = getProblems(FILE_URL + "/INDEX");
		if (problems == null)
			return;

		for (String id : problems) {
			String[] metadata = getMetadata(downloadProblems(FILE_URL + "/" + id + "/KEY"));
			startProblem(id, metadata[1], Long.valueOf(metadata[0]));
		}

	}

	private String[] getMetadata(String raw) {
		//TODO DO I NEED TO TRIM THE FILE?
		return raw.split("\n");
	}

	private void decryptPayload(String source, String target, String key) {
		try {
			AESUtils.decryptFile(new File(source), FileUtils.createFile(target), key);
		} catch (InvalidKeyException e) {
			log.error("it was unable to generate a proper key");
			return;
		} catch (NoSuchAlgorithmException e) {
			log.error("AES is not available");
			return;
		} catch (InvalidKeySpecException e) {
			log.error("it was unable to generate a proper key");
			return;
		} catch (NoSuchPaddingException e) {
			log.error("there is an error with the padding");
			return;
		} catch (IllegalBlockSizeException e) {
			log.error("there is an error with the block size (?)");
			return;
		} catch (BadPaddingException e) {
			log.error("there is an error with the padding");
			return;
		} catch (IOException e) {
			log.error("it failed to write the decrypted payload");
			return;
		}
	}
	
	private void startProblem(String id, String solution, Long delay) {
		Thread newProblem = new Thread() {
			public void run() {
				try {
					downloadAndEncrypt(FILE_URL + "/" + id + "/safe.tar", TARGET_DIR + "/" + id + "/safe.tar.encrypted", solution);
					log.info("A new problem has started");
					
					Thread.sleep(delay * 1000);
					
					decryptPayload(TARGET_DIR + "/" + id + "/safe.tar.encrypted", TARGET_DIR + "/" + id + "/safe.tar", solution);
					log.info("The problem has completed the delay");
					log.info("This was the solution:" + solution);
				} catch (InterruptedException v) {
					log.error("The problem thread was interrupted");
				}
			}
		};

		newProblem.start();
	}

	@Scheduled(fixedDelay = 1800 * 1000)
	public void checkProblems() {
		startProblems();
	}
}

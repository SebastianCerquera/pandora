package pandora.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	@Autowired
	private ConfigurationProperties instanceProperties;

	private Map<String, Boolean> problems = new ConcurrentHashMap<>();

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private String downloadProblems(String fileUrl) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(fileUrl);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed: " + fileUrl);
			return null;
		} catch (IOException e) {
			log.error("It fails to read from the stream: " +  fileUrl);
			return null;
		}

		return new String(payload, Charset.forName("UTF-8"));
	}

	private void downloadAndEncrypt(String source, String target, Long key) {
		downloadAndEncrypt(source,target, String.valueOf(key));
	}
	
	private void downloadAndEncrypt(String source, String target, String key) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(source);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed: " + source);
			return;
		} catch (IOException e) {
			log.error("It fails to download the payload: " + source);
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
		log.info("Downloading problems from: " +  instanceProperties.getServerEndpoint());
		String[] problems = getProblems(instanceProperties.getServerEndpoint() + "/INDEX");
		if (problems == null)
			return;

		for (String id : problems) {
			String[] metadata = getMetadata(
					downloadProblems(instanceProperties.getServerEndpoint() + "/" + id + "/KEY"));

			if (this.problems.get(id) == null) {
				this.problems.put(id, true);
				startProblem(id, Long.valueOf(metadata[1]), Long.valueOf(metadata[0]));
			} else {
				log.error("The problem with code: " + id + "has already being used");
			}
				
		}

	}

	private String[] getMetadata(String raw) {
		// TODO DO I NEED TO TRIM THE FILE?
		return raw.split("\n");
	}
	
	private void decryptPayload(String source, String target, Long key) {
		decryptPayload(source,target, String.valueOf(key));
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

	private void startProblem(String id, Long solution, Long delay) {
		Thread newProblem = new Thread() {
			public void run() {
				try {
					downloadAndEncrypt(instanceProperties.getServerEndpoint() + "/" + id + "/safe.tar",
							instanceProperties.getTargetFolder() + "/" + id + "/safe.tar.encrypted", solution);
					log.info("A new problem has started, id code: " + id);

					Thread.sleep(delay * 1000);

					decryptPayload(instanceProperties.getTargetFolder() + "/" + id + "/safe.tar.encrypted",
							instanceProperties.getTargetFolder() + "/" + id + "/safe.tar", solution);
					log.info("The problem has completed the delay");
					log.info("This was the solution for problem with code id " + id + ": " + solution);
				} catch (InterruptedException v) {
					log.error("The problem thread was interrupted");
				}
			}
		};

		newProblem.start();
		
		System.gc();
	}

	@Scheduled(fixedDelay = 900 * 1000)
	public void checkProblems() {
		startProblems();
	}
}

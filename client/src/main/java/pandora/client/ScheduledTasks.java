package pandora.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pandora.client.model.PandoraClient;
import pandora.client.model.RSAProblem;
import pandora.client.utils.AESUtils;
import pandora.client.utils.ConfigurationProperties;
import pandora.client.utils.FileUtils;
import pandora.client.utils.RegisterHelperServer;

import org.springframework.web.client.HttpServerErrorException;

@Component
public class ScheduledTasks {

	@Autowired
	RestTemplate template;

	@Autowired
	private ConfigurationProperties instanceProperties;

	@Autowired
	RegisterHelperServer registerHelper;

	private Map<String, RSAProblem.STATES> problems = new ConcurrentHashMap<>();

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private String downloadProblems(String fileUrl) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(fileUrl);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed: " + fileUrl);
			return null;
		} catch (IOException e) {
			log.error("It fails to read from the stream: " + fileUrl);
			return null;
		}

		return new String(payload, Charset.forName("UTF-8"));
	}

	private void downloadAndEncrypt(String source, String target, Long key) {
		downloadAndEncrypt(source, target, String.valueOf(key));
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
		log.info("Downloading problems from: " + instanceProperties.getServerEndpoint());
		String[] problems = getProblems(instanceProperties.getServerEndpoint() + "/v1/problems");
		if (problems == null) {
			updateState();
			return;
		}

		for (String id : problems) {
			if (this.problems.get(id) == null) {
                 		String raw = downloadProblems(instanceProperties.getServerEndpoint() + "/v1/problems/" + id);
                 		RSAProblem problem = new RSAProblem(raw);
                            
				if (problem.getState() == RSAProblem.STATES.READY) {
					startProblem(id, problem.getModulus(), Long.valueOf(problem.getSecret()),
							Long.valueOf(problem.getDelay()));
				} else {
					updateState();
					log.error("The problem with code: " + id + "is not ready yet");
				}
			} else {
				updateState();
				log.error("The problem with code: " + id + "has already being used");
			}

		}

	}

	private void decryptPayload(String source, String target, Long key) {
		decryptPayload(source, target, String.valueOf(key));
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

	private void updateState() {
		String target = instanceProperties.getServerEndpoint() + "/v1/clients/";

		String hostname = registerHelper.getHostname(instanceProperties.getAmazonMetadata());

                ResponseEntity<PandoraClient> entity = null;                                   
                try {                                                                          
                        entity = template.getForEntity(target + hostname, PandoraClient.class);
                } catch (HttpServerErrorException e) {
                    /*
                        A connection lost does not imply that the server went down and the client needs to register again.

                      
                        try {                                                                  
                                registerHelper.register();                                     
                        } catch (IOException e1) {                                             
                                // TODO Auto-generated catch block                             
                                e1.printStackTrace();                                          
                        }                                                                      
                        entity = template.getForEntity(target + hostname, PandoraClient.class);
                    */
                    log.error("The connectivity to the server was lost");
                    return;
                }


		PandoraClient pandoraClient = entity.getBody();

		ArrayList<RSAProblem> problems = new ArrayList<>();
		for (String id : this.problems.keySet()) {
			RSAProblem.STATES state = this.problems.get(id);
			
			if (state == RSAProblem.STATES.COMPLETED)
				continue;
			
			RSAProblem problem = new RSAProblem();
			problem.setId(Long.valueOf(id));
			problems.add(problem);
		}

		pandoraClient.setProblems(problems);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PandoraClient> httpEntity = new HttpEntity<PandoraClient>(pandoraClient, headers);
		template.put(target, httpEntity);

		log.info("New state was sent to the server: " + target);
	}

	private void startProblem(String id, String modulus, Long solution, Long delay) {
		Thread newProblem = new Thread() {
			public void run() {
				try {
					downloadAndEncrypt(instanceProperties.getServerEndpoint() + "/v1/problems/" + id + "/images",
							instanceProperties.getTargetFolder() + "/" + id + "/safe.tar.encrypted", solution);
					log.info("A new problem has started, id code: " + id);
					log.info("This is the modulus for the problem with id: " + id + " " + modulus);

                                        ScheduledTasks.this.problems.put(id, RSAProblem.STATES.READY);
                                        
					updateState();

                                        ScheduledTasks.this.problems.put(id, RSAProblem.STATES.COMPLETED);

					Thread.sleep(delay * 1000);

					decryptPayload(instanceProperties.getTargetFolder() + "/" + id + "/safe.tar.encrypted",
							instanceProperties.getTargetFolder() + "/" + id + "/safe.tar", solution);

                                        /*
                                         * It is missing to update the problem using the API, the problem state is still READY on the server.
                                         */
					
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

	public void checkProblems() {
		startProblems();
	}

}

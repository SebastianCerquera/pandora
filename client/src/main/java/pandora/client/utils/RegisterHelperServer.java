package pandora.client.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pandora.client.ScheduledTasks;
import pandora.client.model.PandoraClient;

@Component("default")
public class RegisterHelperServer implements RegisterHelper {
	
	@Autowired
	RestTemplate template;
	
	@Autowired
	ConfigurationProperties instanceProperties; 
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
	
	public String register() throws IOException {
		String hostname = getHostname(instanceProperties.getAmazonMetadata());
		
		PandoraClient client = new PandoraClient(hostname);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PandoraClient> entity = new HttpEntity<PandoraClient>(client, headers);
		template.postForEntity(instanceProperties.getServerEndpoint() + "/v1/clients", entity, Void.class);
		
		return hostname;
	}
	
	private String getHostname(String amazonMetadata) {
		byte[] payload = null;

		try {
			payload = FileUtils.downloadFileToMemory(amazonMetadata);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed: " + amazonMetadata);
			return null;
		} catch (IOException e) {
			log.error("It fails to read from the stream: " + amazonMetadata);
			return null;
		}

		return new String(payload, Charset.forName("UTF-8"));
	}
	
	public static String execReadToString(String execCommand) throws IOException {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream()).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}
}

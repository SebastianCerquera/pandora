package pandora.client.utils;

import java.io.IOException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pandora.client.model.PandoraClient;

@Component("default")
public class RegisterHelperServer implements RegisterHelper {
	
	@Autowired
	RestTemplate template;
	
	@Autowired
	ConfigurationProperties instanceProperties;
	
	public String register() throws IOException {
		String hostname = execReadToString("cat /etc/hostname");
		
		PandoraClient client = new PandoraClient(hostname);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PandoraClient> entity = new HttpEntity<PandoraClient>(client, headers);
		template.postForEntity(instanceProperties.getServerEndpoint() + "/v1/clients", entity, Void.class);
		
		return hostname;
	}
	
	public static String execReadToString(String execCommand) throws IOException {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream()).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}
}

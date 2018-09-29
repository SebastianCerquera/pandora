package pandora.client;

import java.io.IOException;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import pandora.client.model.PandoraClient;
import pandora.client.utils.ConfigurationProperties;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner setup(RestTemplate template, ConfigurationProperties instanceProperties) {
		return (args) -> {
			String hostname = execReadToString("cat /etc/hostname");

			PandoraClient client = new PandoraClient(hostname);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<PandoraClient> entity = new HttpEntity<PandoraClient>(client, headers);
			template.postForEntity(instanceProperties.getServerEndpoint() + "/v1/clients", entity, Void.class);
		};
	}

	public static String execReadToString(String execCommand) throws IOException {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream()).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}
}

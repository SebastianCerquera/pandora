package pandora.client;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import pandora.client.utils.ConfigurationProperties;
import pandora.client.utils.RegisterHelper;
import pandora.client.utils.RegisterHelperDummy;
import pandora.client.utils.RegisterHelperServer;

@SpringBootApplication
@EnableScheduling
public class Application {
	
	@Autowired
	ConfigurationProperties properties; 
	
	@Autowired
	RegisterHelperDummy registerDummy;
	
	@Autowired
	RegisterHelperServer registerServer;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner setup() {
		return (args) -> {
			RegisterHelper register = properties.getProfile().equals("development") ? registerDummy : registerServer;
			register.register();
		};
	}

	@PreDestroy
	public void unregisterClient() {
		RegisterHelper register = properties.getProfile().equals("development") ? registerDummy : registerServer;
		try {
			register.unregister();
		} catch (IOException e) {
			log.error("It fail to unregister to the server");
		}
	}
	
}

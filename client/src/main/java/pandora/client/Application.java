package pandora.client;

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

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner setup(ConfigurationProperties properties,
			RegisterHelperDummy registerDummy,
			RegisterHelperServer registerServer) {
		return (args) -> {
			RegisterHelper register = properties.getProfile().equals("development") ? registerDummy : registerServer;
			register.register();
		};
	}

}

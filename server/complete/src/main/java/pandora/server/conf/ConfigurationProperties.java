package pandora.server.conf;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import pandora.server.Application;
import pandora.server.model.RSAProblem;
import pandora.server.repository.RSAProblemRepository;

@Component
@Getter
@Configuration
@EnableConfigurationProperties
public class ConfigurationProperties {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Value("${rsagen}")
	String rsagen;	

	@Value("${clientTimeout}")
	String clientTimeout;	
	
	@Bean
	public CommandLineRunner setup(RSAProblemRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new RSAProblem("123456789", "1234567", "300"));
			repository.save(new RSAProblem("123456789", "1234567", "300"));

			// fetch all customers
			log.info("-------------------------------");
			for (RSAProblem problem : repository.findAll()) {
				log.info(problem.toString());
			}
			log.info("");
		};
	}
	
	@Bean 
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
	
	
	

}

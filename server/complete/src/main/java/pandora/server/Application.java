package pandora.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import pandora.server.conf.RSAProblemRepository;
import pandora.server.model.RSAProblem;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

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
    
}

package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource(value = "${ws.properties}", ignoreResourceNotFound = true)
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}
}

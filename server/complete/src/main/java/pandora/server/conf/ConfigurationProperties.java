package pandora.server.conf;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Component
@Configuration
@EnableConfigurationProperties
public class ConfigurationProperties {
	
	@Value("${rsagen}")
	String rsagen;
	
	public String getRsagen() {
		return rsagen;
	}

}

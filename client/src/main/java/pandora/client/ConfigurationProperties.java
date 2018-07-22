package pandora.client;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Component
@Configuration
@EnableConfigurationProperties
public class ConfigurationProperties {

	@Value("${serverEndpoint}")
	String endpoint;
	
	@Value("${targetFolder}")
	String targetFolder;
	
	public String getServerEndpoint() {
		return endpoint;
	}

	public String getTargetFolder() {
		return targetFolder;
	}

}

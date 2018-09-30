package pandora.client.utils;

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
	
	@Value("${jobDelay}")
	String jobDelay;
	
	@Value("${spring.profiles.active}")
	String profile;
	
	public String getProfile() {
		return profile;
	}
	
	public String getJobDelay() {
		return jobDelay;
	}

	public String getServerEndpoint() {
		return endpoint;
	}

	public String getTargetFolder() {
		return targetFolder;
	}

}

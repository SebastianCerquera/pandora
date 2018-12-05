package pandora.client.model;

import java.util.List;
import java.util.Map;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class RSAProblem {

	private Long id;

	private String modulus;

	private String secret;

	private String delay;

	private STATES state;

	private List<RSAPayload> images;

	public RSAProblem() {
	}

	public RSAProblem(String raw) {
		JsonParser parser = JsonParserFactory.getJsonParser();
		Map<String, Object> metadata =  parser.parseMap(raw.replace("\\\"", "\""));
		
		this.modulus = (String) metadata.get("modulus");
		this.secret =  (String) metadata.get("secret");
		this.delay =  (String) metadata.get("delay");
		
		Object state = metadata.get("state");
		if (state != null)
			this.state = STATES.valueOf((String)state);
	}

	public RSAProblem(String modulus, String secret, String delay) {
		this.modulus = modulus;
		this.secret = secret;
		this.delay = delay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModulus() {
		return modulus;
	}

	public void setModulus(String modulus) {
		this.modulus = modulus;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public List<RSAPayload> getImages() {
		return images;
	}

	public void setImages(List<RSAPayload> images) {
		this.images = images;
	}

	public STATES getState() {
		return state;
	}

	public void setState(STATES state) {
		this.state = state;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RSAProblem: <");
		builder.append(this.modulus);
		builder.append(",");
		builder.append(this.delay);
		builder.append(">");
		return builder.toString();
	}

	public static enum STATES {
		CREATED, IN_PROGESS, COMPLETED
	}

}

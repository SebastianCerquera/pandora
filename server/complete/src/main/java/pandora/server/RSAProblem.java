package pandora.server;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class RSAProblem {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String modulus;
	
	private String secret;
	
	private String delay;
	
	@OneToMany(cascade=CascadeType.REMOVE)
	private List<RSAPayload> images;
	
	protected RSAProblem() {}
	
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
	
}

package pandora.server.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class RSAProblem implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String modulus;
	
	private String secret;
	
	private String delay;
	
	private STATES state;

	@OneToMany(cascade=CascadeType.REMOVE)
	private Set<RSAPayload> images;
	
	protected RSAProblem() {}
	
	public RSAProblem(String modulus, String secret, String delay) {
		this.modulus = modulus;
		this.secret = secret;
		this.delay = delay;
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

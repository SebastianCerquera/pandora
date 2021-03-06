package pandora.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class PandoraClient {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(unique=true)
	private String hostname;
	
	private STATES state;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastSeen;

	@ManyToMany
	@JsonIgnore
	private Set<RSAProblem> problems;
	
	protected PandoraClient() {}
	
	public PandoraClient(String hostname) {
		this.hostname = hostname;
		this.state = STATES.HEALTHY;
		this.lastSeen = new Date();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PandoraClient: <");
		builder.append(this.hostname);
		builder.append(">");
		return builder.toString();
	}
	
	public static enum STATES {
		HEALTHY, UNRESPONSIVE
	}
	
}

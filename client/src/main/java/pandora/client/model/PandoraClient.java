package pandora.client.model;

import java.util.List;

public class PandoraClient {

	private Long id;
	
	private String hostname;
	
	private STATES state;

	private List<RSAProblem> problems;
	
	protected PandoraClient() {}
	
	public PandoraClient(String hostname) {
		this.hostname = hostname;
		this.state = STATES.HEALTHY;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public List<RSAProblem> getProblems() {
		return problems;
	}

	public void setProblems(List<RSAProblem> images) {
		this.problems = images;
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
		builder.append(this.hostname);
		builder.append(">");
		return builder.toString();
	}
	
	public static enum STATES {
		HEALTHY, UNRESPONSIVE
	}
	
}

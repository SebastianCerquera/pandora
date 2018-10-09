package pandora.server.dto;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pandora.server.model.PandoraClient.STATES;

@Getter
@Setter
public class PandoraClientDTO  {

	private Long id;
	private String hostname;
	private STATES state;
	private List<RSAProblemDTO> problems;
	
	public PandoraClientDTO(String hostname) {
		this.state = STATES.HEALTHY;
		this.hostname = hostname;
	}	
	
	public PandoraClientDTO() {}

	public static PandoraClientDTO ofDefault() {
		PandoraClientDTO pandoraClientDTO = new PandoraClientDTO();
		pandoraClientDTO.setId(Long.valueOf(1));
		pandoraClientDTO.setHostname("localhost");
		pandoraClientDTO.setState(STATES.HEALTHY);
		pandoraClientDTO.setProblems(Arrays.asList(RSAProblemDTO.ofDefault()));
		return pandoraClientDTO;
	}
	
}

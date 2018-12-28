package pandora.server.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pandora.server.model.RSAProblem.STATES;

@Setter
@Getter
public class RSAProblemDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String modulus;
	private String secret;
	private String delay;
	private STATES state;
	private List<RSAPayloadDTO> images;

	protected RSAProblemDTO() {}

	public static RSAProblemDTO ofDefault() {
		RSAProblemDTO rsaProblemDTO = new RSAProblemDTO();
		rsaProblemDTO.setDelay("1");
		rsaProblemDTO.setId(Long.valueOf(1));
		rsaProblemDTO.setImages(Arrays.asList(RSAPayloadDTO.ofDefault()));
		rsaProblemDTO.setModulus("Modulus");
		rsaProblemDTO.setSecret("1234567");
		rsaProblemDTO.setState(STATES.IN_PROGESS);
		return rsaProblemDTO;
	}
	
	

}

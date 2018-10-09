package pandora.server.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RSAPayloadDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private byte[] blob;
	
	protected RSAPayloadDTO() {}

	public static RSAPayloadDTO ofDefault() {

		RSAPayloadDTO rsaPayloadDTO = new RSAPayloadDTO();
		rsaPayloadDTO.setId(Long.valueOf(1));
		rsaPayloadDTO.setName("New RSA");
		rsaPayloadDTO.setBlob(null);
		return rsaPayloadDTO;
	}
}

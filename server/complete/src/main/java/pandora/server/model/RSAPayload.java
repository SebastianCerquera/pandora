package pandora.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class RSAPayload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@Column(columnDefinition="BLOB")
	private byte[] blob;

	protected RSAPayload() {}
	
	public RSAPayload(byte[] bs, String name) {
		this.blob = bs;
		this.name = name;
	}

}

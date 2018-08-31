package pandora.client;

public class RSAPayload {

	private Long id;
	
	private String name;
	
	private byte[] blob;

	protected RSAPayload() {}
	
	public RSAPayload(byte[] bs, String name) {
		this.blob = bs;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getBlob() {
		return blob;
	}

	public void setBlob(byte[] blob) {
		this.blob = blob;
	}
}

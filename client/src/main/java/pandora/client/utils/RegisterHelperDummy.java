package pandora.client.utils;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component("development")
public class RegisterHelperDummy implements RegisterHelper{

	@Override
	public String register() throws IOException {
		return "HOSTNAME";
	}

}

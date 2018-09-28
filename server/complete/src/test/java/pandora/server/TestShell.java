package pandora.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestShell {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Process p = Runtime.getRuntime().exec("/opt/rsagen.sh");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = br.readLine()) != null)
			System.out.println(line);
		p.getOutputStream();
	}

}

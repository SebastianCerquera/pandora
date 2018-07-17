package hello;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	public static void writeFile(String filename, byte[] data) throws FileNotFoundException, IOException {
		writeFile(new FileOutputStream(new File(filename)), data);
	}

	public static void writeFile(OutputStream fout, byte[] data) throws IOException {
		fout.write(data, 0, data.length);
		if (fout != null)
			fout.close();
	}

	public static OutputStream readFile(OutputStream fout, InputStream fin) throws IOException {
		BufferedInputStream in = new BufferedInputStream(fin);
		
		final byte data[] = new byte[1024];
		
		int count;
		while ((count = in.read(data, 0, 1024)) != -1) {
			fout.write(data, 0, count);
		}

		if (in != null)
			in.close();
		
		if (fout != null)
			fout.close();

		return fout;
	}
}

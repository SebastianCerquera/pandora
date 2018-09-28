package pandora.client.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileUtils {

	//TODO what if the file already exists
	public static File createFile(String fileName) throws IOException {
		String[] herarchy = fileName.split("/");
		StringBuilder dir = new StringBuilder();
		for (int i = 1; i < herarchy.length - 1; i++)
			dir.append("/" + herarchy[i]);
		new File(dir.toString()).mkdirs();
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	public static void downloadFile(String source, String target) throws MalformedURLException, IOException {
		URL website = new URL(source);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(FileUtils.createFile(target));
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

		if (fos != null)
			fos.close();
	}

	public static byte[] downloadFileToMemory(String fileUrl) throws MalformedURLException, IOException {
		// TODO rewrite the code, i don't want to be casting.
		ByteArrayOutputStream out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(),
				new URL(fileUrl).openStream());
		return out.toByteArray();
	}

	public static void writeFile(File file, byte[] data) throws FileNotFoundException, IOException {
		writeFile(new FileOutputStream(file), data);
	}
		
	public static void writeFile(String filename, byte[] data) throws FileNotFoundException, IOException {
		writeFile(new File(filename), data);
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

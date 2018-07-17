package hello;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;

public class FileUtils {
	
	public static void writeFile(String filename, byte[] data, Logger log) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(new File(filename));
		} catch (FileNotFoundException e) {
			log.error("the ouput file should exists");
			return;
		}
		writeFile(stream, data, log);
	}
	
	public static void writeFile(OutputStream fout, byte[] data, Logger log) {
		try {
			fout.write(data, 0, data.length);
		} catch (IOException e) {
			log.error("it failed when writing to the stream");
		}
		finally {
			if(fout != null)
				try {
					fout.close();
				} catch (IOException e) {
					log.error("it failed to close the stream");
				}
		}
	}
	
	public static OutputStream readFile(OutputStream fout, InputStream fin, Logger log) {
    	BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(fin);
            
            fout = new ByteArrayOutputStream();

            final byte data[] = new byte[1024];
            int count;
            try {
				while ((count = in.read(data, 0, 1024)) != -1) {
				    fout.write(data, 0, count);
				}
			} catch (IOException e) {
				log.error("It fails to read from the stream");
				return null;
			}
        } finally {
            if (in != null) {
                try {
					in.close();
				} catch (IOException e) {
					log.error("It fails to close the input stream");
				}
            }
            if (fout != null) {
                try {
					fout.close();
				} catch (IOException e) {
					log.error("Can it fail to close ByteArrayOutputStream?");
				}
            }
        }
        
        return fout;
	}
}

package hello;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private static String FILE_URL = "";
	
	private static String TARGET_DIR = "";
	
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private String downloadFileIntoMemory(String fileUrl){
    	ByteArrayOutputStream out;
    	
    	try {
			out = (ByteArrayOutputStream) FileUtils.readFile(new ByteArrayOutputStream(), new URL(fileUrl).openStream(), log);
		} catch (MalformedURLException e) {
			log.error("The URL is malformed");
			return null;
		} catch (IOException e) {
			log.error("It fails to read from the stream");
			return null;
		}
    	        
        try {
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("this shouln't happen");
			return null;
		}
    }
    
    private String[] getProblems(String fileUrl){
    	String raw = downloadFileIntoMemory(fileUrl);
    	
    	if(raw == null) {
    		log.info("There are no available problems");
    		return null;
    	}
    	
    	return raw.split("\n");
    }
    
    private void startProblems() {
    	String[] problems = getProblems(FILE_URL);
    	if(problems == null)
    		return;
    	
    	for(String id : problems) {
    		downloadFile(FILE_URL + "/" + id + "/KEY/payload", TARGET_DIR + "/" + id);
    		String[] metadata = getMetadata(downloadFileIntoMemory(FILE_URL + "/" + id + "/KEY"));
    		startProblem(id, metadata[1], Long.valueOf(metadata[0]));
    	}
    		
    }
    
    private String[] getMetadata(String raw) {
    	// DO I NEED TO TRIM THE FILE?
    	return raw.split("\n");
    }
    
    private void downloadFile(String source, String target) {
    	URL website;
		try {
			website = new URL(source);
		} catch (MalformedURLException e1) {
			log.error("The URL is malformed");
			return;
		}
    	
    	ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(website.openStream());
		} catch (IOException e) {
			log.error("It did fail to open the stream to the source server");
			return;
		}
    	
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(target);
		} catch (FileNotFoundException e) {
			log.error("It was unable to find the target file");
			return;
		}
    	
		try {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			log.error("It fails to read from the stream");
		}
		
		finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("It fails to close the ouput stream");
				}
			}
				
		}
    }
    
    private void startProblem(String id, String solution, Long delay) {
    	Thread newProblem = new Thread() {
    		String key = solution;
    	    public void run() {
    	        try {
    	        	log.info("A new problem has started");
    	            Thread.sleep(delay);
    	            log.info("The problem has completed the delay");
    	        } catch(InterruptedException v) {
    	            log.error("The problem thread was interrupted");
    	        }
    	    }  
    	};

    	newProblem.start();
    }   
    
    @Scheduled(fixedDelay = 600*1000)
    public void checkProblems() {
    	startProblems();
    }
}

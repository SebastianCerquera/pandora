package pandora.server;

import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

@RestController
public class RSAController {

	private static final Logger log = LoggerFactory.getLogger(RSAController.class);

	@Autowired
	RSAProblemRepository repositoryProblem;

	@Autowired
	RSAPayloadRepository repositoryPayload;

	@RequestMapping("/")
	public String root() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping(value = "/v1/problems", produces = { "plain/text" })
	public String index() {
		StringBuilder builder = new StringBuilder();
		for (RSAProblem problem : repositoryProblem.findAll()) {
			builder.append(problem.getId());
			builder.append("\n");
		}
		return builder.toString();
	}

	@PostMapping(value = "/v1/problems", produces = { "application/json" })
	public String create() {
		/**
		 * it is missing to generate the RSA key pair, the easiest is to use the bash
		 * utils.
		 * 
		 * i am temporaly hardcoding the values.
		 */
		RSAProblem problem = repositoryProblem.save(new RSAProblem("123456789111", "1234567111", "123456789111"));
		return "{\"id\": \"" + problem.getId() + "\"}";
	}

	@DeleteMapping(value = "/v1/problems/{id}", produces = { "application/json" })
	public void delete(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();
		repositoryProblem.delete(problem.get());
	}
	
	@GetMapping(value = "/v1/problems/{id}", produces = { "plain/text" })
	public String key(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		StringBuilder builder = new StringBuilder();
		builder.append(problem.get().getDelay());
		builder.append("\n");
		builder.append(problem.get().getModulus());
		builder.append("\n");
		builder.append(problem.get().getSecret());
		builder.append("\n");

		return builder.toString();
	}

	@GetMapping(value = "/v1/problems/{id}/images", produces = { "plain/text" })
	public byte[] payload(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		List<RSAPayload> images = problem.get().getImages();

		ByteArrayOutputStream out = new ByteArrayOutputStream();


		TarOutputStream tar = new TarOutputStream(out);
		for (RSAPayload image : images) {
			try {
				//tar will complain if the timestamp is in the future 
				TarHeader header = TarHeader.createHeader(image.getName(), Long.valueOf(image.getBlob().length),
						(new Date()).getTime(), false, 0700 + 0070 + 0007);
				tar.putNextEntry(new TarEntry(header));
				tar.write(image.getBlob(), 0, image.getBlob().length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			tar.flush();
			tar.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	@PostMapping(value = "/v1/problems/{id}/images", produces = { "application/json" })
	public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
			@PathVariable("id") Long id) {

		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		RSAPayload payload;
		try {
			payload = new RSAPayload(file.getBytes(), fileName);
		} catch (IOException e) {
			log.error("It failed to upload the file");
			return "{\"error\": \"IO exception\"}";
		}

		repositoryPayload.save(payload);

		problem.get().getImages().add(payload);
		repositoryProblem.save(problem.get());

		return "{\"id\": \" " + problem.get().getId() + " \"}";
	}

}
package pandora.server;

import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import pandora.server.ConfigurationProperties;
import pandora.server.RSAProblem.STATES;

@RestController
public class RSAController {

	private static final Logger log = LoggerFactory.getLogger(RSAController.class);

	@Autowired
	private ConfigurationProperties properties;

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

	@PostMapping(value = "/v1/problems/{delay}", produces = { "application/json" })
	public String create(@PathVariable("delay") String delay) {
		/**
		 * it is missing to generate the RSA key pair, the easiest is to use the bash
		 * utils.
		 * 
		 * i am temporaly hardcoding the values.
		 */
		ArrayList<String> key = new ArrayList<>();
		try {
			Process p = Runtime.getRuntime().exec(properties.getRsagen());
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null)
				key.add(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RSAProblem problem = repositoryProblem.save(new RSAProblem(key.get(0), key.get(1), delay));
		problem.setState(STATES.CREATED);
		return "{\"id\": \"" + problem.getId() + "\"}";
	}

	@DeleteMapping(value = "/v1/problems/{id}", produces = { "application/json" })
	public void delete(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException("There is no problem with the provided ID");
		repositoryProblem.delete(problem.get());
	}

	/*
	 * It might be that the client syncs the problem before uploading the images
	 * that compose the problem, the client would downlad an incomplete copy and
	 * later won't update the value because there is a problem with the id.
	 * 
	 * I decided to add a 4th line which tells if the user already uploaded all the
	 * images tha compose the payload.
	 */
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
		builder.append(problem.get().getState());
		builder.append("\n");

		return builder.toString();
	}

	@PutMapping(value = "/v1/problems/{id}", produces = { "plain/text" })
	public void setState(@RequestParam("state") STATES state, @PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		problem.get().setState(state);
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
				// tar will complain if the timestamp is in the future
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

		problem.get().setState(STATES.IN_PROGESS);
		problem.get().getImages().add(payload);
		repositoryProblem.save(problem.get());

		return "{\"id\": \" " + problem.get().getId() + " \"}";
	}

}

package pandora.server.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pandora.server.conf.ConfigurationProperties;
import pandora.server.model.PandoraClient;
import pandora.server.model.RSAPayload;
import pandora.server.model.RSAProblem;
import pandora.server.model.RSAProblem.STATES;
import pandora.server.repository.PandoraClientRepository;
import pandora.server.repository.RSAPayloadRepository;
import pandora.server.repository.RSAProblemRepository;

@RestController
public class RSAController {

	private static final Logger log = LoggerFactory.getLogger(RSAController.class);

	@Autowired
	private ConfigurationProperties properties;

	@Autowired
	RSAProblemRepository repositoryProblem;
	
	@Autowired
	PandoraClientRepository repositoryClient;
	

	@Autowired
	RSAPayloadRepository repositoryPayload;

	/*
	 * TODO to be consistent this shuould produce a json file.
	 */
	@GetMapping(value = "/v1/problems", produces = { "plain/text" })
	public String index() {
		StringBuilder builder = new StringBuilder();
		for (RSAProblem problem : repositoryProblem.findAll()) {
			builder.append(problem.getId());
			builder.append("\n");
		}
		return builder.toString();
	}

	/*
	 * It might be that the client syncs the problem before uploading the images
	 * that compose the problem, the client would downlad an incomplete copy and
	 * later won't update the value because there is a problem with the id.
	 * 
	 * I decided to add a 4th line which tells if the user already uploaded all the
	 * images tha compose the payload.
	 */
	@GetMapping(value = "/v1/problems/{id}", produces = { "application/json" })
	public RSAProblem key(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		problem.get().setImages(null);

		return problem.get();
	}

	/*
	 * TODO i wasn't able to user @RequestParam, it calls the service twice.
	 */
	@PostMapping(value = "/v1/problems/{delay}", produces = { "application/json" })
	public RSAProblem create(@PathVariable String delay) {
		/*
		 * TODO it is missing to generate the RSA key pair, the easiest is to use the
		 * bash utils.
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
		return problem;
	}

	@PutMapping(value = "/v1/problems/{id}", consumes = { "application/json" })
	public void setState(@PathVariable("id") Long id, @RequestBody RSAProblem problem) {
		Optional<RSAProblem> entity = repositoryProblem.findById(id);
		if (entity == Optional.<RSAProblem>empty())
			throw new IllegalStateException("There is no problem with the provided ID");
		entity.get().setState(problem.getState());
		repositoryProblem.save(entity.get());
	}

	// Este metodo contiene logica repetica con ClientController
	private List<PandoraClient> checkClientsSynced(Long id) {
		ArrayList<PandoraClient> pending = new ArrayList<>();

		List<PandoraClient> clients = repositoryClient.findAll();
		for (PandoraClient client : clients) {
			Boolean synced = false;
			for (RSAProblem problem : client.getProblems())
				if (problem.getId().equals(id))
					synced = true;
			if (!synced)
				pending.add(client);
		}

		return pending;
	}

	
	/*
	 * I think this can be rewriten using functional sintax.
	 */
	private PandoraClient removeProblemFromClient(PandoraClient client, RSAProblem problem) {
		Set<RSAProblem> active = new HashSet<>();

		for (RSAProblem storedProblem : client.getProblems())
			if (!problem.getId().equals(storedProblem.getId()))
				active.add(problem);

		client.setProblems(active);
		client = repositoryClient.saveAndFlush(client);
		return client;
	}
	
	
	/*
	 * The problem is going to be deleted even if nobody calls this service, the PandoraClientServiceImpl.update will remove the problem when the last
	 * client updates its state.
	 */
	
	/*
	 * it is missing to set an apropiate code when the problems doesn't exists, right now it is
	 * sending a 500 code error, it is hard to distinguish in between a crash and the normal flow.
	 */	
	@DeleteMapping(value = "/v1/problems/{id}", produces = { "application/json" })
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException("There is no problem with the provided ID");

		List<PandoraClient> synced = checkClientsSynced(id);

		HttpStatus status = HttpStatus.ACCEPTED;
		if (synced.isEmpty()) {
			List<PandoraClient> clients = repositoryClient.findAll();
			for (PandoraClient client : clients)
				removeProblemFromClient(client, problem.get());
			
			status = HttpStatus.OK;
			repositoryProblem.delete(problem.get());
		}

		return new ResponseEntity<>(status);
	}

	/*
	 * TODO it should notify the user if trying to download the problem without
	 * checking that it is already completed.
	 */
	@GetMapping(value = "/v1/problems/{id}/images", produces = { "plain/text" })
	public byte[] payload(@PathVariable("id") Long id) {
		Optional<RSAProblem> problem = repositoryProblem.findById(id);
		if (problem == Optional.<RSAProblem>empty())
			throw new IllegalStateException();

		Set<RSAPayload> images = problem.get().getImages();

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

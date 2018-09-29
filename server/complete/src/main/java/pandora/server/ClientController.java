package pandora.server;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pandora.server.conf.PandoraClientRepository;
import pandora.server.conf.RSAProblemRepository;
import pandora.server.model.PandoraClient;
import pandora.server.model.RSAProblem;

@RestController
public class ClientController {

	private static final Logger log = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	RSAProblemRepository repositoryProblem;

	@Autowired
	PandoraClientRepository clientRepository;

	/*
	 * TODO to be consistent this shuould produce a json file.
	 */
	@GetMapping(value = "/v1/clients", produces = { "application/json" })
	public Iterable<PandoraClient> index() {
		Iterable<PandoraClient> clients = clientRepository.findAll();
		for (PandoraClient client : clients) {
			client.setProblems(null);
		}
		return clients;
	}

	@PostMapping(value = "/v1/clients", produces = { "application/json" })
	public PandoraClient create(@RequestBody PandoraClient client) {
		PandoraClient last = new PandoraClient(client.getHostname());
		last = clientRepository.save(last);
		last.setProblems(null);
		return last;
	}

	@PatchMapping(value = "/v1/clients/{id}", produces = { "application/json" })
	public void update(@PathVariable("id") Long id, @RequestBody PandoraClient client) {
		Optional<PandoraClient> entity = clientRepository.findByHostname(client.getHostname());
		if (entity == Optional.<PandoraClient>empty())
			throw new IllegalStateException("There is no client with the provided hostname");

		List<RSAProblem> localProblems = new ArrayList<>();
		List<RSAProblem> remoteProblems = client.getProblems();
		for (RSAProblem problem : remoteProblems) {
			Optional<RSAProblem> remote = repositoryProblem.findById(problem.getId());
			if (remote == Optional.<RSAProblem>empty()) {
				log.error("The client is running a problem that doesn't even exists");
				continue;
			}

			localProblems.add(remote.get());
		}
		
		entity.get().setState(PandoraClient.STATES.HEALTHY);
		entity.get().setProblems(localProblems);
		clientRepository.save(entity.get());
	}
	

	@DeleteMapping(value = "/v1/clients/{id}", produces = { "application/json" })
	public void delete(@PathVariable("id") Long id, @RequestBody PandoraClient client) {
		Optional<PandoraClient> entity = clientRepository.findByHostname(client.getHostname());
		if (entity == Optional.<PandoraClient>empty())
			throw new IllegalStateException("There is no client with the provided hostname");
		clientRepository.delete(entity.get());
	}

}

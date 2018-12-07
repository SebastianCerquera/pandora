package pandora.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pandora.server.dto.PandoraClientDTO;
import pandora.server.model.PandoraClient;
import pandora.server.model.RSAProblem;
import pandora.server.repository.PandoraClientRepository;
import pandora.server.repository.RSAProblemRepository;
import pandora.server.service.PandoraService;

@Service
public class PandoraClientServiceImpl implements PandoraService {

	public static final String ERR_NOT_FOUND = "There is no client with the provided hostname";

        private static final Logger log = LoggerFactory.getLogger(PandoraClientServiceImpl.class);
    
	@Autowired
	ModelMapService mapService;

	@Autowired
	RSAServiceImpl rsaServiceImpl;
	
	@Autowired
	PandoraClientRepository pandoraClientRepository;
	
	@Autowired
	RSAProblemRepository repositoryProblem;
	
    
	@Override
	public List<PandoraClientDTO> findAll() {
		log.info("Retrieving registered clients from db.");
		return mapService.mapList(pandoraClientRepository.findAll(), PandoraClientDTO.class);
	}

	@Override
	public Optional<PandoraClientDTO> findById(Long id) {
		return mapService.map(pandoraClientRepository.findById(id), PandoraClientDTO.class);
	}

	@Override
	public PandoraClientDTO save(PandoraClientDTO pandoraClientDTO) {
		log.info("Saving cliento to db, client hostname: " + pandoraClientDTO.getHostname());
		PandoraClient entity = mapService.map(pandoraClientDTO, PandoraClient.class);
		entity = pandoraClientRepository.saveAndFlush(entity);
		return mapService.map(entity, PandoraClientDTO.class);
	}

	private List<PandoraClient> checkClientsSynced(Long id) {
		ArrayList<PandoraClient> pending = new ArrayList<>();

		List<PandoraClient> clients = pandoraClientRepository.findAll();
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
	
	@Override
	public PandoraClientDTO update(PandoraClientDTO pandoraClientDTO) {

		PandoraClient client = retrieveByHostname(pandoraClientDTO);

		Set<RSAProblem> problems = refreshClientProblems(client); 
		client.setProblems(problems);
		client.setState(PandoraClient.STATES.HEALTHY);

		log.info("Cliento state succesfully updated, client hostname: " + pandoraClientDTO.getHostname());
		
		
		for(RSAProblem problem : problems) {
			List<PandoraClient> clients = checkClientsSynced(problem.getId());
			if(clients.size() == 1) {
				PandoraClient singleton = clients.get(0); 
				if(singleton.getId().equals(problem.getId())) {
					repositoryProblem.delete(problem);
					log.info("The remaming client synced the problem. deletes problem: " + problem.getModulus());
				}
			}
				
		}		
		
		return mapService.map(client, PandoraClientDTO.class);		
	}
	
	@Override
	public PandoraClientDTO findByHostname(String hostname) {
		return mapService.map(retrieveByHostname(hostname), PandoraClientDTO.class);
	}

	private Set<RSAProblem> refreshClientProblems(PandoraClient pandoraClient) {
		return pandoraClient.getProblems().stream()
				.map(p -> rsaServiceImpl.fetchByProblemId().apply(p.getId()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private PandoraClient retrieveByHostname(PandoraClientDTO pandoraClientDTO) {
		return retrieveByHostname(pandoraClientDTO.getHostname());
	}
	
	private PandoraClient retrieveByHostname(String hostname) {
		return pandoraClientRepository.findByHostname(hostname)
				.map(p -> p)
				.orElseThrow(() -> new IllegalStateException(ERR_NOT_FOUND));
	}
	

	@Override
	public Boolean deleteById(Long id) {		
		pandoraClientRepository.deleteById(id);
		return Boolean.TRUE;
	}
}

package pandora.server.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pandora.server.dto.PandoraClientDTO;
import pandora.server.model.PandoraClient;
import pandora.server.model.RSAProblem;
import pandora.server.repository.PandoraClientRepository;
import pandora.server.service.PandoraService;

@Service
public class PandoraClientServiceImpl implements PandoraService {

	public static final String ERR_NOT_FOUND = "There is no client with the provided hostname";
	
	@Autowired
	ModelMapService mapService;

	@Autowired
	RSAServiceImpl rsaServiceImpl;

	@Autowired
	PandoraClientRepository pandoraClientRepository;
	

	@Override
	public List<PandoraClientDTO> findAll() {	
		return mapService.mapList(pandoraClientRepository.findAll(), PandoraClientDTO.class);
	}

	@Override
	public Optional<PandoraClientDTO> findById(Long id) {
		return mapService.map(pandoraClientRepository.findById(id), PandoraClientDTO.class);
	}

	@Override
	public PandoraClientDTO save(PandoraClientDTO pandoraClientDTO) {
		return mapService.map(pandoraClientDTO, PandoraClientDTO.class);
	}

	@Override
	public PandoraClientDTO update(PandoraClientDTO pandoraClientDTO) {

		PandoraClient client = retrieveByHostname(pandoraClientDTO);

		client.setProblems(refreshClientProblems(client));
		client.setState(PandoraClient.STATES.HEALTHY);

		return mapService.map(client, PandoraClientDTO.class);		
	}
	
	@Override
	public PandoraClientDTO findByHostname(String hostname) {
		return mapService.map(retrieveByHostname(hostname), PandoraClientDTO.class);
	}

	private Set<RSAProblem> refreshClientProblems(PandoraClient pandoraClient) {
		return pandoraClient.getProblems().stream()
				.map(p -> rsaServiceImpl.fetchByProblemId().apply(p))
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

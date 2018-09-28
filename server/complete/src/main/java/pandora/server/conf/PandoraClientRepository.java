package pandora.server.conf;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import pandora.server.model.PandoraClient;

public interface PandoraClientRepository extends CrudRepository<PandoraClient, Long> {
	
	Optional<PandoraClient> findByHostname(String hostname);

}

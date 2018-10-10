package pandora.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pandora.server.model.PandoraClient;

public interface PandoraClientRepository extends JpaRepository<PandoraClient, Long> {
	
	Optional<PandoraClient> findByHostname(String hostname);

}

package pandora.server.conf;

import org.springframework.data.repository.CrudRepository;

import pandora.server.model.RSAPayload;

public interface RSAPayloadRepository extends CrudRepository<RSAPayload, Long> {

}


package pandora.server.conf;

import org.springframework.data.repository.CrudRepository;

import pandora.server.model.RSAProblem;

public interface RSAProblemRepository extends CrudRepository<RSAProblem, Long> {

}

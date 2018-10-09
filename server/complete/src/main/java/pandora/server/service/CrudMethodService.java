package pandora.server.service;

import java.util.List;
import java.util.Optional;

import pandora.server.dto.PandoraClientDTO;

public interface CrudMethodService <E,I> {

	List<E> findAll();
	Optional<PandoraClientDTO> findById(I id);
	E save (E entity);
	E update(E entity);	
	Boolean deleteById(Long id);
}

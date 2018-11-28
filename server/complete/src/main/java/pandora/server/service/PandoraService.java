package pandora.server.service;

import pandora.server.dto.PandoraClientDTO;

public interface PandoraService extends CrudMethodService<PandoraClientDTO, Long> {
	PandoraClientDTO findByHostname(String hostname);
}
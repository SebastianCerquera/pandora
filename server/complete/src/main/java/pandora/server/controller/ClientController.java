package pandora.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pandora.server.dto.PandoraClientDTO;
import pandora.server.service.PandoraService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/v1")
public class ClientController {

	@Autowired
	PandoraService pandoraService;

	@PostMapping(value = "/clients")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody ResponseEntity<PandoraClientDTO> create(@RequestBody PandoraClientDTO pandoraClientDTO) {
		return new ResponseEntity<>(pandoraService.save(pandoraClientDTO), HttpStatus.CREATED);
	}

	@GetMapping(value = "/clients")
	public ResponseEntity<List<PandoraClientDTO>> index() {
		return new ResponseEntity<>(pandoraService.findAll(),HttpStatus.OK);
	}
	
	@GetMapping(value = "/clients/{hostname}")
	public ResponseEntity<PandoraClientDTO> getByHostname(@PathVariable("hostname") String hostname) {
		return new ResponseEntity<>(pandoraService.findByHostname(hostname),HttpStatus.OK);
	}

	@PutMapping(value = "/clients")
	public ResponseEntity<PandoraClientDTO> update(@RequestBody PandoraClientDTO pandoraClientDTO) {
		return new ResponseEntity<>(pandoraService.update(pandoraClientDTO), HttpStatus.ACCEPTED);
	}

	@ResponseBody
	@DeleteMapping(value = "/clients/{id}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("id") Long id) {
		return new ResponseEntity<>(pandoraService.deleteById(id), HttpStatus.OK);
	}
}

package pandora.server.service.impl;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pandora.server.model.RSAProblem;
import pandora.server.repository.RSAProblemRepository;

@Service
public class RSAServiceImpl {
	
	private static final Logger log = LoggerFactory.getLogger(PandoraClientServiceImpl.class);
	
	@Autowired
	RSAProblemRepository problemRepository;
	
	protected Function<RSAProblem, RSAProblem> fetchByProblemId() {
		
		return problem -> {
			
			Optional<RSAProblem> rsaProblem = problemRepository.findById(problem.getId());

			if(!rsaProblem.isPresent()) {				
				log.error("The client is running a problem that doesn't even exists");
				return null;
			}else {
				return rsaProblem.get();
			}
		};
	}

}

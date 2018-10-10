package pandora.server.service.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pandora.server.util.ReflectionUtil;

@Service
public class ModelMapService {

	@Autowired
	ModelMapper mapper;
	
	public <D,T> D map(T modelEntity, Class<D> dtoClass) {
		return mapper.map(modelEntity, dtoClass);
	}
	
	public <D,T> Optional<D> map(Optional<T> modelEntity, Class<D> dtoClass) {
		return modelEntity.map(t -> mapper.map(t, dtoClass));
	}
	
	public <D,T> List<D> mapList(T modelEntity, Class<D> dtoClass) {
		return mapper.map(modelEntity, ReflectionUtil.getTypeToken(dtoClass));
	}
	
	

}

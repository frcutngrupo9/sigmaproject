package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ToolType;

public interface ToolTypeService {

	List<ToolType> getToolTypeList();

	ToolType getToolType(Integer idToolType);

	ToolType saveToolType(ToolType supplyType);

	ToolType updateToolType(ToolType supplyType);

	void deleteToolType(ToolType supply);
}

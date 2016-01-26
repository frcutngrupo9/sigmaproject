package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ToolType;
import ar.edu.utn.sigmaproject.service.ToolTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ToolTypeServiceImpl implements ToolTypeService {

    static List<ToolType> toolTypeList = new ArrayList<ToolType>();
    private SerializationService serializator = new SerializationService("tool_type");
    
    public ToolTypeServiceImpl() {
        List<ToolType> aux = serializator.obtenerLista();
        if(aux != null) {
        	toolTypeList = aux;
        } else {
            serializator.grabarLista(toolTypeList);
        }
    }
    
    public synchronized List<ToolType> getToolTypeList() {
        List<ToolType> list = new ArrayList<ToolType>();
        for(ToolType each: toolTypeList) {
            list.add(ToolType.clone(each));
        }
        return list;
    }
    
    public synchronized ToolType getToolType(Integer id) {
        int size = toolTypeList.size();
        for(int i=0; i<size; i++) {
        	ToolType t = toolTypeList.get(i);
            if(t.getId().equals(id)) {
                return ToolType.clone(t);
            }
        }
        return null;
    }
    
    public synchronized ToolType saveToolType(ToolType toolType) {
        if(toolType.getId() == null) {
        	toolType.setId(getNewId());
        }
        toolType = ToolType.clone(toolType);
        toolTypeList.add(toolType);
        serializator.grabarLista(toolTypeList);
        return toolType;
    }
    
    public synchronized ToolType updateToolType(ToolType toolType) {
        if(toolType.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id tool type, save it first");
        }else {
        	toolType = ToolType.clone(toolType);
            int size = toolTypeList.size();
            for(int i=0; i<size; i++) {
            	ToolType t = toolTypeList.get(i);
                if(t.getId().equals(toolType.getId())){
                	toolTypeList.set(i, toolType);
                    serializator.grabarLista(toolTypeList);
                    return toolType;
                }
            }
            throw new RuntimeException("Tool Type not found " + toolType.getId());
        }
    }
    
    public synchronized void deleteToolType(ToolType toolType) {
        if(toolType.getId() != null) {
            int size = toolTypeList.size();
            for(int i=0; i<size; i++) {
            	ToolType t = toolTypeList.get(i);
                if(t.getId().equals(toolType.getId())){
                	toolTypeList.remove(i);
                    serializator.grabarLista(toolTypeList);
                    return;
                }
            }
        }
    }
    
    private synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0; i<toolTypeList.size(); i++) {
        	ToolType aux = toolTypeList.get(i);
            if(lastId < aux.getId()){
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }
}

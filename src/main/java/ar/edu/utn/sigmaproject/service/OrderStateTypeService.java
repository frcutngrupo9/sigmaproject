package ar.edu.utn.sigmaproject.service;

import java.util.List;
import ar.edu.utn.sigmaproject.domain.OrderStateType;

public interface OrderStateTypeService {
	
	List<OrderStateType> getOrderStateTypeList();
	
	OrderStateType getOrderStateType(Integer id);

}

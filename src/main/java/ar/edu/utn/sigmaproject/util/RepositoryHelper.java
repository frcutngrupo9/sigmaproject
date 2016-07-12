package ar.edu.utn.sigmaproject.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RepositoryHelper {

	@Autowired
	private MeasureUnitRepository measureUnitRepository;

	@Autowired
	private MeasureUnitTypeRepository measureUnitTypeRepository;

	@Autowired
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;

	@Autowired
	private ProductionOrderStateRepository productionOrderStateRepository;

	@Autowired
	private OrderStateTypeRepository orderStateTypeRepository;

	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Autowired
	private ProcessTypeRepository processTypeRepository;
	
	@Autowired
	private MachineTypeRepository machineTypeRepository;

	@PostConstruct
	public void afterConstruct() {
		generateMeasureUnitTypeList();
		generateProductionPlanStateTypes();
		generateProductionOrderStates();
		generateOrderStateType();
		generateProductCategory();
		generateProcessType();
	}

	private void generateMeasureUnitTypeList() {
		addMeasureUnitsIfNeeded("Longitud", Arrays.asList(
				Arrays.asList("Metros", "M"),
				Arrays.asList("Centimetros", "Cm"),
				Arrays.asList("Milimetros", "Mm"),
				Arrays.asList("Pulgadas", "Pul")
		));
		addMeasureUnitsIfNeeded("Tiempo", Arrays.asList(
				Arrays.asList("Minutos", "Min"),
				Arrays.asList("Horas", "Hr"),
				Arrays.asList("Dias", "D")
		));
		addMeasureUnitsIfNeeded("Masa", Arrays.asList(
				Arrays.asList("Kilogramos", "Kg"),
				Arrays.asList("Gramos", "Gr"),
				Arrays.asList("Litros", "L"),
				Arrays.asList("Mililitros", "Ml")
		));
		addMeasureUnitsIfNeeded("Cantidad", Arrays.asList(
				Arrays.asList("Unidad", "Unid")
		));
	}

	private void addMeasureUnitsIfNeeded(String measureUnitTypeName, List<List<String>> definitions) {
		MeasureUnitType lengthMeasureUnitType = measureUnitTypeRepository.findFirstByName(measureUnitTypeName);
		if (lengthMeasureUnitType == null) {
			MeasureUnitType measureUnitType = new MeasureUnitType(measureUnitTypeName);
			measureUnitTypeRepository.save(measureUnitType);
			List<MeasureUnit> measureUnitToInsert = new ArrayList<>();
			for (List<String> definition : definitions) {
				measureUnitToInsert.add(new MeasureUnit(definition.get(0), definition.get(1), measureUnitType));
			}
			measureUnitRepository.save(measureUnitToInsert);
		}
	}

	private void generateProductionPlanStateTypes() {
		if (productionPlanStateTypeRepository.count() == 0) {
			List<ProductionPlanStateType> list = new ArrayList<>();
			list.add(new ProductionPlanStateType("Iniciado", null));
			list.add(new ProductionPlanStateType("Cancelado", null));
			list.add(new ProductionPlanStateType("Abastecido", null));
			list.add(new ProductionPlanStateType("Lanzado", null));
			list.add(new ProductionPlanStateType("En Produccion", null));
			list.add(new ProductionPlanStateType("Finalizado", null));
			productionPlanStateTypeRepository.save(list);
		}
	}

	private void generateProductionOrderStates() {
		if (productionOrderStateRepository.count() == 0) {
			List<ProductionOrderState> list = new ArrayList<>();
			list.add(new ProductionOrderState("Generada"));
			list.add(new ProductionOrderState("Iniciada"));
			list.add(new ProductionOrderState("Finalizada"));
			list.add(new ProductionOrderState("Cancelada"));
			productionOrderStateRepository.save(list);
		}
	}

	private void generateOrderStateType() {
		if (orderStateTypeRepository.count() == 0) {
			List<OrderStateType> list = new ArrayList<>();
			list.add(new OrderStateType("Iniciado", null));
			list.add(new OrderStateType("Cancelado", null));
			list.add(new OrderStateType("Planificado", null));
			list.add(new OrderStateType("En Produccion", null));
			list.add(new OrderStateType("Finalizado", null));
			list.add(new OrderStateType("Entregado", null));
			orderStateTypeRepository.save(list);
		}
	}

	private void generateProductCategory() {
		if (productCategoryRepository.count() == 0) {
			List<ProductCategory> list = new ArrayList<>();
			list.add(new ProductCategory("Armario"));
			list.add(new ProductCategory("Biblioteca"));
			list.add(new ProductCategory("Comoda"));
			list.add(new ProductCategory("Cajonera"));
			list.add(new ProductCategory("Cama"));
			list.add(new ProductCategory("Escritorio"));
			list.add(new ProductCategory("Mesa"));
			list.add(new ProductCategory("Silla"));
			list.add(new ProductCategory("Sillon"));
			productCategoryRepository.save(list);
		}
	}
	
	private void generateMachineType() {
		List<MachineType> list = new ArrayList<>();
		list.add(new MachineType("Garlopa", "", null));
		list.add(new MachineType("Cepilladora", "", null));
		list.add(new MachineType("Escuadradora", "", null));
		list.add(new MachineType("Escopladora", "", null));
		list.add(new MachineType("Tupí", "", null));
		list.add(new MachineType("Sierra Sin Fin", "", null));
		list.add(new MachineType("Lijadora", "", null));
		machineTypeRepository.save(list);
	}


	private void generateProcessType() {
		if (processTypeRepository.count() == 0) {
			generateMachineType();
			List<ProcessType> list = new ArrayList<>();
			list.add(new ProcessType("Trazar", null));
			list.add(new ProcessType("Garlopear", machineTypeRepository.findFirstByName("Garlopa")));
			list.add(new ProcessType("Asentar", machineTypeRepository.findFirstByName("Garlopa")));
			list.add(new ProcessType("Cepillar", machineTypeRepository.findFirstByName("Cepilladora")));
			list.add(new ProcessType("Cortar el Ancho", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType("Cortar el Largo", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType("Hacer Cortes Curvos", machineTypeRepository.findFirstByName("Sierra Sin Fin")));
			list.add(new ProcessType("Hacer Escopladuras", machineTypeRepository.findFirstByName("Escopladora")));
			list.add(new ProcessType("Hacer Espigas", machineTypeRepository.findFirstByName("Tupí")));
			list.add(new ProcessType("Hacer Molduras", machineTypeRepository.findFirstByName("Tupí")));
			list.add(new ProcessType("Hacer Canales", machineTypeRepository.findFirstByName("Tupí")));
			list.add(new ProcessType("Replanar", null));
			list.add(new ProcessType("Masillar", null));
			list.add(new ProcessType("Clavar", null));
			list.add(new ProcessType("Lijar Cruzado", machineTypeRepository.findFirstByName("Lijadora")));
			list.add(new ProcessType("Lijar Derecho", machineTypeRepository.findFirstByName("Lijadora")));
			list.add(new ProcessType("Agregar Herrajes", null));
			list.add(new ProcessType("Armar", null));
			processTypeRepository.save(list);
		}
	}

}

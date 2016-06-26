package ar.edu.utn.sigmaproject.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
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
			// asumimos que como no estan creados los tipos de UM tampoco lo estan los UM
			// por lo tanto creamos los UM
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
		List<ProductionPlanStateType> list = new ArrayList<>();
		list.add(new ProductionPlanStateType("Iniciado", null));
		list.add(new ProductionPlanStateType("Cancelado", null));
		list.add(new ProductionPlanStateType("Abastecido", null));
		list.add(new ProductionPlanStateType("Lanzado", null));
		list.add(new ProductionPlanStateType("En Produccion", null));
		list.add(new ProductionPlanStateType("Finalizado", null));
		for(ProductionPlanStateType each : list) {
			productionPlanStateTypeRepository.save(each);
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

	private void generateProcessType() {
		if (processTypeRepository.count() == 0) {
			List<ProcessType> list = new ArrayList<>();
			list.add(new ProcessType("Trazar", null));
			list.add(new ProcessType("Garlopear", null));
			list.add(new ProcessType("Asentar", null));
			list.add(new ProcessType("Cepillar", null));
			list.add(new ProcessType("Cortar el Ancho", null));
			list.add(new ProcessType("Cortar el Largo", null));
			list.add(new ProcessType("Hacer Cortes Curvos", null));
			list.add(new ProcessType("Hacer Escopladuras", null));
			list.add(new ProcessType("Hacer Espigas", null));
			list.add(new ProcessType("Hacer Molduras", null));
			list.add(new ProcessType("Hacer Canales", null));
			list.add(new ProcessType("Replanar", null));
			list.add(new ProcessType("Masillar", null));
			list.add(new ProcessType("Clavar", null));
			list.add(new ProcessType("Lijar Cruzado", null));
			list.add(new ProcessType("Lijar Derecho", null));
			list.add(new ProcessType("Agregar Herrajes", null));
			list.add(new ProcessType("Armar", null));
			processTypeRepository.save(list);
		}
	}

}

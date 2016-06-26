package ar.edu.utn.sigmaproject.util;

import java.util.ArrayList;
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

public class RepositoryHelper {

	public void generateMeasureUnitTypeList(MeasureUnitRepository mUR, MeasureUnitTypeRepository mUTR) {
		List<MeasureUnitType> measureUnitTypeList = new ArrayList<MeasureUnitType>();
		measureUnitTypeList.add(new MeasureUnitType("Longitud"));
		measureUnitTypeList.add(new MeasureUnitType("Tiempo"));
		measureUnitTypeList.add(new MeasureUnitType("Masa"));
		measureUnitTypeList.add(new MeasureUnitType("Cantidad"));
		for(MeasureUnitType aux : measureUnitTypeList) {
			mUTR.save(aux);
		}
		// asumimos que como no estan creados los tipos de UM tampoco lo estan los UM
		// por lo tanto creamos los UM
		MeasureUnitType mUTLongitud = mUTR.findByName("Longitud");
		MeasureUnitType mUTTiempo = mUTR.findByName("Tiempo");
		MeasureUnitType mUTMasa = mUTR.findByName("Masa");
		MeasureUnitType mUTCantidad = mUTR.findByName("Cantidad");
		List<MeasureUnit> measureUnitList = new ArrayList<MeasureUnit>();
		measureUnitList.add(new MeasureUnit("Metros", "M", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Centimetros", "Cm", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Milimetros", "Mm", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Pulgadas", "Pul", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Minutos", "Min", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Horas", "Hr", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Dias", "D", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Kilogramos", "Kg", mUTMasa));
		measureUnitList.add(new MeasureUnit("Gramos", "Gr", mUTMasa));
		measureUnitList.add(new MeasureUnit("Litros", "L", mUTMasa));
		measureUnitList.add(new MeasureUnit("Mililitros", "Ml", mUTMasa));
		measureUnitList.add(new MeasureUnit("Unidad", "Unid", mUTCantidad));
		for(MeasureUnit aux : measureUnitList) {
			mUR.save(aux);
		}
	}

	public void generateProductionPlanStateTypes(ProductionPlanStateTypeRepository repository) {
		List<ProductionPlanStateType> list = new ArrayList<ProductionPlanStateType>();
		list.add(new ProductionPlanStateType("Iniciado", null));
		list.add(new ProductionPlanStateType("Cancelado", null));
		list.add(new ProductionPlanStateType("Abastecido", null));
		list.add(new ProductionPlanStateType("Lanzado", null));
		list.add(new ProductionPlanStateType("En Produccion", null));
		list.add(new ProductionPlanStateType("Finalizado", null));
		for(ProductionPlanStateType each : list) {
			repository.save(each);
		}
	}

	public void generateProductionOrderStates(ProductionOrderStateRepository repository) {
		List<ProductionOrderState> list = new ArrayList<ProductionOrderState>();
		list.add(new ProductionOrderState("Generada"));
		list.add(new ProductionOrderState("Iniciada"));
		list.add(new ProductionOrderState("Finalizada"));
		list.add(new ProductionOrderState("Cancelada"));
		for(ProductionOrderState each : list) {
			repository.save(each);
		}
	}

	public void generateOrderStateType(OrderStateTypeRepository repository) {
		List<OrderStateType> list = new ArrayList<OrderStateType>();
		list.add(new OrderStateType("Iniciado", null));
		list.add(new OrderStateType("Cancelado", null));
		list.add(new OrderStateType("Planificado", null));
		list.add(new OrderStateType("En Produccion", null));
		list.add(new OrderStateType("Finalizado", null));
		for(OrderStateType each : list) {
			repository.save(each);
		}
	}

	public void generateProductCategory(ProductCategoryRepository repository) {
		List<ProductCategory> list = new ArrayList<ProductCategory>();
		list.add(new ProductCategory("Armario"));
		list.add(new ProductCategory("Biblioteca"));
		list.add(new ProductCategory("Comoda"));
		list.add(new ProductCategory("Cajonera"));
		list.add(new ProductCategory("Cama"));
		list.add(new ProductCategory("Escritorio"));
		list.add(new ProductCategory("Mesa"));
		list.add(new ProductCategory("Silla"));
		list.add(new ProductCategory("Sillon"));
		for(ProductCategory each : list) {
			repository.save(each);
		}
	}
	
	public void generateProcessType(ProcessTypeRepository repository) {
		List<ProcessType> list = new ArrayList<ProcessType>();
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
		for(ProcessType each : list) {
			repository.save(each);
		}
	}

}

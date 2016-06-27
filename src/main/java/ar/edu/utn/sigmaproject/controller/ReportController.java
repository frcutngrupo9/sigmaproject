package ar.edu.utn.sigmaproject.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.RepositoryHelper;

public class ReportController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Label informationLabel;
	
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductCategoryRepository productCategoryRepository;
	@WireVariable
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;
	@WireVariable
	private ClientRepository clientRepository;
	@WireVariable
	private WorkerRepository workerRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(productRepository.findAll().isEmpty()) {
			informationLabel.setValue("Carga de valores iniciada.");
			new RepositoryHelper().generateEverything(productRepository, productCategoryRepository, 
					processTypeRepository, machineTypeRepository, productionPlanStateTypeRepository, 
					productionOrderStateRepository, orderStateTypeRepository, measureUnitRepository, 
					measureUnitTypeRepository, clientRepository, workerRepository, 
					supplyTypeRepository, woodTypeRepository);
			informationLabel.setValue("Carga de valores iniciales completa.");
		} else {
			informationLabel.setValue("Base de Datos con valores iniciales cargados.");
		}
	}
}

package ar.edu.utn.sigmaproject.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

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

	@Autowired
	private WoodTypeRepository woodTypeRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private WorkerRepository workerRepository;

	@Autowired
	private RawMaterialTypeRepository rawMaterialTypeRepository;

	@Autowired
	private SupplyTypeRepository supplyTypeRepository;
	
	@Autowired
	private WoodRepository woodRepository;
	
	@Autowired
	private MachineRepository machineRepository;

	@PostConstruct
	public void afterConstruct() {
		generateMeasureUnitTypeList();
		generateProductionPlanStateTypes();
		generateProductionOrderStates();
		generateOrderStateType();
		generateProduct();
		generateProcessType();
		generateWood();
		generateClient();
		generateWorker();
		generateSupplyType();
		generateMachine();
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

	private void generateMachineType() {
		if (machineTypeRepository.count() == 0) {
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

	private void generateWoodType() {
		if (woodTypeRepository.count() == 0) {
			List<WoodType> list = new ArrayList<>();
			list.add(new WoodType("Pino", "Semi-pesada, semi-dura."));
			list.add(new WoodType("Caoba", "Tradicional, dura y compacta."));
			list.add(new WoodType("Nogal", "Dura, homogénea."));
			list.add(new WoodType("Roble", "Resistente, duradera y compacta."));
			woodTypeRepository.save(list);
		}
	}

	private void generateProductCategory() {
		if (productCategoryRepository.count() == 0) {
			List<ProductCategory> list = new ArrayList<>();
			list.add(new ProductCategory("Banco"));
			list.add(new ProductCategory("Biblioteca"));
			list.add(new ProductCategory("Comoda"));
			list.add(new ProductCategory("Cajonera"));
			list.add(new ProductCategory("Cama"));
			list.add(new ProductCategory("Marco"));
			list.add(new ProductCategory("Mesa"));
			list.add(new ProductCategory("Respaldo"));
			list.add(new ProductCategory("Silla"));
			list.add(new ProductCategory("Sillon"));
			productCategoryRepository.save(list);
		}
	}

	private void generateProduct() {
		if (productRepository.count() == 0) {
			generateProductCategory();
			List<Product> list = new ArrayList<>();
			list.add(new Product("1", "Mesa patas 4x4 rectas de 1.20x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("840")));
			list.add(new Product("7", "Mesa patas Reina Ana de 1.40x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1045")));
			list.add(new Product("26", "Dressoir patas rectas de 0.90x0.45mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Comoda"), new BigDecimal("483")));
			list.add(new Product("29", "Banqueta alta  con respaldo de 0.30mts de diámetro".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("226")));
			list.add(new Product("30", "Banqueta alta con respaldo omega".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("294")));
			list.add(new Product("31", "Silla de campo resp. c/ tablero o varillas".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("252")));
			list.add(new Product("32", "Silla Omega respaldo inclinado patas rectas".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("231")));
			list.add(new Product("34", "Cama Omega de 1.40mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("704")));
			list.add(new Product("37", "Cama Monterrey c/curva patas 4x4 de 080cm".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1050")));
			list.add(new Product("40", "Respaldo para Somier Monterrey X o Curvo de 1.50mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Respaldo"), new BigDecimal("1029")));
			list.add(new Product("43", "Marco de espejo".toUpperCase(), "", productCategoryRepository.findFirstByName("Marco"), new BigDecimal("189")));
			list.add(new Product("44", "Sillon Hamaca grande".toUpperCase(), "", productCategoryRepository.findFirstByName("Sillon"), new BigDecimal("504")));
			list.add(new Product("45", "Dresoir de 3 cajones con estante".toUpperCase(), "", productCategoryRepository.findFirstByName("Comoda"), new BigDecimal("536")));
			list.add(new Product("47", "Mesa de lámpara con 1 cajon".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("284")));
			list.add(new Product("51", "Chiffonier de 1,45x0,70x0,40 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cajonera"), new BigDecimal("1245")));
			list.add(new Product("53", "Mesa de luz de 0,70x0,50x0,40 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("473")));
			list.add(new Product("56", "Biblioteca de 0,60 mts con estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("756")));
			list.add(new Product("57", "Biblioteca de 0,60 mts con 4 cajones y estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("1024")));
			list.add(new Product("59", "Sillon Romano de dos cuerpos".toUpperCase(), "", productCategoryRepository.findFirstByName("Sillon"), new BigDecimal("483")));
			list.add(new Product("60", "Banquito recto o redondo chico".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("153")));
			list.add(new Product("61", "Sillon punta de cama Reina Ana 1,00 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Sillon"), new BigDecimal("380")));
			productRepository.save(list);
		}
	}

	private void generateClient() {
		if (clientRepository.count() == 0) {
			List<Client> list = new ArrayList<>();
			list.add(new Client("RESTOCK", "", "", "", "RESERVADO RESTOCK"));
			list.add(new Client("CLIENTE 1", "03514704411", "EMAIL1@MAILSERVER.COM", "DIRECCION 1", "DETALLES 1"));
			list.add(new Client("CLIENTE 2", "03514704412", "EMAIL2@MAILSERVER.COM", "DIRECCION 2", "DETALLES 2"));
			list.add(new Client("CLIENTE 3", "03514704413", "EMAIL3@MAILSERVER.COM", "DIRECCION 3", "DETALLES 3"));
			list.add(new Client("CLIENTE 4", "03514704414", "EMAIL4@MAILSERVER.COM", "DIRECCION 4", "DETALLES 4"));
			list.add(new Client("CLIENTE 5", "03514704415", "EMAIL5@MAILSERVER.COM", "DIRECCION 5", "DETALLES 5"));
			clientRepository.save(list);
		}
	}

	private void generateWorker() {
		if (workerRepository.count() == 0) {
			List<Worker> list = new ArrayList<>();
			list.add(new Worker("EMPLEADO 1", null));
			list.add(new Worker("EMPLEADO 2", null));
			list.add(new Worker("EMPLEADO 3", null));
			list.add(new Worker("EMPLEADO 4", null));
			workerRepository.save(list);
		}
	}

	private void generateRawMaterialType() {
		if (rawMaterialTypeRepository.count() == 0) {
			generateMeasureUnitTypeList();
			//  las tablas vienen en distintos largos, 2,40mts, 3,00mts, 3,60mts y 4,20mts. el ancho x espesor que usan (en pulgadas) 1x3, 1x4, 1x5, 1x6, 1x8, 1,5x6, 2x4, 2x6, 3x3, 3x6, 4x4
			MeasureUnit pulgadas = measureUnitRepository.findFirstByName("Pulgadas");
			MeasureUnit metros = measureUnitRepository.findFirstByName("Metros");
			List<List<String>> definitions = Arrays.asList(
					Arrays.asList("1", "3"),
					Arrays.asList("1", "4"),
					Arrays.asList("1", "5"),
					Arrays.asList("1", "6"),
					Arrays.asList("1", "8"),
					Arrays.asList("1.5", "6"),
					Arrays.asList("2", "4"),
					Arrays.asList("2", "6"),
					Arrays.asList("3", "3"),
					Arrays.asList("3", "6"),
					Arrays.asList("4", "4")
					);
			List<RawMaterialType> list = new ArrayList<>();
			for (List<String> definition : definitions) {
				String espesor = definition.get(0);
				String ancho = definition.get(1);
				list.add(new RawMaterialType("Tabla " + espesor + "x" + ancho + " x 2.40mts", new BigDecimal("2.40"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas));
				list.add(new RawMaterialType("Tabla " + espesor + "x" + ancho + " x 3.00mts", new BigDecimal("3.00"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas));
				list.add(new RawMaterialType("Tabla " + espesor + "x" + ancho + " x 3.60mts", new BigDecimal("3.60"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas));
				list.add(new RawMaterialType("Tabla " + espesor + "x" + ancho + " x 4.20mts", new BigDecimal("4.20"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas));
			}
			rawMaterialTypeRepository.save(list);
		}
	}
	
	private void generateSupplyType() {
		if (supplyTypeRepository.count() == 0) {
			List<SupplyType> list = new ArrayList<>();
			list.add(new SupplyType("15", "Tornillo Autoperforante Hexagonal Punta Mecha 14x4".toUpperCase(), "", "", "", "14x4", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("16", "Tornillo Fix Autoperforante 3x35".toUpperCase(), "", "", "", "3x35", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("26", "Arandela Plana Zincada 5/16".toUpperCase(), "", "", "", "5/16", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("27", "Arandela Plana Zincada 1/4".toUpperCase(), "", "", "", "1/4", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("34", "Tuerca Zincada Alta 7/16".toUpperCase(), "", "", "", "7/16", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			supplyTypeRepository.save(list);
		}
	}
	
	private void generateWood() {
		if (woodRepository.count() == 0) {
			List<Wood> list = new ArrayList<>();
			generateWoodType();
			WoodType woodType = woodTypeRepository.findFirstByName("Pino");
			generateRawMaterialType();
			List<RawMaterialType> listRawMaterialType = rawMaterialTypeRepository.findAll();
			for(RawMaterialType each : listRawMaterialType) {
				list.add(new Wood(each, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20")));
			}
			woodRepository.save(list);
		}
	}
	
	private void generateMachine() {
		if (machineRepository.count() == 0) {
			List<Machine> list = new ArrayList<>();
			generateMachineType();
			List<MachineType> listMachineType = machineTypeRepository.findAll();
			for(MachineType each : listMachineType) {
				list.add(new Machine(each, each.getName().toUpperCase() + " 1", null, null));
				list.add(new Machine(each, each.getName().toUpperCase() + " 2", null, null));
				list.add(new Machine(each, each.getName().toUpperCase() + " 3", null, null));
			}
			machineRepository.save(list);
		}
	}
	
}

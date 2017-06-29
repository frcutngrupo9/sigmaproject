package ar.edu.utn.sigmaproject.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
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
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
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
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
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
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;

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
	private SupplyTypeRepository supplyTypeRepository;

	@Autowired
	private WoodRepository woodRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private EntityManager entityManager;

	@PostConstruct
	public void afterConstruct() throws InterruptedException {
		generateMeasureUnitTypeList();
		generateProductionPlanStateTypes();
		generateProductionOrderStateTypes();
		generateOrderStateType();
		generateProduct();
		generateProcessType();
		generateWood();
		generateClient();
		generateWorker();
		generateSupplyType();
		generateMachine();
		hibernateSearchReIndex();
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
			list.add(new ProductionPlanStateType("Registrado", null));
			list.add(new ProductionPlanStateType("Abastecido", null));
			list.add(new ProductionPlanStateType("Lanzado", null));
			list.add(new ProductionPlanStateType("En Ejecucion", null));
			list.add(new ProductionPlanStateType("Finalizado", null));
			list.add(new ProductionPlanStateType("Cancelado", null));
			list.add(new ProductionPlanStateType("Suspendido", null));
			productionPlanStateTypeRepository.save(list);
		}
	}

	private void generateProductionOrderStateTypes() {
		if (productionOrderStateTypeRepository.count() == 0) {
			List<ProductionOrderStateType> list = new ArrayList<>();
			list.add(new ProductionOrderStateType("Registrada"));
			list.add(new ProductionOrderStateType("Preparada"));
			list.add(new ProductionOrderStateType("Iniciada"));
			list.add(new ProductionOrderStateType("Finalizada"));
			list.add(new ProductionOrderStateType("Cancelada"));
			productionOrderStateTypeRepository.save(list);
		}
	}

	private void generateOrderStateType() {
		if (orderStateTypeRepository.count() == 0) {
			List<OrderStateType> list = new ArrayList<>();
			list.add(new OrderStateType("Creado", null));// TODO: cambiar por estado presupuestado y confirmado.
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
			list.add(new MachineType("Tupi", "", null));
			list.add(new MachineType("Sierra Sin Fin", "", null));
			list.add(new MachineType("Lijadora", "", null));
			list.add(new MachineType("Caladora", "", null));
			list.add(new MachineType("Taladro", "", null));
			machineTypeRepository.save(list);
		}
	}


	private void generateProcessType() {
		// TODO: agregar las 3 etapas - (Corte) (Armado) (Terminacion)
		if (processTypeRepository.count() == 0) {
			generateMachineType();
			List<ProcessType> list = new ArrayList<>();
			list.add(new ProcessType(1, "Trazado de Madera", null));
			list.add(new ProcessType(2, "Garlopeado", machineTypeRepository.findFirstByName("Garlopa")));
			list.add(new ProcessType(3, "Cepillado", machineTypeRepository.findFirstByName("Cepilladora")));
			list.add(new ProcessType(4, "Cortado de Ancho", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType(5, "Cortado de Largo", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType(6, "Cortado Curvo", machineTypeRepository.findFirstByName("Sierra Sin Fin")));
			list.add(new ProcessType(7, "Escoplado", machineTypeRepository.findFirstByName("Escopladora")));
			list.add(new ProcessType(8, "Espigado", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(9, "Hacer Molduras", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(10, "Hacer Canal", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(11, "Replanado", null));
			list.add(new ProcessType(12, "Masillado", null));
			list.add(new ProcessType(13, "Clavado", null));
			list.add(new ProcessType(14, "Lijado", machineTypeRepository.findFirstByName("Lijadora")));
			list.add(new ProcessType(15, "Armado", null));

			processTypeRepository.save(list);
		}
	}

	private void generateWoodType() {
		if (woodTypeRepository.count() == 0) {
			List<WoodType> list = new ArrayList<>();
			list.add(new WoodType("Pino", "Semi-pesada, semi-dura."));
			list.add(new WoodType("Caoba", "Tradicional, dura y compacta."));
			list.add(new WoodType("Nogal", "Dura, homogenea."));
			list.add(new WoodType("Roble", "Resistente, duradera y compacta."));
			woodTypeRepository.save(list);
		}
	}

	private void generateProductCategory() {
		if (productCategoryRepository.count() == 0) {
			List<ProductCategory> list = new ArrayList<>();
			list.add(new ProductCategory("Banco"));
			list.add(new ProductCategory("Biblioteca"));
			list.add(new ProductCategory("Cajonera"));
			list.add(new ProductCategory("Cama"));
			list.add(new ProductCategory("Marco"));
			list.add(new ProductCategory("Mesa"));
			//			list.add(new ProductCategory("Respaldo"));se usa cama
			list.add(new ProductCategory("Silla"));
			//			list.add(new ProductCategory("Sillon"));se usa silla
			productCategoryRepository.save(list);
		}
	}

	private void generateClient() {
		if (clientRepository.count() == 0) {
			List<Client> list = new ArrayList<>();
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

	private void generateSupplyType() {
		if (supplyTypeRepository.count() == 0) {
			List<SupplyType> list = new ArrayList<>();
			list.add(new SupplyType("1", "INSUMO 1", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("2", "INSUMO 2", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("3", "INSUMO 3", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("4", "INSUMO 4", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			list.add(new SupplyType("5", "INSUMO 5", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			//			list.add(new SupplyType("15", "Tornillo Autoperforante Hexagonal Punta Mecha 14x4".toUpperCase(), "", "", "", "14x4", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			//			list.add(new SupplyType("16", "Tornillo Fix Autoperforante 3x35".toUpperCase(), "", "", "", "3x35", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			//			list.add(new SupplyType("26", "Arandela Plana Zincada 5/16".toUpperCase(), "", "", "", "5/16", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			//			list.add(new SupplyType("27", "Arandela Plana Zincada 1/4".toUpperCase(), "", "", "", "1/4", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			//			list.add(new SupplyType("34", "Tuerca Zincada Alta 7/16".toUpperCase(), "", "", "", "7/16", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20")));
			supplyTypeRepository.save(list);
		}
	}

	private void generateWood() {
		if (woodRepository.count() == 0) {
			List<Wood> list = new ArrayList<>();
			generateWoodType();
			WoodType woodType = woodTypeRepository.findFirstByName("Pino");

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

			for (List<String> definition : definitions) {
				String espesor = definition.get(0);
				String ancho = definition.get(1);
				list.add(new Wood("Tabla " + espesor + "x" + ancho + " x 2.40mts", new BigDecimal("2.40"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20")));
				list.add(new Wood("Tabla " + espesor + "x" + ancho + " x 3.00mts", new BigDecimal("3.00"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20")));
				list.add(new Wood("Tabla " + espesor + "x" + ancho + " x 3.60mts", new BigDecimal("3.60"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20")));
				list.add(new Wood("Tabla " + espesor + "x" + ancho + " x 4.20mts", new BigDecimal("4.20"), metros, new BigDecimal(espesor), pulgadas, new BigDecimal(ancho), pulgadas, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20")));
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

	private void hibernateSearchReIndex() throws InterruptedException {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.createIndexer().startAndWait();
	}

	private void generateProduct() {
		if (productRepository.count() == 0) {
			generateProductCategory();
			List<Product> list = new ArrayList<>();
			list.add(new Product("1", "Mesa patas 4x4 rectas de 1.20x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("840")));
			//			list.add(new Product("2", "Mesa patas 4x4 rectas de 1.40x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("956")));
			//			list.add(new Product("3", "Mesa patas 4x4 rectas de 1.60x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1045")));
			//			list.add(new Product("4", "Mesa patas 4x4 rectas de 1.80x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1150")));
			list.add(new Product("5", "Mesa patas 4x4 rectas de 2.00x0.90mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1360")));
			//			list.add(new Product("6", "Mesa patas Reina Ana de 1.20x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("956")));
			//			list.add(new Product("7", "Mesa patas Reina Ana de 1.40x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1045")));
			//			list.add(new Product("8", "Mesa patas Reina Ana de 1.60x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1150")));
			//			list.add(new Product("9", "Mesa patas Reina Ana de 1.80x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1255")));
			//			list.add(new Product("10", "Mesa patas Reina Ana de 2.00x0.90mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1449")));
			list.add(new Product("11", "Mesa patas 3x3 tapa de 1\" de 1.20x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("609")));
			//			list.add(new Product("12", "Mesa patas 3x3 tapa de 1\" de 1.40x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("693")));
			//			list.add(new Product("13", "Mesa patas 3x3 tapa de 1\" de 1.60x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("756")));
			//			list.add(new Product("14", "Mesa patas 3x3 tapa de 1\" de 1.80x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("872")));
			list.add(new Product("15", "Mesa redonda pata central de 1.00mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("872")));
			//			list.add(new Product("16", "Mesa redonda pata central de 1.20mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1061")));
			//			list.add(new Product("17", "Mesa redonda pata central de 1.40mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1239")));
			//			list.add(new Product("18", "Mesa redonda patas Reina Ana de 1.00mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1019")));
			//			list.add(new Product("19", "Mesa redonda patas Reina Ana de 1.20mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1208")));
			//			list.add(new Product("20", "Mesa redonda patas Reina Ana de 1.40mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1371")));
			list.add(new Product("21", "Mesa de living patas 3x3 rectas de 0.60x0.45mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("378")));
			//			list.add(new Product("22", "Mesa de living patas 4x4 rectas de 0.90x0.60mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("588")));
			//			list.add(new Product("23", "Mesa de living patas 4x4 rectas de 1.00x0.70mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("678")));
			//			list.add(new Product("24", "Mesa de living patas 4x4 rectas de 1.20x0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("798")));
			//			list.add(new Product("25", "Mesa de living Reina Ana 0.90x0.60 tapa de 1\" con regrueso".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("390")));
			//			list.add(new Product("26", "Dressoir patas rectas de 0.90x0.45mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("483")));
			//			list.add(new Product("27", "Dressoir patas Reina Ana de 0.90x0.45mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("567")));
			//			list.add(new Product("28", "Banqueta alta sin respaldo de 0.30mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("177")));
			list.add(new Product("29", "Banqueta alta  con respaldo de 0.30mts de di\u00e1metro".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("226")));
			//			list.add(new Product("30", "Banqueta alta con respaldo omega".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("294")));
			//			list.add(new Product("31", "Silla de campo resp. c/ tablero o varillas".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("252")));
			list.add(new Product("32", "Silla Omega respaldo inclinado patas rectas".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("231")));
			//			list.add(new Product("33", "Cama Omega de 0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("399")));
			list.add(new Product("34", "Cama Omega de 1.40mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("704")));
			//			list.add(new Product("35", "Cama Mexicana X patas 2x4 de 0.80mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("667")));
			list.add(new Product("36", "Cama Mexicana X patas 2x4 de 1.40mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1045")));
			//			list.add(new Product("37", "Cama Monterrey c/curva patas 4x4 de 080cm".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1050")));
			//			list.add(new Product("38", "Cama Monterrey resp. c/ X o Curva patas 4x4 de 1.40mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1292")));
			list.add(new Product("39", "Respaldo para Somier Mexicano X patas 2x4 de 1.50mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("798")));
			//			list.add(new Product("40", "Respaldo para Somier Monterrey X o Curvo de 1.50mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1029")));
			//			list.add(new Product("41", "Respaldos Mexicanos X para Marineras".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("578")));
			//			list.add(new Product("42", "Cucheta Mexicana X fija y desmontable".toUpperCase(), "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1271")));
			//			list.add(new Product("43", "Marco de espejo".toUpperCase(), "", productCategoryRepository.findFirstByName("Marco"), new BigDecimal("189")));
			//			list.add(new Product("44", "Sillon Hamaca grande".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("504")));
			//			list.add(new Product("45", "Dresoir de 3 cajones con estante".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("536")));
			list.add(new Product("46", "Dresoir de 2 cajones con estante".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("431")));
			list.add(new Product("47", "Mesa de l\u00e1mpara con 1 cajon".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("284")));
			//			list.add(new Product("48", "Mesa de l\u00e1mpara con 2 cajones", "".toUpperCase(), productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("347")));
			//			list.add(new Product("49", "Mesa de bar de 70x70 pata 3x3 tapa de 1\"".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("473")));
			//			list.add(new Product("50", "Mesa de bar de 80x80 pata  3x3 tapa de 1\"".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("525")));
			list.add(new Product("51", "Chiffonier de 1,45x0,70x0,40 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cajonera"), new BigDecimal("1245")));
			list.add(new Product("52", "C\u00f3moda de 1,00x0,90x0,40 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Cajonera"), new BigDecimal("1197")));
			list.add(new Product("53", "Mesa de luz de 0,70x0,50x0,40 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("473")));
			list.add(new Product("54", "Biblioteca de 0,30 mts con estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("546")));
			//			list.add(new Product("55", "Biblioteca de 0,30 mts con 4 cajones y estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("756")));
			//			list.add(new Product("56", "Biblioteca de 0,60 mts con estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("756")));
			//			list.add(new Product("57", "Biblioteca de 0,60 mts con 4 cajones y estantes".toUpperCase(), "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("1024")));
			//			list.add(new Product("58", "Sillon Romano de un cuerpo".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("321")));
			//			list.add(new Product("59", "Sillon Romano de dos cuerpos".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("483")));
			//			list.add(new Product("60", "Banquito recto o redondo chico".toUpperCase(), "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("153")));
			list.add(new Product("61", "Sillon punta de cama Reina Ana 1,00 mts".toUpperCase(), "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("380")));

			productRepository.save(list);
		}
	}
}

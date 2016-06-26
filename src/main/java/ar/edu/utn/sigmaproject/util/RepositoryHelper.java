package ar.edu.utn.sigmaproject.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.domain.Worker;
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

public class RepositoryHelper {
	
	public void generateEverything(ProductRepository productRepository,
			ProductCategoryRepository productCategoryRepository,
			ProcessTypeRepository processTypeRepository,
			MachineTypeRepository machineTypeRepository,
			ProductionPlanStateTypeRepository productionPlanStateTypeRepository,
			ProductionOrderStateRepository productionOrderStateRepository,
			OrderStateTypeRepository orderStateTypeRepository,
			MeasureUnitRepository measureUnitRepository,
			MeasureUnitTypeRepository measureUnitTypeRepository,
			ClientRepository clientRepository,
			WorkerRepository workerRepository,
			SupplyTypeRepository supplyTypeRepository,
			WoodTypeRepository woodTypeRepository) {
		generateProduct(productRepository, productCategoryRepository);
		generateProcessType(processTypeRepository, machineTypeRepository);
		generateProductionPlanStateType(productionPlanStateTypeRepository);
		generateProductionOrderState(productionOrderStateRepository);
		generateOrderStateType(orderStateTypeRepository);
		generateMeasureUnitTypeAndMeasureUnit(measureUnitRepository, measureUnitTypeRepository);
		generateClient(clientRepository);
		generateWorker(workerRepository);
		generateSupplyType(supplyTypeRepository);
		generateWoodType(woodTypeRepository);
	}

	private void generateProcessType(ProcessTypeRepository processTypeRepository, MachineTypeRepository machineTypeRepository) {
		if(machineTypeRepository.findAll().isEmpty()) {
			generateMachineType(machineTypeRepository);
		}
		MachineType machineGarlopa = machineTypeRepository.findByName("Garlopa");
		MachineType machineCepilladora = machineTypeRepository.findByName("Cepilladora");
		MachineType machineEscuadradora = machineTypeRepository.findByName("Escuadradora");
		MachineType machineEscopladora = machineTypeRepository.findByName("Escopladora");
		MachineType machineTupi = machineTypeRepository.findByName("Tupí");
		MachineType machineSierraSinFin = machineTypeRepository.findByName("Sierra Sin Fin");
		MachineType machineLijadora = machineTypeRepository.findByName("Lijadora");
		List<ProcessType> list = new ArrayList<ProcessType>();
		list.add(new ProcessType("Trazar", null));
		list.add(new ProcessType("Garlopear", machineGarlopa));
		list.add(new ProcessType("Asentar", machineGarlopa));
		list.add(new ProcessType("Cepillar", machineCepilladora));
		list.add(new ProcessType("Cortar el Ancho", machineEscuadradora));
		list.add(new ProcessType("Cortar el Largo", machineEscuadradora));
		list.add(new ProcessType("Hacer Cortes Curvos", machineSierraSinFin));
		list.add(new ProcessType("Hacer Escopladuras", machineEscopladora));
		list.add(new ProcessType("Hacer Espigas", machineTupi));
		list.add(new ProcessType("Hacer Molduras", machineTupi));
		list.add(new ProcessType("Hacer Canales", machineTupi));
		list.add(new ProcessType("Replanar", null));
		list.add(new ProcessType("Masillar", null));
		list.add(new ProcessType("Clavar", null));
		list.add(new ProcessType("Lijar Cruzado", machineLijadora));
		list.add(new ProcessType("Lijar Derecho", machineLijadora));
		list.add(new ProcessType("Agregar Herrajes", null));
		list.add(new ProcessType("Armar", null));
		for(ProcessType each : list) {
			processTypeRepository.save(each);
		}
	}

	private void generateMachineType(MachineTypeRepository repository) {
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 5, 0, 0, 0, 0, 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en generateMachineType, al crear duracion: " + e.toString());
		}
		List<MachineType> list = new ArrayList<>();
		list.add(new MachineType("Garlopa", "", duration));
		list.add(new MachineType("Cepilladora", "", duration));
		list.add(new MachineType("Escuadradora", "", duration));
		list.add(new MachineType("Escopladora", "", duration));
		list.add(new MachineType("Tupí", "", duration));
		list.add(new MachineType("Sierra Sin Fin", "", duration));
		list.add(new MachineType("Lijadora", "", duration));
		for(MachineType each : list) {
			repository.save(each);
		}
	}

	private void generateProductionPlanStateType(ProductionPlanStateTypeRepository repository) {
		List<ProductionPlanStateType> list = new ArrayList<ProductionPlanStateType>();
		list.add(new ProductionPlanStateType("Iniciado", null));
		list.add(new ProductionPlanStateType("Cancelado", null));
		list.add(new ProductionPlanStateType("Abastecido", null));
		list.add(new ProductionPlanStateType("Lanzado", null)); // cuando se generan las ordenes de produccion
		list.add(new ProductionPlanStateType("En Produccion", null));// cuando se inicia alguna orden de produccion
		list.add(new ProductionPlanStateType("Finalizado", null));
		for(ProductionPlanStateType each : list) {
			repository.save(each);
		}
	}

	private void generateProductionOrderState(ProductionOrderStateRepository repository) {
		List<ProductionOrderState> list = new ArrayList<ProductionOrderState>();
		list.add(new ProductionOrderState("Generada"));// cuando se crea y se selecciona empleado y fecha de inicio
		list.add(new ProductionOrderState("Iniciada"));// cuando se llega a la fecha de inicio
		list.add(new ProductionOrderState("Finalizada"));
		list.add(new ProductionOrderState("Cancelada"));
		for(ProductionOrderState each : list) {
			repository.save(each);
		}
	}

	private void generateOrderStateType(OrderStateTypeRepository repository) {
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

	private void generateMeasureUnitTypeAndMeasureUnit(MeasureUnitRepository mUR, MeasureUnitTypeRepository mUTR) {
		List<MeasureUnitType> measureUnitTypeList = new ArrayList<MeasureUnitType>();
		measureUnitTypeList.add(new MeasureUnitType("Longitud"));
		measureUnitTypeList.add(new MeasureUnitType("Tiempo"));
		measureUnitTypeList.add(new MeasureUnitType("Masa"));
		measureUnitTypeList.add(new MeasureUnitType("Cantidad"));
		for(MeasureUnitType aux : measureUnitTypeList) {
			mUTR.save(aux);
		}
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

	private void generateProductCategory(ProductCategoryRepository repository) {
		List<ProductCategory> list = new ArrayList<ProductCategory>();
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
		for(ProductCategory each : list) {
			repository.save(each);
		}
	}

	private void generateProduct(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
		if(productCategoryRepository.findAll().isEmpty()) {
			generateProductCategory(productCategoryRepository);
		}
		ProductCategory productCategoryBanco = productCategoryRepository.findByName("Banco");
		ProductCategory productCategoryBiblioteca = productCategoryRepository.findByName("Biblioteca");
		ProductCategory productCategoryComoda = productCategoryRepository.findByName("Comoda");
		ProductCategory productCategoryCajonera = productCategoryRepository.findByName("Cajonera");
		ProductCategory productCategoryCama = productCategoryRepository.findByName("Cama");
		ProductCategory productCategoryMarco = productCategoryRepository.findByName("Marco");
		ProductCategory productCategoryMesa = productCategoryRepository.findByName("Mesa");
		ProductCategory productCategoryRespaldo = productCategoryRepository.findByName("Respaldo");
		ProductCategory productCategorySilla = productCategoryRepository.findByName("Silla");
		ProductCategory productCategorySillon = productCategoryRepository.findByName("Sillon");
		List<Product> list = new ArrayList<>();
		list.add(new Product("1", "Mesa patas 4x4 rectas de 1.20x0.80mts", "", productCategoryMesa, new BigDecimal("840")));
		list.add(new Product("2", "Mesa patas 4x4 rectas de 1.40x0.80mts", "", productCategoryMesa, new BigDecimal("956")));
		list.add(new Product("3", "Mesa patas 4x4 rectas de 1.60x0.80mts", "", productCategoryMesa, new BigDecimal("1045")));
		list.add(new Product("4", "Mesa patas 4x4 rectas de 1.80x0.80mts", "", productCategoryMesa, new BigDecimal("1150")));
		list.add(new Product("5", "Mesa patas 4x4 rectas de 2.00x0.90mts", "", productCategoryMesa, new BigDecimal("1360")));
		list.add(new Product("6", "Mesa patas Reina Ana de 1.20x0.80mts", "", productCategoryMesa, new BigDecimal("956")));
		list.add(new Product("7", "Mesa patas Reina Ana de 1.40x0.80mts", "", productCategoryMesa, new BigDecimal("1045")));
		list.add(new Product("8", "Mesa patas Reina Ana de 1.60x0.80mts", "", productCategoryMesa, new BigDecimal("1150")));
		list.add(new Product("9", "Mesa patas Reina Ana de 1.80x0.80mts", "", productCategoryMesa, new BigDecimal("1255")));
		list.add(new Product("10", "Mesa patas Reina Ana de 2.00x0.90mts", "", productCategoryMesa, new BigDecimal("1449")));
		list.add(new Product("11", "Mesa patas 3x3 tapa de 1” de 1.20x0.80mts", "", productCategoryMesa, new BigDecimal("609")));
		list.add(new Product("12", "Mesa pata 3x3 tapa de 1” de 1.40x0.80mts", "", productCategoryMesa, new BigDecimal("693")));
		list.add(new Product("13", "Mesa pata 3x3 tapa de 1” de 1.60x0.80mts", "", productCategoryMesa, new BigDecimal("756")));
		list.add(new Product("14", "Mesa pata 3x3 tapa de 1” de 1.80x0.80mts", "", productCategoryMesa, new BigDecimal("872")));
		list.add(new Product("15", "Mesa redonda pata central de 1.00mts de diámetro", "", productCategoryMesa, new BigDecimal("872")));
		list.add(new Product("16", "Mesa redonda pata central de 1.20mts de diámetro", "", productCategoryMesa, new BigDecimal("1061")));
		list.add(new Product("17", "Mesa redonda pata central de 1.40mts de diámetro", "", productCategoryMesa, new BigDecimal("1239")));
		list.add(new Product("18", "Mesa redonda patas Reina Ana de 1.00mts de diámetro", "", productCategoryMesa, new BigDecimal("1019")));
		list.add(new Product("19", "Mesa redonda patas Reina Ana de 1.20mts de diámetro", "", productCategoryMesa, new BigDecimal("1208")));
		list.add(new Product("20", "Mesa redonda patas Reina Ana de 1.40mts de diámetro", "", productCategoryMesa, new BigDecimal("1371")));
		list.add(new Product("21", "Mesa de living patas 3x3 rectas de 0.60x0.45mts", "", productCategoryMesa, new BigDecimal("378")));
		list.add(new Product("22", "Mesa de living patas 4x4 rectas de 0.90x0.60mts", "", productCategoryMesa, new BigDecimal("588")));
		list.add(new Product("23", "Mesa de living patas 4x4 rectas de 1.00x0.70mts", "", productCategoryMesa, new BigDecimal("678")));
		list.add(new Product("24", "Mesa de living patas 4x4 rectas de 1.20x0.80mts", "", productCategoryMesa, new BigDecimal("798")));
		list.add(new Product("25", "Mesa de living Reina Ana 0.90x0.60 tapa de 1” con regrueso", "", productCategoryMesa, new BigDecimal("390")));
		list.add(new Product("26", "Dressoir patas rectas de 0.90x0.45mts", "", productCategoryComoda, new BigDecimal("483")));
		list.add(new Product("27", "Dressoir patas Reina Ana de 0.90x0.45mts", "", productCategoryComoda, new BigDecimal("567")));
		list.add(new Product("28", "Banqueta alta sin respaldo de 0.30mts de diámetro", "", productCategoryBanco, new BigDecimal("177")));
		list.add(new Product("29", "Banqueta alta  con respaldo de 0.30mts de diámetro", "", productCategoryBanco, new BigDecimal("226")));
		list.add(new Product("30", "Banqueta alta con respaldo omega", "", productCategoryBanco, new BigDecimal("294")));
		list.add(new Product("31", "Silla de campo resp. c/ tablero o varillas", "", productCategorySilla, new BigDecimal("252")));
		list.add(new Product("32", "Silla Omega respaldo inclinado patas rectas", "", productCategorySilla, new BigDecimal("231")));
		list.add(new Product("33", "Cama Omega de 0.80mts", "", productCategoryCama, new BigDecimal("399")));
		list.add(new Product("34", "Cama Omega de 1.40mts", "", productCategoryCama, new BigDecimal("704")));
		list.add(new Product("35", "Cama Mexicana X patas 2x4 de 0.80mts", "", productCategoryCama, new BigDecimal("667")));
		list.add(new Product("36", "Cama Mexicana X patas 2x4 de 1.40mts", "", productCategoryCama, new BigDecimal("1045")));
		list.add(new Product("37", "Cama Monterrey c/curva patas 4x4 de 080cm", "", productCategoryCama, new BigDecimal("1050")));
		list.add(new Product("38", "Cama Monterrey resp. c/ X o Curva patas 4x4 de 1.40mts", "", productCategoryCama, new BigDecimal("1292")));
		list.add(new Product("39", "Respaldo para Somier Mexicano X patas 2x4 de 1.50mts", "", productCategoryRespaldo, new BigDecimal("798")));
		list.add(new Product("40", "Respaldo para Somier Monterrey X o Curvo de 1.50mts", "", productCategoryRespaldo, new BigDecimal("1029")));
		list.add(new Product("41", "Respaldos Mexicanos X para Marineras", "", productCategoryRespaldo, new BigDecimal("578")));
		list.add(new Product("42", "Cucheta Mexicana X fija y desmontable", "", productCategoryCama, new BigDecimal("1271")));
		list.add(new Product("43", "Marco de espejo", "", productCategoryMarco, new BigDecimal("189")));
		list.add(new Product("44", "Sillon Hamaca grande", "", productCategorySillon, new BigDecimal("504")));
		list.add(new Product("45", "Dresoir de 3 cajones con estante", "", productCategoryComoda, new BigDecimal("536")));
		list.add(new Product("46", "Dresoir de 2 cajones con estante", "", productCategoryComoda, new BigDecimal("431")));
		list.add(new Product("47", "Mesa de lámpara con 1 cajon", "", productCategoryMesa, new BigDecimal("284")));
		list.add(new Product("48", "Mesa de lámpara con 2 cajones", "", productCategoryMesa, new BigDecimal("347")));
		list.add(new Product("49", "Mesa de bar de 70x70 pata 3x3 tapa de 1”", "", productCategoryMesa, new BigDecimal("473")));
		list.add(new Product("50", "Mesa de bar de 80x80 pata  3x3 tapa de 1”", "", productCategoryMesa, new BigDecimal("525")));
		list.add(new Product("51", "Chiffonier de 1,45x0,70x0,40 mts", "", productCategoryCajonera, new BigDecimal("1245")));
		list.add(new Product("52", "Cómoda de 1,00x0,90x0,40 mts", "", productCategoryComoda, new BigDecimal("1197")));
		list.add(new Product("53", "Mesa de luz de 0,70x0,50x0,40 mts", "", productCategoryMesa, new BigDecimal("473")));
		list.add(new Product("54", "Biblioteca de 0,30 mts con estantes", "", productCategoryBiblioteca, new BigDecimal("546")));
		list.add(new Product("55", "Biblioteca de 0,30 mts con 4 cajones y estantes", "", productCategoryBiblioteca, new BigDecimal("756")));
		list.add(new Product("56", "Biblioteca de 0,60 mts con estantes", "", productCategoryBiblioteca, new BigDecimal("756")));
		list.add(new Product("57", "Biblioteca de 0,60 mts con 4 cajones y estantes", "", productCategoryBiblioteca, new BigDecimal("1024")));
		list.add(new Product("58", "Sillon Romano de un cuerpo", "", productCategorySillon, new BigDecimal("321")));
		list.add(new Product("59", "Sillon Romano de dos cuerpos", "", productCategorySillon, new BigDecimal("483")));
		list.add(new Product("60", "Banquito recto o redondo chico", "", productCategoryBanco, new BigDecimal("153")));
		list.add(new Product("61", "Sillon punta de cama Reina Ana 1,00 mts", "", productCategorySillon, new BigDecimal("380")));
		for(Product each : list) {
			productRepository.save(each);
		}
	}

	private void generateClient(ClientRepository repository) {
		List<Client> list = new ArrayList<>();
		list.add( new Client("RESTOCK", "", "", "", "RESERVADO RESTOCK"));
		list.add( new Client("CLIENTE 1", "03514704411", "EMAIL1@MAILSERVER.COM", "DIRECCION 1", "DETALLES 1"));
		list.add( new Client("CLIENTE 2", "03514704412", "EMAIL2@MAILSERVER.COM", "DIRECCION 2", "DETALLES 2"));
		list.add( new Client("CLIENTE 3", "03514704413", "EMAIL3@MAILSERVER.COM", "DIRECCION 3", "DETALLES 3"));
		list.add( new Client("CLIENTE 4", "03514704414", "EMAIL4@MAILSERVER.COM", "DIRECCION 4", "DETALLES 4"));
		list.add( new Client("CLIENTE 5", "03514704415", "EMAIL5@MAILSERVER.COM", "DIRECCION 5", "DETALLES 5"));
		for(Client each : list) {
			repository.save(each);
		}
	}

	private void generateWorker(WorkerRepository repository) {
		List<Worker> list = new ArrayList<>();
		list.add( new Worker("EMPLEADO 1", new Date()));
		list.add( new Worker("EMPLEADO 2", new Date()));
		list.add( new Worker("EMPLEADO 3", new Date()));
		for(Worker each : list) {
			repository.save(each);
		}
	}

	private void generateSupplyType(SupplyTypeRepository repository) {
		List<SupplyType> list = new ArrayList<>();
		list.add( new SupplyType("1", "INSUMO 1", "", "", "", "", new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("30")));
		list.add( new SupplyType("2", "INSUMO 2", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("30")));
		list.add( new SupplyType("3", "INSUMO 3", "", "", "", "", new BigDecimal("300"), new BigDecimal("10"), new BigDecimal("30")));
		list.add( new SupplyType("4", "INSUMO 4", "", "", "", "", new BigDecimal("400"), new BigDecimal("10"), new BigDecimal("30")));
		for(SupplyType each : list) {
			repository.save(each);
		}
	}
	
	private void generateWoodType(WoodTypeRepository repository) {
		List<WoodType> list = new ArrayList<>();
		list.add(new WoodType("Pino", "Semi-pesada, semi-dura."));
		list.add(new WoodType("Caoba", "Tradicional, dura y compacta."));
		list.add(new WoodType("Nogal", "Dura, homogénea."));
		list.add(new WoodType("Roble", "Resistente, duradera y compacta."));
		for(WoodType each : list) {
			repository.save(each);
		}
	}

}

/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import ar.edu.utn.sigmaproject.domain.Settings;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.UserType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.domain.WorkHour;
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
import ar.edu.utn.sigmaproject.service.SettingsRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.UserTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkHourRepository;
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
	private WorkHourRepository workHourRepository;

	@Autowired
	private SupplyTypeRepository supplyTypeRepository;

	@Autowired
	private WoodRepository woodRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private SettingsRepository settingsRepository;
	
	@Autowired
	private UserTypeRepository userTypeRepository;

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
		generateWorkHour();
		generateSupplyType();
		generateMachine();
		generateSettings();
		generateUserType();
		hibernateSearchReIndex();
	}
	
	private void generateUserType() {
		if (userTypeRepository.count() == 0) {
			List<UserType> list = new ArrayList<>();
			list.add(new UserType("Admin", ""));
			list.add(new UserType("Gerente General", ""));
			list.add(new UserType("Jefe de Ventas", ""));
			list.add(new UserType("Jefe de Produccion", ""));
			list.add(new UserType("Encargado de Ventas", ""));
			list.add(new UserType("Encargado de Compras", ""));
			list.add(new UserType("Jefe de Personal", ""));
			userTypeRepository.save(list);
		}
	}

	private void generateSettings() {
		if (settingsRepository.count() == 0) {
			Settings settings = new Settings();
			settings.setPercentProfit(new BigDecimal(25));
			settingsRepository.save(settings);
		}
	}

	private void generateProductionPlanStateTypes() {
		if (productionPlanStateTypeRepository.count() == 0) {
			List<ProductionPlanStateType> list = new ArrayList<>();
			list.add(new ProductionPlanStateType("Registrado", null));
			list.add(new ProductionPlanStateType("Parcialmente Abastecido", null));
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
			list.add(new MachineType("Escuadradora", "Utilizado para dar largo y ancho de la madera.", null));
			list.add(new MachineType("Sierra sin fin", "Utilizado para realizar los cortes curvos.", null));
			list.add(new MachineType("Cepilladora", "Utilizado para dar el grosor a la cara de la madera.", null));
			list.add(new MachineType("Tupi", "Utilizado para realizar fresado, canales y espigas a la madera.", null));
			list.add(new MachineType("Lijadora", "Utilizado para lijar la superficie de la madera", null));
			machineTypeRepository.save(list);
		}
	}


	private void generateProcessType() {
		if (processTypeRepository.count() == 0) {
			generateMachineType();
			List<ProcessType> list = new ArrayList<>();
			list.add(new ProcessType(1, "Trazado de Madera", "Trazar maderas para el posterior cortado.", null));
			list.add(new ProcessType(2, "Cortado de Madera", "Cortar maderas en las medidas trazadas.", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType(3, "Cortado Curvo", machineTypeRepository.findFirstByName("Sierra sin fin")));
			list.add(new ProcessType(4, "Lijado", machineTypeRepository.findFirstByName("Lijadora")));
			list.add(new ProcessType(5, "Cepillado", machineTypeRepository.findFirstByName("Cepilladora")));
			list.add(new ProcessType(6, "Espigado", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(7, "Hacer Molduras", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(8, "Acanalado", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(9, "Ensamblado", "Unir diferentes piezas con tornillos, engrapado, pegamento, clavos, etc.", null));
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
			list.add(new ProductCategory("Mesa"));
			list.add(new ProductCategory("Silla"));
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
		if(workerRepository.count() == 0) {
			List<Worker> list = new ArrayList<>();
			list.add(new Worker("EMPLEADO 1", null));
			list.add(new Worker("EMPLEADO 2", null));
			list.add(new Worker("EMPLEADO 3", null));
			list.add(new Worker("EMPLEADO 4", null));
			workerRepository.save(list);
		}
	}

	private void generateWorkHour() {
		if(workHourRepository.count() == 0) {
			List<WorkHour> list = new ArrayList<>();
			list.add(new WorkHour("Operario", new BigDecimal("180")));
			list.add(new WorkHour("General", new BigDecimal("145")));
			list.add(new WorkHour("Control", new BigDecimal("120")));
			workHourRepository.save(list);
		}

	}

	private void generateSupplyType() {
		if(supplyTypeRepository.count() == 0) {
			List<SupplyType> list = new ArrayList<>();
			list.add(new SupplyType("1", "Lija al Agua", "Lijado fino prelustrado", "Szumik", "", "Unidad", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("11")));
			list.add(new SupplyType("2", "Adhesivo Vinilico", "Encolado de piezas para su union", "Lencisa", "Envase", "5 Kg", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("316")));
			list.add(new SupplyType("3", "Pintura Asfáltica", "Lustrado de muebles", "Szumik", "Lata", "18Lts", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("1200")));
			list.add(new SupplyType("4", "Bulon Camero", "Di\u00e1metro 8 mm - Largo 110 mm", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("9")));
			list.add(new SupplyType("5", "Tuerca", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("1.5")));
			list.add(new SupplyType("6", "Arandela", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("1.15")));
			list.add(new SupplyType("7", "Clavo", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("1")));
			/*
			list.add(new SupplyType("1", "INSUMO 1", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("9,87")));
			list.add(new SupplyType("2", "INSUMO 2", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("7,77")));
			list.add(new SupplyType("3", "INSUMO 3", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("5,52")));
			list.add(new SupplyType("4", "INSUMO 4", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("3,14")));
			list.add(new SupplyType("5", "INSUMO 5", "", "", "", "", new BigDecimal("200"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("1,61")));
			 */
			supplyTypeRepository.save(list);
		}
	}

	private void addMeasureUnits(String measureUnitTypeName, List<List<String>> definitions) {
		MeasureUnitType measureUnitType = measureUnitTypeRepository.findFirstByName(measureUnitTypeName);
		if (measureUnitType == null) {
			measureUnitType = new MeasureUnitType(measureUnitTypeName);
			List<MeasureUnit> measureUnitToInsert = new ArrayList<>();
			for (List<String> definition : definitions) {
				measureUnitToInsert.add(new MeasureUnit(definition.get(0), definition.get(1), measureUnitType));
			}
			measureUnitType.getList().addAll(measureUnitToInsert);
			measureUnitTypeRepository.save(measureUnitType);
		}
	}

	private void generateMeasureUnitTypeList() {
		addMeasureUnits("Longitud", Arrays.asList(
				Arrays.asList("Metros", "M"),
				Arrays.asList("Centimetros", "Cm"),
				Arrays.asList("Milimetros", "Mm"),
				Arrays.asList("Pulgadas", "Pulg")
				));
		addMeasureUnits("Tiempo", Arrays.asList(
				Arrays.asList("Minutos", "Min"),
				Arrays.asList("Horas", "Hr"),
				Arrays.asList("Dias", "D")
				));
		addMeasureUnits("Masa", Arrays.asList(
				Arrays.asList("Kilogramos", "Kg"),
				Arrays.asList("Gramos", "Gr"),
				Arrays.asList("Litros", "L"),
				Arrays.asList("Mililitros", "Ml")
				));
		addMeasureUnits("Cantidad", Arrays.asList(
				Arrays.asList("Unidad", "Unid")
				));
	}

	/*
	 * width=Largo
	 * depth=Profundidad/Ancho
	 * length=Alto/Espesor
	 * Wood(name, length, lengthMeasureUnit, depth, depthMeasureUnit, width, widthMeasureUnit, woodType, stock, stockMin, stockRepo, price)
	 */

	private void generateWood() {
		if (woodRepository.count() == 0) {
			List<Wood> list = new ArrayList<>();
			generateWoodType();
			WoodType woodType = woodTypeRepository.findFirstByName("Pino");
			generateMeasureUnitTypeList();
			//  las tablas vienen en distintos largos, 2,40mts, 3,00mts, 3,60mts y 4,20mts. el ancho x espesor que usan (en pulgadas) 1x3, 1x4, 1x5, 1x6, 1x8, 1,5x6, 2x4, 2x6, 3x3, 3x6, 4x4
			MeasureUnit pulgadas = measureUnitRepository.findFirstByName("Pulgadas");
			MeasureUnit metros = measureUnitRepository.findFirstByName("Metros");
			List<List<String>> listLengthAndDepth = Arrays.asList(
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
			List<String> listWidth = Arrays.asList("2.40", "3.00", "3.60", "4.20");
			for (List<String> each : listLengthAndDepth) {
				String length = each.get(0);
				String depth = each.get(1);
				for (String width : listWidth) {
					BigDecimal price = getWoodPrice(new BigDecimal(length), pulgadas, new BigDecimal(depth), pulgadas, new BigDecimal(width), metros, woodType);
					list.add(new Wood("Tabla " + length + "x" + depth + " x " + width + "mts", new BigDecimal(length), pulgadas, new BigDecimal(depth), pulgadas, new BigDecimal(width), metros, woodType, new BigDecimal("50"), new BigDecimal("10"), new BigDecimal("20"), price));
				}
			}
			woodRepository.save(list);
		}
	}

	private BigDecimal getWoodPrice(BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal width, MeasureUnit widthMeasureUnit, WoodType woodType) {
		// calcula los metros cubicos y el valor
		BigDecimal lengthMeters = convertToMeters(length, lengthMeasureUnit);
		BigDecimal depthMeters = convertToMeters(depth, depthMeasureUnit);
		BigDecimal widthMeters = convertToMeters(width, widthMeasureUnit);
		BigDecimal cubicMeters = lengthMeters.multiply(depthMeters).multiply(widthMeters);
		String price = "0";
		if(woodType.getName().equalsIgnoreCase("Pino")) {
			price = "8350";
		}
		BigDecimal cubicMeterPrice = new BigDecimal(price);
		return cubicMeters.multiply(cubicMeterPrice);
	}

	private BigDecimal convertToMeters(BigDecimal measure, MeasureUnit measureUnit) {
		BigDecimal bd100 = new BigDecimal("100");
		//devuelve el measure convertido a metros
		if(measureUnit.getName().equalsIgnoreCase("Pulgadas")) {
			//pulgadas a centimetros
			BigDecimal cm = measure.multiply(new BigDecimal("2.54"));
			//centimetros a metros
			return cm.divide(bd100);
		} else if (measureUnit.getName().equalsIgnoreCase("Centimetros")) {
			return measure.divide(bd100);
		} else if (measureUnit.getName().equalsIgnoreCase("Milimetros")) {
			return measure.divide(new BigDecimal("1000"));
		}
		// si ya esta en metros, se devuelve sin modificar
		return measure;
	}

	private void generateMachine() {
		if (machineRepository.count() == 0) {
			List<Machine> list = new ArrayList<>();
			generateMachineType();
			List<MachineType> listMachineType = machineTypeRepository.findAll();
			for(MachineType each : listMachineType) {
				list.add(new Machine(each, each.getName().toUpperCase() + " 1", 2004, null));
				list.add(new Machine(each, each.getName().toUpperCase() + " 2", 2008, null));
				list.add(new Machine(each, each.getName().toUpperCase() + " 3", 2014, null));
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
			Product product = null;
			List<Product> list = new ArrayList<>();
			product = new Product("1", "C\u00f3moda 4 Cajones 1 Puerta", "Medidas: Ancho: 105cm - Profundidad: 45cm – Alto: 100cm", productCategoryRepository.findFirstByName("Cajonera"), new BigDecimal("2300"));
			product.setStockMax(15);
			list.add(product);
			product = new Product("2", "Cama 1 Plaza", "Medidas: Ancho: 90cm - Profundidad: 200cm – Alto: 36cm", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("940"));
			product.setStockMax(20);
			list.add(product);
			product = new Product("3", "Biblioteca 4 Estantes", "Medidas: Ancho: 100cm - Profundidad: 25cm – Alto: 175cm", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("1100"));
			product.setStockMax(10);
			list.add(product);
			product = new Product("4", "Mesa Maciza Pata Recta", "Medidas: Ancho: 140cm - Profundidad: 80cm – Alto: 80cm", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("1250"));
			product.setStockMax(15);
			list.add(product);
			product = new Product("5", "Silla", "Medidas: Ancho: 40cm - Profundidad: 38cm – Alto: 95cm", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("250"));
			product.setStockMax(150);
			list.add(product);
			product = new Product("6", "Mesa De Luz 1 Caj\u00f3n", "Medidas: Ancho: 49cm - Profundidad: 41cm – Alto: 63cm", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("915"));
			product.setStockMax(40);
			list.add(product);
			product = new Product("7", "Banco Mediano", "Medidas: Ancho: 30cm - Profundidad: 30cm – Alto: 60cm", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("320"));
			product.setStockMax(100);
			list.add(product);
			/*
			list.add(new Product("2", "Mesa patas 4x4 rectas de 1.40x0.80mts", "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("956")));
			list.add(new Product("23", "Mesa de living patas 4x4 rectas de 1.00x0.70mts", "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("678")));
			list.add(new Product("28", "Taburete bajo estilo r\u00fastico", "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("177")));
			list.add(new Product("32", "Silla omega respaldo inclinado patas rectas", "", productCategoryRepository.findFirstByName("Silla"), new BigDecimal("231")));
			list.add(new Product("34", "Cama omega de 1.40mts", "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("704")));
			list.add(new Product("36", "Cama sill\u00f3n", "", productCategoryRepository.findFirstByName("Cama"), new BigDecimal("1045")));
			list.add(new Product("53", "Mesa de luz de 0,70x0,50x0,40 mts", "", productCategoryRepository.findFirstByName("Mesa"), new BigDecimal("473")));
			list.add(new Product("54", "Biblioteca de 0,30 mts con estantes", "", productCategoryRepository.findFirstByName("Biblioteca"), new BigDecimal("546")));
			list.add(new Product("60", "Banco cuadrado chico", "", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("153")));
			 */
			productRepository.save(list);
		}
	}
}

/* 
\u00e1 -> á
\u00e9 -> é
\u00ed -> í
\u00f3 -> ó
\u00fa -> ú
\u00c1 -> Á
\u00c9 -> É
\u00cd -> Í
\u00d3 -> Ó
\u00da -> Ú
\u00f1 -> ñ
\u00d1 -> Ñ
 */
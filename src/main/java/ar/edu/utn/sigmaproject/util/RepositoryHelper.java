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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

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
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
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
import ar.edu.utn.sigmaproject.domain.WorkerRole;
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

	private List<String> namesUserType = Arrays.asList("Admin", "Gerente General", "Jefe de Ventas", "Jefe de Produccion", "Encargado de Ventas", "Encargado de Compras", "Jefe de Personal");
	private List<String> namesProductionPlanStateType = Arrays.asList("Registrado", "Parcialmente Abastecido", "Abastecido", "Lanzado", "En Ejecucion", "Finalizado", "Cancelado");
	private List<String> namesProductionOrderStateType = Arrays.asList("Registrada", "Preparada", "Iniciada", "Finalizada", "Cancelada");
	private List<String> namesOrderStateType = Arrays.asList("Creado", "Cancelado", "Planificado", "En Produccion", "Finalizado", "Entregado");
	private List<String> namesProductCategory = Arrays.asList("Banco", "Biblioteca", "Cajonera", "Cama", "Mesa", "Silla");
	private List<List<String>> namesDetailsMachineType = Arrays.asList(
			Arrays.asList("Escuadradora", "Utilizado para dar largo y ancho de la madera."),
			Arrays.asList("Sierra sin fin", "Utilizado para realizar los cortes curvos."),
			Arrays.asList("Cepilladora", "Utilizado para dar el grosor a la cara de la madera."),
			Arrays.asList("Tupi", "Utilizado para realizar fresado, canales y espigas a la madera."),
			Arrays.asList("Lijadora", "Utilizado para lijar la superficie de la madera.")
			);
	private List<List<String>> namesDetailsWoodType = Arrays.asList(
			Arrays.asList("Pino", "Semi-pesada, semi-dura."),
			Arrays.asList("Caoba", "Tradicional y compacta."),
			Arrays.asList("Nogal", "Dura, homogenea."),
			Arrays.asList("Roble", "Resistente, duradera y compacta.")
			);
	private List<List<String>> namesWorkHour = Arrays.asList(
			Arrays.asList("Operario", "180"),
			Arrays.asList("General", "145"),
			Arrays.asList("Control", "120")
			);
	private List<List<String>> namesSupplyType = Arrays.asList(
			Arrays.asList("1", "Lija al Agua", "Lijado fino prelustrado", "Szumik", "", "Unidad", "200", "10", "20", "11"),
			Arrays.asList("2", "Adhesivo Vinilico", "Encolado de piezas para su union", "Lencisa", "Envase", "5 Kg", "200", "10", "20", "316"),
			Arrays.asList("3", "Pintura Asfáltica", "Lustrado de muebles", "Szumik", "Lata", "18Lts", "200", "10", "20", "1200"),
			Arrays.asList("4", "Bulon Camero", "Di\u00e1metro 8 mm - Largo 110 mm", "", "", "", "200", "10", "20", "9"),
			Arrays.asList("5", "Tuerca", "", "", "", "", "200", "10", "20", "1.5"),
			Arrays.asList("6", "Arandela", "", "", "", "", "200", "10", "20", "1.15"),
			Arrays.asList("7", "Clavo", "", "", "", "", "800", "10", "20", "1")
			);
	private List<List<String>> namesWorker = Arrays.asList(
			Arrays.asList("EMPLEADO 1", "22/5/2008"),
			Arrays.asList("EMPLEADO 2", "15/2/2006"),
			Arrays.asList("EMPLEADO 3", "18/1/2010"),
			Arrays.asList("EMPLEADO 4", "25/8/2012"),
			Arrays.asList("EMPLEADO 5", "12/10/2009"),
			Arrays.asList("EMPLEADO 6", "28/6/2014"),
			Arrays.asList("EMPLEADO 7", "11/7/2016")
			);
	private List<List<String>> namesClient = Arrays.asList(
			Arrays.asList("CLIENTE 1", "03514704411", "EMAIL1@MAILSERVER.COM", "DIRECCION 1", "DETALLES 1"),
			Arrays.asList("CLIENTE 2", "03514704412", "EMAIL2@MAILSERVER.COM", "DIRECCION 2", "DETALLES 2"),
			Arrays.asList("CLIENTE 3", "03514704413", "EMAIL3@MAILSERVER.COM", "DIRECCION 3", "DETALLES 3"),
			Arrays.asList("CLIENTE 4", "03514704414", "EMAIL4@MAILSERVER.COM", "DIRECCION 4", "DETALLES 4"),
			Arrays.asList("CLIENTE 5", "03514704415", "EMAIL5@MAILSERVER.COM", "DIRECCION 5", "DETALLES 5")
			);
	private List<List<String>> namesProduct = Arrays.asList(
			Arrays.asList("1", "C\u00f3moda 4 Cajones 1 Puerta", "Medidas: Ancho: 105cm - Profundidad: 45cm – Alto: 100cm", "Cajonera", "2300", "15"),
			Arrays.asList("2", "Cama 1 Plaza", "Medidas: Ancho: 90cm - Profundidad: 200cm – Alto: 36cm", "Cama", "940","20"),
			Arrays.asList("3", "Biblioteca 4 Estantes", "Medidas: Ancho: 100cm - Profundidad: 25cm – Alto: 175cm", "Biblioteca", "1100", "10"),
			Arrays.asList("4", "Mesa Maciza Pata Recta", "Medidas: Ancho: 140cm - Profundidad: 80cm – Alto: 80cm", "Mesa", "1250", "15"),
			Arrays.asList("5", "Silla", "Medidas: Ancho: 40cm - Profundidad: 38cm – Alto: 95cm", "Silla", "250", "150"),
			Arrays.asList("6", "Mesa De Luz 1 Caj\u00f3n", "Medidas: Ancho: 49cm - Profundidad: 41cm – Alto: 63cm", "Mesa", "915", "40"),
			Arrays.asList("7", "Banco Mediano", "Medidas: Ancho: 30cm - Profundidad: 30cm – Alto: 60cm", "Banco", "320", "100"),
			Arrays.asList("8", "Cubo Asiento", "Medidas: Ancho: 40cm - Profundidad: 40cm – Alto: 40cm", "Banco", "500", "100")
			);
	private List<List<String>> namesProcessType = Arrays.asList(
			Arrays.asList("1", "Trazado de Madera", "Trazar maderas para el posterior cortado.", ""),
			Arrays.asList("2", "Cortado de Madera", "Cortar maderas en las medidas trazadas.", "Escuadradora"),
			Arrays.asList("3", "Cortado Curvo", "", "Sierra sin fin"),
			Arrays.asList("4", "Lijado", "", "Lijadora"),
			Arrays.asList("5", "Cepillado", "", "Cepilladora"),
			Arrays.asList("6", "Espigado", "", "Tupi"),
			Arrays.asList("7", "Hacer Molduras", "", "Tupi"),
			Arrays.asList("8", "Acanalado", "", "Tupi"),
			Arrays.asList("9", "Ensamblado", "Unir diferentes piezas con tornillos, engrapado, pegamento, clavos, etc.", "")
			);

	@PostConstruct
	public void afterConstruct() throws InterruptedException {
		generateMeasureUnitTypeList();
		generateProductionPlanStateTypes();
		generateProductionOrderStateTypes();
		generateOrderStateType();
		generateProcessType();
		generateWood();
		generateClient();
		generateWorkHour();
		generateWorker();
		generateSupplyType();
		generateMachine();
		generateSettings();
		generateUserType();
		generateProduct();
		hibernateSearchReIndex();
	}

	private void generateSettings() {
		if (settingsRepository.count() == 0) {
			Settings settings = new Settings();
			settings.setPercentProfit(new BigDecimal(25));
			settingsRepository.save(settings);
		}
	}

	private void generateUserType() {
		if (userTypeRepository.count() == 0) {
			List<UserType> list = new ArrayList<>();
			for(String each : namesUserType) {
				list.add(new UserType(each, ""));
			}
			userTypeRepository.save(list);
		}
	}

	private void generateProductCategory() {
		if (productCategoryRepository.count() == 0) {
			List<ProductCategory> list = new ArrayList<>();
			for(String each : namesProductCategory) {
				list.add(new ProductCategory(each));
			}
			productCategoryRepository.save(list);
		}
	}

	private void generateProductionPlanStateTypes() {
		if (productionPlanStateTypeRepository.count() == 0) {
			List<ProductionPlanStateType> list = new ArrayList<>();
			for(String each : namesProductionPlanStateType) {
				list.add(new ProductionPlanStateType(each, null));
			}
			productionPlanStateTypeRepository.save(list);
		}
	}

	private void generateProductionOrderStateTypes() {
		if (productionOrderStateTypeRepository.count() == 0) {
			List<ProductionOrderStateType> list = new ArrayList<>();
			for(String each : namesProductionOrderStateType) {
				list.add(new ProductionOrderStateType(each));
			}
			productionOrderStateTypeRepository.save(list);
		}
	}

	private void generateOrderStateType() {
		if (orderStateTypeRepository.count() == 0) {
			List<OrderStateType> list = new ArrayList<>();
			for(String each : namesOrderStateType) {
				list.add(new OrderStateType(each, null));
			}
			orderStateTypeRepository.save(list);
		}
	}

	private void generateMachineType() {
		if (machineTypeRepository.count() == 0) {
			List<MachineType> list = new ArrayList<>();
			for(List<String> each : namesDetailsMachineType) {
				String name = each.get(0);
				String detail = each.get(1);
				list.add(new MachineType(name, detail, null));
			}
			machineTypeRepository.save(list);
		}
	}

	private void generateWoodType() {
		if (woodTypeRepository.count() == 0) {
			List<WoodType> list = new ArrayList<>();
			for(List<String> each : namesDetailsWoodType) {
				String name = each.get(0);
				String detail = each.get(1);
				list.add(new WoodType(name, detail));
			}
			woodTypeRepository.save(list);
		}
	}

	private void generateWorkHour() {
		if(workHourRepository.count() == 0) {
			List<WorkHour> list = new ArrayList<>();
			for(List<String> each : namesWorkHour) {
				list.add(new WorkHour(each.get(0), new BigDecimal(each.get(1))));
			}
			workHourRepository.save(list);
		}
	}

	private void generateSupplyType() {
		if(supplyTypeRepository.count() == 0) {
			List<SupplyType> list = new ArrayList<>();
			for(List<String> each : namesSupplyType) {
				list.add(new SupplyType(each.get(0), each.get(1), each.get(2), each.get(3), each.get(4), each.get(5), new BigDecimal(each.get(6)), new BigDecimal(each.get(7)), new BigDecimal(each.get(8)), new BigDecimal(each.get(9))));
			}
			supplyTypeRepository.save(list);
		}
	}

	private void generateClient() {
		if (clientRepository.count() == 0) {
			List<Client> list = new ArrayList<>();
			for(List<String> each : namesClient) {
				list.add(new Client(each.get(0), each.get(1), each.get(2), each.get(3), each.get(4)));
			}
			clientRepository.save(list);
		}
	}

	private void generateProduct() {
		if (productRepository.count() == 0) {
			generateProductCategory();
			List<Product> list = new ArrayList<>();
			for(List<String> each : namesProduct) {
				Product product = new Product(each.get(0), each.get(1), each.get(2), productCategoryRepository.findFirstByName(each.get(3)), new BigDecimal(each.get(4)));
				product.setStockMax(Integer.parseInt(each.get(5)));
				list.add(product);
			}
			productRepository.save(list);
			generateProductComplete();
		}
	}

	private void generateWorker() {
		if(workerRepository.count() == 0) {
			List<Worker> list = new ArrayList<>();
			for(List<String> each : namesWorker) {
				Date date = null;
				if(each.get(1).equals("")) {
					try {
						date=new SimpleDateFormat("dd/MM/yyyy").parse(each.get(1));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				list.add(new Worker(each.get(0), date));
			}
			List<WorkHour> workHourList = workHourRepository.findAll();
			for(Worker each : list) {
				WorkHour randomWorkHour = workHourList.get(new Random().nextInt(workHourList.size()));
				WorkerRole randomWorkerRole = new WorkerRole(randomWorkHour);
				randomWorkerRole.setWorker(each);
				each.getWorkerRoleList().add(randomWorkerRole);
			}
			workerRepository.save(list);
		}
	}

	private void generateProcessType() {
		if (processTypeRepository.count() == 0) {
			generateMachineType();
			List<ProcessType> list = new ArrayList<>();
			for(List<String> each : namesProcessType) {
				MachineType machineType = null;
				if(!each.get(3).equals("")) {
					machineType = machineTypeRepository.findFirstByName(each.get(3));
				}
				list.add(new ProcessType(Integer.parseInt(each.get(0)), each.get(1), each.get(2), machineType));
			}
			processTypeRepository.save(list);
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
			price = "17000";
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

	private void generateProductComplete() {
		Product product = null;
		List<Piece> listPiece = new ArrayList<>();
		product = new Product("8", "Cubo Asiento", "Medidas: Ancho: 40cm - Profundidad: 40cm – Alto: 40cm", productCategoryRepository.findFirstByName("Banco"), new BigDecimal("300"));
		product.setStockMax(200);
		

		for(String each : namesPieces) {
			listPiece.add(createPieceComplete(each, product));
		}
		product.getPieces().addAll(listPiece);
		productRepository.save(product);
	}

	private Piece createPieceComplete(Product product, String unitsPiece, String namePiece, String length, String width, String depth) {
		boolean isGroup = false;
		if(length.equals("0") && width.equals("0") && depth.equals("0")) {
			isGroup = true;
		}
		int units = Integer.parseInt(unitsPiece);
		MeasureUnit centimeter = measureUnitRepository.findFirstByName("Centimetros");
		Piece piece = new Piece(product, namePiece.toUpperCase(), new BigDecimal(length), centimeter, new BigDecimal(depth), centimeter, new BigDecimal(width), centimeter, "", isGroup, units);
		List<Process> listProcess = new ArrayList<>();
		List<ProcessType> processTypeList = processTypeRepository.findAll();
		List<WorkHour>workHourList = workHourRepository.findAll();
		for(Integer each : randomNotDuplicates(processTypeList.size())) {
			ProcessType randomProcessType = processTypeList.get(each);
			WorkHour randomWorkHour = workHourList.get(new Random().nextInt(workHourList.size()));
			int hours = 0;
			int minutes = new Random().nextInt(15)+1;
			int seconds = new Random().nextInt(59)+1;
			// la suma de la duracion no puede ser cero
			while(hours+minutes+seconds==0) {
				// se vuelven a generar
				hours = 0;
				minutes = new Random().nextInt(59)+1;
				seconds = new Random().nextInt(59)+1;
			}
			listProcess.add(createProcess(piece, randomProcessType, hours, minutes , seconds, randomWorkHour));
		}
		piece.getProcesses().addAll(listProcess);
		return piece;
	}

	private List<Integer> randomNotDuplicates(int maxSize) {// necesario para los numeros de procesos, una pieza no puede tener procesos repetidos
		// devuelve una lista sin duplicados y de cantidad menor a la maxima
		int howMany = new Random().nextInt(maxSize-1)+1;// no puede ser cero
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < howMany; i++) {
			Integer add = new Random().nextInt(maxSize);
			while (list.contains(add)) {
				add = new Random().nextInt(maxSize); //si existe duplicado se genera nuevamente
			}
			//al llegar aca no sera duplicado
			list.add(add);
		}
		return list;
	}

	private Process createProcess(Piece piece, ProcessType processType, int hours, int minutes, int seconds, WorkHour workHour) {
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, hours, minutes, seconds);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		return new Process(piece, processType, "", duration, workHour);
	}
}

/*
 * list.add(new ProcessType(1, "Trazado de Madera", "Trazar maderas para el posterior cortado.", null));
			list.add(new ProcessType(2, "Cortado de Madera", "Cortar maderas en las medidas trazadas.", machineTypeRepository.findFirstByName("Escuadradora")));
			list.add(new ProcessType(3, "Cortado Curvo", machineTypeRepository.findFirstByName("Sierra sin fin")));
			list.add(new ProcessType(4, "Lijado", machineTypeRepository.findFirstByName("Lijadora")));
			list.add(new ProcessType(5, "Cepillado", machineTypeRepository.findFirstByName("Cepilladora")));
			list.add(new ProcessType(6, "Espigado", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(7, "Hacer Molduras", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(8, "Acanalado", machineTypeRepository.findFirstByName("Tupi")));
			list.add(new ProcessType(9, "Ensamblado", "Unir diferentes piezas con tornillos, engrapado, pegamento, clavos, etc.", null));

			list.add(new WorkHour("Operario", new BigDecimal("180")));
			list.add(new WorkHour("General", new BigDecimal("145")));
			list.add(new WorkHour("Control", new BigDecimal("120")));

			piece = new Piece(product, "lateral derecho".toUpperCase(), BigDecimal.ZERO, null, BigDecimal.ZERO, null, BigDecimal.ZERO, null, "", false, 1);
		listProcess = new ArrayList<>();
		listProcess.add(createProcess(piece, "Trazado de Madera", 0, 0 , 35, "Operario"));
		listProcess.add(createProcess(piece, "Cortado de Madera", 0, 1 , 15, "Operario"));
		listProcess.add(createProcess(piece, "Lijado", 0, 0 , 35, "Control"));
		piece.getProcesses().addAll(listProcess);
		listPiece.add(piece);
		piece = new Piece(product, "lateral izquierdo".toUpperCase(), BigDecimal.ZERO, null, BigDecimal.ZERO, null, BigDecimal.ZERO, null, "", false, 1);
		listProcess = new ArrayList<>();
		listProcess.add(createProcess(piece, "Trazado de Madera", 0, 0 , 35, "Operario"));
		listProcess.add(createProcess(piece, "Cortado de Madera", 0, 1 , 15, "General"));
		listProcess.add(createProcess(piece, "Lijado", 0, 0 , 35, "Operario"));
		piece.getProcesses().addAll(listProcess);
		listPiece.add(piece);
		piece = new Piece(product, "base intermedia".toUpperCase(), BigDecimal.ZERO, null, BigDecimal.ZERO, null, BigDecimal.ZERO, null, "", false, 4);
		listProcess = new ArrayList<>();
		listProcess.add(createProcess(piece, "Trazado de Madera", 0, 0 , 45, "General"));
		listProcess.add(createProcess(piece, "Cortado Curvo", 0, 1 , 3, "Operario"));
		listProcess.add(createProcess(piece, "Lijado", 0, 2 , 5, "General"));
		piece.getProcesses().addAll(listProcess);
		listPiece.add(piece);
		piece = new Piece(product, "estante completo".toUpperCase(), BigDecimal.ZERO, null, BigDecimal.ZERO, null, BigDecimal.ZERO, null, "", true, 4);
		listProcess = new ArrayList<>();
		listProcess.add(createProcess(piece, "Ensamblado", 0, 4 , 45, "Operario"));
		piece.getProcesses().addAll(listProcess);
		listPiece.add(piece);
 */

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
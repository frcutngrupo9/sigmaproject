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

package ar.edu.utn.sigmaproject.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;
import ar.edu.utn.sigmaproject.util.ProductionOrderReportDataSource;
import ar.edu.utn.sigmaproject.util.RenderElHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Textbox productionPlanStateTypeTextbox;
	@Wire
	Textbox productionOrderStateTypeTextbox;
	@Wire
	Textbox productionOrderNumberTextbox;
	@Wire
	Textbox productCodeTextbox;
	@Wire
	Textbox productNameTextbox;
	@Wire
	Grid productionOrderDetailGrid;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Datebox productionOrderStartDatebox;
	@Wire
	Datebox productionOrderFinishDatebox;
	@Wire
	Button generateDetailsButton;
	@Wire
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;
	@Wire
	Button autoAssignButton;
	@Wire
	Image productImage;
	@Wire
	Listbox reportTypeListbox;
	@Wire
	Button createReportButton;
	@Wire
	Jasperreport reportBlockJasperreport;
	@Wire
	Label productionOrderSupplyTotalPriceLabel;
	@Wire
	Label productionOrderRawMaterialTotalPriceLabel;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionOrderDetailRepository productionOrderDetailRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private WorkerRepository workerRepository;
	@WireVariable
	private ProcessTypeRepository processTypeRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Machine> machineList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderRawMaterialListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}
		productionOrderDetailList = currentProductionOrder.getDetails();
		machineList = machineRepository.findAll();
		setTotalPrices();
		refreshView();
	}

	private void setTotalPrices() {
		BigDecimal totalPriceSupply = currentProductionOrder.getTotalCostSupply();
		BigDecimal totalPriceRawMaterial = currentProductionOrder.getTotalCostRawMaterial();
		productionOrderSupplyTotalPriceLabel.setValue(totalPriceSupply.doubleValue() + "");
		productionOrderRawMaterialTotalPriceLabel.setValue(totalPriceRawMaterial.doubleValue() + "");
	}

	private void refreshView() {
		org.zkoss.image.Image img = null;
		try {
			img = new AImage("", currentProductionOrder.getProduct().getImageData());
		} catch (IOException exception) { }
		if(img != null) {
			int[] heightAndWidthArray = RenderElHelper.getHeightAndWidthScaled(img, 75);
			productImage.setHeight(heightAndWidthArray[0] + "px");
			productImage.setWidth(heightAndWidthArray[1] + "px");
			productImage.setStyle("margin: 8px");
		} else {
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
		}
		productImage.setContent(img);
		sortProductionOrderDetailListByProcessTypeSequence();
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productionOrderStateTypeTextbox.setText(currentProductionOrder.getCurrentStateType().getName());
		productionOrderNumberTextbox.setText(currentProductionOrder.getNumber() + "");
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productCodeTextbox.setText(currentProductionOrder.getProduct().getCode());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
		refreshProductionOrderOrderSupplyAndRawMaterialListbox();
	}

	private void refreshProductionOrderDetailGridView() {
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
	}

	private void refreshProductionOrderOrderSupplyAndRawMaterialListbox() {
		List<ProductionOrderMaterial> productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		List<ProductionOrderMaterial> productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderMaterial>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);
	}

	private Integer getNewProductionOrderNumber() {
		Integer lastValue = 0;
		List<ProductionOrder> list = productionOrderRepository.findByProductionPlan(currentProductionPlan);
		for(ProductionOrder each : list) {
			if(each.getNumber()!=null && each.getNumber()>lastValue) {
				lastValue = each.getNumber();
			}
		}
		return lastValue + 1;
	}

	private boolean isEditionAllowed() {
		// no se puede modificar si el plan esta Cancelado, o Finalizado. Si esta Suspendido se puede modificar para solucionar problemas de maquinas en reparacion o empreados ausentes.
		ProductionPlanStateType currentStateType = currentProductionPlan.getCurrentStateType();
		if(currentStateType.getName().equalsIgnoreCase("Finalizado") || currentStateType.getName().equalsIgnoreCase("Cancelado")) {
			return false;
		}
		return true;
	}

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(!isEditionAllowed()) {
			alert("No se puede modificar porque el Plan de Produccion esta Cancelado, Lanzado, En Ejecucion o Finalizado.");
			return;
		}
		String stateType = currentProductionOrder.getCurrentStateType().getName();
		if(stateType.equalsIgnoreCase("Iniciada") || stateType.equalsIgnoreCase("Finalizada") || stateType.equalsIgnoreCase("Cancelada")) {
			alert("No se puede modificar una Orden de Produccion " + stateType + ".");
			return;
		}
		/*
		for (ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
			if (productionOrderDetail.getState() != ProcessState.Cancelado) {
				if (productionOrderDetail.getWorker() == null) {
					Clients.showNotification("Existen Procesos sin Trabajador Asignado", productionOrderDetailGrid);
					return;
				}
				MachineType machineType = productionOrderDetail.getProcess().getType().getMachineType();
				if (machineType != null) {
					if (productionOrderDetail.getMachine() == null) {
						Clients.showNotification("Existen Procesos sin Maquina Asignada", productionOrderDetailGrid);
						return;
					}
				}
				if (productionOrderDetail.getDateStart() == null) {
					Clients.showNotification("Existen Procesos sin Fecha Seleccionada", productionOrderDetailGrid);
					return;
				}
			}
		}
		 */
		if(currentProductionOrder.getNumber() == 0) {
			currentProductionOrder.setNumber(getNewProductionOrderNumber());
		}
		// la fecha inicio y fin de la orden se actualiza
		currentProductionOrder.setDateStart(currentProductionOrder.getStartDateFromDetails());
		currentProductionOrder.setDateFinish(currentProductionOrder.getFinishDateFromDetails());
		// el estado de la orden debe cambiar automaticamente 
		ProductionOrderStateType productionOrderStateType = null;
		if(currentProductionOrder.isReady()) {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Preparada");
		} else {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Registrada");
		}
		if(!productionOrderStateType.getName().equalsIgnoreCase(currentProductionOrder.getCurrentStateType().getName())) { // no se vuelve a grabar si es el mismo estado
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
		}
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		productionOrderDetailList = currentProductionOrder.getDetails();
		refreshView();
		alert("Orden de Produccion Guardada.");
	}

	// Estados Orden de Produccion: "Registrada" "Preparada" "Iniciada" "Finalizada" "Cancelada"
	// Estados Plan de Produccion: "Registrado""Abastecido""Lanzado""En Ejecucion""Finalizado""Cancelado""Suspendido"

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(productionOrderDetailGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		currentProductionOrder = productionOrderRepository.findOne(currentProductionOrder.getId());// obtiene la misma orden sin cambios en los detalles
		productionOrderDetailList = currentProductionOrder.getDetails();
		refreshView();
	}

	public boolean isMachineNecessary(ProcessType processType) {
		if(processType.getMachineType() != null) {
			return true;
		}
		return false;
	}

	public ListModelList<Machine> getCompleteMachineListModel(ProductionOrderDetail productionOrderDetail) {
		if (productionOrderDetail.getProcess().getType().getMachineType() == null || isProductionOrderDetailNotValid(productionOrderDetail)) {
			return new ListModelList<>(new ArrayList<Machine>());
		}
		// devuelve todas las maquinas del mismo tipo
		return new ListModelList<>(getMachineListByProcessType(productionOrderDetail.getProcess().getType()));
	}

	private List<Machine> getMachineListByProcessType(ProcessType processType) {
		List<Machine> list = new ArrayList<Machine>();
		if(processType.getMachineType() != null) {
			MachineType machineType = machineTypeRepository.findOne(processType.getMachineType().getId());
			if (machineType != null) {
				for (Machine machine : machineList) {
					if (machineType.getId() == machine.getMachineType().getId()) {
						list.add(machine);
					}
				}
			}
		}
		return list;
	}

	public String getMachineAvailabilityDescription(Machine machine, ProductionOrderDetail productionOrderDetail) {
		if (machine == null || productionOrderDetail == null) {
			return "";
		}
		// devuelve el estado de la maquina
		// se busca todos los detalles de las ordenes que esten superpuestas en horario y sean el mismo tipo de maquina
		Map<ProductionOrder, Map<Piece, List<ProcessType>>> productionOrderMap = getMachineProductionOrderMap(machine, getMachineOverlappingDetails(productionOrderDetail));
		if(productionOrderMap != null) {
			String availabilityText = "No Disponible ( usada en ";
			availabilityText += getDescriptionWhere(productionOrderMap);
			return availabilityText += ")";
		} else {
			return "Disponible";
		}
	}

	private Map<ProductionOrder, Map<Piece, List<ProcessType>>> getMachineProductionOrderMap(Machine machine, List<ProductionOrderDetail> machineOverlappingDetails) {
		Map<ProductionOrder, Map<Piece, List<ProcessType>>> productionOrderMap = null;
		for(ProductionOrderDetail each : machineOverlappingDetails) {
			// no se considera si el detalle pertenece a la misma orden actual
			if(each.getProductionOrder().getId() != currentProductionOrder.getId()) {
				if(each.getMachine().getId() == machine.getId()) {
					if(productionOrderMap == null) {
						productionOrderMap = new HashMap<ProductionOrder, Map<Piece, List<ProcessType>>>();
					}
					Map<Piece, List<ProcessType>> pieceMap = productionOrderMap.get(each.getProductionOrder());
					Piece piece = each.getProcess().getPiece();
					ProductionOrder productionOrder = each.getProductionOrder();
					List<ProcessType> processList = null;
					if(pieceMap == null) {
						pieceMap = new HashMap<Piece, List<ProcessType>>();
						processList = new ArrayList<ProcessType>();
						processList.add(each.getProcess().getType());
					} else {
						processList = pieceMap.get(piece);
						if(processList == null) {
							processList = new ArrayList<ProcessType>();
						}
						processList.add(each.getProcess().getType());
					}
					pieceMap.put(piece, processList);
					productionOrderMap.put(productionOrder, pieceMap);
				}
			}
		}
		return productionOrderMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onSelectMachineListbox = #productionOrderDetailGrid")
	public void doSelectMachineListbox(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Listbox element = (Listbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Machine machineSelected = (Machine)element.getSelectedItem().getValue();
		// comprueba que la maquina seleccionada no este ocupada en otra orden, en ese caso mostrar mensaje y volver al valor anterior, caso contrario asignar
		if(isMachineAvailable(machineSelected, data)) {
			data.setMachine(machineSelected);// cargamos al objeto el valor actualizado del elemento web
			refreshProductionOrderDetailGridView();
		} else {
			//alert("Maquina no disponible en ese horario");
			final Machine machineSelectedFinal = machineSelected;
			final ProductionOrderDetail dataFinal = data;
			// pregunta si se quiere asignar igual estando ocupada
			Messagebox.show("La maquina no esta disponible en el horario seleccionado, desea asignarla de todas formas?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						dataFinal.setMachine(machineSelectedFinal);// cargamos al objeto el valor actualizado del elemento web
						refreshProductionOrderDetailGridView();
						alert("Maquina asignada");
					}
				}
			});
		}
	}

	private boolean isMachineAvailable(Machine machine, ProductionOrderDetail productionOrderDetail) {
		if (machine == null || productionOrderDetail == null) {
			return false;
		}
		List<ProductionOrderDetail> machineOverlappingDetails = getMachineOverlappingDetails(productionOrderDetail);
		// se busca en las restantes ordenes si la maquina parametro es la misma asignada
		for(ProductionOrderDetail each : machineOverlappingDetails) {
			// no se considera si el detalle pertenece a la misma orden actual
			if(each.getProductionOrder().getId() != currentProductionOrder.getId()) {
				if(each.getMachine().getId() == machine.getId()) {
					return false;
				}
			}
		}
		return true;
	}

	private List<ProductionOrderDetail> getMachineOverlappingDetails(ProductionOrderDetail productionOrderDetail) {
		// busca todos los otros detalles existentes que sus horarios coincidan con el parametro y que requieran y tengan una maquina asignada
		Date start = productionOrderDetail.getDateStart();
		Date finish = productionOrderDetail.getDateFinish();
		List<ProductionOrderDetail> overlappingDetails = productionOrderDetailRepository.findByDateFinishAfterAndDateStartBefore(start, finish);
		// se elimina de la lista el proceso actual
		overlappingDetails.remove(productionOrderDetailRepository.findOne(productionOrderDetail.getId()));
		// se crea una lista con los detalles de ordenes canceladas o finalizadas o sin maquina para luego removerlos de la lista
		List<ProductionOrderDetail> invalidDetails = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : overlappingDetails) {
			if(each.getState() == ProcessState.Cancelado || each.getState() == ProcessState.Realizado || each.getProcess().getType().getMachineType() == null || each.getMachine() == null) {
				invalidDetails.add(each);
			}
		}
		// se remueven los detalles invalidos de la lista
		for(ProductionOrderDetail each : invalidDetails) {
			overlappingDetails.remove(each);
		}
		return overlappingDetails;
	}

	private boolean isProductionOrderDetailNotValid(ProductionOrderDetail productionOrderDetail) {
		// si esta cancelado o si tiene fechas en null no es valido
		return productionOrderDetail.getState() == ProcessState.Cancelado || productionOrderDetail.getDateStart() == null || productionOrderDetail.getDateFinish() == null;
	}

	public ListModelList<Worker> getCompleteWorkerListModel(ProductionOrderDetail productionOrderDetail) {
		if (isProductionOrderDetailNotValid(productionOrderDetail)) {
			return new ListModelList<>(new ArrayList<Worker>());
		}
		return new ListModelList<>(workerRepository.findAll());
	}

	public String getWorkerAvailabilityDescription(Worker worker, ProductionOrderDetail productionOrderDetail) {
		if (worker == null || productionOrderDetail == null) {
			return "";
		}
		Map<ProductionOrder, Map<Piece, List<ProcessType>>> productionOrderMap = getProductionOrderMap(worker, getWorkerOverlappingDetails(productionOrderDetail));
		if(productionOrderMap != null) {
			String availabilityText = "No Disponible ( asignado a ";
			availabilityText += getDescriptionWhere(productionOrderMap);
			return availabilityText += ")";
		} else {
			return "Disponible";
		}
	}

	private Map<ProductionOrder, Map<Piece, List<ProcessType>>> getProductionOrderMap(Worker worker, List<ProductionOrderDetail> workerOverlappingDetails) {
		Map<ProductionOrder, Map<Piece, List<ProcessType>>> productionOrderMap = null;
		for(ProductionOrderDetail each : workerOverlappingDetails) {
			// no se considera si el detalle pertenece a la misma orden actual
			if(each.getProductionOrder().getId() != currentProductionOrder.getId()) {
				if(each.getWorker().getId() == worker.getId()) {
					if(productionOrderMap == null) {
						productionOrderMap = new HashMap<ProductionOrder, Map<Piece, List<ProcessType>>>();
					}
					Map<Piece, List<ProcessType>> pieceMap = productionOrderMap.get(each.getProductionOrder());
					Piece piece = each.getProcess().getPiece();
					ProductionOrder productionOrder = each.getProductionOrder();
					List<ProcessType> processList = null;
					if(pieceMap == null) {
						pieceMap = new HashMap<Piece, List<ProcessType>>();
						processList = new ArrayList<ProcessType>();
						processList.add(each.getProcess().getType());
					} else {
						processList = pieceMap.get(piece);
						if(processList == null) {
							processList = new ArrayList<ProcessType>();
						}
						processList.add(each.getProcess().getType());
					}
					pieceMap.put(piece, processList);
					productionOrderMap.put(productionOrder, pieceMap);
				}
			}
		}
		return productionOrderMap;
	}

	private String getDescriptionWhere(Map<ProductionOrder, Map<Piece, List<ProcessType>>> productionOrderMap) {
		String descriptionWhere = "";
		for (Map.Entry<ProductionOrder, Map<Piece, List<ProcessType>>> entryProductionOrder : productionOrderMap.entrySet()) {
			descriptionWhere += "Orden " + entryProductionOrder.getKey().getNumber();
			for (Map.Entry<Piece, List<ProcessType>> entryPiece : entryProductionOrder.getValue().entrySet()) {
				descriptionWhere += " " + entryPiece.getKey().getName();
				boolean firstTime = true;
				for(ProcessType eachProcessType : entryPiece.getValue()) {
					if(firstTime) {
						descriptionWhere += ": " + eachProcessType.getName();
						firstTime = false;
					} else {
						descriptionWhere += ", " + eachProcessType.getName();
					}
				}
				descriptionWhere += " ";
			}
		}
		return descriptionWhere;
	}

	private boolean isWorkerAvailable(Worker worker, ProductionOrderDetail productionOrderDetail) {
		if (worker == null || productionOrderDetail == null) {
			return false;
		}
		List<ProductionOrderDetail> workerOverlappingDetails = getWorkerOverlappingDetails(productionOrderDetail);
		// se busca en las restantes ordenes si la maquina parametro es la misma asignada
		for(ProductionOrderDetail each : workerOverlappingDetails) {
			// no se considera si el detalle pertenece a la misma orden actual
			if(each.getProductionOrder().getId() != currentProductionOrder.getId()) {
				if(each.getWorker().getId() == worker.getId()) {
					return false;
				}
			}
		}
		return true;
	}

	private List<ProductionOrderDetail> getWorkerOverlappingDetails(ProductionOrderDetail productionOrderDetail) {
		// busca todos los otros detalles existentes que sus horarios coincidan con el parametro y tengan un empleado asignado
		Date start = productionOrderDetail.getDateStart();
		Date finish = productionOrderDetail.getDateFinish();
		List<ProductionOrderDetail> overlappingDetails = productionOrderDetailRepository.findByDateFinishAfterAndDateStartBefore(start, finish);
		// se elimina de la lista el proceso actual
		overlappingDetails.remove(productionOrderDetailRepository.findOne(productionOrderDetail.getId()));
		// se crea una lista con los detalles de ordenes canceladas o finalizadas o sin worker para luego removerlos de la lista
		List<ProductionOrderDetail> invalidDetails = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : overlappingDetails) {
			if(each.getState() == ProcessState.Cancelado || each.getState() == ProcessState.Realizado || each.getWorker() == null) {
				invalidDetails.add(each);
			}
		}
		// se remueven los detalles invalidos de la lista
		for(ProductionOrderDetail each : invalidDetails) {
			overlappingDetails.remove(each);
		}
		return overlappingDetails;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onSelectWorkerListbox = #productionOrderDetailGrid")
	public void doSelectWorkerListbox(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Listbox element = (Listbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Worker workerSelected = (Worker)element.getSelectedItem().getValue();
		// comprueba que la seleccion no este ocupada en otra orden, en ese caso mostrar mensaje y volver al valor anterior, caso contrario asignar
		if(isWorkerAvailable(workerSelected, data)) {
			data.setWorker(workerSelected);// cargamos al objeto el valor actualizado del elemento web
			// si el trabajador asignado es diferente al anterior, verificar si no es necesario maquina y si no lo es, cambiar el inicio al mismo inicio del proceso anterior
			/*ProductionOrderDetail prevDetail = getFirstDetailOfProcess(data);
			if(prevDetail != null) {
				if(!prevDetail.getWorker().equals(workerSelected)) {
					if(data.getProcess().getType().getMachineType() == null) {
						data.setDateStart(prevDetail.getDateStart());
						data.setDateFinish(ProductionDateTimeHelper.getFinishDate(prevDetail.getDateStart(), data.getDurationTotal()));
					}
				}
			}*/
			//recalculateDates();
			//assignAllDates();
			setAllDates();
			refreshProductionOrderDetailGridView();
		} else {
			final Worker workerSelectedFinal = workerSelected;
			final ProductionOrderDetail dataFinal = data;
			// pregunta si se quiere asignar igual estando ocupado
			Messagebox.show("El empleado no esta disponible en el horario seleccionado, desea asignarlo de todas formas?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						dataFinal.setWorker(workerSelectedFinal);// cargamos al objeto el valor actualizado del elemento web
						refreshProductionOrderDetailGridView();
						alert("Empleado asignado");
					}
				}
			});
		}
	}

	/*private Map<ProcessType, Map<Worker, List<ProductionOrderDetail>>> getCompleteMap() {
		//devuelve un map donde la llave son todos los tipos de procesos y el valor es otro map en el cual la llave es cada empleado asignados al tipo de proceso y el valor son todos los detalles a los que el empleado esta asignado
		Map<ProcessType, Map<Worker, List<ProductionOrderDetail>>> completeMap = new HashMap<ProcessType, Map<Worker, List<ProductionOrderDetail>>>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			ProcessType processType = each.getProcess().getType();
			Map<Worker, List<ProductionOrderDetail>> workerMap = completeMap.get(processType);
			Worker worker = each.getWorker();
			if(workerMap == null) {
				workerMap = new HashMap<Worker, List<ProductionOrderDetail>>();
				List<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
				list.add(each);
				workerMap.put(worker, list);
			} else {
				List<ProductionOrderDetail> list = workerMap.get(worker);
				if(list == null) {
					list = new ArrayList<ProductionOrderDetail>();
				} else {
					list.add(each);
				}
				workerMap.put(worker, list);
			}
		}
		return completeMap;
	}*/

	private Map<Worker, List<ProductionOrderDetail>> getWorkerMapByProcessType(ProcessType processType) {
		//devuelve un map donde la llave son todos los empleados asignados al tipo de proceso y el valor son todos los detalles a los que el empleado esta asignado
		Map<Worker, List<ProductionOrderDetail>> workerMap = new HashMap<Worker, List<ProductionOrderDetail>>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() != ProcessState.Cancelado) {
				if(each.getProcess().getType().equals(processType)) {
					Worker worker = each.getWorker();
					List<ProductionOrderDetail> list = workerMap.get(worker);
					if(list == null) {
						list = new ArrayList<ProductionOrderDetail>();
						list.add(each);
					} else {
						list.add(each);
					}
					workerMap.put(worker, list);
				}
			}
		}
		return workerMap;
	}

	private List<ProcessType> getProcessTypeList() {
		// devuelve una lista de todos los tipos de proceso asignados a la orden
		Set<ProcessType> processTypeSet = new HashSet<ProcessType>();
		for(ProductionOrderDetail eachProductionOrderDetail : productionOrderDetailList) {
			if(eachProductionOrderDetail.getState() != ProcessState.Cancelado) {
				processTypeSet.add(eachProductionOrderDetail.getProcess().getType());// garantiza que los tipo de procesos no se repitan
			}
		}
		List<ProcessType> list = new ArrayList<ProcessType>();
		for (ProcessType eachProcessType : processTypeSet) {
			list.add(eachProcessType);
		}
		return list;
	}

	private void setAllDates() {
		// recorre todos los procesos, por cada proceso recorre cada empleado y asigna la fechas a uno de ellos
		// al siguiente empleado se verifica si tiene una maquina distinta o no se requiere maquina y en base a eso se asigna la fecha del inicio del proceso la misma que la del empleado anterior
		// si alguna maquina es igual a otra, la pieza se realiza al finalizar el ultimo proceso del otro empleado
		// por cada proceso se obtiene cual es la ultima fecha de finalizacion, la cual sera el incio del proximo proceso
		sortListBySequence(productionOrderDetailList);
		Date startDate = getDateTimeStartWork(productionOrderStartDatebox.getValue());
		Date finishDateLast = null;
		for(Map.Entry<ProcessType, List<ProductionOrderDetail>> entryProcessType : getProcessTypeMap().entrySet()) {// tipos de proceso
			ProcessType currentProcessType = entryProcessType.getKey();
			List<ProductionOrderDetail> listProcessType = entryProcessType.getValue();
			// por cada lista de detalles de tipo de proceso sacamos un  map con los empleados y los detalles asignados a cada uno
			if(finishDateLast != null) {
				startDate = finishDateLast;
				finishDateLast = null;
			}
			if(currentProcessType.getMachineType() == null) {// si el proceso no necesita maquina
				for(Map.Entry<Worker, List<ProductionOrderDetail>> entryWorker : getWorkerMap(listProcessType).entrySet()) {// empleados
					List<ProductionOrderDetail> listWorker = entryWorker.getValue();
					Date startDateWorker = startDate;
					Date finishDateWorker = null;
					// al primer empleado asignamos los tiempos y al resto vemos si posee maquina igual a los demas y si es asi se lo pone despues, sino se lo pone al comienzo
					// a menos que los que tengan maquinas diferentes terminen despues de lo que terminan los demas procesos, en ese caso se pone a continuacion de ellos
					for(ProductionOrderDetail eachDetail : listWorker) {
						if(eachDetail.getState() != ProcessState.Cancelado) {// solo se calcula para los procesos que no esten cancelados
							if(finishDateWorker == null) {// si es la primera vez que ingresa
								finishDateWorker = ProductionDateTimeHelper.getFinishDate(startDateWorker, eachDetail.getDurationTotal());
							} else {
								// el inicio de la actual es al finalizar la ultima
								startDateWorker = finishDateWorker;
								finishDateWorker = ProductionDateTimeHelper.getFinishDate(startDateWorker, eachDetail.getDurationTotal());
							}
							eachDetail.setDateStart(startDateWorker);
							eachDetail.setDateFinish(finishDateWorker);
							if(finishDateLast==null || finishDateWorker.after(finishDateLast)) {
								finishDateLast = finishDateWorker;
							}
						} else {
							eachDetail.setDateStart(null);
							eachDetail.setDateFinish(null);
							// por las dudas borramos tambien las fechas reales
							eachDetail.setDateStartReal(null);
							eachDetail.setDateFinishReal(null);
						}
					}
				}
			} else {// si el proceso necesita maquina
				Date finishDate = null;
				for(ProductionOrderDetail eachDetail : listProcessType) {
					if(eachDetail.getState() != ProcessState.Cancelado) {// solo se calcula para los procesos que no esten cancelados
						if(finishDate == null) {// si es la primera vez que ingresa
							finishDate = ProductionDateTimeHelper.getFinishDate(startDate, eachDetail.getDurationTotal());
						} else {
							// el inicio de la actual es al finalizar la ultima
							startDate = finishDate;
							finishDate = ProductionDateTimeHelper.getFinishDate(startDate, eachDetail.getDurationTotal());
						}
						eachDetail.setDateStart(startDate);
						eachDetail.setDateFinish(finishDate);
						finishDateLast = finishDate;
					} else {
						eachDetail.setDateStart(null);
						eachDetail.setDateFinish(null);
						// por las dudas borramos tambien las fechas reales
						eachDetail.setDateStartReal(null);
						eachDetail.setDateFinishReal(null);
					}
				}
			}
		}
	}

	private Map<ProcessType, List<ProductionOrderDetail>> getProcessTypeMap() {
		// devuelve un map que por cada tipo de proceso contiene un listado de los detalles que lo incluyen
		Map<ProcessType, List<ProductionOrderDetail>> processTypeMap = new LinkedHashMap<ProcessType, List<ProductionOrderDetail>>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() != ProcessState.Cancelado) {
				ProcessType processType = each.getProcess().getType();
				processType = processTypeRepository.findOne(processType.getId());
				List<ProductionOrderDetail> list = processTypeMap.get(processType);
				if(list == null) {
					list = new ArrayList<ProductionOrderDetail>();
					list.add(each);
				} else {
					list.add(each);
				}
				processTypeMap.put(processType, list);
			}
		}
		return processTypeMap;
	}

	private void assignAllDates() {
		// hacemos una lista de todos los empleados asignados actualmente
		// por cada empleado ordenamos todas las fechas de sus procesos en secuencia
		// ordenamos todos los procesos en base a las fechas establecidas
		for(Map.Entry<Worker, List<ProductionOrderDetail>> entry : getWorkerMap(productionOrderDetailList).entrySet()) {
			//Worker worker = entry.getKey();
			//System.out.println("- - - -  - - - - - - - - -  - - - -  recorriendo worker: " + worker.getName());
			List<ProductionOrderDetail> list = entry.getValue();
			sortListBySequence(list);
			Date startDate = getDateTimeStartWork(productionOrderStartDatebox.getValue());
			Date finishDate = null;
			for(ProductionOrderDetail eachDetail : list) {
				if(eachDetail.getState() != ProcessState.Cancelado) {// solo se calcula para los procesos que no esten cancelados
					if(finishDate == null) {// si es la primera vez que ingresa
						finishDate = ProductionDateTimeHelper.getFinishDate(startDate, eachDetail.getDurationTotal());
					} else {
						// el inicio de la actual es al finalizar la ultima
						startDate = finishDate;
						finishDate = ProductionDateTimeHelper.getFinishDate(startDate, eachDetail.getDurationTotal());
					}
					eachDetail.setDateStart(startDate);
					eachDetail.setDateFinish(finishDate);
				} else {
					eachDetail.setDateStart(null);
					eachDetail.setDateFinish(null);
					// por las dudas borramos tambien las fechas reales
					eachDetail.setDateStartReal(null);
					eachDetail.setDateFinishReal(null);
				}
			}
		}
	}

	public void sortListBySequence(List<ProductionOrderDetail> details) {
		Comparator<ProductionOrderDetail> comp = new Comparator<ProductionOrderDetail>() {
			@Override
			public int compare(ProductionOrderDetail a, ProductionOrderDetail b) {
				return a.getProcess().getType().getSequence().compareTo(b.getProcess().getType().getSequence());
			}
		};
		Collections.sort(details, comp);
	}

	private Map<Worker, List<ProductionOrderDetail>> getWorkerMap(List<ProductionOrderDetail> detailList) {
		// devuelve un map que por cada empleado contiene el listado de todos los procesos asignados a el
		Map<Worker, List<ProductionOrderDetail>> workerMap = new LinkedHashMap<Worker, List<ProductionOrderDetail>>();
		for(ProductionOrderDetail each : detailList) {
			if(each.getState() != ProcessState.Cancelado) {
				Worker worker = each.getWorker();
				worker = workerRepository.findOne(worker.getId());
				List<ProductionOrderDetail> list = workerMap.get(worker);
				if(list == null) {
					list = new ArrayList<ProductionOrderDetail>();
					list.add(each);
				} else {
					list.add(each);
				}
				workerMap.put(worker, list);
			}
		}
		return workerMap;
	}

	private void recalculateDates() {
		// recalcula las fechas de los procesos basandose en los empleados asignados de tal forma que los procesos que tengan distintos empleados asignados se ejecuten en paralelo
		// si varios empleados estan asignados a diferentes piezas del mismo proceso, todos iniciaran al mismo tiempo, y el proximo proceso iniciara al finalizar el ultimo de los procesos anteriores
		//por cada tipo de proceso hacemos un map con todos los empleados asignados, y por cada empleado, una lista con todos los detalles en los q se le asigno
		Date startDateProcess = null;//inicio proceso
		Date finishDateProcess = null;//fin proceso
		for(ProcessType eachProcessType : getProcessTypeList()) {
			if(startDateProcess == null) {//la primera vez
				startDateProcess = getDateTimeStartWork(productionOrderStartDatebox.getValue());
			} else {//al iniciar otro proceso la fecha de inicio es la fecha fin del anterior
				startDateProcess = finishDateProcess;
			}
			for(Map.Entry<Worker, List<ProductionOrderDetail>> entry : getWorkerMapByProcessType(eachProcessType).entrySet()) {
				Date startDate = startDateProcess;
				Date finishDate = null;
				Worker worker = entry.getKey();
				List<ProductionOrderDetail> list = entry.getValue();
				for(ProductionOrderDetail eachDetail : list) {
					if(finishDate == null) {// si es la primera vez que ingresa
						finishDate = ProductionDateTimeHelper.getFinishDate(startDateProcess, eachDetail.getDurationTotal());
					} else {
						// el inicio de la actual es al finalizar la ultima
						startDate = finishDate;
						finishDate = ProductionDateTimeHelper.getFinishDate(startDate, eachDetail.getDurationTotal());
					}
					eachDetail.setDateStart(startDate);
					eachDetail.setDateFinish(finishDate);
					if(finishDateProcess==null || finishDate.after(finishDateProcess)) {//guardamos la ultima fecha de proceso q aparece
						finishDateProcess = finishDate;
					}
				}
			}
		}
	}

	private ProductionOrderDetail getPreviousDetail(ProductionOrderDetail data) {
		ProductionOrderDetail prevDetail = null;
		int index = getIndex(data);
		if(index > 0) {
			prevDetail = productionOrderDetailList.get(index - 1);
		}
		return prevDetail;
	}

	private int getIndex(ProductionOrderDetail data) {
		for(int i=0; i<productionOrderDetailList.size(); i++) {
			if(productionOrderDetailList.get(i).getId() == data.getId()) {
				return i;
			}
		}
		return -1;
	}

	private ProductionOrderDetail getFirstDetailOfProcess(ProductionOrderDetail data) {
		// busca el primer detalle que sea del mismo proceso
		ProductionOrderDetail currentDetail = null;
		ProductionOrderDetail prevDetail = getPreviousDetail(data);
		while(prevDetail!=null && prevDetail.getProcess().getType().equals(data.getProcess().getType())) {
			currentDetail = prevDetail;
			prevDetail = getPreviousDetail(prevDetail);
		}
		return currentDetail;
	}

	/*
	private void sortProcessTypeListBySequence() {
		Comparator<ProcessType> comp = new Comparator<ProcessType>() {
			@Override
			public int compare(ProcessType a, ProcessType b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(processTypeList, comp);
	}
	 */

	@Listen("onClick = #autoAssignButton")
	public void autoAssignButtonClick() {
		// si no estan asignadas las fechas de los procesos se informa
		if(productionOrderStartDatebox.getValue()==null || productionOrderFinishDatebox.getValue()==null) {
			alert("Debe seleccionar las fecha de inicio antes de asignar recursos.");
			return;
		}
		// asigna la primer maquina o trabajador disponible a cada detalle
		Worker worker = null;
		Machine machine = null;
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() == ProcessState.Cancelado) {
				each.setWorker(null);
				each.setMachine(null);
			} else {// si el detalle no es cancelado
				List<Worker> availableWorkerList = new ArrayList<Worker>();
				for(Worker eachWorker : workerRepository.findAll()) {
					if(isWorkerAvailable(eachWorker, each)) {
						availableWorkerList.add(eachWorker);
					}
				}
				if(availableWorkerList.size() > 0) {
					worker = availableWorkerList.get(0);
				}
				each.setWorker(worker);
				// recorre la lista de maquinas y solo agrega las que estan disponibles
				List<Machine> availableMachineList = new ArrayList<Machine>();
				for(Machine eachMachine : getMachineListByProcessType(each.getProcess().getType())) {
					if(isMachineAvailable(eachMachine, each)) {
						availableMachineList.add(eachMachine);
					}
				}
				//getMachineListByProcessType(each.getProcess().getType());
				if(availableMachineList.size() > 0) {
					machine = availableMachineList.get(0);
				}
				each.setMachine(machine);
			}
		}
		refreshProductionOrderDetailGridView();
	}

	@Listen("onChange = #productionOrderStartDatebox")
	public void doChangeProductionOrderStartDatebox() {
		refreshProcessDates();
	}

	private void refreshProcessDates() {
		Date startDate = getDateTimeStartWork(productionOrderStartDatebox.getValue());
		// asigna el valor a la clase y hace que calcule todos los tiempos de los detalles
		currentProductionOrder.setDateStart(startDate);
		currentProductionOrder.updateDetailDates(startDate);
		productionOrderDetailList = currentProductionOrder.getDetails();
		sortProductionOrderDetailListByProcessTypeSequence();
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());// modifica el valor del datebox para que contenga la hora de inicio
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
	}

	private Date getDateTimeStartWork(Date startDate) {
		// devuelve la union entre la fecha del datebox y la hora de inicio del dia
		if(startDate != null) {
			Calendar calStartDate = Calendar.getInstance();
			calStartDate.setTime(startDate);
			calStartDate.set(Calendar.HOUR_OF_DAY, ProductionDateTimeHelper.getFirstHourOfDay());
			calStartDate.set(Calendar.MINUTE, ProductionDateTimeHelper.getFirstMinuteOfDay());
			calStartDate.set(Calendar.SECOND, 0);
			return calStartDate.getTime();
		}
		return null;
	}

	private void sortProductionOrderDetailListByProcessTypeSequence() {
		Comparator<ProductionOrderDetail> comp = new Comparator<ProductionOrderDetail>() {
			@Override
			public int compare(ProductionOrderDetail a, ProductionOrderDetail b) {
				return a.getProcess().getType().getSequence().compareTo(b.getProcess().getType().getSequence());
			}
		};
		Collections.sort(productionOrderDetailList, comp);
	}

	public boolean isStateCancel(ProcessState processState) {
		return processState == ProcessState.Cancelado;
	}

	@Listen("onEditProductionOrderDetailIsCanceled = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsCanceled(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		if(element.isChecked()) {
			data.setState(ProcessState.Cancelado);
		} else {
			data.setState(ProcessState.Pendiente);
		}
		refreshProcessDates();
		autoAssignButtonClick();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onChangeProductionOrderDetailStartDate = #productionOrderDetailGrid")
	public void doChangeProductionOrderDetailStartDate(ForwardEvent evt) {
		final ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		final Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		// se verifica que la hora este dentro del horario de trabajo
		if(ProductionDateTimeHelper.isOutsideWorkingHours(element.getValue())) {
			alert("Error en la Hora. Debe seleccionar un horario entre las " + ProductionDateTimeHelper.getFormattedFirst() + " hs y las " + ProductionDateTimeHelper.getFormattedLast() + " hs");
			// se regresa al valor original
			element.setValue(data.getDateStart());
			return;
		}
		// se envia el mensaje: se modificaran tiempos de los procesos posteriores para que no se superpongan
		Messagebox.show("Los tiempos posteriores se modificaran, y los recursos asignados se removeran, desea continuar?", "Confirmar Modificacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					changeProductionOrderDetailDates(data, element);
				} else {
					// si se cancela se regresa al valor original
					element.setValue(data.getDateStart());
				}
			}
		});
	}

	private void changeProductionOrderDetailDates(ProductionOrderDetail productionOrderDetail, Datebox datebox) {
		changeDetailStartDate(productionOrderDetail, datebox);
		// recalcula todos los tiempos de los procesos posteriores para que inicien en el final
		changeRemainDetailsDate(productionOrderDetail);
		// se hace refresh de la lista para q aparezcan los cambios
		refreshProductionOrderDetailGridView();
		// se actualizan los datebox de inicio y fin
		productionOrderStartDatebox.setValue(productionOrderDetail.getProductionOrder().getStartDateFromDetails());
		productionOrderFinishDatebox.setValue(productionOrderDetail.getProductionOrder().getFinishDateFromDetails());
	}

	private void changeDetailStartDate(ProductionOrderDetail detail, Datebox dateboxStart) {
		detail.setDateStart(dateboxStart.getValue());
		// cambia la fecha de fin tambien
		Date finish = ProductionDateTimeHelper.getFinishDate(dateboxStart.getValue(), detail.getDurationTotal());
		detail.setDateFinish(finish);
		// se remueven las maquinas y empleados asignados, ya que puede ser que esten no disponibles para el nuevo horario
		detail.setWorker(null);
		detail.setMachine(null);
		// cambia el valor del elemento con fecha fin
		//dateboxFinishSetValue(dateboxStart, finish); en lugar de cambiar el elemento se hace refresh de la lista
	}

	private void changeRemainDetailsDate(ProductionOrderDetail detail) {
		ProductionOrder productionOrder = detail.getProductionOrder();
		productionOrder.updateRemainDetailDates(detail);
	}

	@Listen("onClick = #createReportButton")
	public void reportButtonClick() {
		loadJasperreport();
	}

	private void loadJasperreport() {
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("reportTitle", "Orden de Produccion");
		parameters.put("productionPlanName", currentProductionPlan.getName());
		parameters.put("productionOrderNumber", currentProductionOrder.getNumber());
		parameters.put("productName", currentProductionOrder.getProduct().getDescription());

		Executions.getCurrent().setAttribute("jr_datasource", new ProductionOrderReportDataSource(productionOrderDetailList));
		Executions.getCurrent().setAttribute("return_page_name", "production_order_creation");
		Map<String, Object> returnParameters = new HashMap<String, Object>();
		returnParameters.put("selected_production_order", currentProductionOrder);
		returnParameters.put("selected_production_plan", currentProductionPlan);
		Executions.getCurrent().setAttribute("return_parameters", returnParameters);
		Executions.getCurrent().setAttribute("report_src_name", "production_order");
		Executions.getCurrent().setAttribute("report_parameters", parameters);
		Window window = (Window)Executions.createComponents("/report_selection_modal.zul", null, null);
		window.doModal();
		/*reportBlockJasperreport.setSrc("/jasperreport/production_order.jasper");
		reportBlockJasperreport.setType(type);
		reportBlockJasperreport.setDatasource(new ProductionOrderReportDataSource(productionOrderDetailList));*/
	}
}

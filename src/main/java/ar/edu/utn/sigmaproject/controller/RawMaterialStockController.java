package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RawMaterialStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox woodListbox;
	@Wire
	Grid woodCreationGrid;
	@Wire
	Combobox rawMaterialTypeCombobox;
	@Wire
	Combobox woodTypeCombobox;
	@Wire
	Doublebox stockDoublebox;
	@Wire
	Doublebox stockMinDoublebox;
	@Wire
	Doublebox stockRepoDoublebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button newButton;
	@Wire
	Listbox woodReservedListbox;

	// services
	@WireVariable
	private RawMaterialTypeRepository rawMaterialTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private WoodReservedRepository woodReservedRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	private Wood currentWood;

	// list
	private List<RawMaterialType> rawMaterialTypeList;
	private List<Wood> woodList;
	private List<WoodType> woodTypeList;
	private List<WoodReserved> woodReservedList;

	// list models
	private ListModelList<RawMaterialType> rawMaterialTypeListModel;
	private ListModelList<Wood> woodListModel;
	private ListModelList<WoodType> woodTypeListModel;
	private ListModelList<WoodReserved> woodReservedListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<>(rawMaterialTypeList);
		rawMaterialTypeCombobox.setModel(rawMaterialTypeListModel);
		woodTypeList = woodTypeRepository.findAll();
		woodTypeListModel = new ListModelList<>(woodTypeList);
		woodTypeCombobox.setModel(woodTypeListModel);
		woodList = woodRepository.findAll();
		woodListModel = new ListModelList<>(woodList);
		woodListbox.setModel(woodListModel);
		woodReservedList = woodReservedRepository.findAll();
		woodReservedListModel = new ListModelList<>(woodReservedList);
		woodReservedListbox.setModel(woodReservedListModel);
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onSelect = #woodListbox")
	public void doWoodListBoxSelect() {
		if(woodListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentWood = null;
		} else {
			if(currentWood == null) {// si no hay nada editandose
				currentWood = woodListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		woodListModel.clearSelection();
	}

	private void refreshView() {
		woodListModel.clearSelection();
		woodListbox.setModel(woodListModel);// se actualiza la lista limpiar la seleccion
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentWood == null) {// nuevo
			woodCreationGrid.setVisible(false);
			woodTypeListModel.addToSelection(woodTypeRepository.findFirstByName("Pino"));
			woodTypeCombobox.setModel(woodTypeListModel);
			rawMaterialTypeCombobox.setSelectedIndex(-1);
			stockDoublebox.setValue(0.0);
			stockMinDoublebox.setValue(0.0);
			stockRepoDoublebox.setValue(0.0);
			woodTypeCombobox.setDisabled(false);
			rawMaterialTypeCombobox.setDisabled(false);
			stockDoublebox.setDisabled(false);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			resetButton.setDisabled(true);
		}else {// editar
			woodCreationGrid.setVisible(true);
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(currentWood.getWoodType()));
			RawMaterialType currentRawMaterialType = currentWood.getRawMaterialType();
			rawMaterialTypeCombobox.setSelectedIndex(rawMaterialTypeListModel.indexOf(currentRawMaterialType));
			stockDoublebox.setValue(currentWood.getStock().doubleValue());
			stockMinDoublebox.setValue(currentWood.getStockMin().doubleValue());
			stockRepoDoublebox.setValue(currentWood.getStockRepo().doubleValue());
			woodTypeCombobox.setDisabled(true);
			rawMaterialTypeCombobox.setDisabled(true);
			stockDoublebox.setDisabled(true);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentWood = null;
		refreshView();
		woodCreationGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(rawMaterialTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Materia Prima", rawMaterialTypeCombobox);
			return;
		}
		if(woodTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Madera", woodTypeCombobox);
			return;
		}
		RawMaterialType rawMaterialType = rawMaterialTypeCombobox.getSelectedItem().getValue();
		WoodType woodType = woodTypeCombobox.getSelectedItem().getValue();
		BigDecimal stock = BigDecimal.valueOf(stockDoublebox.getValue());
		BigDecimal stockMin = BigDecimal.valueOf(stockMinDoublebox.getValue());
		BigDecimal stockRepo = BigDecimal.valueOf(stockRepoDoublebox.getValue());
		if(currentWood == null) {// nuevo
			currentWood = new Wood(rawMaterialType, woodType, stock, stockMin, stockRepo);
		} else {// edicion
			currentWood.setRawMaterialType(rawMaterialType);
			currentWood.setWoodType(woodType);
			currentWood.setStock(stock);
			currentWood.setStockRepo(stockRepo);
			currentWood.setStockMin(stockMin);
		}
		currentWood = woodRepository.save(currentWood);
		woodList = woodRepository.findAll();
		woodListModel = new ListModelList<>(woodList);
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}
	
	public String getProductionPlanName(WoodReserved woodReserved) {
		if(woodReserved == null) {
			return "";
		} else {
			RawMaterialRequirement rawMaterialRequirement = woodReserved.getRawMaterialRequirement();
			if(rawMaterialRequirement != null) {
				ProductionPlan productionPlan = productionPlanRepository.findByRawMaterialRequirements(rawMaterialRequirement);
				if(productionPlan != null) {
					return productionPlan.getName();
				} else {
					return "";
				}
			} else {
				return "";
			}
		}
	}
	
	public Wood getWood(WoodReserved woodReserved) {
		return woodRepository.findFirstByWoodsReserved(woodReserved);
	}
	/*
	public BigDecimal getRawMaterialStockReserved(Wood supplyType) {
		List<SupplyReserved> supplyReservedTotal = supplyReservedRepository.findBySupplyRequirementSupplyType(supplyType);
		BigDecimal stockReservedTotal = BigDecimal.ZERO;
		for(SupplyReserved each : supplyReservedTotal) {
			if(!each.isWithdrawn()) {// suma todas las reservas del insumo que aun no han sido retiradas
				stockReservedTotal = stockReservedTotal.add(each.getStockReserved());
			}
		}
		return stockReservedTotal;
	}

	public BigDecimal getRawMaterialStockAvailable(Wood supplyType) {
		// devuelve la diferencia entre el stock total y el total reservado
		BigDecimal stockTotal = supplyType.getStock();
		BigDecimal stockReservedTotal = getRawMaterialStockReserved(supplyType);
		return stockTotal.subtract(stockReservedTotal);
	}*/
}

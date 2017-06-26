package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.MaterialReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;

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
	Textbox woodNameTextbox;
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

	@Wire("#included #materialsOrderDetailListbox")
	Listbox materialsOrderDetailListbox;

	// services
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private MaterialReservedRepository materialReservedRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	private Wood currentWood;

	// list
	private List<Wood> woodList;
	private List<WoodType> woodTypeList;
	private List<MaterialReserved> woodReservedList;

	// list models
	private ListModelList<Wood> woodListModel;
	private ListModelList<WoodType> woodTypeListModel;
	private ListModelList<MaterialReserved> woodReservedListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		woodTypeList = woodTypeRepository.findAll();
		woodTypeListModel = new ListModelList<>(woodTypeList);
		woodTypeCombobox.setModel(woodTypeListModel);
		woodList = woodRepository.findAll();
		woodListModel = new ListModelList<>(woodList);
		woodListbox.setModel(woodListModel);
		woodReservedList = materialReservedRepository.findAllByType(MaterialType.Wood);
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
			woodNameTextbox.setText("");
			stockDoublebox.setValue(0.0);
			stockMinDoublebox.setValue(0.0);
			stockRepoDoublebox.setValue(0.0);
			woodTypeCombobox.setDisabled(false);
			woodNameTextbox.setDisabled(false);
			stockDoublebox.setDisabled(false);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			resetButton.setDisabled(true);
		}else {// editar
			woodCreationGrid.setVisible(true);
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(currentWood.getWoodType()));
			woodNameTextbox.setText(currentWood.getName());
			stockDoublebox.setValue(currentWood.getStock().doubleValue());
			stockMinDoublebox.setValue(currentWood.getStockMin().doubleValue());
			stockRepoDoublebox.setValue(currentWood.getStockRepo().doubleValue());
			woodTypeCombobox.setDisabled(true);
			woodNameTextbox.setDisabled(true);
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
		if(woodTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Madera", woodTypeCombobox);
			return;
		}
		WoodType woodType = woodTypeCombobox.getSelectedItem().getValue();
		BigDecimal stock = BigDecimal.valueOf(stockDoublebox.getValue());
		BigDecimal stockMin = BigDecimal.valueOf(stockMinDoublebox.getValue());
		BigDecimal stockRepo = BigDecimal.valueOf(stockRepoDoublebox.getValue());
		currentWood.setWoodType(woodType);
		currentWood.setStock(stock);
		currentWood.setStockRepo(stockRepo);
		currentWood.setStockMin(stockMin);
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

	public String getProductionPlanName(MaterialReserved woodReserved) {
		if(woodReserved == null) {
			return "";
		} else {
			MaterialRequirement rawMaterialRequirement = woodReserved.getMaterialRequirement();
			if(rawMaterialRequirement != null) {
				ProductionPlan productionPlan = productionPlanRepository.findByMaterialRequirements(rawMaterialRequirement);
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

	@Listen("onSelectMaterialsOrder = #included #materialsOrderDetailListbox")
	public void doSelectMaterialsOrder(ForwardEvent evt) {
		MaterialsOrder materialsOrder = (MaterialsOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_materials_order", materialsOrder);
		Include include = (Include) Selectors.iterable(materialsOrderDetailListbox.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_creation.zul");
	}
}

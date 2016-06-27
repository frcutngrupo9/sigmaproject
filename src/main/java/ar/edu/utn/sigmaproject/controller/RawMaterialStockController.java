package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
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
	Textbox nameTextbox;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox measureTextbox;
	@Wire
	Doublebox stockDoublebox;
	@Wire
	Doublebox stockMinDoublebox;
	@Wire
	Doublebox stockRepoDoublebox;
	@Wire
	Button stockIncreaseButton;
	@Wire
	Button stockDecreaseButton;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button newButton;
	@Wire
	Grid stockModificationGrid;
	@Wire
	Doublebox quantityDoublebox;
	@Wire
	Label stockModificationLabel;
	@Wire
	Intbox numberIntbox;
	@Wire
	Combobox workerCombobox;
	@Wire
	Button saveNewStockButton;
	@Wire
	Button cancelNewStockButton;
	@Wire
	Button resetNewStockButton;

	// services
	@WireVariable
	private RawMaterialTypeRepository rawMaterialTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	// attributes
	private Wood currentWood;

	// list
	private List<RawMaterialType> rawMaterialTypeList;
	private List<Wood> woodList;
	private List<WoodType> woodTypeList;
	private List<Worker> workerList;

	// list models
	private ListModelList<RawMaterialType> rawMaterialTypeListModel;
	private ListModelList<Wood> woodListModel;
	private ListModelList<WoodType> woodTypeListModel;
	private ListModelList<Worker> workerListModel;

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
		currentWood = null;
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		workerCombobox.setModel(workerListModel);
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
		nameTextbox.setDisabled(true);
		measureTextbox.setDisabled(true);// no se deben poder modificar
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentWood == null) {// nuevo
			woodCreationGrid.setVisible(false);
			codeTextbox.setValue("");
			woodTypeListModel.addToSelection(woodTypeRepository.findByName("Pino"));
			woodTypeCombobox.setModel(woodTypeListModel);
			rawMaterialTypeCombobox.setSelectedIndex(-1);
			nameTextbox.setValue("(seleccionar Materia Prima)");
			measureTextbox.setValue("(seleccionar Materia Prima)");
			stockDoublebox.setValue(0.0);
			stockMinDoublebox.setValue(0.0);
			stockRepoDoublebox.setValue(0.0);
			codeTextbox.setDisabled(false);
			woodTypeCombobox.setDisabled(false);
			rawMaterialTypeCombobox.setDisabled(false);
			stockDoublebox.setDisabled(false);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			stockIncreaseButton.setDisabled(true);
			stockDecreaseButton.setDisabled(true);
			resetButton.setDisabled(true);
			stockModificationGrid.setVisible(false);
		}else {// editar
			woodCreationGrid.setVisible(true);
			codeTextbox.setValue(currentWood.getCode());
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(currentWood.getWoodType()));
			RawMaterialType currentRawMaterialType = currentWood.getRawMaterialType();
			rawMaterialTypeCombobox.setSelectedIndex(rawMaterialTypeListModel.indexOf(currentRawMaterialType));
			nameTextbox.setValue(currentRawMaterialType.getName());
			measureTextbox.setValue(getMeasureFormated(currentWood));
			stockDoublebox.setValue(currentWood.getStock().doubleValue());
			stockMinDoublebox.setValue(currentWood.getStockMin().doubleValue());
			stockRepoDoublebox.setValue(currentWood.getStockRepo().doubleValue());
			codeTextbox.setDisabled(true);
			woodTypeCombobox.setDisabled(true);
			rawMaterialTypeCombobox.setDisabled(true);
			stockDoublebox.setDisabled(true);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			stockIncreaseButton.setDisabled(false);
			stockDecreaseButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	public String getMeasureFormated(Wood wood) {
		RawMaterialType rawMaterialType = wood.getRawMaterialType();
		String lenght = "(L) " + rawMaterialType.getLength().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getLengthMeasureUnit());
		String depth = "(E) " + rawMaterialType.getDepth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getDepthMeasureUnit());
		String width = "(A) " + rawMaterialType.getWidth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getWidthMeasureUnit());
		return lenght + " x " + depth + " x " + width;
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
		String code = codeTextbox.getText();
		RawMaterialType rawMaterialType = rawMaterialTypeCombobox.getSelectedItem().getValue();
		WoodType woodType = woodTypeCombobox.getSelectedItem().getValue();
		BigDecimal stock = BigDecimal.valueOf(stockDoublebox.getValue());
		BigDecimal stockMin = BigDecimal.valueOf(stockMinDoublebox.getValue());
		BigDecimal stockRepo = BigDecimal.valueOf(stockRepoDoublebox.getValue());
		if(currentWood == null) {// nuevo
			currentWood = new Wood(rawMaterialType, woodType, code, stock, stockMin, stockRepo);
		} else {// edicion
			currentWood.setCode(code);
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

	@Listen("onClick = #stockIncreaseButton")
	public void stockIncreaseButtonClick() {
		stockModificationGrid.setVisible(true);
		stockModificationLabel.setValue("Ingreso Stock");
		workerCombobox.setSelectedIndex(-1);
		numberIntbox.setValue(null);
		quantityDoublebox.setValue(null);
	}

	@Listen("onClick = #saveNewStockButton")
	public void saveNewStockButton() {
		if(numberIntbox.getValue() <= 0) {
			Clients.showNotification("Debe ingresar un numero", numberIntbox);
			return;
		}
		if(quantityDoublebox.getValue() <= 0) {
			Clients.showNotification("Debe ingresar una cantidad", quantityDoublebox);
			return;
		}
		if(workerCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar un empleado", workerCombobox);
			return;
		}
		if(currentWood != null) {
			BigDecimal newStock = BigDecimal.ZERO;
			BigDecimal quantity = BigDecimal.valueOf(quantityDoublebox.doubleValue());
			if(stockModificationLabel.getValue().equals("Ingreso Stock")) {
				newStock = currentWood.getStock().add(quantity);
			} else {
				if(currentWood.getStock().compareTo(quantity) > 0) {// hay suficiente stock
					newStock = currentWood.getStock().subtract(quantity);
				} else {
					Clients.showNotification("No hay stock suficiente", quantityDoublebox);
					return;
				}
			}
			stockDoublebox.setValue(newStock.doubleValue());
			//			currentWood.setStock(newStock);
			//			currentWood = woodRepository.updateWood(currentWood);
			//			refreshView();
		}
		stockModificationGrid.setVisible(false);
	}

	@Listen("onClick = #cancelNewStockButton")
	public void cancelNewStockButtonClick() {
		stockModificationGrid.setVisible(false);
	}

	@Listen("onClick = #resetNewStockButton")
	public void resetNewStockButtonClick() {
		workerCombobox.setSelectedIndex(-1);
		numberIntbox.setValue(null);
		quantityDoublebox.setValue(null);
	}

	@Listen("onClick = #stockDecreaseButton")
	public void stockDecreaseButtonClick() {
		stockModificationGrid.setVisible(true);
		stockModificationLabel.setValue("Egreso Stock");
		workerCombobox.setSelectedIndex(-1);
		numberIntbox.setValue(null);
		quantityDoublebox.setValue(null);
	}

	@Listen("onSelect = #rawMaterialTypeCombobox")
	public void rawMaterialTypeComboboxSelect() {
		if(rawMaterialTypeCombobox.getSelectedItem() == null) {
			nameTextbox.setValue("(seleccionar Materia Prima)");
			measureTextbox.setValue("(seleccionar Materia Prima)");
		} else {
			RawMaterialType rawMaterialType = (RawMaterialType)rawMaterialTypeCombobox.getSelectedItem().getValue();
			nameTextbox.setValue(rawMaterialType.getName());
			String lenght = "(L) " + rawMaterialType.getLength().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getLengthMeasureUnit());
			String depth = "(E) " + rawMaterialType.getDepth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getDepthMeasureUnit());
			String width = "(A) " + rawMaterialType.getWidth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getWidthMeasureUnit());
			measureTextbox.setValue(lenght + " x " + depth + " x " + width);
		}
	}
}

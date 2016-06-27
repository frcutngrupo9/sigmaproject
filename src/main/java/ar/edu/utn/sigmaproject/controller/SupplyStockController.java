package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
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

import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Worker;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SupplyStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox supplyTypeListbox;
	@Wire
	Grid supplyTypeExistenceGrid;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox nameTextbox;
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
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	// attributes
	private SupplyType currentSupplyType;

	// list
	private List<SupplyType> supplyTypeList;
	private List<Worker> workerList;

	// list models
	private ListModelList<SupplyType> supplyTypeListModel;
	private ListModelList<Worker> workerListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		supplyTypeList = supplyTypeRepository.findAll();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		supplyTypeListbox.setModel(supplyTypeListModel);
		currentSupplyType = null;
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<Worker>(workerList);
		workerCombobox.setModel(workerListModel);
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onSelect = #supplyTypeListbox")
	public void doListBoxSelect() {
		if(supplyTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentSupplyType = null;
		} else {
			if(currentSupplyType == null) {// si no hay nada editandose
				currentSupplyType = supplyTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		supplyTypeListModel.clearSelection();
	}

	private void refreshView() {
		supplyTypeListModel.clearSelection();
		supplyTypeListbox.setModel(supplyTypeListModel);// se actualiza la lista
		codeTextbox.setDisabled(true);// no se deben poder modificar
		nameTextbox.setDisabled(true);
		measureTextbox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		if(currentSupplyType == null) {// no editando
			supplyTypeExistenceGrid.setVisible(false);
			codeTextbox.setValue(null);
			nameTextbox.setValue(null);
			measureTextbox.setValue(null);
			stockDoublebox.setValue(null);
			stockMinDoublebox.setValue(null);
			stockRepoDoublebox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
		}else {// editando
			supplyTypeExistenceGrid.setVisible(true);
			codeTextbox.setValue(currentSupplyType.getCode());
			nameTextbox.setValue(currentSupplyType.getDescription());
			measureTextbox.setValue(currentSupplyType.getMeasure());
			stockDoublebox.setValue(currentSupplyType.getStock().doubleValue());
			stockMinDoublebox.setValue(currentSupplyType.getStockMin().doubleValue());
			stockRepoDoublebox.setValue(currentSupplyType.getStockRepo().doubleValue());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		currentSupplyType.setStock(BigDecimal.valueOf(stockDoublebox.getValue()));
		currentSupplyType.setStockMin(BigDecimal.valueOf(stockMinDoublebox.getValue()));
		currentSupplyType.setStockRepo(BigDecimal.valueOf(stockRepoDoublebox.getValue()));
		// es siempre una edicion ya que debe existir para poder modificar su stock
		currentSupplyType = supplyTypeRepository.save(currentSupplyType);
		supplyTypeList = supplyTypeRepository.findAll();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		currentSupplyType = null;
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

	@Listen("onClick = #stockDecreaseButton")
	public void stockDecreaseButtonClick() {
		stockModificationGrid.setVisible(true);
		stockModificationLabel.setValue("Egreso Stock");
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
		if(currentSupplyType != null) {
			BigDecimal newStock;
			BigDecimal quantity = BigDecimal.valueOf(quantityDoublebox.getValue());
			if(stockModificationLabel.getValue().equals("Ingreso Stock")) {
				newStock = currentSupplyType.getStock().add(quantity);
			} else {
				if(currentSupplyType.getStock().compareTo(quantity) > 0) {// hay suficiente stock
					newStock = currentSupplyType.getStock().subtract(quantity);
				} else {
					Clients.showNotification("No hay stock suficiente", quantityDoublebox);
					return;
				}
			}
			stockDoublebox.setValue(newStock.doubleValue());
			//			currentWood.setStock(newStock);
			//			currentWood = woodService.updateWood(currentWood);
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
}

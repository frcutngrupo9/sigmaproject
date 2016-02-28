package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.impl.SupplyTypeServiceImpl;

public class SupplyStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox supplyTypeListbox;
	@Wire
	Grid supplyTypeExistenceGrid;
	@Wire
	Textbox codeTextBox;
	@Wire
	Textbox nameTextBox;
	@Wire
	Textbox measureTextBox;
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

	// services
	private SupplyTypeService supplyTypeService = new SupplyTypeServiceImpl();

	// attributes
	private SupplyType currentSupplyType;

	// list
	private List<SupplyType> supplyTypeList;

	// list models
	private ListModelList<SupplyType> supplyTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		supplyTypeList = supplyTypeService.getSupplyTypeList();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		supplyTypeListbox.setModel(supplyTypeListModel);
		currentSupplyType = null;
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
				currentSupplyType = supplyTypeListModel.getSelection().iterator().next();
				refreshView();
			}
		}
		supplyTypeListModel.clearSelection();
	}

	private void refreshView() {
		supplyTypeListModel.clearSelection();
		supplyTypeListbox.setModel(supplyTypeListModel);// se actualiza la lista
		codeTextBox.setDisabled(true);// no se deben poder modificar
		nameTextBox.setDisabled(true);
		measureTextBox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		if(currentSupplyType == null) {// no editando
			supplyTypeExistenceGrid.setVisible(false);
			codeTextBox.setValue(null);
			nameTextBox.setValue(null);
			measureTextBox.setValue(null);
			stockDoublebox.setValue(null);
			stockMinDoublebox.setValue(null);
			stockRepoDoublebox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
		}else {// editando
			supplyTypeExistenceGrid.setVisible(true);
			codeTextBox.setValue(currentSupplyType.getCode());
			nameTextBox.setValue(currentSupplyType.getDescription());
			measureTextBox.setValue(currentSupplyType.getMeasure());
			stockDoublebox.setValue(currentSupplyType.getStock());
			stockMinDoublebox.setValue(currentSupplyType.getStockMin());
			stockRepoDoublebox.setValue(currentSupplyType.getStockRepo());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		currentSupplyType.setStock(stockDoublebox.getValue());
		currentSupplyType.setStockMin(stockMinDoublebox.getValue());
		currentSupplyType.setStockRepo(stockRepoDoublebox.getValue());
		// es siempre una edicion ya que debe existir para poder modificar su stock
		currentSupplyType = supplyTypeService.updateSupplyType(currentSupplyType);
		supplyTypeList = supplyTypeService.getSupplyTypeList();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		currentSupplyType = null;
		refreshView();
	}
}

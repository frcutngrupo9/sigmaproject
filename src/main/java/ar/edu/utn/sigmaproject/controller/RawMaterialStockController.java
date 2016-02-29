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

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;

public class RawMaterialStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox rawMaterialTypeListbox;
	@Wire
	Grid rawMaterialTypeExistenceGrid;
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
	Button stockIncreaseButton;
	@Wire
	Button stockDecreaseButton;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;

	// services
	private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();

	// attributes
	private RawMaterialType currentRawMaterialType;

	// list
	private List<RawMaterialType> rawMaterialTypeList;

	// list models
	private ListModelList<RawMaterialType> rawMaterialTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
		rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);
		currentRawMaterialType = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentRawMaterialType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onSelect = #supplyTypeListbox")
	public void doListBoxSelect() {
		if(rawMaterialTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentRawMaterialType = null;
		} else {
			if(currentRawMaterialType == null) {// si no hay nada editandose
				currentRawMaterialType = rawMaterialTypeListModel.getSelection().iterator().next();
				refreshView();
			}
		}
		rawMaterialTypeListModel.clearSelection();
	}

	private void refreshView() {
		rawMaterialTypeListModel.clearSelection();
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);// se actualiza la lista
		codeTextBox.setDisabled(true);// no se deben poder modificar
		nameTextBox.setDisabled(true);
		measureTextBox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		if(currentRawMaterialType == null) {// no editando
			rawMaterialTypeExistenceGrid.setVisible(false);
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
			rawMaterialTypeExistenceGrid.setVisible(true);
//			codeTextBox.setValue(currentRawMaterialType.getCode());
//			nameTextBox.setValue(currentRawMaterialType.getDescription());
//			measureTextBox.setValue(currentRawMaterialType.getMeasure());
//			stockDoublebox.setValue(currentRawMaterialType.getStock());
//			stockMinDoublebox.setValue(currentRawMaterialType.getStockMin());
//			stockRepoDoublebox.setValue(currentRawMaterialType.getStockRepo());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
//		currentRawMaterialType.setStock(stockDoublebox.getValue());
//		currentRawMaterialType.setStockMin(stockMinDoublebox.getValue());
//		currentRawMaterialType.setStockRepo(stockRepoDoublebox.getValue());
//		// es siempre una edicion ya que debe existir para poder modificar su stock
//		currentRawMaterialType = supplyTypeService.updateSupplyType(currentRawMaterialType);
//		supplyTypeList = supplyTypeService.getSupplyTypeList();
//		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
//		currentRawMaterialType = null;
		refreshView();
	}
	
	@Listen("onClick = #stockIncreaseButton")
	public void stockIncreaseButtonClick() {
		// TODO
	}
	
	@Listen("onClick = #stockDecreaseButton")
	public void stockDecreaseButtonClick() {
		// TODO
	}
}

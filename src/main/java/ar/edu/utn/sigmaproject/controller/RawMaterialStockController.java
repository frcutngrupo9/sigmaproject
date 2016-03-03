package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.WoodService;
import ar.edu.utn.sigmaproject.service.WoodTypeService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WoodServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WoodTypeServiceImpl;

public class RawMaterialStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox woodListbox;
	@Wire
	Grid woodCreationGrid;
	@Wire
	Selectbox rawMaterialTypeSelectbox;
	@Wire
	Selectbox woodTypeSelectbox;
	@Wire
	Textbox nameTextBox;
	@Wire
	Textbox codeTextBox;
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
	@Wire
	Button newButton;

	// services
	private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	private WoodService woodService = new WoodServiceImpl();
	private WoodTypeService woodTypeService = new WoodTypeServiceImpl();

	// attributes
	private Wood currentWood;

	// list
	private List<RawMaterialType> rawMaterialTypeList;
	private List<Wood> woodList;
	private List<WoodType> woodTypeList;

	// list models
	private ListModelList<RawMaterialType> rawMaterialTypeListModel;
	private ListModelList<Wood> woodListModel;
	private ListModelList<WoodType> woodTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
		rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
		rawMaterialTypeSelectbox.setModel(rawMaterialTypeListModel);
		woodTypeList = woodTypeService.getWoodTypeList();
		woodTypeListModel = new ListModelList<WoodType>(woodTypeList);
		woodTypeSelectbox.setModel(woodTypeListModel);
		woodList = woodService.getWoodList();
		woodListModel = new ListModelList<Wood>(woodList);
		woodListbox.setModel(woodListModel);
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
		nameTextBox.setDisabled(true);
		measureTextBox.setDisabled(true);// no se deben poder modificar
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		if(currentWood == null) {// nuevo
			woodCreationGrid.setVisible(false);
			codeTextBox.setValue("");
			woodTypeSelectbox.setSelectedIndex(-1);
			rawMaterialTypeSelectbox.setSelectedIndex(-1);
			nameTextBox.setValue("(seleccionar Materia Prima)");
			measureTextBox.setValue("(seleccionar Materia Prima)");
			stockDoublebox.setValue(0.0);
			stockMinDoublebox.setValue(0.0);
			stockRepoDoublebox.setValue(0.0);
			codeTextBox.setDisabled(false);
			woodTypeSelectbox.setDisabled(false);
			rawMaterialTypeSelectbox.setDisabled(false);
			stockDoublebox.setDisabled(false);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			stockIncreaseButton.setDisabled(true);
			stockDecreaseButton.setDisabled(true);
			resetButton.setDisabled(true);
			newButton.setDisabled(false);
		}else {// editar
			woodCreationGrid.setVisible(true);
			codeTextBox.setValue(currentWood.getCode());
			WoodType auxWoodType = woodTypeService.getWoodType(currentWood.getIdWoodType());
			woodTypeSelectbox.setSelectedIndex(woodTypeListModel.indexOf(auxWoodType));
			RawMaterialType currentRawMaterialType = rawMaterialTypeService.getRawMaterialType(currentWood.getIdRawMaterialType());
			rawMaterialTypeSelectbox.setSelectedIndex(rawMaterialTypeListModel.indexOf(currentRawMaterialType));
			nameTextBox.setValue(currentRawMaterialType.getName());
			measureTextBox.setValue(getMeasureFormated(currentWood));
			stockDoublebox.setValue(currentWood.getStock());
			stockMinDoublebox.setValue(currentWood.getStockMin());
			stockRepoDoublebox.setValue(currentWood.getStockRepo());
			codeTextBox.setDisabled(true);
			woodTypeSelectbox.setDisabled(true);
			rawMaterialTypeSelectbox.setDisabled(true);
			stockDoublebox.setDisabled(true);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			stockIncreaseButton.setDisabled(false);
			stockDecreaseButton.setDisabled(false);
			resetButton.setDisabled(false);
			newButton.setDisabled(true);
		}
	}

	public String getMeasureFormated(Wood wood) {
		RawMaterialType rawMaterialType = getRawMaterialType(wood);
		String lenght = "(L) " + rawMaterialType.getLength().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getLengthIdMeasureUnit());
		String depth = "(E) " + rawMaterialType.getDepth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getDepthIdMeasureUnit());
		String width = "(A) " + rawMaterialType.getWidth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getWidthIdMeasureUnit());
		return lenght + " x " + depth + " x " + width;
	}

	public RawMaterialType getRawMaterialType(Wood wood) {
		return rawMaterialTypeService.getRawMaterialType(wood.getIdRawMaterialType());
	}

	public WoodType getWoodType(Wood wood) {
		return woodTypeService.getWoodType(wood.getIdWoodType());
	}

	public String getMeasureUnitName(int idMeasureUnit) {
		if(measureUnitService.getMeasureUnit(idMeasureUnit) != null) {
			return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
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
		if(rawMaterialTypeSelectbox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Materia Prima", rawMaterialTypeSelectbox);
			return;
		}
		if(woodTypeSelectbox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Madera", woodTypeSelectbox);
			return;
		}
		String code = codeTextBox.getText();
		Integer idRawMaterialType = rawMaterialTypeListModel.getElementAt(rawMaterialTypeSelectbox.getSelectedIndex()).getId();
		Integer idWoodType = woodTypeListModel.getElementAt(woodTypeSelectbox.getSelectedIndex()).getId();
		Double stock = stockDoublebox.getValue();
		Double stockMin = stockMinDoublebox.getValue();
		Double stockRepo = stockRepoDoublebox.getValue();
		if(currentWood == null) {// nuevo
			currentWood = new Wood(null, idRawMaterialType, idWoodType, code, stock, stockMin, stockRepo);
			currentWood = woodService.saveWood(currentWood);
		} else {// edicion
			currentWood.setCode(code);
			currentWood.setIdRawMaterialType(idRawMaterialType);
			currentWood.setIdWoodType(idWoodType);
			currentWood.setStock(stock);
			currentWood.setStockRepo(stockRepo);
			currentWood.setStockMin(stockMin);
			currentWood = woodService.updateWood(currentWood);
		}
		woodList = woodService.getWoodList();
		woodListModel = new ListModelList<Wood>(woodList);
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
		// TODO
	}

	@Listen("onClick = #stockDecreaseButton")
	public void stockDecreaseButtonClick() {
		// TODO
	}
}

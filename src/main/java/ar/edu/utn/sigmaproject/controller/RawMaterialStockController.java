package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.WoodService;
import ar.edu.utn.sigmaproject.service.WoodTypeService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WoodServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WoodTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

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
	private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	private WoodService woodService = new WoodServiceImpl();
	private WoodTypeService woodTypeService = new WoodTypeServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();

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
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
		rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
		rawMaterialTypeCombobox.setModel(rawMaterialTypeListModel);
		woodTypeList = woodTypeService.getWoodTypeList();
		woodTypeListModel = new ListModelList<WoodType>(woodTypeList);
		woodTypeCombobox.setModel(woodTypeListModel);
		woodList = woodService.getWoodList();
		woodListModel = new ListModelList<Wood>(woodList);
		woodListbox.setModel(woodListModel);
		currentWood = null;
		workerList = workerService.getWorkerList();
		workerListModel = new ListModelList<Worker>(workerList);
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
			woodTypeCombobox.setSelectedIndex(-1);
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
			WoodType auxWoodType = woodTypeService.getWoodType(currentWood.getIdWoodType());
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(auxWoodType));
			RawMaterialType currentRawMaterialType = rawMaterialTypeService.getRawMaterialType(currentWood.getIdRawMaterialType());
			rawMaterialTypeCombobox.setSelectedIndex(rawMaterialTypeListModel.indexOf(currentRawMaterialType));
			nameTextbox.setValue(currentRawMaterialType.getName());
			measureTextbox.setValue(getMeasureFormated(currentWood));
			stockDoublebox.setValue(currentWood.getStock());
			stockMinDoublebox.setValue(currentWood.getStockMin());
			stockRepoDoublebox.setValue(currentWood.getStockRepo());
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
		Double stock = stockDoublebox.getValue();
		Double stockMin = stockMinDoublebox.getValue();
		Double stockRepo = stockRepoDoublebox.getValue();
		if(currentWood == null) {// nuevo
			currentWood = new Wood(null, rawMaterialType.getId(), woodType.getId(), code, stock, stockMin, stockRepo);
			currentWood = woodService.saveWood(currentWood);
		} else {// edicion
			currentWood.setCode(code);
			currentWood.setIdRawMaterialType(rawMaterialType.getId());
			currentWood.setIdWoodType(woodType.getId());
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
		stockModificationGrid.setVisible(true);
		stockModificationLabel.setValue("Ingreso Stock");
		workerCombobox.setSelectedIndex(-1);
		numberIntbox.setValue(null);
		quantityDoublebox.setValue(null);
	}
	
	@Listen("onClick = #cancelNewStockButton")
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
			double  newStock = 0;
			if(stockModificationLabel.getValue().equals("Ingreso Stock")) {
				newStock = currentWood.getStock() + quantityDoublebox.doubleValue();
				
			} else {
				if(currentWood.getStock() > quantityDoublebox.getValue()) {// hay suficiente stock
					newStock = currentWood.getStock() - quantityDoublebox.doubleValue();
				} else {
					Clients.showNotification("No hay stock suficiente", quantityDoublebox);
					return;
				}
			}
			currentWood.setStock(newStock);
			currentWood = woodService.updateWood(currentWood);
			refreshView();
		}
		stockModificationGrid.setVisible(false);
	}
	
	@Listen("onClick = #cancelNewStockButton")
	public void cancelNewStockButtonClick() {
		stockModificationGrid.setVisible(false);
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
			String lenght = "(L) " + rawMaterialType.getLength().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getLengthIdMeasureUnit());
			String depth = "(E) " + rawMaterialType.getDepth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getDepthIdMeasureUnit());
			String width = "(A) " + rawMaterialType.getWidth().doubleValue() + " " + getMeasureUnitName(rawMaterialType.getWidthIdMeasureUnit());
			measureTextbox.setValue(lenght + " x " + depth + " x " + width);
		}
	}
}

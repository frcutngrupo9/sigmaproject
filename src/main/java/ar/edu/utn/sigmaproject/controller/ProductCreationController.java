package ar.edu.utn.sigmaproject.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.*;
import ar.edu.utn.sigmaproject.util.RepositoryHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	final Logger logger = LoggerFactory.getLogger(ProductCreationController.class);

	@Wire
	Component productCreationBlock;
	@Wire
	Textbox productNameTextbox;
	@Wire
	Textbox productDetailsTextbox;
	@Wire
	Doublebox productPriceDoublebox;
	@Wire
	Button createPieceButton;
	@Wire
	Button resetPieceButton;
	@Wire
	Button deletePieceButton;
	@Wire
	Button saveProductButton;
	@Wire
	Button resetProductButton;
	@Wire
	Button deleteProductButton;
	@Wire
	Button uploadProductPhotoButton;
	@Wire
	Button deleteProductPhotoButton;
	@Wire
	Image productImage;
	@Wire
	Component pieceCreationBlock;
	@Wire
	Textbox pieceNameTextbox;
	@Wire
	Textbox productCodeTextbox;
	@Wire
	Doublebox pieceLengthDoublebox;
	@Wire
	Doublebox pieceDepthDoublebox;
	@Wire
	Doublebox pieceWidthDoublebox;
	@Wire
	Selectbox lengthMeasureUnitSelectbox;
	@Wire
	Selectbox depthMeasureUnitSelectbox;
	@Wire
	Selectbox widthMeasureUnitSelectbox;
	@Wire
	Textbox pieceSizeTextbox;
	@Wire
	Checkbox pieceGroupCheckbox;
	@Wire
	Intbox pieceUnitsByProductIntbox;
	@Wire
	Button createProcessButton;
	@Wire
	Button cancelPieceButton;
	@Wire
	Component processCreationBlock;
	@Wire
	Listbox processListbox;
	@Wire
	Listbox pieceListbox;
	@Wire
	Caption productCaption;
	@Wire
	Button pieceCopyButton;
	@Wire
	Grid productGrid;
	@Wire
	Combobox productCategoryCombobox;

	// supply
	@Wire
	Button openSupplyListButton;
	@Wire
	Button closeSupplyListButton;
	@Wire
	Component supplyCreationBlock;
	@Wire
	Bandbox supplyTypeBandbox;
	@Wire
	Listbox supplyTypePopupListbox;
	@Wire
	Listbox supplyListbox;
	@Wire
	Doublebox supplyQuantityDoublebox;
	@Wire
	Button saveSupplyButton;
	@Wire
	Button resetSupplyButton;
	@Wire
	Button deleteSupplyButton;
	@Wire
	Button cancelSupplyButton;

	// raw material
	@Wire
	Button openRawMaterialListButton;
	@Wire
	Button closeRawMaterialListButton;
	@Wire
	Component rawMaterialCreationBlock;
	@Wire
	Bandbox rawMaterialTypeBandbox;
	@Wire
	Listbox rawMaterialTypePopupListbox;
	@Wire
	Listbox rawMaterialListbox;
	@Wire
	Doublebox rawMaterialQuantityDoublebox;
	@Wire
	Button saveRawMaterialButton;
	@Wire
	Button resetRawMaterialButton;
	@Wire
	Button deleteRawMaterialButton;
	@Wire
	Button cancelRawMaterialButton;

	// services
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;
	@WireVariable
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductCategoryRepository productCategoryRepository;
	@WireVariable
	private RawMaterialTypeRepository rawMaterialTypeRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	// attributes
	private Product currentProduct;
	private Piece currentPiece;
	@SuppressWarnings("rawtypes")
	private EventQueue eq;
	private Supply currentSupply;
	private SupplyType currentSupplyType;
	private RawMaterial currentRawMaterial;
	private RawMaterialType currentRawMaterialType;

	// list
	private List<Piece> pieceList;
	private List<ProcessType> processTypeList;
	private List<Process> listboxProcessList;
	private List<Supply> supplyList;
	private List<Supply> lateDeleteSupplyList;
	private List<SupplyType> supplyTypePopupList;
	private List<RawMaterial> rawMaterialList;
	private List<RawMaterial> lateDeleteRawMaterialList;
	private List<RawMaterialType> rawMaterialTypePopupList;

	// list models
	private ListModelList<Piece> pieceListModel;
	private ListModelList<ProductCategory> productCategoryListModel;
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
	private ListModelList<MeasureUnit> depthMeasureUnitListModel;
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;
	private ListModelList<Supply> supplyListModel;
	private ListModelList<SupplyType> supplyTypePopupListModel;
	private ListModelList<RawMaterial> rawMaterialListModel;
	private ListModelList<RawMaterialType> rawMaterialTypePopupListModel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		processTypeList = processTypeRepository.findAll();
		processTypeListModel = new ListModelList<>(processTypeList);
		processListbox.setModel(processTypeListModel);
		listboxProcessList = new ArrayList<>();// listbox que contiene los procesos de la pieza seleccionada o vacio si es una nueva pieza

		pieceList = new ArrayList<Piece>();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);

		MeasureUnitType measureUnitType = measureUnitTypeRepository.findByName("Longitud");
		if(measureUnitType == null) {
			new RepositoryHelper().generateMeasureUnitTypeList(measureUnitRepository, measureUnitTypeRepository);
			measureUnitType = measureUnitTypeRepository.findByName("Longitud");
		}
		List<MeasureUnit> measureUnitList = measureUnitRepository.findByType(measureUnitType);
		lengthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		depthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		widthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		lengthMeasureUnitSelectbox.setModel(lengthMeasureUnitListModel);
		depthMeasureUnitSelectbox.setModel(depthMeasureUnitListModel);
		widthMeasureUnitSelectbox.setModel(widthMeasureUnitListModel);
		currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
		currentPiece = null;

		// agregamos un listener para cuando se seleccione una pieza en el modal de copia de otra pieza
		eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				Piece value = (Piece)event.getData();
				fillPieceCopy(value);
			}
		});

		lateDeleteSupplyList = new ArrayList<Supply>();
		supplyList = new ArrayList<Supply>();
		supplyListModel = new ListModelList<Supply>(supplyList);
		supplyListbox.setModel(supplyListModel);
		currentSupply = null;
		currentSupplyType = null;

		lateDeleteRawMaterialList = new ArrayList<>();
		rawMaterialList = new ArrayList<>();
		rawMaterialListModel = new ListModelList<>(rawMaterialList);
		rawMaterialListbox.setModel(rawMaterialListModel);
		currentRawMaterial = null;
		currentRawMaterialType = null;

		productCategoryListModel = new ListModelList<>(productCategoryRepository.findAll());
		productCategoryCombobox.setModel(productCategoryListModel);
	}

	@Listen("onAfterRender = #productCategoryCombobox")
	public void productCategoryComboboxSelection() {// se hace refresh despues de q se renderizo el combobox para que se le pueda setear un valor seleccionado
		refreshViewProduct();
		refreshViewPiece();
	}

	@Transactional
	@Listen("onClick = #saveProductButton")
	public void saveProduct() {
		if(Strings.isBlank(productNameTextbox.getValue())){
			Clients.showNotification("Ingresar Nombre Producto", productNameTextbox);
			return;
		}
		if (productCategoryCombobox.getSelectedIndex() == -1) {
			String manuallyEnteredCategoryName = productCategoryCombobox.getValue();
			if (manuallyEnteredCategoryName.trim().length() > 0) {
				ProductCategory newProductCategory = new ProductCategory(manuallyEnteredCategoryName);
				newProductCategory = productCategoryRepository.save(newProductCategory);
				productCategoryListModel = new ListModelList<>(productCategoryRepository.findAll());
				productCategoryCombobox.setModel(productCategoryListModel);
				productCategoryCombobox.onInitRender(new MouseEvent(Events.ON_CLICK, productCategoryCombobox));
				productCategoryCombobox.setSelectedIndex(productCategoryListModel.indexOf(newProductCategory));
			} else {
				Clients.showNotification("Seleccionar Categoria Producto", productCategoryCombobox);
				return;
			}
		}
		String productName = productNameTextbox.getText();
		String productDetails = productDetailsTextbox.getText();
		String productCode = productCodeTextbox.getText();
		ProductCategory productCategory = productCategoryCombobox.getSelectedItem().getValue();
		BigDecimal productPrice = new BigDecimal(productPriceDoublebox.doubleValue());
		org.zkoss.image.Image image = productImage.getContent();

		if(currentProduct == null) {// se esta creando un nuevo producto
			currentProduct = new Product(productCode, productName, productDetails, productCategory, productPrice);
		} else {// se esta editando un producto
			currentProduct.setName(productName);
			currentProduct.setDetails(productDetails);
			currentProduct.setCode(productCode);
			currentProduct.setCategory(productCategory);
			currentProduct.setPrice(productPrice);
		}
		if (image != null) {
			currentProduct.setImageData(image.getByteData());			
		}
		productRepository.save(currentProduct);

		// mostrar mensaje al user
		Clients.showNotification("Producto guardado");

		// limpiar todo
		currentProduct = null;
		currentPiece = null;
		refreshViewProduct();
		refreshViewPiece();
		supplyCreationBlock.setVisible(false);
		rawMaterialCreationBlock.setVisible(false);
	}

	public void doUploadProductPhoto(org.zkoss.image.AImage media) {
		if (media instanceof org.zkoss.image.Image) {
			org.zkoss.image.Image img = media;
			productImage.setHeight("225px");
			productImage.setWidth("225px");
			productImage.setStyle("margin: 8px");
			productImage.setContent(img);
		} else {
			Messagebox.show("No es una imagen: " + media, "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick = #deleteProductPhotoButton")
	public void deleteProductPhoto() {
		org.zkoss.image.Image img = productImage.getContent();
		if(img != null) {// se borra solo  si hay una imagen
			img = null;
			productImage.setContent(img);
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
			productGrid.setHflex("2");
		}
	}

	@Listen("onClick = #createPieceButton")
	public void newPieceButtonClick() {
		currentPiece = null;
		refreshViewPiece();
		pieceCreationBlock.setVisible(true);
		processCreationBlock.setVisible(true);
	}

	@Listen("onClick = #cancelPieceButton")
	public void cancelPiece() {
		currentPiece = null;
		refreshViewPiece();
	}

	@Listen("onClick = #createProcessButton")
	public void createNewProcess() {
		if(Strings.isBlank(pieceNameTextbox.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Pieza", pieceNameTextbox);
			return;
		}
		if(pieceUnitsByProductIntbox.getValue() == null || pieceUnitsByProductIntbox.getValue() <= 0){
			Clients.showNotification("La cantidad debe ser mayor a 0.", pieceUnitsByProductIntbox);
			return;
		}
		processCreationBlock.setVisible(true);
	}

	@Listen("onClick = #cancelProcessButton")
	public void cancelProcess() {
		pieceCreationBlock.setVisible(true);
		processCreationBlock.setVisible(false);
	}

	@Listen("onClick = #finishProcessButton")
	public void finishPiece() {
		// comprobamos que no existan checkbox activados que no posean valores de duracion
		for(int i = 1; i < processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
			Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
			Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
			Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
			Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
			if(chkbox.isChecked() && intboxDays.intValue() == 0 && intboxHours.intValue() == 0 && intboxMinutes.intValue() == 0){
				Clients.showNotification("Ingrese el Tiempo para el Proceso", intboxMinutes);
				return;
			}
		}
		String pieceName = pieceNameTextbox.getText();
		MeasureUnit lengthMeasureUnit = null;
		MeasureUnit depthMeasureUnit = null;
		MeasureUnit widthMeasureUnit = null;
		if(lengthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			lengthMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthMeasureUnitSelectbox.getSelectedIndex());
		}
		if(depthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			depthMeasureUnit = depthMeasureUnitListModel.getElementAt(depthMeasureUnitSelectbox.getSelectedIndex());
		}
		if(widthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			widthMeasureUnit = widthMeasureUnitListModel.getElementAt(widthMeasureUnitSelectbox.getSelectedIndex());
		}
		BigDecimal pieceLength = new BigDecimal(pieceLengthDoublebox.doubleValue());
		BigDecimal pieceDepth = new BigDecimal(pieceDepthDoublebox.doubleValue());
		BigDecimal pieceWidth = new BigDecimal(pieceWidthDoublebox.doubleValue());
		String pieceSize = pieceSizeTextbox.getText();
		Integer pieceUnits = pieceUnitsByProductIntbox.getValue();
		boolean pieceIsGroup = pieceGroupCheckbox.isChecked();

		if(currentPiece == null) { // se esta creando una pieza
			currentPiece = new Piece(pieceName, pieceLength, lengthMeasureUnit, pieceDepth, depthMeasureUnit, pieceWidth, widthMeasureUnit, pieceSize, pieceIsGroup, pieceUnits);
			pieceList.add(currentPiece);// lo agregamos a la lista
		} else { // se esta editando una pieza
			currentPiece.setName(pieceName);
			currentPiece.setLength(pieceLength);
			currentPiece.setLengthMeasureUnit(lengthMeasureUnit);
			currentPiece.setDepth(pieceDepth);
			currentPiece.setDepthMeasureUnit(depthMeasureUnit);
			currentPiece.setWidth(pieceWidth);
			currentPiece.setWidthMeasureUnit(widthMeasureUnit);
			currentPiece.setSize(pieceSize);
			currentPiece.setUnits(pieceUnits);
			currentPiece.setGroup(pieceIsGroup);
			//TODO corroborar si al modificar tbn se modifica adentro de la lista
		}
		currentPiece.setProcesses(listboxProcessList);
		// actualizamos el view
		currentPiece = null;
		refreshViewPiece();
	}

	private void refreshViewProduct() {
		if (currentProduct == null) {
			productCaption.setLabel("Creacion de Producto");
			deleteProductButton.setDisabled(true);
			productNameTextbox.setText("");
			productDetailsTextbox.setText("");
			productCodeTextbox.setText("");
			productCategoryCombobox.setSelectedIndex(-1);
			productPriceDoublebox.setText("");
			org.zkoss.image.Image img = null;
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
			productImage.setContent(img);
			pieceList = new ArrayList<Piece>();
			pieceListModel = new ListModelList<Piece>(pieceList);
			pieceListbox.setModel(pieceListModel);
			supplyList = new ArrayList<Supply>();
			rawMaterialList = new ArrayList<RawMaterial>();
		} else {
			productCaption.setLabel("Edicion de Producto");
			deleteProductButton.setDisabled(false);
			productNameTextbox.setText(currentProduct.getName());
			productDetailsTextbox.setText(currentProduct.getDetails());
			productCategoryCombobox.setSelectedIndex(productCategoryListModel.indexOf(currentProduct.getCategory()));
			productCodeTextbox.setText(currentProduct.getCode());
			BigDecimal product_price = currentProduct.getPrice();
			if(product_price != null) {
				productPriceDoublebox.setValue(product_price.doubleValue());
			} else {
				productPriceDoublebox.setValue(null);
			}
			org.zkoss.image.Image img = null;
			try {
				img = new AImage("", currentProduct.getImageData());
			} catch (IOException exception) {

			}
			if(img != null) {
				productImage.setHeight("225px");
				productImage.setWidth("225px");
				productImage.setStyle("margin: 8px");
			} else {
				productImage.setHeight("0px");
				productImage.setWidth("0px");
				productImage.setStyle("margin: 0px");
			}
			productImage.setContent(img);
			pieceList = currentProduct.getPieces();
			pieceListModel = new ListModelList<>(pieceList);
			pieceListbox.setModel(pieceListModel);
			supplyList = currentProduct.getSupplies();
			rawMaterialList = currentProduct.getRawMaterials();
		}
		refreshViewSupply();
		refreshSupplyTypePopup();
		refreshViewRawMaterial();
		refreshRawMaterialTypePopup();
	}

	private void refreshViewPiece() {
		pieceListModel = new ListModelList<>(pieceList);
		pieceListbox.setModel(pieceListModel);
		if (currentPiece == null) {// nueva pieza
			pieceCreationBlock.setVisible(false);
			processCreationBlock.setVisible(false);
			deletePieceButton.setDisabled(true);
			pieceCopyButton.setDisabled(false);
			// limpiar form pieza
			pieceNameTextbox.setText("");
			pieceGroupCheckbox.setChecked(false);
			// seleccionamos metros y pulgadas como valores predeterminados de las dimensiones de las piezas
			MeasureUnit meters = measureUnitRepository.findByName("Metros");
			MeasureUnit inch = measureUnitRepository.findByName("Pulgadas");
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(meters));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(inch));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(inch));
			pieceLengthDoublebox.setValue(0);
			pieceWidthDoublebox.setValue(0);
			pieceDepthDoublebox.setValue(0);
			pieceSizeTextbox.setText("");
			pieceUnitsByProductIntbox.setValue(0);
			listboxProcessList = new ArrayList<Process>();
		} else { // se esta editando una pieza
			pieceCreationBlock.setVisible(true);
			processCreationBlock.setVisible(true);
			deletePieceButton.setDisabled(false);
			pieceCopyButton.setDisabled(true);
			// cargar form pieza
			pieceNameTextbox.setText(currentPiece.getName());
			pieceGroupCheckbox.setChecked(currentPiece.isGroup());
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(currentPiece.getLengthMeasureUnit()));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(currentPiece.getDepthMeasureUnit()));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(currentPiece.getWidthMeasureUnit()));
			BigDecimal lenght = currentPiece.getLength();
			if(lenght != null) {
				pieceLengthDoublebox.setValue(lenght.doubleValue());
			} else {
				pieceLengthDoublebox.setValue(0);
			}
			BigDecimal depth = currentPiece.getDepth();
			if(depth != null) {
				pieceDepthDoublebox.setValue(depth.doubleValue());
			} else {
				pieceDepthDoublebox.setValue(0);
			}
			BigDecimal width = currentPiece.getWidth();
			if(width != null) {
				pieceWidthDoublebox.setValue(width.doubleValue());
			} else {
				pieceWidthDoublebox.setValue(0);
			}
			pieceSizeTextbox.setValue(currentPiece.getSize());
			pieceUnitsByProductIntbox.setValue(currentPiece.getUnits());

			listboxProcessList = currentPiece.getProcesses();// actualizamos la lista de procesos del Listbox con la lista de procesos obtenida de los procesos totales
		}
		refreshViewProcess();
	}

	private void refreshViewProcess() {
		processTypeListModel = new ListModelList<ProcessType>(processTypeList);
		processListbox.setModel(processTypeListModel);
	}

	public String getProcessDetails(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return "";
		} else {
			return aux.getDetails();
		}
	}

	public Integer getProcessDays(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getDays();
		}
	}

	public Integer getProcessHours(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getHours();
		}
	}

	public Integer getProcessMinutes(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getMinutes();
		}
	}

	public boolean isProcessCheck(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return false;
		} else {
			return true;
		}
	}

	private Process getProcessFromListbox(ProcessType processType) {
		if(listboxProcessList != null) {
			for(Process process : listboxProcessList) {
				if(process.getType().equals(processType)) {
					return process;
				}
			}
		}
		return null;
	}

	private void deleteProcessFromListbox(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux != null) {
			listboxProcessList.remove(aux);
		}
	}

	private void addProcessToListbox(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux != null) {
			System.out.println("deberia estar en null: " + aux.getType().getId());// porque se esta agregando
		} else {
			Duration duration = null;
			try {
				duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 0, 0, 0);
			} catch (DatatypeConfigurationException e) {
				System.out.println("Error en convertir a duracion: " + e.toString());
			}
			listboxProcessList.add(new Process(processType, "", duration));
		}
	}

	//when user checks on the checkbox of each process on the list
	@Listen("onProcessCheck = #processListbox")
	public void doProcessCheck(ForwardEvent evt) {// se usa para eliminar o agregar el proceso a la lista
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
		if(cbox.isChecked()) {
			addProcessToListbox(data);
		} else {
			deleteProcessFromListbox(data);
		}
		refreshViewProcess();
	}

	@Listen("onProcessDetailsChange = #processListbox")
	public void doProcessDetailsChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Textbox origin = (Textbox)evt.getOrigin().getTarget();
		Process process = getProcessFromListbox(data);
		process.setDetails(origin.getText());
		refreshViewProcess();
	}

	@Listen("onProcessDaysChange = #processListbox")
	public void doProcessDaysChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, origin.intValue(), process.getTime().getHours(), process.getTime().getMinutes(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
		refreshViewProcess();
	}

	@Listen("onProcessHoursChange = #processListbox")
	public void doProcessHoursChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, process.getTime().getDays(), origin.intValue(), process.getTime().getMinutes(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
		refreshViewProcess();
	}

	@Listen("onProcessMinutesChange = #processListbox")
	public void doProcessMinutesChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, process.getTime().getDays(), process.getTime().getHours(), origin.intValue(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
		refreshViewProcess();
	}

	private void deletePiece(Piece piece) {
		if(piece.getId() != null) {
			Piece deletePiece = null;
			for(Piece auxPiece:pieceList) {
				if(auxPiece.getId().equals(piece.getId())) {
					deletePiece = auxPiece;
				}
			}
			if(deletePiece != null) {
				pieceList.remove(deletePiece);// eliminamos la pieza
			}
		}
	}

	public String quantityOfProcess(Piece piece) {
		return "" + piece.getProcesses().size();
	}

	@Listen("onSelect = #pieceListbox")
	public void selectPiece() {
		if(pieceListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentPiece = null;
		}else {
			if(currentPiece == null) {// si no hay nada editandose
				currentPiece = pieceListbox.getSelectedItem().getValue();
				refreshViewPiece();
			}
		}
		pieceListModel.clearSelection();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteProductButton")
	public void deleteProduct() {
		if(currentProduct != null) {
			Messagebox.show("Esta seguro que quiere eliminar el producto? Se eliminaran las piezas y procesos relacionados", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						// la eliminacion de las piezas y procesos relacionados al producto se realizan en el servicio
						productRepository.delete(currentProduct);
						currentProduct = null;
						currentPiece = null;
						refreshViewProduct();
						refreshViewPiece();
						alert("Producto eliminado.");
					}
				}
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deletePieceButton")
	public void deletePiece() {
		if(currentPiece != null) {
			Messagebox.show("Esta seguro que desea eliminar la pieza " + currentPiece.getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						//eliminamos la pieza, los procesos tambien se eliminan en el metodo
						deletePiece(currentPiece);
						pieceListModel.remove(currentPiece);
						pieceListbox.setModel(pieceListModel);
						currentPiece = null;
						refreshViewPiece();
						alert("Pieza eliminada.");
					}
				}
			});

		}
	}

	@Listen("onClick = #resetProductButton")
	public void resetProduct() {
		refreshViewProduct();
		currentPiece = null;
		refreshViewPiece();
	}

	@Listen("onClick = #resetPieceButton")
	public void resetPiece() {
		refreshViewPiece();
		pieceCreationBlock.setVisible(true);
		processCreationBlock.setVisible(true);
	}
	@Listen("onClick = #pieceCopyButton")
	public void doPieceCopyButtonClick() {
		newPieceButtonClick();
		// mostramos el modal para seleccionar la pieza
		Window window = (Window)Executions.createComponents(
				"/piece_selection_modal.zul", null, null);
		window.doModal();
	}

	private void fillPieceCopy(Piece pieceCopy) {
		pieceNameTextbox.setText(pieceCopy.getName());
		pieceGroupCheckbox.setChecked(pieceCopy.isGroup());
		lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(pieceCopy.getLengthMeasureUnit()));
		depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(pieceCopy.getDepthMeasureUnit()));
		widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(pieceCopy.getWidthMeasureUnit()));
		BigDecimal lenght = pieceCopy.getLength();
		if(lenght != null) {
			pieceLengthDoublebox.setValue(lenght.doubleValue());
		} else {
			pieceLengthDoublebox.setValue(0);
		}
		BigDecimal depth = pieceCopy.getDepth();
		if(depth != null) {
			pieceDepthDoublebox.setValue(depth.doubleValue());
		} else {
			pieceDepthDoublebox.setValue(0);
		}
		BigDecimal width = pieceCopy.getWidth();
		if(width != null) {
			pieceWidthDoublebox.setValue(width.doubleValue());
		} else {
			pieceWidthDoublebox.setValue(0);
		}
		pieceSizeTextbox.setValue(pieceCopy.getSize());
		pieceUnitsByProductIntbox.setValue(pieceCopy.getUnits());
		fillProcessCopy(pieceCopy);
	}

	private void fillProcessCopy(Piece pieceCopy) {
		processCreationBlock.setVisible(true);
		List<Process> processListCopy = pieceCopy.getProcesses();
		for(Process processCopy : processListCopy) {
			listboxProcessList.add(new Process(processCopy.getType(), processCopy.getDetails(), processCopy.getTime()));
		}
		refreshViewProcess();
	}

	// supply methods

	@Listen("onClick = #openSupplyListButton")
	public void showSupplyCreationBlock() {
		supplyCreationBlock.setVisible(true);
	}

	@Listen("onClick = #closeSupplyListButton")
	public void hideSupplyCreationBlock() {
		supplyCreationBlock.setVisible(false);
	}

	private void refreshViewSupply() {
		if (currentSupply == null) {
			// borramos el text del insumo
			// deseleccionamos la tabla y borramos la cantidad
			supplyTypeBandbox.setDisabled(false);
			supplyTypeBandbox.setValue("");
			supplyQuantityDoublebox.setValue(null);
			currentSupplyType = null;
			deleteSupplyButton.setDisabled(true);
			cancelSupplyButton.setDisabled(true);
		} else {
			currentSupplyType = currentSupply.getSupplyType();
			supplyTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			supplyTypeBandbox.setValue(currentSupplyType.getDescription());
			supplyQuantityDoublebox.setValue(currentSupply.getQuantity().doubleValue());
			deleteSupplyButton.setDisabled(false);
			cancelSupplyButton.setDisabled(false);
		}
		supplyTypePopupListbox.clearSelection();
		refreshSupplyListbox();
	}

	private void refreshSupplyListbox() {
		supplyListModel = new ListModelList<Supply>(supplyList);
		supplyListbox.setModel(supplyListModel);
	}

	private void refreshSupplyTypePopup() {// el popup se actualiza en base a la lista
		supplyTypePopupList = supplyTypeRepository.findAll();
		for(Supply supply : supplyList) {
			supplyTypePopupList.remove(supply.getSupplyType());// sacamos del popup
		}
		supplyTypePopupListModel = new ListModelList<SupplyType>(supplyTypePopupList);
		supplyTypePopupListbox.setModel(supplyTypePopupListModel);
	}

	@Listen("onSelect = #supplyTypePopupListbox")
	public void selectionSupplyTypePopupListbox() {
		currentSupplyType = supplyTypePopupListbox.getSelectedItem().getValue();
		supplyTypeBandbox.setValue(currentSupplyType.getDescription());
		supplyTypeBandbox.close();
	}

	@Listen("onSelect = #supplyListbox")
	public void selectSupply() {
		if(supplyListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentSupply = null;
		} else {
			if(currentSupply == null) {// permite la seleccion solo si no existe nada seleccionado
				currentSupply = supplyListbox.getSelectedItem().getValue();
				currentSupplyType = currentSupply.getSupplyType();
				refreshViewSupply();
			}
		}
		supplyListModel.clearSelection();
	}

	@Listen("onClick = #cancelSupplyButton")
	public void cancelSupply() {
		currentSupply = null;
		refreshViewSupply();
	}

	@Listen("onClick = #resetSupplyButton")
	public void resetSupply() {
		refreshViewSupply();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteSupplyButton")
	public void deleteSupply() {
		if(currentSupply != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentSupply.getSupplyType().getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						if(currentSupply.getId() != null) {// si el detalle existe en la bd
							lateDeleteSupplyList.add(currentSupply);// agregamos a la lista de eliminacion por las dudas que se vuelva a agregar
						}
						supplyList.remove(currentSupply);// quitamos de la lista
						currentSupply = null;// eliminamos
						refreshSupplyTypePopup();// actualizamos el popup para que aparezca el insumo eliminado
						refreshViewSupply();
					}
				}
			});
		} 
	}

	@Listen("onClick = #saveSupplyButton")
	public void saveSupply() {
		if(supplyQuantityDoublebox.getValue()==null || supplyQuantityDoublebox.getValue()<=0) {
			Clients.showNotification("Ingresar Cantidad del Insumo", supplyQuantityDoublebox);
			return;
		}
		if(currentSupplyType == null) {
			Clients.showNotification("Debe seleccionar un Insumo", supplyTypeBandbox);
			return;
		}
		// buscamos si no esta eliminado
		Supply aux = null;
		for(Supply lateDeleteSupply : lateDeleteSupplyList) {
			if(currentSupplyType.getId().equals(lateDeleteSupply.getId())) {
				aux = lateDeleteSupply;
			}
		}
		if(aux != null) {
			lateDeleteSupplyList.remove(aux);// lo eliminamos de la lista de eliminacion tardia porque el sera agregado nuevamente
		}
		double supplyQuantity = supplyQuantityDoublebox.getValue();
		if(currentSupply == null) { // es nuevo
			if(aux != null) {// si ya estaba en un detalle
				aux.setQuantity(BigDecimal.valueOf(supplyQuantity));;
				supplyList.add(aux);
			} else {
				// se crea un detalle sin id porque recien se le asignara uno al momento de grabarse definitivamente
				currentSupply = new Supply(null, currentSupplyType, BigDecimal.valueOf(supplyQuantity));
				supplyList.add(currentSupply);
			}
		} else { // se edita
			currentSupply.setSupplyType(currentSupplyType);;
			currentSupply.setQuantity(BigDecimal.valueOf(supplyQuantity));
		}
		refreshSupplyTypePopup();// actualizamos el popup
		currentSupply = null;
		refreshViewSupply();
	}

	// raw material methods

	@Listen("onClick = #openRawMaterialListButton")
	public void showRawMaterialCreationBlock() {
		rawMaterialCreationBlock.setVisible(true);
	}

	@Listen("onClick = #closeRawMaterialListButton")
	public void hideRawMaterialCreationBlock() {
		rawMaterialCreationBlock.setVisible(false);
	}

	private void refreshViewRawMaterial() {
		if (currentRawMaterial == null) {
			// borramos el text de la materia prima
			// deseleccionamos la tabla y borramos la cantidad
			rawMaterialTypeBandbox.setDisabled(false);
			rawMaterialTypeBandbox.setValue("");
			rawMaterialQuantityDoublebox.setValue(null);
			currentRawMaterialType = null;
			deleteRawMaterialButton.setDisabled(true);
			cancelRawMaterialButton.setDisabled(true);
		} else {
			currentRawMaterialType = currentRawMaterial.getRawMaterialType();
			rawMaterialTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			rawMaterialTypeBandbox.setValue(currentRawMaterialType.getName());
			rawMaterialQuantityDoublebox.setValue(currentRawMaterial.getQuantity().doubleValue());
			deleteRawMaterialButton.setDisabled(false);
			cancelRawMaterialButton.setDisabled(false);
		}
		rawMaterialTypePopupListbox.clearSelection();
		refreshRawMaterialListbox();
	}

	private void refreshRawMaterialListbox() {
		rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
		rawMaterialListbox.setModel(rawMaterialListModel);
	}

	private void refreshRawMaterialTypePopup() {// el popup se actualiza en base a la lista
		rawMaterialTypePopupList = rawMaterialTypeRepository.findAll();
		for(RawMaterial rawMaterial : rawMaterialList) {
			rawMaterialTypePopupList.remove(rawMaterial.getRawMaterialType());// sacamos del popup
		}
		rawMaterialTypePopupListModel = new ListModelList<>(rawMaterialTypePopupList);
		rawMaterialTypePopupListbox.setModel(rawMaterialTypePopupListModel);
	}

	@Listen("onSelect = #rawMaterialTypePopupListbox")
	public void selectionRawMaterialTypePopupListbox() {
		currentRawMaterialType = (RawMaterialType) rawMaterialTypePopupListbox.getSelectedItem().getValue();
		rawMaterialTypeBandbox.setValue(currentRawMaterialType.getName());
		rawMaterialTypeBandbox.close();
	}

	@Listen("onSelect = #rawMaterialListbox")
	public void selectRawMaterial() {
		if(rawMaterialListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentRawMaterial = null;
		} else {
			if(currentRawMaterial == null) {// permite la seleccion solo si no existe nada seleccionado
				currentRawMaterial = rawMaterialListbox.getSelectedItem().getValue();
				currentRawMaterialType = currentRawMaterial.getRawMaterialType();
				refreshViewRawMaterial();
			}
		}
		rawMaterialListModel.clearSelection();
	}

	@Listen("onClick = #cancelRawMaterialButton")
	public void cancelRawMaterial() {
		currentRawMaterial = null;
		refreshViewRawMaterial();
	}

	@Listen("onClick = #resetRawMaterialButton")
	public void resetRawMaterial() {
		refreshViewRawMaterial();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteRawMaterialButton")
	public void deleteRawMaterial() {
		if(currentRawMaterial != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentRawMaterial.getRawMaterialType().getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						if(currentRawMaterial.getId() != null) {// si el detalle existe en la bd
							lateDeleteRawMaterialList.add(currentRawMaterial);// agregamos a la lista de eliminacion por las dudas que se vuelva a agregar
						}
						rawMaterialList.remove(currentRawMaterial);// quitamos de la lista
						currentRawMaterial = null;// eliminamos
						refreshRawMaterialTypePopup();// actualizamos el popup para que aparezca vuelva a aparecer el eliminado
						refreshViewRawMaterial();
					}
				}
			});
		} 
	}

	@Listen("onClick = #saveRawMaterialButton")
	public void saveRawMaterial() {
		if(rawMaterialQuantityDoublebox.getValue()==null || rawMaterialQuantityDoublebox.getValue()<=0) {
			Clients.showNotification("Ingresar Cantidad de la Materia Prima", rawMaterialQuantityDoublebox);
			return;
		}
		if(currentRawMaterialType == null) {
			Clients.showNotification("Debe seleccionar una Materia Prima", rawMaterialTypeBandbox);
			return;
		}
		// buscamos si no esta eliminado
		RawMaterial aux = null;
		for(RawMaterial lateDeleteRawMaterial : lateDeleteRawMaterialList) {
			if(currentRawMaterialType.getId().equals(lateDeleteRawMaterial.getId())) {
				aux = lateDeleteRawMaterial;
			}
		}
		if(aux != null) {
			lateDeleteRawMaterialList.remove(aux);// lo eliminamos de la lista de eliminacion tardia porque el sera agregado nuevamente
		}
		double rawMaterialQuantity = rawMaterialQuantityDoublebox.getValue();
		if(currentRawMaterial == null) { // es nuevo
			if(aux != null) {// si ya estaba en un detalle
				aux.setQuantity(BigDecimal.valueOf(rawMaterialQuantity));;
				rawMaterialList.add(aux);
			} else {
				// se crea un detalle sin id porque recien se le asignara uno al momento de grabarse definitivamente
				currentRawMaterial = new RawMaterial(currentRawMaterialType, BigDecimal.valueOf(rawMaterialQuantity));
				rawMaterialList.add(currentRawMaterial);
			}
		} else { // se edita
			currentRawMaterial.setRawMaterialType(currentRawMaterialType);;
			currentRawMaterial.setQuantity(BigDecimal.valueOf(rawMaterialQuantity));
		}
		refreshRawMaterialTypePopup();// actualizamos el popup
		currentRawMaterial = null;
		refreshViewRawMaterial();
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

}

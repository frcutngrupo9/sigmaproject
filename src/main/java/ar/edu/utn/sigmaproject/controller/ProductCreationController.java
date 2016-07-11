package ar.edu.utn.sigmaproject.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

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
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
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

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProcessRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductCreationController extends SelectorComposer<Component> {
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
	@Wire
	Button openRawMaterialListButton;
	@Wire
	Button openSupplyListButton;

	// services
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;
	@WireVariable
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private PieceRepository pieceRepository;
	@WireVariable
	private ProcessRepository processRepository;
	@WireVariable
	private ProductCategoryRepository productCategoryRepository;

	// attributes
	private Product currentProduct;
	private Piece currentPiece;
	@SuppressWarnings("rawtypes")
	private EventQueue eq;

	// list
	private List<Piece> pieceList;
	private List<ProcessType> processTypeList;
	private List<Process> listboxProcessList;

	// list models
	private ListModelList<Piece> pieceListModel;
	private ListModelList<ProductCategory> productCategoryListModel;
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
	private ListModelList<MeasureUnit> depthMeasureUnitListModel;
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		processTypeList = processTypeRepository.findAll();
		processTypeListModel = new ListModelList<>(processTypeList);
		processListbox.setModel(processTypeListModel);
		listboxProcessList = new ArrayList<>();// listbox que contiene los procesos de la pieza seleccionada o vacio si es una nueva pieza

		pieceList = new ArrayList<Piece>();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);

		MeasureUnitType measureUnitType = measureUnitTypeRepository.findFirstByName("Longitud");
		List<MeasureUnit> measureUnitList = measureUnitRepository.findByType(measureUnitType);
		lengthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		depthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		widthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		lengthMeasureUnitSelectbox.setModel(lengthMeasureUnitListModel);
		depthMeasureUnitSelectbox.setModel(depthMeasureUnitListModel);
		widthMeasureUnitSelectbox.setModel(widthMeasureUnitListModel);
		currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
		currentPiece = null;

		// listener para cuando se modifique el producto al agregar materias primas o insumos
		eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				Product value = (Product)event.getData();
				currentProduct = value;
			}
		});

		// agregamos un listener para cuando se seleccione una pieza en el modal de copia de otra pieza
		eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				Piece value = (Piece)event.getData();
				fillPieceCopy(value);
			}
		});
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
		String productName = productNameTextbox.getText().toUpperCase();
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
		currentProduct.setPieces(pieceList);
		for(Piece eachPiece : currentProduct.getPieces()) {
			for(Process eachProcess : eachPiece.getProcesses()) {
				eachProcess = processRepository.save(eachProcess);
			}
			eachPiece = pieceRepository.save(eachPiece);
		}
		currentProduct = productRepository.save(currentProduct);

		// mostrar mensaje al user
		Clients.showNotification("Producto guardado");

		currentPiece = null;
		refreshViewProduct();
		refreshViewPiece();
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
		String pieceName = pieceNameTextbox.getText().toUpperCase();
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
			// no se permite agregar insumos o matertias prima mientras el producto no est� guardado
			openRawMaterialListButton.setDisabled(true);
			openSupplyListButton.setDisabled(true);
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
			openRawMaterialListButton.setDisabled(false);
			openSupplyListButton.setDisabled(false);
		}
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
			MeasureUnit meters = measureUnitRepository.findFirstByName("Metros");
			MeasureUnit inch = measureUnitRepository.findFirstByName("Pulgadas");
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
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(inputEvent.getValue());
		Process process = getProcessFromListbox(data);
		process.setDetails(origin.getText());
	}

	@Listen("onProcessDaysChange = #processListbox")
	public void doProcessDaysChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, origin.intValue(), process.getTime().getHours(), process.getTime().getMinutes(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
	}

	@Listen("onProcessHoursChange = #processListbox")
	public void doProcessHoursChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, process.getTime().getDays(), origin.intValue(), process.getTime().getMinutes(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
	}

	@Listen("onProcessMinutesChange = #processListbox")
	public void doProcessMinutesChange(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Intbox origin = (Intbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, process.getTime().getDays(), process.getTime().getHours(), origin.intValue(), 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
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
	
	@Listen("onClick = #openRawMaterialListButton")
	public void openRawMaterialListButtonClick() {
		Executions.getCurrent().setAttribute("selected_product", currentProduct);
		Window window = (Window)Executions.createComponents("/product_raw_material.zul", null, null);
		window.doModal();
	}
	
	@Listen("onClick = #openSupplyListButton")
	public void openSupplyListButtonClick() {
		Executions.getCurrent().setAttribute("selected_product", currentProduct);
		Window window = (Window)Executions.createComponents("/product_supply.zul", null, null);
		window.doModal();
	}
}

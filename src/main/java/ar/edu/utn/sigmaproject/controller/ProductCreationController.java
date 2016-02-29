package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.SupplyService;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyTypeServiceImpl;

public class ProductCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

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
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	private MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
	private SupplyTypeService supplyTypeService = new SupplyTypeServiceImpl();
	private SupplyService supplyService = new SupplyServiceImpl();
	private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();
	private RawMaterialService rawMaterialService = new RawMaterialServiceImpl();

	// attributes
	private Product currentProduct;
	private Piece currentPiece;
	private EventQueue eq;
	private Supply currentSupply;
	private SupplyType currentSupplyType;
	private RawMaterial currentRawMaterial;
	private RawMaterialType currentRawMaterialType;

	// list
	private List<Piece> pieceList;
	private List<Process> processList;
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
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
	private ListModelList<MeasureUnit> depthMeasureUnitListModel;
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;
	private ListModelList<Supply> supplyListModel;
	private ListModelList<SupplyType> supplyTypePopupListModel;
	private ListModelList<RawMaterial> rawMaterialListModel;
	private ListModelList<RawMaterialType> rawMaterialTypePopupListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		processTypeList = processTypeService.getProcessTypeList();
		processTypeListModel = new ListModelList<ProcessType>(processTypeList);
		processListbox.setModel(processTypeListModel);
		listboxProcessList = new ArrayList<Process>();// listbox que contiene los procesos de la pieza seleccionada o vacio si es una nueva pieza

		pieceList = new ArrayList<Piece>();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);

		processList = new ArrayList<Process>();

		Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
		List<MeasureUnit> measureUnitList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
		lengthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
		depthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
		widthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
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

		lateDeleteRawMaterialList = new ArrayList<RawMaterial>();
		rawMaterialList = new ArrayList<RawMaterial>();
		rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
		rawMaterialListbox.setModel(rawMaterialListModel);
		currentRawMaterial = null;
		currentRawMaterialType = null;

		refreshViewProduct();
		refreshViewPiece();
	}

	@Listen("onClick = #saveProductButton")
	public void saveProduct() {
		if(Strings.isBlank(productNameTextbox.getValue())){
			Clients.showNotification("Ingresar Nombre Producto", productNameTextbox);
			return;
		}
		String productName = productNameTextbox.getText();
		String productDetails = productDetailsTextbox.getText();
		String productCode = productCodeTextbox.getText();
		BigDecimal productPrice = new BigDecimal(productPriceDoublebox.doubleValue());

		if(currentProduct == null) {// se esta creando un nuevo producto
			currentProduct = new Product(null, productCode, productName, productDetails, productPrice);
			productService.saveProduct(currentProduct, pieceList, processList, supplyList, rawMaterialList);
		} else {// se esta editando un producto
			currentProduct.setName(productName);
			currentProduct.setDetails(productDetails);
			currentProduct.setCode(productCode);
			currentProduct.setPrice(productPrice);
			currentProduct = productService.updateProduct(currentProduct, pieceList, processList, supplyList, rawMaterialList);
		}
		// mostrar mensaje al user
		Clients.showNotification("Producto guardado");

		// limpiar todo
		currentProduct = null;
		currentPiece = null;
		refreshViewProduct();
		refreshViewPiece();
	}

	@Listen("onClick = #createPieceButton")
	public void createNewPiece() {
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
		// actualizamos la lista de piezas
		Integer pieceId = 0;
		String pieceName = pieceNameTextbox.getText();
		Integer lengthIdMeasureUnit = null;
		Integer depthIdMeasureUnit = null;
		Integer widthIdMeasureUnit = null;
		if(lengthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			lengthIdMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthMeasureUnitSelectbox.getSelectedIndex()).getId();
		}
		if(depthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			depthIdMeasureUnit = depthMeasureUnitListModel.getElementAt(depthMeasureUnitSelectbox.getSelectedIndex()).getId();
		}
		if(widthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			widthIdMeasureUnit = widthMeasureUnitListModel.getElementAt(widthMeasureUnitSelectbox.getSelectedIndex()).getId();
		}
		BigDecimal pieceLength = new BigDecimal(pieceLengthDoublebox.doubleValue());
		BigDecimal pieceDepth = new BigDecimal(pieceDepthDoublebox.doubleValue());
		BigDecimal pieceWidth = new BigDecimal(pieceWidthDoublebox.doubleValue());
		String pieceSize = pieceSizeTextbox.getText();
		Integer pieceUnits = pieceUnitsByProductIntbox.getValue();
		boolean pieceIsGroup = pieceGroupCheckbox.isChecked();

		if(currentPiece == null) { // se esta creando una pieza
			Integer serviceNewPieceId = pieceService.getNewId();
			if(pieceList.isEmpty() == true) {// buscamos un id para la pieza nueva
				pieceId = serviceNewPieceId;
			} else {
				pieceId = getLastPieceId() + 1;// buscamos el ultimo id y sumamos 1
				if(pieceId < serviceNewPieceId) { // si el ultimo id es menor que uno nuevo del servicio quiere decir que las piezas en la lista son viejas y hay que agarra el id mas grande osea el que viene del servicio
					pieceId = serviceNewPieceId;
				}
			}
			currentPiece = new Piece(pieceId, null, pieceName, pieceLength, lengthIdMeasureUnit, pieceDepth, depthIdMeasureUnit, pieceWidth, widthIdMeasureUnit, pieceSize, pieceIsGroup, pieceUnits);
			pieceList.add(currentPiece);// lo agregamos a la lista
			pieceListModel.add(currentPiece);
			pieceListbox.setModel(pieceListModel);// y al modelo para que aparezca en la pantalla

			// es una pieza nueva se inserta el id de la pieza a los procesos y se agregan a la lista total
			for(Process process : listboxProcessList) {
				process.setIdPiece(pieceId);
				processList.add(process);
			}

		} else { // se esta editando una pieza
			currentPiece.setName(pieceName);
			currentPiece.setLength(pieceLength);
			currentPiece.setLengthIdMeasureUnit(lengthIdMeasureUnit);
			currentPiece.setDepth(pieceDepth);
			currentPiece.setDepthIdMeasureUnit(depthIdMeasureUnit);
			currentPiece.setWidth(pieceWidth);
			currentPiece.setWidthIdMeasureUnit(widthIdMeasureUnit);
			currentPiece.setSize(pieceSize);
			currentPiece.setUnits(pieceUnits);
			currentPiece.setGroup(pieceIsGroup);
			updatePieceList(currentPiece);// actualizamos la lista
			pieceListModel = new ListModelList<Piece>(pieceList); 
			pieceListbox.setModel(pieceListModel);// actualizamos el modelo para que aparezca en la pantalla
			
			for(ProcessType processType : processTypeList) {
				Process insideCompleteList = searchProcess(currentPiece, processType);
				Process insideCurrentList = getProcessFromListbox(processType);
				
				if(insideCompleteList != null) {
					if (insideCurrentList != null) {
						// esta en las 2 listas, se actualiza la lista total
						insideCompleteList.setDetails(insideCurrentList.getDetails());
						insideCompleteList.setTime(insideCurrentList.getTime());
						updateProcessList(insideCompleteList);
					} else {
						// no esta mas en la lista del listbox, se elimina de la lista total
						deleteProcess(insideCompleteList);
					}
				} else {
					if (insideCurrentList != null) {
						// esta en la lista del listbox pero no en la lista total, se agrega a la lista total
						insideCurrentList.setIdPiece(currentPiece.getId());// se agrega el id de la pieza, ya que al crearse no se agrega
						processList.add(insideCurrentList);
					}
				}
			}
		}
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
			productPriceDoublebox.setText("");
			processList = new ArrayList<Process>();
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
			productCodeTextbox.setText(currentProduct.getCode());
			BigDecimal product_price = currentProduct.getPrice();
			if(product_price != null) {
				productPriceDoublebox.setValue(product_price.doubleValue());
			}else {
				productPriceDoublebox.setValue(null);
			}
			processList = getProcessList(currentProduct.getId());
			pieceList = pieceService.getPieceList(currentProduct.getId());
			pieceListModel = new ListModelList<Piece>(pieceList);
			pieceListbox.setModel(pieceListModel);
			supplyList = supplyService.getSupplyList(currentProduct.getId());
			rawMaterialList = rawMaterialService.getRawMaterialList(currentProduct.getId());
		}
		refreshViewSupply();
		refreshSupplyTypePopup();
		refreshViewRawMaterial();
		refreshRawMaterialTypePopup();
	}

	private void refreshViewPiece() {
		if (currentPiece == null) {// no se esta editando ninguna pieza
			pieceCreationBlock.setVisible(false);
			processCreationBlock.setVisible(false);
			deletePieceButton.setDisabled(true);
			pieceCopyButton.setDisabled(false);
			// limpiar form pieza
			pieceNameTextbox.setText("");
			pieceGroupCheckbox.setChecked(false);
			// seleccionamos metros y pulgadas como valores predeterminados de las dimensiones de las piezas
			Integer idMeasureUnitMeters = measureUnitService.getMeasureUnit("Metros").getId();
			MeasureUnit meters = measureUnitService.getMeasureUnit(idMeasureUnitMeters);
			Integer idMeasureUnitInch = measureUnitService.getMeasureUnit("Pulgadas").getId();
			MeasureUnit inch = measureUnitService.getMeasureUnit(idMeasureUnitInch);
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
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getLengthIdMeasureUnit())));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getDepthIdMeasureUnit())));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getWidthIdMeasureUnit())));
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

			listboxProcessList = getListboxProcessList(currentPiece.getId());// actualizamos la lista de procesos del Listbox con la lista de procesos obtenida de los procesos totales
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
				if(process.getIdProcessType().equals(processType.getId())) {
					return process;
				}
			}
		}
		return null;
	}

	private Process updateProcessFromListbox(Process other) {
		if(listboxProcessList != null) {
			int size = listboxProcessList.size();
			for(int i = 0; i < size; i++) {
				Process t = listboxProcessList.get(i);
				if(t.getIdPiece().equals(other.getIdPiece()) && t.getIdProcessType().equals(other.getIdProcessType())){
					other = listboxProcessList.set(i, other);
					return other;
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
			System.out.println("deberia estar en null: " + aux.getIdProcessType());// porque se esta agregando
		} else {
			Duration duration = null;
			try {
				duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 0, 0, 0);
			} catch (DatatypeConfigurationException e) {
				System.out.println("Error en convertir a duracion: " + e.toString());
			}
			listboxProcessList.add(new Process(null, null,  processType.getId(), "", duration));
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

	private int getLastPieceId() {
		int piece_id = 0;
		int size = pieceList.size();
		for(int i = 0; i < size; i++) {
			Piece t = pieceList.get(i);
			if(piece_id < t.getId()) { // asignamos el mas alto a la variable piece id
				piece_id = t.getId(); 
			}
		}
		return piece_id;
	}

	private  Piece updatePieceList(Piece piece) {
		if(piece.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id piece");
		} else {
			piece = Piece.clone(piece);
			int size = pieceList.size();
			for(int i = 0; i < size; i++) {
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())) {
					pieceList.set(i, piece);
					return piece;
				}
			}
			throw new RuntimeException("Piece not found " + piece.getId());
		}
	}

	private void deletePiece(Piece piece) {
		if(piece.getId() != null) {
			//eliminamos los procesos vinculados a esta pieza
			List<Process> deleteProcessList = new ArrayList<Process>();
			for(Process auxProcess:processList) {
				if(auxProcess.getIdPiece().equals(piece.getId())) {
					deleteProcessList.add(auxProcess);// no podemos eliminar directamte mientras se recorre la lista porque se la modifica
				}
			}
			for(Process auxProcess:deleteProcessList) {
				processList.remove(auxProcess);// eliminamos los procesos de la pieza
			}
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

	private List<Process> getProcessList(Integer idProduct) {// buscar todos los procesos de ese producto
		List<Process> list = new ArrayList<Process>();
		List<Piece> auxPieceList = pieceService.getPieceList(idProduct);
		for(Piece piece : auxPieceList) {
			List<Process> auxProcessList = processService.getProcessList(piece.getId());
			for(Process process : auxProcessList) {
				list.add(Process.clone(process));
			}
		}
		return list;
	}

	private List<Process> getListboxProcessList(Integer idPiece) {// buscar todos los procesos de la pieza en la lista total de procesos
		List<Process> list = new ArrayList<Process>();
		for(Process process : processList) {
			if(idPiece.equals(process.getIdPiece())) {
				list.add(Process.clone(process));
			}
		}
		return list;
	}

	private  Process updateProcessList(Process process) {
		if(process.getIdPiece() == null && process.getIdProcessType() == null) {
			throw new IllegalArgumentException("can't update a null-id process");
		}else {
			process = Process.clone(process);
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getIdPiece().equals(process.getIdPiece()) && t.getIdProcessType().equals(process.getIdProcessType())){
					processList.set(i, process);
					return process;
				}
			}
			throw new RuntimeException("Process not found " + process.getIdPiece()+" "+process.getIdProcessType());
		}
	}

	private void deleteProcess(Process process) {
		if(process.getIdPiece()!=null && process.getIdProcessType()!=null) {
			Process deleteProcess = null;
			for(Process auxProcess:processList) {
				if(auxProcess.getIdPiece().equals(process.getIdPiece()) && auxProcess.getIdProcessType().equals(process.getIdProcessType())) {
					deleteProcess = auxProcess;// no podemos eliminar directamte mientras se recorre la lista porque se la modifica
				}
			}
			if(deleteProcess != null) {
				processList.remove(deleteProcess);
				return;
			}
		}
	}

	public String quantityOfProcess(int idPiece) {
		int quantity = 0;
		if(processList != null && processList.isEmpty() == false) {
			for(int i=0; i<processList.size(); i++) {
				if(processList.get(i).getIdPiece() == idPiece) {
					quantity++;
				}
			}
		}
		return "" + quantity;
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

	@Listen("onClick = #deleteProductButton")
	public void deleteProduct() {
		if(currentProduct != null) {
			Messagebox.show("Esta seguro que quiere eliminar el producto? Se eliminaran las piezas y procesos relacionados", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						// la eliminacion de las piezas y procesos relacionados al producto se realizan en el servicio
						productService.deleteProduct(currentProduct);
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

	private Process searchProcess(Piece piece, ProcessType processType) {
		int size = processList.size();
		for(int i = 0; i < size; i++) {
			Process t = processList.get(i);
			if(t.getIdPiece().equals(piece.getId()) && t.getIdProcessType().equals(processType.getId())) {
				return Process.clone(t);
			}
		}
		return null;
	}

	@Listen("onClick = #pieceCopyButton")
	public void doPieceCopyButtonClick() {
		createNewPiece();
		// mostramos el modal para seleccionar la pieza
		Window window = (Window)Executions.createComponents(
				"/piece_selection_modal.zul", null, null);
		window.doModal();
	}

	private void fillPieceCopy(Piece pieceCopy) {
		pieceNameTextbox.setText(pieceCopy.getName());
		pieceGroupCheckbox.setChecked(pieceCopy.isGroup());
		lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(pieceCopy.getLengthIdMeasureUnit())));
		depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(pieceCopy.getDepthIdMeasureUnit())));
		widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(pieceCopy.getWidthIdMeasureUnit())));
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
		List<Process> processListCopy = processService.getProcessList(pieceCopy.getId());
		for(Process processCopy : processListCopy) {
			listboxProcessList.add(new Process(null, null,  processCopy.getIdProcessType(), processCopy.getDetails(), processCopy.getTime()));
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
			currentSupplyType = supplyTypeService.getSupplyType(currentSupply.getIdSupplyType());
			supplyTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			supplyTypeBandbox.setValue(currentSupplyType.getDescription());
			supplyQuantityDoublebox.setValue(currentSupply.getQuantity());
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
		supplyTypePopupList = supplyTypeService.getSupplyTypeList();
		for(Supply supply : supplyList) {
			SupplyType aux = supplyTypeService.getSupplyType(supply.getIdSupplyType());
			supplyTypePopupList.remove(aux);// sacamos del popup
		}
		supplyTypePopupListModel = new ListModelList<SupplyType>(supplyTypePopupList);
		supplyTypePopupListbox.setModel(supplyTypePopupListModel);
	}

	@Listen("onSelect = #supplyTypePopupListbox")
	public void selectionSupplyTypePopupListbox() {
		currentSupplyType = (SupplyType) supplyTypePopupListbox.getSelectedItem().getValue();
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
				currentSupply = supplyListModel.getSelection().iterator().next();
				currentSupplyType = supplyTypeService.getSupplyType(currentSupply.getIdSupplyType());
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

	@Listen("onClick = #deleteSupplyButton")
	public void deleteSupply() {
		if(currentSupply != null) {
			Messagebox.show("Esta seguro que desea eliminar " + supplyTypeService.getSupplyType(currentSupply.getIdSupplyType()).getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
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
		int supplyTypeId = currentSupplyType.getId();
		double supplyQuantity = supplyQuantityDoublebox.getValue();
		if(currentSupply == null) { // es nuevo
			if(aux != null) {// si ya estaba en un detalle
				aux.setQuantity(supplyQuantity);;
				supplyList.add(aux);
			} else {
				// se crea un detalle sin id porque recien se le asignara uno al momento de grabarse definitivamente
				currentSupply = new Supply(null, null, supplyTypeId, supplyQuantity);
				supplyList.add(currentSupply);
			}
		} else { // se edita
			currentSupply.setIdSupplyType(supplyTypeId);;
			currentSupply.setQuantity(supplyQuantity);
			updateSupplyList(currentSupply);// actualizamos la lista
		}
		refreshSupplyTypePopup();// actualizamos el popup
		currentSupply = null;
		refreshViewSupply();
	}

	private Supply updateSupplyList(Supply supply) {
		if(supply.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id Supply");
		} else {
			supply = Supply.clone(supply);
			int size = supplyList.size();
			for(int i = 0; i < size; i++) {
				Supply t = supplyList.get(i);
				if(t.getIdSupplyType().equals(supply.getIdSupplyType())) {
					supplyList.set(i, supply);
					return supply;
				}
			}
			throw new RuntimeException("Supply not found " + supply.getId());
		}
	}

	public SupplyType getSupplyType(int idSupplyType) {
		return supplyTypeService.getSupplyType(idSupplyType);
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
			currentRawMaterialType = rawMaterialTypeService.getRawMaterialType(currentRawMaterial.getIdRawMaterialType());
			rawMaterialTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			rawMaterialTypeBandbox.setValue(currentRawMaterialType.getName());
			rawMaterialQuantityDoublebox.setValue(currentRawMaterial.getQuantity());
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
		rawMaterialTypePopupList = rawMaterialTypeService.getRawMaterialTypeList();
		for(RawMaterial rawMaterial : rawMaterialList) {
			RawMaterialType aux = rawMaterialTypeService.getRawMaterialType(rawMaterial.getIdRawMaterialType());
			rawMaterialTypePopupList.remove(aux);// sacamos del popup
		}
		rawMaterialTypePopupListModel = new ListModelList<RawMaterialType>(rawMaterialTypePopupList);
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
				currentRawMaterial = rawMaterialListModel.getSelection().iterator().next();
				currentRawMaterialType = rawMaterialTypeService.getRawMaterialType(currentRawMaterial.getIdRawMaterialType());
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

	@Listen("onClick = #deleteRawMaterialButton")
	public void deleteRawMaterial() {
		if(currentRawMaterial != null) {
			Messagebox.show("Esta seguro que desea eliminar " + rawMaterialTypeService.getRawMaterialType(currentRawMaterial.getIdRawMaterialType()).getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
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
		int rawMaterialTypeId = currentRawMaterialType.getId();
		double rawMaterialQuantity = rawMaterialQuantityDoublebox.getValue();
		if(currentRawMaterial == null) { // es nuevo
			if(aux != null) {// si ya estaba en un detalle
				aux.setQuantity(rawMaterialQuantity);;
				rawMaterialList.add(aux);
			} else {
				// se crea un detalle sin id porque recien se le asignara uno al momento de grabarse definitivamente
				currentRawMaterial = new RawMaterial(null, null, rawMaterialTypeId, rawMaterialQuantity);
				rawMaterialList.add(currentRawMaterial);
			}
		} else { // se edita
			currentRawMaterial.setIdRawMaterialType(rawMaterialTypeId);;
			currentRawMaterial.setQuantity(rawMaterialQuantity);
			updateRawMaterialList(currentRawMaterial);// actualizamos la lista
		}
		refreshRawMaterialTypePopup();// actualizamos el popup
		currentRawMaterial = null;
		refreshViewRawMaterial();
	}

	private RawMaterial updateRawMaterialList(RawMaterial rawMaterial) {
		if(rawMaterial.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id RawMaterial");
		} else {
			rawMaterial = RawMaterial.clone(rawMaterial);
			int size = rawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RawMaterial t = rawMaterialList.get(i);
				if(t.getIdRawMaterialType().equals(rawMaterial.getIdRawMaterialType())) {
					rawMaterialList.set(i, rawMaterial);
					return rawMaterial;
				}
			}
			throw new RuntimeException("RawMaterial not found " + rawMaterial.getId());
		}
	}

	public RawMaterialType getRawMaterialType(int idRawMaterialType) {
		return rawMaterialTypeService.getRawMaterialType(idRawMaterialType);
	}

	public String getMeasureUnitName(int idMeasureUnit) {
		if(measureUnitService.getMeasureUnit(idMeasureUnit) != null) {
			return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}
}

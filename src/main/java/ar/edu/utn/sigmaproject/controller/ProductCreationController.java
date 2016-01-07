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
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

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
     
    // services
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	private MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
	
    // attributes
    private Product currentProduct;
	private Piece currentPiece;
	private EventQueue eq;
	
	// list
	private List<Piece> pieceList;
	private List<Process> processList;
	private List<ProcessType> processTypeList;
	
	// list models
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<Piece> pieceListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
    private ListModelList<MeasureUnit> depthMeasureUnitListModel;
    private ListModelList<MeasureUnit> widthMeasureUnitListModel;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        processTypeList = processTypeService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
        
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
        refreshViewProduct();
        
        // agregamos un listener para cuando se seleccione una pieza en el modal de copia de otra pieza
        eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
        eq.subscribe(new EventListener() {
            public void onEvent(Event event) throws Exception {
            	Piece value = (Piece)event.getData();
            	fillPieceCopy(value);
            }
        });
    }
    
    @Listen("onClick = #saveProductButton")
    public void saveProduct() {
    	if(Strings.isBlank(productNameTextbox.getValue())){
			Clients.showNotification("Ingresar Nombre Producto", productNameTextbox);
			return;
		}
    	String product_name = productNameTextbox.getText();
    	String product_details = productDetailsTextbox.getText();
        String product_code = productCodeTextbox.getText();
        BigDecimal product_price = new BigDecimal(productPriceDoublebox.doubleValue());
    	
    	if(currentProduct == null) {// se esta creando un nuevo producto
    		currentProduct = new Product(null, product_code, product_name, product_details, product_price);
    		productService.saveProduct(currentProduct, pieceList, processList);
    	} else {// se esta editando un producto
    		currentProduct.setName(product_name);
    		currentProduct.setDetails(product_details);
            currentProduct.setCode(product_code);
            currentProduct.setPrice(product_price);;
    		currentProduct = productService.updateProduct(currentProduct, pieceList, processList);
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
    	processCreationBlock.setVisible(false);
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
    	pieceCreationBlock.setVisible(false);
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
    	Integer piece_id = 0;
    	String piece_name = pieceNameTextbox.getText();
    	Integer length_id_measure_unit = null;
    	Integer depth_id_measure_unit = null;
    	Integer width_id_measure_unit = null;
    	if(lengthMeasureUnitSelectbox.getSelectedIndex() != -1) {
    		length_id_measure_unit = lengthMeasureUnitListModel.getElementAt(lengthMeasureUnitSelectbox.getSelectedIndex()).getId();
    	}
    	if(depthMeasureUnitSelectbox.getSelectedIndex() != -1) {
    		depth_id_measure_unit = depthMeasureUnitListModel.getElementAt(depthMeasureUnitSelectbox.getSelectedIndex()).getId();
    	}
    	if(widthMeasureUnitSelectbox.getSelectedIndex() != -1) {
    		width_id_measure_unit = widthMeasureUnitListModel.getElementAt(widthMeasureUnitSelectbox.getSelectedIndex()).getId();
    	}
    	BigDecimal piece_length = new BigDecimal(pieceLengthDoublebox.doubleValue());
    	BigDecimal piece_depth = new BigDecimal(pieceDepthDoublebox.doubleValue());
    	BigDecimal piece_width = new BigDecimal(pieceWidthDoublebox.doubleValue());
    	String piece_size = pieceSizeTextbox.getText();
    	Integer piece_units = pieceUnitsByProductIntbox.getValue();
    	boolean piece_isGroup = pieceGroupCheckbox.isChecked();
    	
    	if(currentPiece == null) { // no se esta editando una pieza
    		Integer serviceNewPieceId = pieceService.getNewId();
    		if(pieceList.isEmpty() == true) {// buscamos un id para la pieza nueva
        		piece_id = serviceNewPieceId;
        	} else {
        		piece_id = getLastPieceId() + 1;// buscamos el ultimo id y sumamos 1
        		if(piece_id < serviceNewPieceId) { // si el ultimo id es menor que uno nuevo del servicio quiere decir que las piezas en la lista son viejas y hay que agarra el id mas grande osea el que viene del servicio
        			piece_id = serviceNewPieceId;
        		}
        	}
    		currentPiece = new Piece(piece_id, null, piece_name, piece_length, length_id_measure_unit, piece_depth, depth_id_measure_unit, piece_width, width_id_measure_unit, piece_size, piece_isGroup, piece_units);
    		pieceList.add(currentPiece);// lo agregamos a la lista
        	pieceListModel.add(currentPiece);
        	pieceListbox.setModel(pieceListModel);// y al modelo para que aparezca en la pantalla
    	} else { // se esta editando una pieza
    	    currentPiece.setName(piece_name);
    		currentPiece.setLength(piece_length);
    		currentPiece.setLengthIdMeasureUnit(length_id_measure_unit);
    		currentPiece.setDepth(piece_depth);
    		currentPiece.setDepthIdMeasureUnit(depth_id_measure_unit);
    		currentPiece.setWidth(piece_width);
    		currentPiece.setWidthIdMeasureUnit(width_id_measure_unit);
    		currentPiece.setSize(piece_size);
    		currentPiece.setUnits(piece_units);
    		currentPiece.setGroup(piece_isGroup);
    		updatePieceList(currentPiece);// actualizamos la lista
    		pieceListModel = new ListModelList<Piece>(pieceList); 
    		pieceListbox.setModel(pieceListModel);// actualizamos el modelo para que aparezca en la pantalla
    	}
    	
    	// actualizamos la lista de procesos
    	for(int i = 1; i < processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
    		Label lbl = (Label)processListbox.getChildren().get(i).getChildren().get(1).getChildren().get(0);
    		Textbox txtboxDetails = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
    		Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
      		Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
      		Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
    		Integer idPiece = currentPiece.getId();
			Integer idProcessType = processTypeList.get(i - 1).getId(); // restamos 1 para empezar del indice 0
    		Process currentProcess = searchProcess(idPiece, idProcessType); // buscamos si ya esta creado
    		if(chkbox.isChecked()) {
    			String details = txtboxDetails.getText();
    			Integer days = intboxDays.intValue();
    			Integer hours = intboxHours.intValue();
    			Integer minutes = intboxMinutes.intValue();
    			Duration duration = null;
				try {
					duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, days, hours, minutes, 0);
				} catch (DatatypeConfigurationException e) {
					System.out.println("Error en finalizar pieza, en convertir a duracion: " + e.toString());
				}
    			if(currentProcess == null) { // no esta creado
	    			currentProcess = new Process(idPiece, idProcessType, details, duration);
	    			processList.add(currentProcess);
    			} else { // esta creado
    				currentProcess.setDetails(details);
    				currentProcess.setTime(duration);
    				currentProcess = updateProcessList(currentProcess);
    			}
    		} else {
    			if(currentProcess != null) { // esta creado pero el check en false, hay que eliminarlo
	    			deleteProcess(currentProcess);
    			}
    		}
    	}
    	// actualizamos el view
    	currentPiece = null;
    	refreshViewPiece();
    }
	
    //when user checks on the checkbox of each process on the list
  	@Listen("onProcessCheck = #processListbox")
  	public void doProcessCheck(ForwardEvent evt) {// se usa para mostrar u ocultar el ingreso de texo en base al checkbox
  		//get data from event
  		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
  		Listitem litem = (Listitem)cbox.getParent().getParent();
  		Textbox txtboxDetails = (Textbox)litem.getChildren().get(2).getFirstChild();
  		Intbox intboxDays = (Intbox)litem.getChildren().get(3).getFirstChild();
  		Intbox intboxHours = (Intbox)litem.getChildren().get(4).getFirstChild();
  		Intbox intboxMinutes = (Intbox)litem.getChildren().get(5).getFirstChild();
  		if(cbox.isChecked()) {
  			txtboxDetails.setVisible(true);
  			intboxDays.setVisible(true);
  			intboxHours.setVisible(true);
  			intboxMinutes.setVisible(true);
  		} else {
  			txtboxDetails.setVisible(false);
  			intboxDays.setVisible(false);
  			intboxHours.setVisible(false);
  			intboxMinutes.setVisible(false);
  		}
  	}
  	
  	/* este metodo era para hacer invisible el ingreso de valores si la pieza es grupo
  	@Listen("onCheck = #pieceGroup")
  	public void doPieceGroupCheck() {
  		Row measureUnitRow = (Row)(measureUnitSelectBox.getParent());
  		Row pieceHeightRow = (Row)(pieceHeight.getParent());
  		Row pieceWidthRow = (Row)(pieceWidth.getParent());
  		Row pieceDepthRow = (Row)(pieceDepth.getParent());
  		Row pieceSize1Row = (Row)(pieceSize1.getParent());
  		Row pieceSize2Row = (Row)(pieceSize2.getParent());
  		if(pieceGroup.isChecked()) { // se muestran solo las medidas principales si es grupo
  			//pieceHeightRow.setVisible(false);
  			//pieceWidthRow.setVisible(false);
  			//pieceDepthRow.setVisible(false);
  	    	pieceSize1Row.setVisible(false);
  	    	pieceSize2Row.setVisible(false);
  		} else {
  			//pieceHeightRow.setVisible(true);
  			//pieceWidthRow.setVisible(true);
  			//pieceDepthRow.setVisible(true);
  	    	pieceSize1Row.setVisible(true);
  	    	pieceSize2Row.setVisible(true);
  		}
  	}
  	*/
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
  		}
  	}
  	
  	private void refreshViewPiece() {
  		if (currentPiece == null) {// no se esta editando ninguna pieza
  			pieceCreationBlock.setVisible(false);
  			processCreationBlock.setVisible(false);
  			deletePieceButton.setDisabled(true);
  	    	// limpiar form pieza
  	    	pieceNameTextbox.setText("");
  	    	pieceGroupCheckbox.setChecked(false);
  	    	// seleccionamos metros y pulgadas como valores predeterminados de las dimensiones de las piezas
  	    	Integer id_measure_unit_meters = measureUnitService.getMeasureUnit("Metros").getId();
  	    	MeasureUnit meters = measureUnitService.getMeasureUnit(id_measure_unit_meters);
  	    	Integer id_measure_unit_inch = measureUnitService.getMeasureUnit("Pulgadas").getId();
  	    	MeasureUnit inch = measureUnitService.getMeasureUnit(id_measure_unit_inch);
    		lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(meters));
  	        depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(inch));
  	        widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(inch));
  	    	pieceLengthDoublebox.setValue(0);
  	    	pieceWidthDoublebox.setValue(0);
  	    	pieceDepthDoublebox.setValue(0);
  	    	pieceSizeTextbox.setText("");
  	    	pieceUnitsByProductIntbox.setValue(0);
  	    	// limpiar procesos (ponerlos en vacio y sin check)
  	    	for(int i=1; i<processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
  	    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
  	    		Textbox txtboxDetails = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
  	    		Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
  	    		Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
  	    		Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
  	    		
  	    		chkbox.setChecked(false);
  	    		txtboxDetails.setVisible(false);
  	    		intboxDays.setVisible(false);
  	    		intboxHours.setVisible(false);
  	    		intboxMinutes.setVisible(false);
  	    		txtboxDetails.setText("");
  	    		intboxDays.setValue(0);
  	    		intboxHours.setValue(0);
  	    		intboxMinutes.setValue(0);
  	    	}
  		} else { // se esta editando una pieza
  			pieceCreationBlock.setVisible(true);
  	    	processCreationBlock.setVisible(true);
  	    	deletePieceButton.setDisabled(false);
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
  	    	// cargar procesos (cargar detalles, tiempos y checks)
  	    	processTypeList = processTypeService.getProcessTypeList();
  	    	// recorremos los elementos del DOM
  	    	for(int i = 1; i < processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
  	    		// obtenemos las referencias a los elementos
  	    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
  	    		Textbox txtboxDetails = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
  	    		Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
  	    		Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
  	    		Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
  	    		// cargamos los valores, de los procesos a los elementos
  	    		Process currentProcess = null;
  	    		for(int j = 0; j < processList.size(); j++) {
  	    			if(processList.get(j).getIdPiece().equals(currentPiece.getId()) && processList.get(j).getIdProcessType().equals(processTypeList.get(i - 1).getId())) { // el tipo de proceso i -1 es para empezar desde el indice 0
  	    				currentProcess = processList.get(j);
  	    			}
  	    		}
  	    		if(currentProcess == null) { //si no se encontro el proceso esta en null
  	    			chkbox.setChecked(false);
  	    			txtboxDetails.setVisible(false);
  	  	    		intboxDays.setVisible(false);
  	  	    		intboxHours.setVisible(false);
  	  	    		intboxMinutes.setVisible(false);
  	    			txtboxDetails.setText("");
  	    			intboxDays.setValue(0);
  	  	    		intboxHours.setValue(0);
  	  	    		intboxMinutes.setValue(0);
  	    		} else {
  	    			chkbox.setChecked(true);
  	    			txtboxDetails.setVisible(true);
  	    			intboxDays.setVisible(true);
  	  	    		intboxHours.setVisible(true);
  	  	    		intboxMinutes.setVisible(true);
  	    			txtboxDetails.setText(currentProcess.getDetails());
  	    			intboxDays.setValue(currentProcess.getTime().getDays());
  	  	    		intboxHours.setValue(currentProcess.getTime().getHours());
  	  	    		intboxMinutes.setValue(currentProcess.getTime().getMinutes());
  	    		}
  	    	}
  		}
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
			currentPiece = pieceListModel.getSelection().iterator().next();
		}
		pieceListModel.clearSelection();
		refreshViewPiece();
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
  		refreshViewPiece();
  	}
  	
  	@Listen("onClick = #resetPieceButton")
    public void resetPiece() {
  		refreshViewPiece();
  		pieceCreationBlock.setVisible(true);
  	}
  	
  	private Process searchProcess(Integer idPiece, Integer idProcessType) {
  		int size = processList.size();
  		for(int i = 0; i < size; i++) {
  			Process t = processList.get(i);
  			if(t.getIdPiece().equals(idPiece) && t.getIdProcessType().equals(idProcessType)) {
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
  	
  	private void fillPieceCopy(Piece piece) {
  		pieceNameTextbox.setText(piece.getName());
    	pieceGroupCheckbox.setChecked(piece.isGroup());
    	lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getLengthIdMeasureUnit())));
    	depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getDepthIdMeasureUnit())));
    	widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getWidthIdMeasureUnit())));
    	BigDecimal lenght = piece.getLength();
		if(lenght != null) {
			pieceLengthDoublebox.setValue(lenght.doubleValue());
		} else {
			pieceLengthDoublebox.setValue(0);
		}
		BigDecimal depth = piece.getDepth();
		if(depth != null) {
			pieceDepthDoublebox.setValue(depth.doubleValue());
		} else {
			pieceDepthDoublebox.setValue(0);
		}
		BigDecimal width = piece.getWidth();
		if(width != null) {
			pieceWidthDoublebox.setValue(width.doubleValue());
		} else {
			pieceWidthDoublebox.setValue(0);
		}
    	pieceSizeTextbox.setValue(piece.getSize());
    	pieceUnitsByProductIntbox.setValue(piece.getUnits());
  	}
  	
}

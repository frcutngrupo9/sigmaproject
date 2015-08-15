package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;

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
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;

public class ProductCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
	Component productCreationBlock;
	@Wire
	Textbox productName;
	@Wire
	Textbox productDetails;
	@Wire
	Button createPieceButton;
	@Wire
	Button saveProductButton;
	
	@Wire
	Component pieceCreationBlock;
	@Wire
	Textbox pieceName;
	@Wire
	Doublebox pieceHeight;
	@Wire
	Doublebox pieceDepth;
	@Wire
	Doublebox pieceWidth;
	@Wire
	Doublebox pieceSize1;
	@Wire
	Doublebox pieceSize2;
	@Wire
	Checkbox pieceGroup;
	@Wire
	Intbox pieceUnitsByProduct;
	@Wire
	Button createProcessButton;
	@Wire
	Button cancelPieceButton;
	@Wire
    Selectbox measureUnitSelectBox;
	
	@Wire
	Component processCreationBlock;
	@Wire
	Listbox processListbox;
	@Wire
	Listbox pieceListbox;
     
    // services
	ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	ProcessService processService = new ProcessServiceImpl();
	PieceService pieceService = new PieceServiceImpl();
	ProductService productService = new ProductServiceImpl();
	MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
	
    ListModelList<ProcessType> processTypeListModel;
    ListModelList<Piece> pieceListModel;
    ListModelList<MeasureUnit> measureUnitListModel;
    
    // atributes
    private Product product;
	private Piece currentPiece;
	private List<Piece> pieceList;
	private List<Process> processList;
	private List<ProcessType> processTypeList;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
//        System.out.println("-adentro de doAfterCompose-");
        processTypeList = processTypeService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
        
        pieceList = new ArrayList<Piece>();
        List<Piece> productPiecesList = pieceList;
        pieceListModel = new ListModelList<Piece>(productPiecesList);
        pieceListbox.setModel(pieceListModel);
        
        processList = new ArrayList<Process>();
        
        Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
        List<MeasureUnit> measureUnitlList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectBox.setModel(measureUnitListModel);
        
        product = null;
        currentPiece = null;
    }
    
    @Listen("onClick = #saveProductButton")
    public void SaveProduct() {
    	if(Strings.isBlank(productName.getValue())){
			Clients.showNotification("Ingresar Nombre Producto",productName);
			return;
		}
    	Integer product_id = productService.getNewId();
    	String product_name = productName.getText();
    	String product_details = productDetails.getText();
    	product = new Product(product_id, product_name, product_details);
    	
    	//save
    	product = productService.updateProduct(product);
    	if(pieceList != null && pieceList.isEmpty() == false) {
    		for(int i=0; i<pieceList.size(); i++) {
    			pieceList.get(i).setIdProduct(product.getId());
    			pieceService.savePiece(pieceList.get(i));
    		}
    	}
    	if(processList != null && processList.isEmpty() == false) {
    		for(int i=0; i<processList.size(); i++) {
    			processService.saveProcess(processList.get(i));
    		}
    	}
    	
		// mostrar mensaje al user
		Clients.showNotification("Producto guardado");
		
		// limpiar todo
		product = null;
		pieceList = new ArrayList<Piece>();
		processList = new ArrayList<Process>();
		productName.setText("");
		productDetails.setText("");
		pieceListModel.clear();
    }
    
    @Listen("onClick = #createPieceButton")
    public void createNewPiece() {
    	pieceCreationBlock.setVisible(true);
    }
    
    @Listen("onClick = #cancelPieceButton")
    public void cancelPiece() {
    	pieceCreationBlock.setVisible(false);
    	processCreationBlock.setVisible(false);
    }
    
    @Listen("onClick = #createProcessButton")
    public void createNewProcess() {
    	if(Strings.isBlank(pieceName.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Pieza", pieceName);
			return;
		}
    	if(pieceUnitsByProduct.getValue() == null || pieceUnitsByProduct.getValue() <= 0){
			Clients.showNotification("La cantidad debe ser mayor a 0.", pieceUnitsByProduct);
			return;
		}
    	Integer piece_id = 0;
    	if(pieceList.isEmpty() == true) {
    		piece_id = pieceService.getNewId();
    	} else {
    		//buscamos el ultimo id y sumamos 1
    		piece_id = pieceList.get(pieceList.size()-1).getId() + 1;
    		//si se esta editando un producto habria que buscar un nuevo id del servicio
    		// porque si las piezas que se estan editando no son las ultimas, 
    		// entonces el nuevo id va a ser duplicado con otras piezas guardadas antes
    	}
    	
    	String piece_name = pieceName.getText();
    	
    	Integer idMeasureUnit = null;
    	if(measureUnitSelectBox.getSelectedIndex() != -1) {
    		idMeasureUnit = measureUnitListModel.getElementAt(measureUnitSelectBox.getSelectedIndex()).getId();
    	}

    	BigDecimal piece_height = new BigDecimal(pieceHeight.doubleValue());
    	BigDecimal piece_width = new BigDecimal(pieceWidth.doubleValue());
    	BigDecimal piece_depth = new BigDecimal(pieceDepth.doubleValue());
    	BigDecimal piece_size1 = new BigDecimal(pieceSize1.doubleValue());
    	BigDecimal piece_size2 = new BigDecimal(pieceSize2.doubleValue());

    	Integer piece_units = pieceUnitsByProduct.getValue();
    	boolean piece_isGroup = pieceGroup.isChecked();
    	currentPiece = new Piece(piece_id, null, piece_name, idMeasureUnit, piece_height, piece_width, piece_depth, piece_size1, piece_size2, piece_isGroup, piece_units);
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
    	for(int i = 1; i < processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
    		Label lbl = (Label)processListbox.getChildren().get(i).getChildren().get(1).getChildren().get(0);
    		Textbox txtboxDetails = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
    		Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
      		Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
      		Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
    		if(chkbox.isChecked() && intboxDays.intValue() == 0 && intboxHours.intValue() == 0 && intboxMinutes.intValue() == 0){
    			Clients.showNotification("Ingrese el Tiempo para el Proceso", intboxMinutes, true);
    			return;
    		}
    		Process currentProcess = null;
    		if(chkbox.isChecked() && (intboxDays.intValue() != 0 || intboxHours.intValue() != 0 || intboxMinutes.intValue() != 0)){
    			int idPiece = currentPiece.getId();
    			int idProcessType = processTypeService.getProcessTypeList().get(i - 1).getId();
    			String details = txtboxDetails.getText();
    			Integer days = intboxDays.intValue();
    			Integer hours = intboxHours.intValue();
    			Integer minutes = intboxMinutes.intValue();
    			Duration duration = null;
				try {
					duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, days, hours, minutes, 0);
				} catch (DatatypeConfigurationException e) {
					System.out.println("Error en grabar duracion del proceso: " + e.toString());
				}
    			currentProcess = new Process(idPiece, idProcessType, details, duration);
    		}
    		if(currentProcess != null) {
    			processList.add(currentProcess);
    		}
    	}
    	//agregamos la pieza a la lista y actualizamos el view
    	pieceList.add(currentPiece);
    	pieceListModel.add(currentPiece);
    	currentPiece = null;
    	refreshView();
    }
	
    //when user checks on the checkbox of each process on the list
  	@Listen("onProcessCheck = #processListbox")
  	public void doProcessCheck(ForwardEvent evt){
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
  	
  	/*
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
    
  	private void refreshView() {
  		if (currentPiece == null) {
  			pieceCreationBlock.setVisible(false);
  	    	processCreationBlock.setVisible(false);
  	    	// limpiar form pieza
  	    	pieceName.setText("");
  	    	pieceGroup.setChecked(false);
  	    	measureUnitSelectBox.setSelectedIndex(-1);
  	    	pieceHeight.setValue(0);
  	    	pieceWidth.setValue(0);
  	    	pieceDepth.setValue(0);
  	    	pieceSize1.setValue(0);
  	    	pieceSize2.setValue(0);
  	    	pieceUnitsByProduct.setValue(0);
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
  		} else {
  			pieceCreationBlock.setVisible(true);
  	    	processCreationBlock.setVisible(true);
  	    	// cargar form pieza
  	    	pieceName.setText(currentPiece.getName());
  	    	pieceGroup.setChecked(currentPiece.isGroup());
  	    	measureUnitSelectBox.setSelectedIndex(measureUnitListModel.indexOf(measureUnitService.getMeasureUnit(currentPiece.getIdMeasureUnit())));
  	    	pieceHeight.setValue(currentPiece.getHeight().doubleValue());
  	    	pieceWidth.setValue(currentPiece.getWidth().doubleValue());
  	    	pieceDepth.setValue(currentPiece.getDepth().doubleValue());
  	    	pieceSize1.setValue(currentPiece.getSize1().doubleValue());
  	    	pieceSize2.setValue(currentPiece.getSize2().doubleValue());
  	    	pieceUnitsByProduct.setValue(currentPiece.getUnits());
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
	public void doListBoxSelect() {
		if(pieceListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentPiece = null;
		}else {
			currentPiece = pieceListModel.getSelection().iterator().next();
		}
		refreshView();
	}
}

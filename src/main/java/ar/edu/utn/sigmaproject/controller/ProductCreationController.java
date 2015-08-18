package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
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
import org.zkoss.zul.Messagebox;
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
    private Product currentProduct;
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
        pieceListModel = new ListModelList<Piece>(pieceList);
        pieceListbox.setModel(pieceListModel);
        
        processList = new ArrayList<Process>();
        
        Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
        List<MeasureUnit> measureUnitlList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectBox.setModel(measureUnitListModel);
        
        currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
        currentPiece = null;
        refreshViewProduct();
    }
    
    @Listen("onClick = #saveProductButton")
    public void SaveProduct() {
    	if(Strings.isBlank(productName.getValue())){
			Clients.showNotification("Ingresar Nombre Producto", productName);
			return;
		}
    	String product_name = productName.getText();
    	String product_details = productDetails.getText();
    	
    	if(currentProduct == null) {// se esta creando un nuevo producto
    		Integer product_id = productService.getNewId();
    		currentProduct = new Product(product_id, product_name, product_details);
    		productService.saveProduct(currentProduct);
    		if(pieceList != null && pieceList.isEmpty() == false) {// se guardan todas las piezas
        		for(int i = 0; i < pieceList.size(); i++) {
        			pieceList.get(i).setIdProduct(currentProduct.getId());// se le asigna el id del producto
        			pieceService.savePiece(pieceList.get(i));
        		}
        	}
        	if(processList != null && processList.isEmpty() == false) {// se guardan todos los procesos
        		for(int i = 0; i < processList.size(); i++) {
        			processService.saveProcess(processList.get(i));
        		}
        	}
    	} else {// se esta editando un producto
    		currentProduct.setName(product_name);
    		currentProduct.setDetails(product_details);
    		currentProduct = productService.updateProduct(currentProduct);
    		if(pieceList != null) {// se actualizan todas las piezas
    			// primero eliminamos las piezas que estan en el service, pero que no existen mas en el producto
    			List<Piece> auxPieceList = pieceService.getPieceList(currentProduct.getId());// obtenemos las piezas del producto que estan en el servicio
    			for(int i = 0; i < auxPieceList.size(); i++) {// recorremos todas las piezas obtenidas
    				Piece current = auxPieceList.get(i);
    				Piece aux = searchPiece(current.getId());
					if(aux == null) {// si la pieza no esta en la lista se debe eliminar del service
						// se deben eliminar del servicio los procesos relacionados a la pieza
						List<Process> deleteProcessList = processService.getProcessList(current.getId());
						if(deleteProcessList != null) {
							for(int j = 0; j < deleteProcessList.size(); j++) {
								processService.deleteProcess(deleteProcessList.get(j));// eliminamos todos los procesos
							}
						}
						pieceService.deletePiece(current);// eliminamos la pieza
					}
    			}
    			// ahora recorremos la lista para actualizar las piezas que ya existen o agregar las que no
        		for(int i = 0; i < pieceList.size(); i++) {
        			Piece current = pieceList.get(i);
        			Piece auxPiece = pieceService.getPiece(current.getId());// se busca a ver si existe la pieza
        			if(auxPiece == null) {// es una nueva pieza, se graba
        				current.setIdProduct(currentProduct.getId());// se le asigna el id del producto
        				pieceService.savePiece(current);
        			} else {// esta pieza existe, se actualiza
        				pieceService.updatePiece(current);
        			}
        		}
        	}
    		if(processList != null && processList.isEmpty() == false) {// se actualizan todos los procesos
    			// primero eliminamos los procesos que estan en el service, pero que no existen mas en el producto
    			for(int i = 0; i < pieceList.size(); i++) {
    				List<Process> auxProcessList = processService.getProcessList(pieceList.get(i).getId());
    				for(int j = 0; j < auxProcessList.size(); j++) {// recorremos todos los procesos de todas las piezas del producto
    					Process current = auxProcessList.get(j);
    					Process aux = searchProcess(current.getIdPiece(), current.getIdProcessType());
    					if(aux == null) {// si el proceso no esta en la lista se debe eliminar del service
    						processService.deleteProcess(current);
    					}
    				}
    			}
    			// ahora recorremos la lista para actualizar los procesos que ya existen o agregar los que no
    			for(int i = 0; i < processList.size(); i++) {
    				Integer pieceId = processList.get(i).getIdPiece();
    				Integer processTypeId = processList.get(i).getIdProcessType();
    				if(processService.getProcess(pieceId, processTypeId) == null) {// no existe, se guarda
    					processService.saveProcess(processList.get(i));
    				} else {// existe, se actualiza
    					processService.updateProcess(processList.get(i));
    				}
        		}
        	}
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
    	if(Strings.isBlank(pieceName.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Pieza", pieceName);
			return;
		}
    	if(pieceUnitsByProduct.getValue() == null || pieceUnitsByProduct.getValue() <= 0){
			Clients.showNotification("La cantidad debe ser mayor a 0.", pieceUnitsByProduct);
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
    	
    	if(currentPiece == null) { // no se esta editando una pieza
    		Integer serviceNewPieceId = pieceService.getNewId();
    		if(pieceList.isEmpty() == true) {
        		piece_id = serviceNewPieceId;
        	} else {
        		piece_id = getLastPieceId() + 1;// buscamos el ultimo id y sumamos 1
        		if(piece_id < serviceNewPieceId) { // si el ultimo id es menor que uno nuevo del servicio quiere decir que las piezas en la lista son viejas y hay que agarra el id mas grande osea el que viene del servicio
        			piece_id = serviceNewPieceId;
        		}
        	}
    		currentPiece = new Piece(piece_id, null, piece_name, idMeasureUnit, piece_height, piece_width, piece_depth, piece_size1, piece_size2, piece_isGroup, piece_units);
    		pieceList.add(currentPiece);// lo agregamos a la lista
        	pieceListModel.add(currentPiece);
        	pieceListbox.setModel(pieceListModel);// y al modelo para que aparezca en la pantalla
    	} else { // se esta editando una pieza
    		currentPiece.setIdMeasureUnit(idMeasureUnit);
    		currentPiece.setHeight(piece_height);
    		currentPiece.setWidth(piece_width);
    		currentPiece.setDepth(piece_depth);
    		currentPiece.setSize1(piece_size1);
    		currentPiece.setSize2(piece_size2);
    		currentPiece.setUnits(piece_units);
    		currentPiece.setGroup(piece_isGroup);
    		updatePiece(currentPiece);// actualizamos la lista
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
    				currentProcess = updateProcess(currentProcess);
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
  	private void refreshViewProduct() {
  		if (currentProduct == null) {
  			productName.setText("");
  			productDetails.setText("");
  			processList = new ArrayList<Process>();
  			pieceList = new ArrayList<Piece>();
  			pieceListModel = new ListModelList<Piece>(pieceList);
  	        pieceListbox.setModel(pieceListModel);
  		} else {
  			productName.setText(currentProduct.getName());
  			productDetails.setText(currentProduct.getDetails());
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
  		} else { // se esta editando una pieza
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
  	
  	private Piece searchPiece(Integer idPiece) {
  		int size = pieceList.size();
  		for(int i = 0; i < size; i++) {
  			Piece t = pieceList.get(i);
  			if(t.getId().equals(idPiece)) {
  				return Piece.clone(t);
  			}
  		}
  		return null;
    }
  	
  	private  Piece updatePiece(Piece piece) {
		if(piece.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id piece");
		}else {
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
			int size = pieceList.size();
			for(int i = 0; i < size; i++) {
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())) {
					pieceList.remove(i);
					return;
				}
			}
		}
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
  	
  	private  Process updateProcess(Process process) {
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
		if(process.getIdPiece()!=null && process.getIdProcessType()!=null){
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getIdPiece().equals(process.getIdPiece()) && t.getIdProcessType().equals(process.getIdProcessType())) {
					processList.remove(i);
					return;
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
		refreshViewPiece();
	}
  	
  	@Listen("onClick = #deleteProductButton")
    public void deleteProduct() {
  		if(currentProduct != null) {
  			Messagebox.show("Esta seguro que quiere eliminar el producto?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  			        	// eliminamos las piezas y procesos relacionados al producto
  			        	List<Piece> deletePieceList = pieceService.getPieceList(currentProduct.getId());// obtenemos todas las piezas del producto
  		    			for(int i = 0; i < deletePieceList.size(); i++) {// recorremos todas las piezas obtenidas
  		    				Piece current = deletePieceList.get(i);
  		    				List<Process> deleteProcessList = processService.getProcessList(current.getId());// buscamos los procesos de la pieza
  		    				for(int j = 0; j < deleteProcessList.size(); j++) {
  		    					processService.deleteProcess(deleteProcessList.get(j));// eliminamos todos los procesos
  		    				}
  		    				pieceService.deletePiece(current);// eliminamos la pieza
  		    			}
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
  			Messagebox.show("Esta seguro que quiere eliminar la pieza?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  			        	//eliminamos los procesos vinculados a esta pieza
  			        	for(int i = 0; i < processList.size(); i++) {
  			        		if(processList.get(i).getIdPiece().equals(currentPiece.getId())) {
  			        			processList.remove(i);// eliminamos los procesos de esa pieza
  			        		}
  			        	}
  			        	//eliminamos la pieza
  			        	pieceList.remove(currentPiece);
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
}

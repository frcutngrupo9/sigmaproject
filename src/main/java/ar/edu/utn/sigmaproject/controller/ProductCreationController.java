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
import org.zkoss.zk.ui.event.ForwardEvent;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
	Doublebox pieceHeightDoublebox;
	@Wire
	Doublebox pieceDepthDoublebox;
	@Wire
	Doublebox pieceWidthDoublebox;
	@Wire
	Doublebox pieceSize1Doublebox;
	@Wire
	Doublebox pieceSize2Doublebox;
	@Wire
	Checkbox pieceGroupCheckbox;
	@Wire
	Intbox pieceUnitsByProductIntbox;
	@Wire
	Button createProcessButton;
	@Wire
	Button cancelPieceButton;
	@Wire
    Selectbox measureUnitSelectBox;
	@Wire
    Combobox measurePresetCombobox;
	@Wire
	Component processCreationBlock;
	@Wire
	Listbox processListbox;
	@Wire
	Listbox pieceListbox;
	@Wire
	Caption productCaption;
     
    // services
	@WireVariable
	ProcessTypeRepository processTypeRepository;

	@WireVariable
	ProductRepository productRepository;

	@WireVariable
	MeasureUnitTypeRepository measureUnitTypeRepository;
	
    ListModelList<ProcessType> processTypeListModel;
    ListModelList<Piece> pieceListModel;
    ListModelList<MeasureUnit> measureUnitListModel;
    
    // atributes
    private Product currentProduct;
	private Piece currentPiece;
	private List<Piece> pieceList;
	private List<ProcessType> processTypeList;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
//        System.out.println("-adentro de doAfterCompose-");
        processTypeList = processTypeRepository.findAll();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
        
        pieceList = new ArrayList<Piece>();
        pieceListModel = new ListModelList<Piece>(pieceList);
        pieceListbox.setModel(pieceListModel);
        
        MeasureUnitType measureUnitType = measureUnitTypeRepository.findByName("Longitud");
        List<MeasureUnit> measureUnitlList = measureUnitType.getMeasureUnits();
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectBox.setModel(measureUnitListModel);
        
        fillComboMeasurePreset();
        
        currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
        currentPiece = null;
        refreshViewProduct();
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
    		currentProduct = new Product(pieceList, product_code, product_name, product_details, product_price);
    		
    	} else {// se esta editando un producto
    		currentProduct.setName(product_name);
    		currentProduct.setDetails(product_details);
            currentProduct.setCode(product_code);
            currentProduct.setPrice(product_price);
            currentProduct.setPieces(pieceList);
    	}
    	productRepository.save(currentProduct);
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
    	String piece_name = pieceNameTextbox.getText();
    	MeasureUnit measureUnit = null;
    	if(measureUnitSelectBox.getSelectedIndex() != -1) {
    		measureUnit = measureUnitListModel.getElementAt(measureUnitSelectBox.getSelectedIndex());
    	}
    	BigDecimal piece_height = new BigDecimal(pieceHeightDoublebox.doubleValue());
    	BigDecimal piece_width = new BigDecimal(pieceWidthDoublebox.doubleValue());
    	BigDecimal piece_depth = new BigDecimal(pieceDepthDoublebox.doubleValue());
    	BigDecimal piece_size1 = new BigDecimal(pieceSize1Doublebox.doubleValue());
    	BigDecimal piece_size2 = new BigDecimal(pieceSize2Doublebox.doubleValue());
    	Integer piece_units = pieceUnitsByProductIntbox.getValue();
    	boolean piece_isGroup = pieceGroupCheckbox.isChecked();
    	
    	if(currentPiece == null) { // no se esta editando una pieza
    		currentPiece = new Piece(currentProduct, piece_name, measureUnit, piece_height, piece_width, piece_depth, piece_size1, piece_size2, piece_isGroup, piece_units);
    		pieceList.add(currentPiece);// lo agregamos a la lista
        	pieceListModel.add(currentPiece);
        	pieceListbox.setModel(pieceListModel);// y al modelo para que aparezca en la pantalla
    	} else { // se esta editando una pieza
    	    currentPiece.setName(piece_name);
    		currentPiece.setMeasureUnit(measureUnit);
    		currentPiece.setHeight(piece_height);
    		currentPiece.setWidth(piece_width);
    		currentPiece.setDepth(piece_depth);
    		currentPiece.setSize1(piece_size1);
    		currentPiece.setSize2(piece_size2);
    		currentPiece.setUnits(piece_units);
    		currentPiece.setGroup(piece_isGroup);
    		updatePieceList(currentPiece);// actualizamos la lista
    		pieceListModel = new ListModelList<Piece>(pieceList); 
    		pieceListbox.setModel(pieceListModel);// actualizamos el modelo para que aparezca en la pantalla
    	}
    	
    	// actualizamos la lista de procesos
    	for(int i = 1; i < processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
    		Textbox txtboxDetails = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
    		Intbox intboxDays = (Intbox)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
      		Intbox intboxHours = (Intbox)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
      		Intbox intboxMinutes = (Intbox)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
    		ProcessType processType = processTypeList.get(i - 1); // restamos 1 para empezar del indice 0
    		Process currentProcess = searchProcess(currentPiece, processType); // buscamos si ya esta creado
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
    				// TODO: not sure if it's needed to set both relations
	    			currentProcess = new Process(currentPiece, processType, details, duration);
	    			currentPiece.getProcesses().add(currentProcess);
    			} else { // esta creado
    				currentProcess.setDetails(details);
    				currentProcess.setTime(duration);
    			}
    		} else {
    			if(currentProcess != null) { // esta creado pero el check en false, hay que eliminarlo
    				currentPiece.getProcesses().remove(currentProcess);
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
  			pieceList = currentProduct.getPieces();
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
  	    	measureUnitSelectBox.setSelectedIndex(-1);
  	    	pieceHeightDoublebox.setValue(0);
  	    	pieceWidthDoublebox.setValue(0);
  	    	pieceDepthDoublebox.setValue(0);
  	    	pieceSize1Doublebox.setValue(0);
  	    	pieceSize2Doublebox.setValue(0);
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
  	    	measureUnitSelectBox.setSelectedIndex(measureUnitListModel.indexOf(currentPiece.getMeasureUnit()));
  	    	pieceHeightDoublebox.setValue(currentPiece.getHeight().doubleValue());
  	    	pieceWidthDoublebox.setValue(currentPiece.getWidth().doubleValue());
  	    	pieceDepthDoublebox.setValue(currentPiece.getDepth().doubleValue());
  	    	pieceSize1Doublebox.setValue(currentPiece.getSize1().doubleValue());
  	    	pieceSize2Doublebox.setValue(currentPiece.getSize2().doubleValue());
  	    	pieceUnitsByProductIntbox.setValue(currentPiece.getUnits());
  	    	// cargar procesos (cargar detalles, tiempos y checks)
  	    	processTypeList = processTypeRepository.findAll();
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
  	    		for(int j = 0; j < currentPiece.getProcesses().size(); j++) {
  	    			if (currentPiece.getProcesses().get(j).getType().equals(processTypeList.get(i - 1))) { // el tipo de proceso i -1 es para empezar desde el indice 0
  	    				currentProcess = currentPiece.getProcesses().get(j);
  	    				break;
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
  	
  	private void fillComboMeasurePreset() {
  		String[] _presets = { 
			"3x2x40", "2x2x20", "3x2x45", "1x2x15", "2x2x50", "2x2x60", "4x2x45",
		};
		ListModel<String> presetsModel= new SimpleListModel<String>(_presets);
		measurePresetCombobox.setModel(presetsModel);
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
  			Messagebox.show("Esta seguro que quiere eliminar el producto? Se eliminaran las piezas y procesos relacionados", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener<Event>() {
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
  	
  	@Listen("onClick = #deletePieceButton")
    public void deletePiece() {
  		if(currentPiece != null) {
  			Messagebox.show("Esta seguro que desea eliminar la pieza " + currentPiece.getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener<Event>() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  			        	//eliminamos la pieza, los procesos tambien se eliminan en el metodo
  			        	currentProduct.getPieces().remove(currentPiece);
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
  	
  	private Process searchProcess(Piece piece, ProcessType processType) {
  		for (Process process : piece.getProcesses()) {
  			if(process.getType() != null && process.getType().equals(processType)) {
  				return Process.clone(process);
  			}  			
  		}
  		return null;
    }
}

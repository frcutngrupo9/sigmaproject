package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
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
	Textbox pieceHeight;
	@Wire
	Textbox pieceDepth;
	@Wire
	Textbox pieceWidth;
	@Wire
	Textbox pieceSize1;
	@Wire
	Textbox pieceSize2;
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
	Listbox productPiecesListbox;
     
    // services
	ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	ProcessService processService = new ProcessServiceImpl();
	PieceService pieceService = new PieceServiceImpl();
	ProductService productService = new ProductServiceImpl();
	MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
	
    ListModelList<ProcessType> processTypeListModel;
    ListModelList<Piece> productPiecesListModel;
    ListModelList<MeasureUnit> measureUnitListModel;
    
    // atributes
    private Product product;
	private Piece activePiece;
	private List<Piece> pieceList;
	private List<Process> processList;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
//        System.out.println("-adentro de doAfterCompose-");
        List<ProcessType> processTypeList = processTypeService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
        
        pieceList = new ArrayList<Piece>();
        List<Piece> productPiecesList = pieceList;
        productPiecesListModel = new ListModelList<Piece>(productPiecesList);
        productPiecesListbox.setModel(productPiecesListModel);
        
        processList = new ArrayList<Process>();
        
        List<MeasureUnit> measureUnitlList = measureUnitService.getMeasureUnitList();
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectBox.setModel(measureUnitListModel);
        
        activePiece = null;
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
    	
		//show message for user
		Clients.showNotification("Producto guardado");
		
		//limpiar todo
		product = null;
		pieceList = new ArrayList<Piece>();
		processList = new ArrayList<Process>();
		productName.setText("");
		productDetails.setText("");
		productPiecesListModel.clear();
    }
    
    @Listen("onClick = #createPieceButton")
    public void createNewPiece() {
    	pieceCreationBlock.setVisible(true);
    }
    
    @Listen("onClick = #cancelPieceButton")
    public void cancelPiece() {
    	pieceCreationBlock.setVisible(false);
    }
    
    @Listen("onClick = #createProcessButton")
    public void createNewProcess() {
    	if(Strings.isBlank(pieceName.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Pieza",pieceName);
			return;
		}
    	if(pieceUnitsByProduct.getValue() == null || pieceUnitsByProduct.getValue() <= 0){
			Clients.showNotification("Ingrese las unidades de esta Pieza que contiene el Producto",pieceUnitsByProduct);
			return;
		}
    	Integer piece_id = 0;
    	if(pieceList.isEmpty() == true) {
    		piece_id = pieceService.getNewId();
    	} else {
    		//navegar pieceList y buscar el ultimo id y agregarle 1
    		piece_id = pieceList.get(pieceList.size()-1).getId() + 1;
    		//si se esta editando un producto habria que buscar un nuevo id del servicio
    	}
    	
    	String piece_name = pieceName.getText();
    	Integer idMeasureUnit = measureUnitListModel.getElementAt(measureUnitSelectBox.getSelectedIndex()).getId();
    	BigDecimal piece_height = BigDecimal.ZERO;
    	BigDecimal piece_width = BigDecimal.ZERO;
    	BigDecimal piece_depth = BigDecimal.ZERO;
    	BigDecimal piece_size1 = BigDecimal.ZERO;
    	BigDecimal piece_size2 = BigDecimal.ZERO;
    	if(pieceHeight.getText().compareTo("")!=0) {
    		piece_height = new BigDecimal(Double.parseDouble(pieceHeight.getText()));
    	}
    	if(pieceWidth.getText().compareTo("")!=0) {
    		piece_width = new BigDecimal(Double.parseDouble(pieceWidth.getText()));
    	}
    	if(pieceDepth.getText().compareTo("")!=0) {
    		piece_depth = new BigDecimal(Double.parseDouble(pieceDepth.getText()));
    	}
    	if(pieceSize1.getText().compareTo("")!=0) {
    		piece_size1 = new BigDecimal(Double.parseDouble(pieceSize1.getText()));
    	}
    	if(pieceSize2.getText().compareTo("")!=0) {
    		piece_size2 = new BigDecimal(Double.parseDouble(pieceSize2.getText()));
    	}
    	Integer piece_units = pieceUnitsByProduct.getValue();
    	boolean piece_isGroup = pieceGroup.isChecked();
    	activePiece = new Piece(piece_id, null, piece_name, idMeasureUnit, piece_height, piece_width, piece_depth, piece_size1, piece_size2, piece_isGroup, piece_units);
    	processCreationBlock.setVisible(true);
    	pieceCreationBlock.setVisible(false);
    }
    
    @Listen("onClick = #cancelProcessButton")
    public void cancelProcess() {
    	processCreationBlock.setVisible(false);
    }
    
    @Listen("onClick = #finishProcessButton")
    public void finishPiece() {
    	for(int i=1; i<processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
    		Label lbl = (Label)processListbox.getChildren().get(i).getChildren().get(1).getChildren().get(0);
    		Textbox txtbox = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
    		//System.out.print("Proceso " + lbl.getValue());//System.out.print(",texto " + txtbox.getText());//System.out.println(",checkbox " + chkbox.isChecked());
    		List<ProcessType> processTypeList = processTypeService.getProcessTypeList();
    		//System.out.println("id proceso " + processTypeList.get(i - 1).getId());
    		if(chkbox.isChecked() && Strings.isBlank(txtbox.getText())){
    			Clients.showNotification("Ingrese el Tiempo para el Proceso",txtbox);
    			return;
    		}
    		Process process = null;
    		if(chkbox.isChecked() && !Strings.isBlank(txtbox.getText())){
    			int idPiece = activePiece.getId();
    			int idProcessType = processTypeService.getProcessTypeList().get(i - 1).getId();
    			//!! Recordar hacer entrada del detail
    			String details = "";
    			Integer minutes = Integer.parseInt(txtbox.getText());
    			Duration duration = null;
				try {
					duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 0, minutes, 0);
				} catch (DatatypeConfigurationException e) {
					System.out.println("Error en grabar duracion del proceso: " + e.toString());
				}
    			process = new Process(idPiece, idProcessType, details, duration);
    		}
    		if(process != null) {
    			processList.add(process);
    		}
    	}
    	//agregamos la pieza a la lista y actualizamos el view
    	pieceList.add(activePiece);
    	productPiecesListModel.add(activePiece);
    	activePiece = null;
    	refreshView();
    }
	
    //when user checks on the checkbox of each process on the list
  	@Listen("onProcessCheck = #processListbox")
  	public void doProcessCheck(ForwardEvent evt){
  		//get data from event
  		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
  		Listitem litem = (Listitem)cbox.getParent().getParent();
  		Textbox tbox = (Textbox)litem.getChildren().get(2).getFirstChild();
  		if(cbox.isChecked()) {
  			tbox.setVisible(true);
  		} else {
  			tbox.setVisible(false);
  		}
  	}
  	
  	@Listen("onCheck = #pieceGroup")
  	public void doPieceGroupCheck() {
  		Row measureUnitRow = (Row)(measureUnitSelectBox.getParent());
  		Row pieceHeightRow = (Row)(pieceHeight.getParent());
  		Row pieceWidthRow = (Row)(pieceWidth.getParent());
  		Row pieceDepthRow = (Row)(pieceDepth.getParent());
  		Row pieceSize1Row = (Row)(pieceSize1.getParent());
  		Row pieceSize2Row = (Row)(pieceSize2.getParent());
  		if(pieceGroup.isChecked()) {
  			measureUnitRow.setVisible(false);
  			pieceHeightRow.setVisible(false);
  	    	pieceWidthRow.setVisible(false);
  	    	pieceDepthRow.setVisible(false);
  	    	pieceSize1Row.setVisible(false);
  	    	pieceSize2Row.setVisible(false);
  		} else {
  			measureUnitRow.setVisible(true);
  			pieceHeightRow.setVisible(true);
  	    	pieceWidthRow.setVisible(true);
  	    	pieceDepthRow.setVisible(true);
  	    	pieceSize1Row.setVisible(true);
  	    	pieceSize2Row.setVisible(true);
  		}
  	}
    
  	private void refreshView() {
  		if (activePiece == null) {
  			pieceCreationBlock.setVisible(false);
  	    	processCreationBlock.setVisible(false);
  	    	//limpiar form pieza
  	    	pieceName.setText("");
  	    	pieceGroup.setChecked(false);
  	    	measureUnitSelectBox.setSelectedIndex(-1);
  	    	pieceHeight.setText("");
  	    	pieceWidth.setText("");
  	    	pieceDepth.setText("");
  	    	pieceSize1.setText("");
  	    	pieceSize2.setText("");
  	    	pieceUnitsByProduct.setValue(null);
  	    	//limpiar procesos (ponerlos en vacio y sin check)
  	    	for(int i=1; i<processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
  	    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
  	    		Textbox txtbox = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
  	    		chkbox.setChecked(false);
  	    		txtbox.setText("");
  	    		txtbox.setVisible(false);
  	    	}
  		} else {
  			
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
  	
}

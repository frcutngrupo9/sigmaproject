package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.service.PieceListService;
import ar.edu.utn.sigmaproject.service.ProcessListService;
import ar.edu.utn.sigmaproject.service.ProcessTypeListService;
import ar.edu.utn.sigmaproject.service.ProductListService;
import ar.edu.utn.sigmaproject.service.impl.PieceListServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessListServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeListServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductListServiceImpl;
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
	Component processCreationBlock;
	@Wire
	Listbox processListbox;
	@Wire
	Listbox productPiecesListbox;
	
	private Product product;
	private Piece activePiece;
	private List<Piece> pieceList;
	private List<Process> processList;
     
    //services
	ProcessTypeListService processTypeListService = new ProcessTypeListServiceImpl();
	ProcessListService processListService = new ProcessListServiceImpl();
	PieceListService pieceListService = new PieceListServiceImpl();
	ProductListService productListService = new ProductListServiceImpl();
	
    ListModelList<ProcessType> processTypeListModel;
    ListModelList<Piece> productPiecesListModel;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
         
        List<ProcessType> processTypeList = processTypeListService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
        
        pieceList = new ArrayList<Piece>();
        List<Piece> productPiecesList = pieceList;
        productPiecesListModel = new ListModelList<Piece>(productPiecesList);
        productPiecesListbox.setModel(productPiecesListModel);
        
        processList = new ArrayList<Process>();
    }
    
    @Listen("onClick = #saveProductButton")
    public void SaveProduct() {
    	if(Strings.isBlank(productName.getValue())){
			Clients.showNotification("Ingresar Nombre Producto",productName);
			return;
		}
    	Integer product_id = productListService.getNewId();
    	String product_name = productName.getText();
    	String product_details = productDetails.getText();
    	product = new Product(product_id, product_name, product_details);
    	
    	//save
    	product = productListService.updateProduct(product);
    	if(pieceList != null && pieceList.isEmpty() == false) {
    		for(int i=0; i<pieceList.size(); i++) {
    			pieceList.get(i).setIdProduct(product.getId());
    			pieceListService.savePiece(pieceList.get(i));
    		}
    	}
    	if(processList != null && processList.isEmpty() == false) {
    		for(int i=0; i<processList.size(); i++) {
    			processListService.saveProcess(processList.get(i));
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
    		piece_id = pieceListService.getNewId();
    	} else {
    		//navegar pieceList y buscar el ultimo id y agregarle 1
    		piece_id = pieceList.get(pieceList.size()-1).getId() + 1;
    		//si se esta editando un producto habria que buscar un nuevo id del servicio
    	}
    	
    	String piece_name = pieceName.getText();
    	Long piece_height = 0L;
    	Long piece_width = 0L;
    	Long piece_depth = 0L;
    	Long piece_size1 = 0L;
    	Long piece_size2 = 0L;
    	if(pieceHeight.getText().compareTo("")!=0) {
    		piece_height = Long.parseLong(pieceHeight.getText());
    	}
    	if(pieceWidth.getText().compareTo("")!=0) {
    		piece_width = Long.parseLong(pieceWidth.getText());
    	}
    	if(pieceDepth.getText().compareTo("")!=0) {
    		piece_depth = Long.parseLong(pieceDepth.getText());
    	}
    	if(pieceSize1.getText().compareTo("")!=0) {
    		piece_size1 = Long.parseLong(pieceSize1.getText());
    	}
    	if(pieceSize2.getText().compareTo("")!=0) {
    		piece_size2 = Long.parseLong(pieceSize2.getText());
    	}
    	Integer piece_units = pieceUnitsByProduct.getValue();
    	boolean piece_isGroup = pieceGroup.isChecked();
    	activePiece = new Piece(piece_id, null, piece_name, piece_height, piece_width, piece_depth, piece_size1, piece_size2, piece_isGroup, piece_units);
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
    		//System.out.print("Proceso " + lbl.getValue());
    		//System.out.print(",texto " + txtbox.getText());
    		//System.out.println(",checkbox " + chkbox.isChecked());
    		List<ProcessType> processTypeList = processTypeListService.getProcessTypeList();
    		//System.out.println("id proceso " + processTypeList.get(i - 1).getId());
    		if(chkbox.isChecked() && Strings.isBlank(txtbox.getText())){
    			Clients.showNotification("Ingrese el Tiempo para el Proceso",txtbox);
    			return;
    		}
    		Process process = null;
    		if(chkbox.isChecked() && !Strings.isBlank(txtbox.getText())){
    			int idPiece = activePiece.getId();
    			int idProcessType = processTypeListService.getProcessTypeList().get(i - 1).getId();
    			Long time = Long.parseLong(txtbox.getText());
    			process = new Process(idPiece, idProcessType, time);
    		}
    		if(process != null) {
    			processList.add(process);
    		}
    	}
    	//agregamos la pieza a la lista y actualizamos el view
    	pieceList.add(activePiece);
    	productPiecesListModel.add(activePiece);
    	activePiece = null;
    	pieceCreationBlock.setVisible(false);
    	processCreationBlock.setVisible(false);
    	//limpiar form pieza
    	pieceName.setText("");
    	pieceHeight.setText("");
    	pieceWidth.setText("");
    	pieceDepth.setText("");
    	pieceSize1.setText("");
    	pieceSize2.setText("");
    	pieceUnitsByProduct.setValue(null);
    	pieceGroup.setChecked(false);;
    	//limpiar procesos (ponerlos en vacio y sin check)
    	for(int i=1; i<processListbox.getChildren().size(); i++) { //empezamos en 1 para no recorrer el Listhead
    		Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
    		Textbox txtbox = (Textbox)processListbox.getChildren().get(i).getChildren().get(2).getChildren().get(0);
    		chkbox.setChecked(false);
    		txtbox.setText("");
    	}
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
    
  	private void refreshView() {
  		if (activePiece == null) {
  			
  		}
  	}
  	
}

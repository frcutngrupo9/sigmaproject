package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
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
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;

public class ProductCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	private Product product;
	private List<Piece> pieceList;
	private List<Process> processList;
	
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
	Intbox pieceUnitsByProduct;
	@Wire
	Checkbox selectedTodoCheck;
	@Wire
	Button createProcessButton;
	@Wire
	Button cancelPieceButton;
	
	@Wire
	Component processCreationBlock;
	@Wire
	Listbox processListbox;
     
    //services
	ProcessTypeListService processTypeListService = new ProcessTypeListServiceImpl();
	ProcessListService processListService = new ProcessListServiceImpl();
	PieceListService pieceListService = new PieceListServiceImpl();
	ProductListService productListService = new ProductListServiceImpl();
	
    ListModelList<ProcessType> processTypeListModel;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
         
        List<ProcessType> processTypeList = processTypeListService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
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
    	Product product = new Product(product_id, product_name, product_details);
    	
    	//save
    	product = productListService.updateProduct(product);
    	
		//show message for user
		Clients.showNotification("Producto guardado");
    	
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
    	processCreationBlock.setVisible(true);
    }
    
    @Listen("onClick = #cancelProcessButton")
    public void cancelProcess() {
    	processCreationBlock.setVisible(false);
    }
    
    @Listen("onClick = #finishPieceButton")
    public void finishPiece() {
    	pieceCreationBlock.setVisible(false);
    	processCreationBlock.setVisible(false);
    }
	
    //when user checks on the checkbox of each process on the list
  	@Listen("onProcessCheck = #processListbox")
  	public void doProcessCheck(ForwardEvent evt){
  		//get data from event
  		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
  		Listitem litem = (Listitem)cbox.getParent().getParent();
  		
  		Textbox tbox = (Textbox)litem.getChildren().get(2).getFirstChild();
  		tbox.setVisible(true);
  		//tbox.setText("se selecciono este");
  	}
    
}

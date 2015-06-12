package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.service.ProcessTypeListService;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeListServiceImpl;
import ar.edu.utn.sigmaproject.domain.ProcessType;

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
	Textbox pieceLong;
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
     
    //data for the view
    ListModelList<ProcessType> processTypeListModel;
     
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
         
        List<ProcessType> processTypeList = processTypeListService.getProcessTypeList();
        processTypeListModel = new ListModelList<ProcessType>(processTypeList);
        processListbox.setModel(processTypeListModel);
    }
	
}

package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class PieceSelectionModalController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Window modalDialog;
	@Wire
	Button closeButton;
	@Wire
	Listbox pieceListbox;

	// services
	private PieceService pieceService = new PieceServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();

	// atributes

	// list
	private List<Piece> pieceList;

	// list models
	private ListModelList<Piece> pieceListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		pieceList = pieceService.getPieceList();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);
	}

	@Listen("onSelect = #pieceListbox")
	public void doListBoxSelect() {
		Piece selected_piece = pieceListbox.getSelectedItem().getValue();
		EventQueue<Event> eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onPieceSelect", null, selected_piece));
		modalDialog.detach();
	}

	@Listen("onClick = #closeButton")
	public void doCloseModal() {
		modalDialog.detach();
	}

	public String getProductName(int idProduct) {
		return productService.getProduct(idProduct).getName();
	}

	public String getMeasureUnitName(int idMeasureUnit) {
		if(measureUnitService.getMeasureUnit(idMeasureUnit) != null) {
			return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

}

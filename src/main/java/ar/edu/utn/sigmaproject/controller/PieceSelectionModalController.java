package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Piece;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PieceSelectionModalController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Window modalDialog;
	@Wire
	Button closeButton;
	@Wire
	Listbox pieceListbox;

	// services
	@WireVariable
	private PieceRepository pieceRepository;
	private ProductRepository productRepository;
	private MeasureUnitRepository measureUnitRepository;

	// atributes

	// list
	private List<Piece> pieceList;

	// list models
	private ListModelList<Piece> pieceListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		pieceList = pieceRepository.findAll();
		pieceListModel = new ListModelList<>(pieceList);
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

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

}

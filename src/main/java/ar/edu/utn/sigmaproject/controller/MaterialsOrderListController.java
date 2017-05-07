package ar.edu.utn.sigmaproject.controller;

import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.service.MaterialsOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MaterialsOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Grid materialsOrderGrid;
	@Wire
	Button newMaterialsOrderButton;

	// services
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;
	@WireVariable
	private MaterialsOrderDetailRepository materialsOrderStateRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// list
	private List<MaterialsOrder> materialsOrderList;

	// list models
	private ListModelList<MaterialsOrder> materialsOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// se crea un listener para cuando se reciba materiales
		EventQueue<Event> eq = EventQueues.lookup("Materials Reception Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onMaterialsReception")) {
					refreshView();
				}
			}
		});
		refreshView();
	}

	private void refreshView() {
		materialsOrderList = materialsOrderRepository.findAll();
		materialsOrderListModel = new ListModelList<>(materialsOrderList);
		materialsOrderGrid.setModel(materialsOrderListModel);
	}

	@Listen("onClick = #newMaterialsOrderButton")
	public void newMaterialsOrderButtonClick() {
		Executions.getCurrent().setAttribute("selected_materials_order", null);
		Include include = (Include) Selectors.iterable(materialsOrderGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_creation.zul");
	}

	@Listen("onEditMaterialsOrder = #materialsOrderGrid")
	public void doEditMaterialsOrder(ForwardEvent evt) {
		MaterialsOrder materialsOrder = (MaterialsOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_materials_order", materialsOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_creation.zul");
	}

	@Listen("onMaterialsOrderReception = #materialsOrderGrid")
	public void doMaterialsOrderReception(ForwardEvent evt) {
		MaterialsOrder materialsOrder = (MaterialsOrder) evt.getData();
		final HashMap<String, MaterialsOrder> map = new HashMap<String, MaterialsOrder>();
		map.put("selected_materials_order", materialsOrder);
		Window window = (Window)Executions.createComponents("/materials_reception.zul", null, map);
		window.doModal();
	}

	public boolean isReceived(MaterialsOrder materialsOrder) {
		return materialsOrder.getDateReception() != null;
	}
}

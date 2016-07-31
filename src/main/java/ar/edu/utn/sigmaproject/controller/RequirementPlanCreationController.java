package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RequirementPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanDatebox;
	@Wire
	Listbox rawMaterialRequirementListbox;
	@Wire
	Listbox supplyRequirementListbox;
	@Wire
	Button returnButton;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private RawMaterialRequirementRepository rawMaterialRequirementRepository;
	@WireVariable
	private SupplyRequirementRepository supplyRequirementRepository;
	@WireVariable
	private WoodReservedRepository woodReservedRepository;
	@WireVariable
	private SupplyReservedRepository supplyReservedRepository;

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<SupplyRequirement> supplyRequirementList;
	private List<RawMaterialRequirement> rawMaterialRequirementList;

	// list models
	private ListModelList<SupplyRequirement> supplyRequirementListModel;
	private ListModelList<RawMaterialRequirement> rawMaterialRequirementListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}

		supplyRequirementList = currentProductionPlan.getSupplyRequirements();
		supplyRequirementListModel = new ListModelList<SupplyRequirement>(supplyRequirementList);
		rawMaterialRequirementList = currentProductionPlan.getRawMaterialRequirements();
		rawMaterialRequirementListModel = new ListModelList<RawMaterialRequirement>(rawMaterialRequirementList);

		refreshView();
	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productionPlanDatebox.setDisabled(true);
		supplyRequirementListbox.setModel(supplyRequirementListModel);
		rawMaterialRequirementListbox.setModel(rawMaterialRequirementListModel);
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
		}
	}

	public BigDecimal getSupplyStockReserved(SupplyRequirement supplyRequirement) {
		// TODO: como es la verdadera relacion entre estas tres clases? un SupplyRequirement tiene un SupplyType,
		// por que un SupplyReserved tiene tambien un SupplyType? La relacion es uno a uno entre SupplyRequirement y SupplyReserved?
		// Respuesta: SupplyType tiene una lista de SupplyReserved, y cada SupplyReserved tiene un SupplyRequirement para saber para que plan se est� reservando.
		//  Por lo tanto SupplyReserved reserved no tiene SupplyType, solo lo tiene SupplyRequirement
		SupplyReserved supplyReserved = supplyReservedRepository.findBySupplyRequirement(supplyRequirement);
		if(supplyReserved == null) {
			return BigDecimal.ZERO;
		} else {
			return supplyReserved.getStockReserved();
		}
	}

	public BigDecimal getSupplyStockMissing(SupplyRequirement supplyRequirement) {
		return supplyRequirement.getQuantity().subtract(getSupplyStockReserved(supplyRequirement));
	}

	public BigDecimal getRawMaterialTypeStock(RawMaterialType rawMaterialType) {
		List<Wood> woodList = woodRepository.findByRawMaterialType(rawMaterialType);
		BigDecimal stock = BigDecimal.ZERO;
		for(Wood each : woodList) {
			stock = stock.add(each.getStock());
		}
		return stock;
	}

	public BigDecimal getRawMaterialStockReserved(RawMaterialRequirement rawMaterialRequirement) {
		WoodReserved woodReserved = woodReservedRepository.findFirstByRawMaterialRequirement(rawMaterialRequirement);
		if(woodReserved == null) {
			return BigDecimal.ZERO;
		} else {
			return woodReserved.getStockReserved();
		}
	}

	public BigDecimal getRawMaterialStockMissing(RawMaterialRequirement rawMaterialRequirement) {
		return rawMaterialRequirement.getQuantity().subtract(getRawMaterialStockReserved(rawMaterialRequirement));
	}

	@Listen("onFulfillSupplyRequirement = #supplyRequirementListbox")
	public void doFulfillSupplyRequirement(ForwardEvent evt) {
		SupplyRequirement data = (SupplyRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setFulfilled(element.isChecked());// cargamos al objeto el valor actualizado del elemento web
	}

	@Listen("onClickReservation = #supplyRequirementListbox")
	public void doSupplyRequirementReservation(ForwardEvent evt) {
		SupplyRequirement data = (SupplyRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		final HashMap<String, SupplyRequirement> map = new HashMap<String, SupplyRequirement>();
		map.put("selected_supply_requirement", data);
		Window window = (Window)Executions.createComponents("/supply_reservation.zul", null, map);
		window.doModal();
		refreshView();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}
}

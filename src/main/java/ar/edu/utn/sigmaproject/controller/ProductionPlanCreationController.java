package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderRawMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderSupply;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRawMaterialRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderSupplyRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyRequirementRepository;

public class ProductionPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox orderPopupListbox;
	@Wire
	Grid productionPlanDetailGrid;
	@Wire
	Bandbox orderBandbox;
	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Button resetProductionPlanButton;
	@Wire
	Button saveProductionPlanButton;
	@Wire
	Button deleteProductionPlanButton;
	@Wire
	Listbox productTotalListbox;
	@Wire
	Combobox productionPlanStateTypeCombobox;
	@Wire
	Caption productionPlanCaption;
	@Wire
	Button returnButton;

	// services
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ClientRepository clientService;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private SupplyRequirementRepository supplyRequirementRepository;
	@WireVariable
	private RawMaterialRequirementRepository rawMaterialRequirementRepository;
//	@WireVariable
//	private ProductionOrderRepository productionOrderRepository;
	//	@WireVariable
	//	private ProductionOrderDetailRepository productionOrderDetailRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;
	@WireVariable
	private ProductionOrderSupplyRepository productionOrderSupplyRepository;
	@WireVariable
	private ProductionOrderRawMaterialRepository productionOrderRawMaterialRepository;

	// list
	private List<Order> orderPopupList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	private List<ProductTotal> productTotalList;
	private List<ProductionPlanStateType> productionPlanStateTypeList;

	// list models
	private ListModelList<Order> orderPopupListModel;
	private ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
	private ListModelList<ProductionPlanStateType> productionPlanStateTypeListModel;

	// atributes
	private ProductionPlan currentProductionPlan;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		productTotalList = new ArrayList<ProductTotal>();
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		productionPlanStateTypeList = productionPlanStateTypeRepository.findAll();
		productionPlanStateTypeListModel = new ListModelList<ProductionPlanStateType>(productionPlanStateTypeList);
		productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);

		refreshViewProductionPlan();
		refreshProductTotalListbox();
	}

	@Listen("onSelect = #orderPopupListbox")
	public void selectionOrderPopupListbox() {
		Order order = (Order) orderPopupListbox.getSelectedItem().getValue();
		addOrder(order);
	}

	private void refreshOrder() {
		orderBandbox.setValue("");
		orderPopupListbox.clearSelection();
		orderBandbox.close();
	}

	private void refreshOrderPopupList() {// el popup se actualiza en base a los detalles
		List<Order> orderList = orderRepository.findAll();
		orderPopupList = new ArrayList<>();
		// se buscan los pedidos que no estan asignados a un plan y no estan cancelados (estan en estado Creado)
		OrderStateType orderStateTypeInitiated = orderStateTypeRepository.findFirstByName("Creado");
		for(Order each : orderList) {
			if(orderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(orderStateTypeInitiated)) {
				orderPopupList.add(each);
			}
		}
		for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {
			// no debe contener los pedidos que ya estan en el detalle
			Order aux = productionPlanDetail.getOrder();
			orderPopupList.remove(orderRepository.findOne(aux.getId()));
		}
		orderPopupListModel = new ListModelList<Order>(orderPopupList);
		orderPopupListbox.setModel(orderPopupListModel);
	}

	@Listen("onClick = #resetProductionPlanButton")
	public void resetProductionPlan() {
		refreshViewProductionPlan();
	}

	@Listen("onClick = #deleteProductionPlanButton")
	public void deleteProductionPlan() {
		if(currentProductionPlan != null) {
			productionPlanRepository.delete(currentProductionPlan);
			currentProductionPlan = null;
			refreshViewProductionPlan();
		}
	}

	@Transactional
	@Listen("onClick = #saveProductionPlanButton")
	public void saveProductionPlan() {
		if(Strings.isBlank(productionPlanNameTextbox.getValue())) {
			Clients.showNotification("Ingresar Nombre Plan de Produccion", productionPlanNameTextbox);
			return;
		}
		if(productionPlanDetailList.size() == 0) {
			Clients.showNotification("Ingresar al menos 1 pedido", orderBandbox);
			return;
		}
		String productionPlanName = productionPlanNameTextbox.getText().toUpperCase();
		ProductionPlanStateType productionPlanStateType;
		if(productionPlanStateTypeCombobox.getSelectedIndex() == -1) {
			productionPlanStateType = null;
		} else {
			productionPlanStateType = productionPlanStateTypeCombobox.getSelectedItem().getValue();
		}
		boolean isNewProductionPlan = false;
		if(currentProductionPlan == null) { // es un plan nuevo
			// creamos el nuevo plan
			currentProductionPlan = new ProductionPlan(productionPlanName);
			for(ProductionPlanDetail each : productionPlanDetailList) {
				// se agregan todas las referencias hacia el nuevo plan
				each.setProductionPlan(currentProductionPlan);
			}
			currentProductionPlan.getPlanDetails().addAll(productionPlanDetailList);
			ProductionPlanState productionPlanState = new ProductionPlanState(productionPlanStateType, new Date());
			productionPlanState = productionPlanStateRepository.save(productionPlanState);
			currentProductionPlan.setState(productionPlanState);
			// cambia el estado de los pedidos
			for(ProductionPlanDetail each : productionPlanDetailList) {
				OrderStateType orderStateType = orderStateTypeRepository.findFirstByName("Planificado");
				Order order = each.getOrder();
				OrderState state = new OrderState(orderStateType, new Date());
				state = orderStateRepository.save(state);
				order.setState(state);
				orderRepository.save(order);
			}
			isNewProductionPlan = true;
		} else { // se edita un plan
			currentProductionPlan.setName(productionPlanName);
			//currentProductionPlan.setPlanDetails(productionPlanDetailList);
			if (!currentProductionPlan.getCurrentStateType().equals(productionPlanStateType)) {
				// si el estado ha cambiado
				ProductionPlanState productionPlanState = new ProductionPlanState(productionPlanStateType, new Date());
				productionPlanState = productionPlanStateRepository.save(productionPlanState);
				currentProductionPlan.setState(productionPlanState);
			}
		}

		currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		//TODO si no es nuevo pero se modificaron los detalles deberia actualizar las ordenes de produccion
		if(isNewProductionPlan) {
			// crea los requerimientos
			List<SupplyRequirement> supplyRequirementList = createSupplyRequirements(currentProductionPlan);
			supplyRequirementList = supplyRequirementRepository.save(supplyRequirementList);
			currentProductionPlan.getSupplyRequirements().addAll(supplyRequirementList);
			List<RawMaterialRequirement> rawMaterialRequirementList = createRawMaterialRequirements(currentProductionPlan);
			rawMaterialRequirementList = rawMaterialRequirementRepository.save(rawMaterialRequirementList);
			currentProductionPlan.getRawMaterialRequirements().addAll(rawMaterialRequirementList);
			// crea ordenes de produccion
			int sequence = 0;
			for(ProductTotal each : getProductTotalList()) {
				ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateTypeRepository.findFirstByName("Registrada"), new Date());
				productionOrderState = productionOrderStateRepository.save(productionOrderState);
				sequence += 1;
				ProductionOrder productionOrder = new ProductionOrder(sequence, currentProductionPlan, each.getProduct(), each.getTotalUnits(), productionOrderState);
				//  agrega los detalles
				List<ProductionOrderDetail> details = createProductionOrderDetailList(productionOrder);
				//				details = productionOrderDetailRepository.save(details);
				productionOrder.setDetails(details);
				// agrega los materiales a las ordenes de produccion
				List<ProductionOrderSupply> productionOrderSupplyList = createProductionOrderSupplyList(productionOrder);
				List<ProductionOrderRawMaterial> productionOrderRawMaterialList = createProductionOrderRawMaterialList(productionOrder);
				productionOrderSupplyList = productionOrderSupplyRepository.save(productionOrderSupplyList);
				productionOrderRawMaterialList = productionOrderRawMaterialRepository.save(productionOrderRawMaterialList);
				productionOrder.setProductionOrderSupplies(productionOrderSupplyList);
				productionOrder.setProductionOrderRawMaterials(productionOrderRawMaterialList);

				//productionOrder = productionOrderRepository.save(productionOrder);
				currentProductionPlan.getProductionOrderList().add(productionOrder);
			}
			currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		}
		refreshViewProductionPlan();
		alert("Plan guardado.");
	}

	private List<ProductionOrderDetail> createProductionOrderDetailList(ProductionOrder productionOrder) {
		List<ProductionOrderDetail> details = new ArrayList<>();
		for(Piece piece : productionOrder.getProduct().getPieces()) {
			List<Process> auxProcessList = piece.getProcesses();
			for(Process process : auxProcessList) {
				// por cada proceso hay que crear un detalle
				Integer units = productionOrder.getUnits();
				Integer quantityPiece = units * piece.getUnits();// cantidad total de la pieza
				Duration timeTotal = process.getTime().multiply(units);// tiempo del proceso de las piezas del producto por las unidades del producto
				details.add(new ProductionOrderDetail(productionOrder, process, ProcessState.Pendiente, null, timeTotal, quantityPiece));
			}
		}
		return details;
	}

	private List<ProductionOrderSupply> createProductionOrderSupplyList(ProductionOrder productionOrder) {
		List<ProductionOrderSupply> list = new ArrayList<>();
		for(Supply each : productionOrder.getProduct().getSupplies()) {
			BigDecimal totalQuantity = each.getQuantity().multiply(new BigDecimal(productionOrder.getUnits()));
			ProductionOrderSupply productionOrderSupply = new ProductionOrderSupply(each.getSupplyType(), totalQuantity);
			list.add(productionOrderSupply);
		}
		return list;
	}

	private List<ProductionOrderRawMaterial> createProductionOrderRawMaterialList(ProductionOrder productionOrder) {
		List<ProductionOrderRawMaterial> list = new ArrayList<>();
		for(RawMaterial each : productionOrder.getProduct().getRawMaterials()) {
			BigDecimal totalQuantity = each.getQuantity().multiply(new BigDecimal(productionOrder.getUnits()));
			ProductionOrderRawMaterial productionOrderRawMaterial = new ProductionOrderRawMaterial(each.getWood(), totalQuantity);
			list.add(productionOrderRawMaterial);
		}
		return list;
	}

	private List<SupplyRequirement> createSupplyRequirements(ProductionPlan productionPlan) {
		// busca los requerimientos
		List<SupplyRequirement> list = new ArrayList<>();
		List<ProductTotal> productTotalList = getProductTotalList();
		for (ProductTotal productTotal : productTotalList) {
			for (Supply supply : productTotal.getProduct().getSupplies()) {
				SupplyRequirement auxSupplyRequirement = null;
				for (SupplyRequirement supplyRequirement : list) {// busca si el insumo no se encuentra agregado
					if (supply.getSupplyType().equals(supplyRequirement.getSupplyType())) {
						auxSupplyRequirement = supplyRequirement;
					}
				}
				if (auxSupplyRequirement != null) {// el insumo si se encuentra agregado, suma sus cantidades
					auxSupplyRequirement.setQuantity(auxSupplyRequirement.getQuantity().add(supply.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				} else {// el insumo no se encuentra, se lo agrega
					list.add(new SupplyRequirement(supply.getSupplyType(), productionPlan, supply.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				}
			}
		}
		return list;
	}

	private List<RawMaterialRequirement> createRawMaterialRequirements(ProductionPlan productionPlan) {
		// busca requerimientos de materias primas
		List<RawMaterialRequirement> list = new ArrayList<RawMaterialRequirement>();
		List<ProductTotal> productTotalList = getProductTotalList();
		for(ProductTotal productTotal : productTotalList) {
			Product product = productTotal.getProduct();
			for(RawMaterial rawMaterial : product.getRawMaterials()) {
				RawMaterialRequirement auxRawMaterialRequirement = null;
				for(RawMaterialRequirement supplyRequirement : list) {// buscamos si la materia prima no se encuentra agregada
					if(rawMaterial.getWood().equals(supplyRequirement.getWood())) {
						auxRawMaterialRequirement = supplyRequirement;
					}
				}
				if(auxRawMaterialRequirement != null) {// la materia prima si se encuentra agregada, sumamos sus cantidades
					auxRawMaterialRequirement.setQuantity(auxRawMaterialRequirement.getQuantity().add(rawMaterial.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				} else {// la materia prima no se encuentra, se la agrega
					list.add(new RawMaterialRequirement(rawMaterial.getWood(), productionPlan, rawMaterial.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				}
			}
		}
		return list;
	}

	private void addOrder(Order order) {
		productionPlanDetailList.add(new ProductionPlanDetail(currentProductionPlan, order));
		refreshProductionPlanDetailListGrid();
		refreshOrderPopupList();
		refreshOrder();
		refreshProductTotalListbox();
	}

	@Listen("onRemoveOrder = #productionPlanDetailGrid")
	public void doRemoveOrder(ForwardEvent evt) {
		ProductionPlanDetail productionPlanDetail = (ProductionPlanDetail) evt.getData();
		productionPlanDetailList.remove(productionPlanDetail);
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
		refreshOrderPopupList();
	}

	private void refreshProductionPlanDetailListGrid() {
		productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(productionPlanDetailList);
		productionPlanDetailGrid.setModel(productionPlanDetailListModel);
	}

	private void refreshProductTotalListbox() {
		productTotalList = getProductTotalList();
		ListModelList<ProductTotal> productTotalListModel = new ListModelList<ProductTotal>(productTotalList);
		productTotalListbox.setModel(productTotalListModel);
	}

	private List<ProductTotal> getProductTotalList() {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				Integer totalUnits = productTotalMap.get(productRepository.findOne(auxOrderDetail.getProduct().getId()));
				productTotalMap.put(productRepository.findOne(auxOrderDetail.getProduct().getId()), (totalUnits == null) ? auxOrderDetail.getUnits() : totalUnits + auxOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}

	private void refreshViewProductionPlan() {
		refreshOrder();
		if (currentProductionPlan == null) {// nuevo plan de produccion
			productionPlanCaption.setLabel("Creacion de Plan de Produccion");
			productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findFirstByName("Registrado"));
			productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			int planNumber = productionPlanRepository.findAll().size() + 1;
			productionPlanNameTextbox.setText("Plan " + planNumber);
			productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
			deleteProductionPlanButton.setDisabled(true);
			productionPlanStateTypeCombobox.setDisabled(true);
		} else {// se edita plan de produccion
			productionPlanCaption.setLabel("Edicion de Plan de Produccion");
			ProductionPlanStateType productionPlanStateType = currentProductionPlan.getCurrentStateType();
			if (productionPlanStateType != null) {
				productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findOne(productionPlanStateType.getId()));
				productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			} else {
				productionPlanStateTypeCombobox.setSelectedIndex(-1);
			}
			if(currentProductionPlan.getName() != null) {
				productionPlanNameTextbox.setText(currentProductionPlan.getName());
			} else {
				productionPlanNameTextbox.setText("");
			}
			productionPlanDetailList = currentProductionPlan.getPlanDetails();
			deleteProductionPlanButton.setDisabled(false);
			productionPlanStateTypeCombobox.setDisabled(false);
		}
		refreshOrderPopupList();
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
	}

	public int getProductTotalUnits(Product product) {
		int productTotalUnits = 0;
		for(ProductTotal productTotal : productTotalList) {// buscamos el total de unidades
			if(productTotal.getProduct().equals(product)) {
				productTotalUnits = productTotal.getTotalUnits();
			}
		}
		return productTotalUnits;
	}

	public BigDecimal getProductTotalPrice(Product product) {
		int productTotalUnits = getProductTotalUnits(product);
		return getTotalPrice(productTotalUnits, product.getPrice());// esta funcion es incorrecta pq agarra el valor actual del producto cuando deberia ser el valor en el pedido
	}

	private BigDecimal getTotalPrice(int units, BigDecimal price) {
		if(price == null) {
			price = BigDecimal.ZERO;
		}
		return price.multiply(new BigDecimal(units));
	}

	public String getPieceTotalUnits(Piece piece) {
		int units = 0;
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
			for (OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				if (auxOrderDetail.getProduct().equals(productRepository.findByPieces(piece))) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			units = piece.getUnits() * units;
		}
		return units + "";
	}

	public String getTotalTime(Piece piece, ProcessType processType) {
		Process process = null;
		for(int j = 0; j < piece.getProcesses().size(); j++) {
			if (piece.getProcesses().get(j).getType().equals(processType)) {
				process = piece.getProcesses().get(j);
				break;
			}
		}
		long total = 0;
		int units = 0;
		for (ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {
			for (OrderDetail auxOrderDetail : productionPlanDetail.getOrder().getDetails()) {
				if (auxOrderDetail.getProduct().equals(productRepository.findByPieces(piece))) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			total = process.getTime().getMinutes() * units;
		}
		return total + "";
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}

	private void filter() {
		List<Order> some = new ArrayList<>();
		String nameFilter = orderBandbox.getValue().toLowerCase();
		for(Order each : orderPopupList) {// busca filtrando por varios atributos
			if((each.getClient().getName()+each.getClient().getEmail()+each.getNumber()).toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				some.add(each);
			}
		}
		orderPopupListModel = new ListModelList<Order>(some);
		orderPopupListbox.setModel(orderPopupListModel);
	}

	@Listen("onChanging = #orderBandbox")
	public void changeFilter(InputEvent event) {
		Bandbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filter();
	}

}

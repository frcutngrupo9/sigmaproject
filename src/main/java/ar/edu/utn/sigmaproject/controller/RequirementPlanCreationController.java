package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementService;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.SupplyRequirementService;
import ar.edu.utn.sigmaproject.service.SupplyReservedService;
import ar.edu.utn.sigmaproject.service.SupplyService;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialRequirementServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyRequirementServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyReservedServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyTypeServiceImpl;

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
	Grid supplyReservationGrid;
	@Wire
	Textbox codeTextBox;
	@Wire
	Textbox descriptionTextBox;
	@Wire
	Doublebox stockDoublebox;
	@Wire
	Doublebox stockReservedDoublebox;
	@Wire
	Doublebox stockMissingDoublebox;

	// services
	private SupplyRequirementService supplyRequirementService = new SupplyRequirementServiceImpl();
	private RawMaterialRequirementService rawMaterialRequirementService = new RawMaterialRequirementServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private SupplyTypeService supplyTypeService = new SupplyTypeServiceImpl();
	private SupplyService supplyService = new SupplyServiceImpl();
	private SupplyReservedService supplyReservedService = new SupplyReservedServiceImpl();
	private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();
	private RawMaterialService rawMaterialService = new RawMaterialServiceImpl();

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<SupplyRequirement> supplyRequirementList;
	private List<RawMaterialRequirement> rawMaterialRequirementList;

	// list models
	private ListModelList<SupplyRequirement> supplyRequirementListModel;
	private ListModelList<RawMaterialRequirement> rawMaterialRequirementListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		if(currentProductionPlan != null) {
			Integer idProductionPlan = currentProductionPlan.getId();

			supplyRequirementList = supplyRequirementService.getSupplyRequirementList(idProductionPlan);
			if(supplyRequirementList.isEmpty()) {
				// debemos generar los requerimientos en caso de que no se hayan generado aun
				List<SupplyRequirement> auxSupplyRequirementList = new ArrayList<SupplyRequirement>();
				ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(idProductionPlan);
				for(ProductTotal productTotal : productTotalList) {
					List<Supply> supplyList = supplyService.getSupplyList(productTotal.getId());
					for(Supply supply : supplyList) {
						SupplyRequirement auxSupplyRequirement = null;
						for(SupplyRequirement supplyRequirement : auxSupplyRequirementList) {// buecamos si el insumo no se encuentra agregado
							if(supply.getIdSupplyType().equals(supplyRequirement.getIdSupplyType())) {
								auxSupplyRequirement = supplyRequirement;
							}
						}
						if(auxSupplyRequirement != null) {// el insumo si se encuentra agregado, sumamos sus cantidades
							auxSupplyRequirement.setQuantity(auxSupplyRequirement.getQuantity() + supply.getQuantity());
						} else {// el insumo no se encuentra, se lo agrega
							auxSupplyRequirementList.add(new SupplyRequirement(null, idProductionPlan, supply.getIdSupplyType(), supply.getQuantity()));
						}
					}
				}
				if(auxSupplyRequirementList.isEmpty() != true) {
					supplyRequirementList = auxSupplyRequirementList;
				}
			}

			rawMaterialRequirementList = rawMaterialRequirementService.getRawMaterialRequirementList(idProductionPlan);
			if(rawMaterialRequirementList.isEmpty()) {
				// debemos generar las materias primas en caso de que no se hayan generado aun
				List<RawMaterialRequirement> auxRawMaterialRequirementList = new ArrayList<RawMaterialRequirement>();
				ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(idProductionPlan);
				for(ProductTotal productTotal : productTotalList) {
					List<RawMaterial> rawMaterialList = rawMaterialService.getRawMaterialList(productTotal.getId());
					for(RawMaterial rawMaterial : rawMaterialList) {
						RawMaterialRequirement auxRawMaterialRequirement = null;
						for(RawMaterialRequirement supplyRequirement : auxRawMaterialRequirementList) {// buecamos si la materia prima no se encuentra agregada
							if(rawMaterial.getIdRawMaterialType().equals(supplyRequirement.getIdRawMaterialType())) {
								auxRawMaterialRequirement = supplyRequirement;
							}
						}
						if(auxRawMaterialRequirement != null) {// la materia prima si se encuentra agregada, sumamos sus cantidades
							auxRawMaterialRequirement.setQuantity(auxRawMaterialRequirement.getQuantity() + rawMaterial.getQuantity());
						} else {// la materia prima no se encuentra, se la agrega
							auxRawMaterialRequirementList.add(new RawMaterialRequirement(null, idProductionPlan, rawMaterial.getIdRawMaterialType(), rawMaterial.getQuantity()));
						}
					}
				}
				if(auxRawMaterialRequirementList.isEmpty() != true) {
					rawMaterialRequirementList = auxRawMaterialRequirementList;
				}
			}

			supplyRequirementListModel = new ListModelList<SupplyRequirement>(supplyRequirementList);
			rawMaterialRequirementListModel = new ListModelList<RawMaterialRequirement>(rawMaterialRequirementList);
		}
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

	public Product getProduct(int idProduct) {
		return productService.getProduct(idProduct);
	}

	public SupplyType getSupplyType(int idSupplyType) {
		return supplyTypeService.getSupplyType(idSupplyType);
	}

	public String getSupplyStockReserved(SupplyRequirement supplyRequirement) {
		SupplyReserved aux = supplyReservedService.getSupplyReserved(supplyRequirement.getIdSupplyType(), supplyRequirement.getId());
		if(aux != null) {
			return aux.getStockReserved() + "";
		} else {
			return "0";
		}
	}

	public double getSupplyStockMissing(SupplyRequirement supplyRequirement) {
		SupplyReserved aux = supplyReservedService.getSupplyReserved(supplyRequirement.getIdSupplyType(), supplyRequirement.getId());
		if(aux != null) {
			return supplyRequirement.getQuantity() - aux.getStockReserved();
		} else {
			return supplyRequirement.getQuantity();
		}
	}

	public RawMaterialType getRawMaterialType(int idRawMaterialType) {
		return rawMaterialTypeService.getRawMaterialType(idRawMaterialType);
	}

	public String getRawMaterialStock(int idRawMaterialType) {
		return "0";
	}

	public String getRawMaterialStockReserved(RawMaterialRequirement rawMaterialRequirement) {
		return "0";
	}

	public String getRawMaterialStockMissing(RawMaterialRequirement rawMaterialRequirement) {
		return rawMaterialRequirement.getQuantity() + "";
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
		openSupplyReservationGrid(data);
	}

	private void openSupplyReservationGrid(SupplyRequirement supplyRequirement) {
		supplyReservationGrid.setVisible(true);
		codeTextBox.setDisabled(true);
		descriptionTextBox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		stockReservedDoublebox.setDisabled(false);
		stockMissingDoublebox.setDisabled(true);
		codeTextBox.setText(supplyTypeService.getSupplyType(supplyRequirement.getIdSupplyType()).getCode());
		descriptionTextBox.setText(supplyTypeService.getSupplyType(supplyRequirement.getIdSupplyType()).getDescription());
		stockDoublebox.setValue(supplyTypeService.getSupplyType(supplyRequirement.getIdSupplyType()).getStock());
		stockReservedDoublebox.setValue(0.0);
		stockMissingDoublebox.setValue(supplyRequirement.getQuantity());
	}
}

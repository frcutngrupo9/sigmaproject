/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.WorkHour;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.SettingsRepository;
import ar.edu.utn.sigmaproject.service.WorkHourRepository;
import ar.edu.utn.sigmaproject.util.RenderElHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	final Logger logger = LoggerFactory.getLogger(ProductCreationController.class);

	@Wire
	Textbox productNameTextbox;
	@Wire
	Textbox productDetailsTextbox;
	@Wire
	Doublebox productPriceDoublebox;
	@Wire
	Button createPieceButton;
	@Wire
	Toolbarbutton cancelPieceButton;
	@Wire
	Toolbarbutton savePieceButton;
	@Wire
	Toolbarbutton resetPieceButton;
	@Wire
	Toolbarbutton deletePieceButton;
	@Wire
	Toolbarbutton deleteProductButton;
	@Wire
	Button uploadProductPhotoButton;
	@Wire
	Button deleteProductPhotoButton;
	@Wire
	Image productImage;
	@Wire
	Tabbox pieceCreationBlock;
	@Wire
	Textbox pieceNameTextbox;
	@Wire
	Textbox productCodeTextbox;
	@Wire
	Doublebox pieceLengthDoublebox;
	@Wire
	Doublebox pieceDepthDoublebox;
	@Wire
	Doublebox pieceWidthDoublebox;
	@Wire
	Selectbox lengthMeasureUnitSelectbox;
	@Wire
	Selectbox depthMeasureUnitSelectbox;
	@Wire
	Selectbox widthMeasureUnitSelectbox;
	@Wire
	Textbox pieceSizeTextbox;
	@Wire
	Checkbox pieceGroupCheckbox;
	@Wire
	Intbox pieceUnitsByProductIntbox;
	@Wire
	Listbox processListbox;
	@Wire
	Listbox pieceListbox;
	@Wire
	Caption productCaption;
	@Wire
	Button pieceCopyButton;
	@Wire
	Grid productGrid;
	@Wire
	Combobox productCategoryCombobox;
	@Wire
	Button openRawMaterialListButton;
	@Wire
	Button openSupplyListButton;
	@Wire
	Grid pieceGrid;
	@Wire
	Image pieceImage;
	@Wire
	Button deletePieceImageButton;
	@Wire
	Button uploadPieceImageButton;
	@Wire
	Tab pieceTab;
	@Wire
	Tab processTab;
	@Wire
	Label totalCostLabel;

	// services
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;
	@WireVariable
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductCategoryRepository productCategoryRepository;
	@WireVariable
	private OrderDetailRepository orderDetailRepository;
	@WireVariable
	private WorkHourRepository workHourRepository;
	@WireVariable
	private SettingsRepository settingsRepository;

	// attributes
	private Product currentProduct;
	private Piece currentPiece;
	private boolean pieceChanged;
	private Piece clonedPiece;
	@SuppressWarnings("rawtypes")
	private EventQueue eq;
	private WorkHour workHourDefault;

	// list
	private List<Piece> pieceList;
	private List<ProcessType> processTypeList;
	private List<Process> listboxProcessList;
	private List<ProductMaterial> supplyList;
	private List<ProductMaterial> rawMaterialList;

	// list models
	private ListModelList<Piece> pieceListModel;
	private ListModelList<ProductCategory> productCategoryListModel;
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
	private ListModelList<MeasureUnit> depthMeasureUnitListModel;
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		processTypeList = processTypeRepository.findAll();
		sortBySequence(processTypeList);
		processTypeListModel = new ListModelList<>(processTypeList);
		processListbox.setModel(processTypeListModel);
		workHourDefault = workHourRepository.findFirstByRole("Operario");
		if(workHourDefault == null) {
			List<WorkHour> workHourList = workHourRepository.findAll();
			if(workHourList.isEmpty() == false) {
				workHourDefault = workHourList.get(0);// si no esta vacio se asigna el primer elemento de la lista
			}
		}
		MeasureUnitType measureUnitType = measureUnitTypeRepository.findFirstByName("Longitud");
		List<MeasureUnit> measureUnitList = measureUnitType.getList();
		lengthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		depthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		widthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		lengthMeasureUnitSelectbox.setModel(lengthMeasureUnitListModel);
		depthMeasureUnitSelectbox.setModel(depthMeasureUnitListModel);
		widthMeasureUnitSelectbox.setModel(widthMeasureUnitListModel);
		currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
		currentPiece = null;
		createListeners();
		productCategoryListModel = new ListModelList<>(productCategoryRepository.findAll());
		productCategoryCombobox.setModel(productCategoryListModel);
		pieceChanged = false;
	}

	private void sortBySequence(List<ProcessType> list) {
		Comparator<ProcessType> comp = new Comparator<ProcessType>() {
			@Override
			public int compare(ProcessType a, ProcessType b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(list, comp);
	}

	public ListModelList<WorkHour> getWorkHourListModel() {
		return new ListModelList<WorkHour>(workHourRepository.findAll());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListeners() {
		// listener para cuando se modifique el producto al agregar materias primas o insumos
		eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onSupplyChange")) {
					supplyList = (List<ProductMaterial>) event.getData();
				} else {
					rawMaterialList = (List<ProductMaterial>) event.getData();
				}
			}
		});
		// agregamos un listener para cuando se seleccione una pieza en el modal de copia de otra pieza
		eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				Piece value = (Piece) event.getData();
				fillPieceCopy(value);
			}
		});
	}

	@Listen("onAfterRender = #productCategoryCombobox")
	public void productCategoryComboboxSelection() {// se hace refresh despues de q se renderizo el combobox para que se le pueda setear un valor seleccionado
		refreshViewProduct();
		refreshViewPiece();
	}

	@Listen("onClick = #profitPercentagePriceButton")
	public void profitPercentagePriceButtonClick() {
		if(currentProduct != null) {
			if(currentProduct.getCostTotal() != BigDecimal.ZERO) {
				BigDecimal totalCost = currentProduct.getCostTotal();
				BigDecimal costPercent = (settingsRepository.findAll().get(0).getPercentProfit().multiply(totalCost)).divide(new BigDecimal(100));
				BigDecimal generatedPrice = totalCost.add(costPercent);
				productPriceDoublebox.setValue(generatedPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
			} else {
				Clients.showNotification("El producto debe contener costos");
			}
		} else {
			Clients.showNotification("El producto debe estar guardado");
		}
	}

	@Listen("onClick = #newProductButton")
	public void newProductButtonClick() {
		currentProduct = null;
		refreshViewProduct();
		currentPiece = null;
		refreshViewPiece();
	}

	@Transactional
	@Listen("onClick = #saveProductButton")
	public void saveProduct() {
		if(Strings.isBlank(productNameTextbox.getValue())) {
			Clients.showNotification("Ingresar Nombre Producto", productNameTextbox);
			return;
		}
		if(supplyList.isEmpty()) {
			Clients.showNotification("Ingresar Insumos", openSupplyListButton);
			return;
		}
		if(rawMaterialList.isEmpty()) {
			Clients.showNotification("Ingresar Materia Prima", openRawMaterialListButton);
			return;
		}
		if(pieceList.isEmpty()) {
			Clients.showNotification("Ingresar Piezas", createPieceButton);
			return;
		}
		ProductCategory productCategory = null;
		if (productCategoryCombobox.getSelectedIndex() == -1) {
			String manuallyEnteredCategoryName = productCategoryCombobox.getValue();
			if (manuallyEnteredCategoryName.trim().length() > 0) {
				ProductCategory newProductCategory = new ProductCategory(manuallyEnteredCategoryName);
				productCategory = productCategoryRepository.save(newProductCategory);
			} else {
				Clients.showNotification("Seleccionar Categoria Producto", productCategoryCombobox);
				return;
			}
		}
		if(productCategory == null) {
			productCategory = productCategoryCombobox.getSelectedItem().getValue();
		}
		String productName = productNameTextbox.getText();//.toUpperCase();
		String productDetails = productDetailsTextbox.getText();
		String productCode = productCodeTextbox.getText();
		BigDecimal productPrice = new BigDecimal(productPriceDoublebox.doubleValue());
		org.zkoss.image.Image image = productImage.getContent();
		List<ProductMaterial> productMaterialList = new ArrayList<ProductMaterial>();
		productMaterialList.addAll(supplyList);
		productMaterialList.addAll(rawMaterialList);
		if(currentProduct == null) {// se esta creando un nuevo producto
			currentProduct = new Product(productCode, productName, productDetails, productCategory, productPrice);
			// se asigna la referencia al producto de los materiales y a las piezas
			for(ProductMaterial each : productMaterialList) {
				each.setProduct(currentProduct);
			}
			for(Piece each : pieceList) {
				each.setProduct(currentProduct);
			}
		} else {// se esta editando un producto
			currentProduct.setName(productName);
			currentProduct.setDetails(productDetails);
			currentProduct.setCode(productCode);
			currentProduct.setCategory(productCategory);
			currentProduct.setPrice(productPrice);
		}
		if (image != null) {
			currentProduct.setImageData(image.getByteData());			
		}
		currentProduct.setPieces(pieceList);
		currentProduct.setMaterials(productMaterialList);
		currentProduct = productRepository.save(currentProduct);
		Clients.showNotification("Producto guardado");
		currentPiece = null;
		refreshViewProduct();
		refreshViewPiece();
		//TODO: que no de error si se elimino del producto una pieza que es usada en algun plan de produccion
		// al momento de eliminar una pieza virificar si esta siendo usada en algun plan y si es asi, cambiar el isClone de la pieza a true
		// hacer que no se muestren las piezas con iscloned true en la lista
	}

	@Listen("onUpload = #uploadProductPhotoButton")
	public void doUploadProductPhoto(UploadEvent event) {
		org.zkoss.image.AImage media = (org.zkoss.image.AImage) event.getMedia();
		if (media instanceof org.zkoss.image.Image) {
			org.zkoss.image.Image img = media;
			int[] heightAndWidth = RenderElHelper.getHeightAndWidthScaled(img, 255);
			productImage.setHeight(heightAndWidth[0] + "px");
			productImage.setWidth(heightAndWidth[1] + "px");
			productImage.setStyle("margin: 8px");
			productImage.setContent(img);
			refreshViewPiece();// para que se pueda scrollear
			productNameTextbox.setFocus(true);
		} else {
			Messagebox.show("No es una imagen: " + media, "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick = #deleteProductPhotoButton")
	public void deleteProductPhoto() {
		org.zkoss.image.Image img = productImage.getContent();
		if(img != null) {// se borra solo  si hay una imagen
			img = null;
			productImage.setContent(img);
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
			productGrid.setHflex("2");
			refreshViewPiece();
			productNameTextbox.setFocus(true);
		}
	}

	@Listen("onClick = #createPieceButton")
	public void newPieceButtonClick() {
		pieceChanged = false;
		currentPiece = null;
		refreshViewPiece();
		pieceCreationBlock.setVisible(true);
		scrollToPieceBlock();
	}

	private void scrollToPieceBlock() {
		Clients.scrollIntoView(pieceCreationBlock);
	}

	@Listen("onClick = #savePieceButton")
	public void savePieceButtonClick() {
		if(savePiece()) {
			// actualizamos el view
			currentPiece = null;
			refreshViewPiece();
			pieceChanged = false;
		}
	}

	private boolean savePiece() {
		// comprueba si tiene procesos asignados
		if(listboxProcessList.isEmpty()) {
			Clients.showNotification("Debe agregar al menos 1 proceso");
			return false;
		}
		if(Strings.isBlank(pieceNameTextbox.getValue())) {
			Clients.showNotification("Ingrese el Nombre de la Pieza", pieceNameTextbox);
			return false;
		}
		if(pieceUnitsByProductIntbox.getValue() == null || pieceUnitsByProductIntbox.getValue() <= 0) {
			Clients.showNotification("La cantidad debe ser mayor a 0.", pieceUnitsByProductIntbox);
			return false;
		}
		// comprobamos que no existan checkbox activados que no posean valores de duracion
		for(int i = 2; i < processListbox.getChildren().size(); i++) { //empezamos en 2 para no recorrer los Listhead
			Checkbox chkbox = (Checkbox)processListbox.getChildren().get(i).getChildren().get(0).getChildren().get(0);
			Spinner hoursSpinner = (Spinner)processListbox.getChildren().get(i).getChildren().get(3).getChildren().get(0);
			Spinner minutesSpinner = (Spinner)processListbox.getChildren().get(i).getChildren().get(4).getChildren().get(0);
			Spinner secondsSpinner = (Spinner)processListbox.getChildren().get(i).getChildren().get(5).getChildren().get(0);
			if(chkbox.isChecked() && hoursSpinner.intValue() == 0 && minutesSpinner.intValue() == 0 && secondsSpinner.intValue() == 0) {
				Clients.showNotification("Ingrese el Tiempo para el Proceso", minutesSpinner);
				return false;
			}
		}
		String pieceName = pieceNameTextbox.getText().toUpperCase();
		MeasureUnit lengthMeasureUnit = null;
		MeasureUnit depthMeasureUnit = null;
		MeasureUnit widthMeasureUnit = null;
		if(lengthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			lengthMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthMeasureUnitSelectbox.getSelectedIndex());
		}
		if(depthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			depthMeasureUnit = depthMeasureUnitListModel.getElementAt(depthMeasureUnitSelectbox.getSelectedIndex());
		}
		if(widthMeasureUnitSelectbox.getSelectedIndex() != -1) {
			widthMeasureUnit = widthMeasureUnitListModel.getElementAt(widthMeasureUnitSelectbox.getSelectedIndex());
		}
		BigDecimal pieceLength = new BigDecimal(pieceLengthDoublebox.doubleValue());
		BigDecimal pieceDepth = new BigDecimal(pieceDepthDoublebox.doubleValue());
		BigDecimal pieceWidth = new BigDecimal(pieceWidthDoublebox.doubleValue());
		String pieceSize = pieceSizeTextbox.getText();
		Integer pieceUnits = pieceUnitsByProductIntbox.getValue();
		boolean pieceIsGroup = pieceGroupCheckbox.isChecked();
		org.zkoss.image.Image image = pieceImage.getContent();
		if(currentPiece == null) { // se esta creando una pieza
			currentPiece = new Piece(currentProduct, pieceName, pieceLength, lengthMeasureUnit, pieceDepth, depthMeasureUnit, pieceWidth, widthMeasureUnit, pieceSize, pieceIsGroup, pieceUnits);
			// se agrega la pieza a todos los procesos
			pieceList.add(currentPiece);// lo agregamos a la lista
			for(Process each : listboxProcessList) {
				each.setPiece(currentPiece);
			}
		} else { // se esta editando una pieza
			currentPiece.setName(pieceName);
			currentPiece.setLength(pieceLength);
			currentPiece.setLengthMeasureUnit(lengthMeasureUnit);
			currentPiece.setDepth(pieceDepth);
			currentPiece.setDepthMeasureUnit(depthMeasureUnit);
			currentPiece.setWidth(pieceWidth);
			currentPiece.setWidthMeasureUnit(widthMeasureUnit);
			currentPiece.setSize(pieceSize);
			currentPiece.setUnits(pieceUnits);
			currentPiece.setGroup(pieceIsGroup);
		}
		currentPiece.setProcesses(listboxProcessList);
		if (image != null) {
			currentPiece.setImageData(image.getByteData());
		} else {
			currentPiece.setImageData(null);
		}
		return true;
	}

	private void refreshViewProduct() {
		if (currentProduct == null) {
			productCaption.setLabel("Creacion de Producto");
			deleteProductButton.setDisabled(true);
			productNameTextbox.setText("");
			productDetailsTextbox.setText("");
			productCodeTextbox.setText(getNewProductCode());
			productCategoryCombobox.setSelectedIndex(-1);
			productPriceDoublebox.setText("");
			org.zkoss.image.Image img = null;
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
			productImage.setContent(img);
			pieceList = new ArrayList<>();
			pieceListModel = new ListModelList<>(pieceList);
			pieceListbox.setModel(pieceListModel);
			supplyList = new ArrayList<>();
			rawMaterialList = new ArrayList<>();
		} else {
			productCaption.setLabel("Edicion de Producto");
			deleteProductButton.setDisabled(false);
			productNameTextbox.setText(currentProduct.getName());
			productDetailsTextbox.setText(currentProduct.getDetails());
			productCategoryListModel = new ListModelList<>(productCategoryRepository.findAll());
			productCategoryCombobox.setSelectedIndex(productCategoryListModel.indexOf(productCategoryRepository.findOne(currentProduct.getCategory().getId())));
			productCodeTextbox.setText(currentProduct.getCode());
			BigDecimal product_price = currentProduct.getPrice();
			if(product_price != null) {
				productPriceDoublebox.setValue(product_price.doubleValue());
			} else {
				productPriceDoublebox.setValue(null);
			}
			org.zkoss.image.Image img = null;
			try {
				img = new AImage("", currentProduct.getImageData());
			} catch (IOException exception) {

			}
			if(img != null) {
				int[] heightAndWidth = RenderElHelper.getHeightAndWidthScaled(img, 255);
				productImage.setHeight(heightAndWidth[0] + "px");
				productImage.setWidth(heightAndWidth[1] + "px");
				productImage.setStyle("margin: 8px");
			} else {
				productImage.setHeight("0px");
				productImage.setWidth("0px");
				productImage.setStyle("margin: 0px");
			}
			productImage.setContent(img);
			pieceList = currentProduct.getPieces();
			pieceListModel = new ListModelList<>(pieceList);
			pieceListbox.setModel(pieceListModel);
			supplyList = new ArrayList<>(currentProduct.getSupplies());
			rawMaterialList = new ArrayList<>(currentProduct.getRawMaterials());
		}
	}

	private String getNewProductCode() {
		Integer lastValue = 0;
		List<Product> list = productRepository.findAll();
		for(Product each : list) {
			int code = 0;
			if(!each.getCode().equals("")) {
				code = Integer.parseInt(each.getCode());
			}
			if(code > lastValue) {
				lastValue = code;
			}
		}
		lastValue = lastValue + 1;
		return lastValue + "";
	}

	private void refreshViewPiece() {
		pieceTab.setSelected(true);
		pieceListModel = new ListModelList<>(pieceList);
		pieceListbox.setModel(pieceListModel);
		refreshTotalCostLabel();
		if (currentPiece == null) {// nueva pieza
			pieceCreationBlock.setVisible(false);
			deletePieceButton.setDisabled(true);
			pieceCopyButton.setDisabled(false);
			// limpiar form pieza
			pieceNameTextbox.setText("");
			pieceGroupCheckbox.setChecked(false);
			// seleccionamos metros y pulgadas como valores predeterminados de las dimensiones de las piezas
			MeasureUnit meters = measureUnitRepository.findFirstByName("Metros");
			MeasureUnit inch = measureUnitRepository.findFirstByName("Pulgadas");
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(meters));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(inch));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(inch));
			pieceLengthDoublebox.setValue(0);
			pieceWidthDoublebox.setValue(0);
			pieceDepthDoublebox.setValue(0);
			pieceSizeTextbox.setText("");
			pieceUnitsByProductIntbox.setValue(0);
			listboxProcessList = new ArrayList<Process>();
			pieceImage.setHeight("0px");
			pieceImage.setWidth("0px");
			org.zkoss.image.Image img = null;
			pieceImage.setContent(img);
		} else { // se esta editando una pieza
			pieceCreationBlock.setVisible(true);
			deletePieceButton.setDisabled(false);
			pieceCopyButton.setDisabled(true);
			// cargar form pieza
			pieceNameTextbox.setText(currentPiece.getName());
			pieceGroupCheckbox.setChecked(currentPiece.isGroup());
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(currentPiece.getLengthMeasureUnit()));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(currentPiece.getDepthMeasureUnit()));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(currentPiece.getWidthMeasureUnit()));
			BigDecimal lenght = currentPiece.getLength();
			if(lenght != null) {
				pieceLengthDoublebox.setValue(lenght.doubleValue());
			} else {
				pieceLengthDoublebox.setValue(0);
			}
			BigDecimal depth = currentPiece.getDepth();
			if(depth != null) {
				pieceDepthDoublebox.setValue(depth.doubleValue());
			} else {
				pieceDepthDoublebox.setValue(0);
			}
			BigDecimal width = currentPiece.getWidth();
			if(width != null) {
				pieceWidthDoublebox.setValue(width.doubleValue());
			} else {
				pieceWidthDoublebox.setValue(0);
			}
			pieceSizeTextbox.setValue(currentPiece.getSize());
			pieceUnitsByProductIntbox.setValue(currentPiece.getUnits());
			listboxProcessList = currentPiece.getProcesses();// actualizamos la lista de procesos del Listbox con la lista de procesos obtenida de los procesos totales
			org.zkoss.image.Image img = null;
			if(currentPiece.getImageData() != null) {
				try {
					img = new AImage("", currentPiece.getImageData());
				} catch (IOException exception) {
					alert("IOException en Piece.getImageData");
				}
				int[] heightAndWidth = RenderElHelper.getHeightAndWidthScaled(img, 150);
				pieceImage.setHeight(heightAndWidth[0] + "px");
				pieceImage.setWidth(heightAndWidth[1] + "px");
				pieceImage.setContent(img);
			} else {
				pieceImage.setHeight("0px");
				pieceImage.setWidth("0px");
				pieceImage.setContent(img);
			}
		}
		refreshViewProcess();
	}

	private void refreshViewProcess() {
		processTypeListModel = new ListModelList<ProcessType>(processTypeList);
		processListbox.setModel(processTypeListModel);
	}

	public String getProcessDetails(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return "";
		} else {
			return aux.getDetails();
		}
	}

	public Integer getProcessHours(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getHours();
		}
	}

	public Integer getProcessMinutes(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getMinutes();
		}
	}

	public Integer getProcessSeconds(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return 0;
		} else {
			return aux.getTime().getSeconds();
		}
	}

	public String getProcessWorkHour(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		String role = "No existen Roles";
		if(workHourDefault != null) {
			role = workHourDefault.getRole();
		}
		if(aux == null) {
			return role;
		} else {
			if(aux.getWorkHour() == null) {
				return role;
			} else {
				return aux.getWorkHour().getRole();
			}
		}
	}

	public boolean isProcessCheck(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux == null) {
			return false;
		} else {
			return true;
		}
	}

	private Process getProcessFromListbox(ProcessType processType) {
		if(listboxProcessList != null) {
			for(Process process : listboxProcessList) {
				if(process.getType().getId().equals(processType.getId())) {
					return process;
				}
			}
		}
		return null;
	}

	private void deleteProcessFromListbox(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		if(aux != null) {
			listboxProcessList.remove(aux);
		}
	}

	private Process addProcessToListbox(ProcessType processType) {
		Process aux = getProcessFromListbox(processType);
		Process process = null;
		if(aux != null) {
			System.out.println("deberia estar en null: " + aux.getType().getId());// porque se esta agregando
		} else {
			Duration duration = null;
			try {
				duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 0, 0, 0);
			} catch (DatatypeConfigurationException e) {
				System.out.println("Error en convertir a duracion: " + e.toString());
			}
			process = new Process(currentPiece, processType, "", duration, workHourDefault);
			listboxProcessList.add(process);
		}
		return process;
	}

	//when user checks on the checkbox of each process on the list
	@Listen("onProcessCheck = #processListbox")
	public void doProcessCheck(ForwardEvent evt) {// se usa para eliminar o agregar el proceso a la lista
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
		Process process = null;
		if(cbox.isChecked()) {
			process = addProcessToListbox(data);
		} else {
			deleteProcessFromListbox(data);
		}
		Listcell listcell = (Listcell)cbox.getParent();
		Listitem listitem = (Listitem)listcell.getParent();
		doProcessLineVisible(cbox.isChecked(), listitem, process);
	}

	private void doProcessLineVisible(boolean visible, Listitem listitem, Process process) {
		// se hacen visibles o invisibles todos los elementos de la fila
		for(int i = 2; i < listitem.getChildren().size(); i++) {//inicia en 2 para no afectar al checkbox y al nombre del proceso
			Listcell listcell = (Listcell) listitem.getChildren().get(i);
			AbstractComponent abstractComponent = (AbstractComponent) listcell.getChildren().get(0);// el unico children debe ser el elemento
			abstractComponent.setVisible(visible);
			// se limpian los valores si se hace visible
			if(visible == true) {
				updateProcessValues(abstractComponent);
			}
		}
	}

	private void updateProcessValues(AbstractComponent abstractComponent) {
		//System.out.println("------------ instanceof " + abstractComponent.getClass().getCanonicalName());
		if(abstractComponent instanceof Bandbox) {
			Bandbox bandboxProcessWorkHour = (Bandbox) abstractComponent;
			bandboxProcessWorkHour.setValue(workHourDefault.getRole());
			return;
		} else if(abstractComponent instanceof Textbox) { 
			Textbox textboxProcessDetails = (Textbox) abstractComponent;
			textboxProcessDetails.setValue("");
			return;
		} else if(abstractComponent instanceof Spinner) {
			Spinner spinnerProcessHours = (Spinner) abstractComponent;
			spinnerProcessHours.setValue(0);
			return;
		}
	}

	@Listen("onProcessDetailsChange = #processListbox")
	public void doProcessDetailsChange(ForwardEvent evt) {
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Textbox origin = (Textbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(inputEvent.getValue());
		Process process = getProcessFromListbox(data);
		process.setDetails(origin.getText());
	}

	@Listen("onProcessHoursChange = #processListbox")
	public void doProcessHoursChange(ForwardEvent evt) {
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Process process = getProcessFromListbox(data);
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, origin.intValue(), process.getTime().getMinutes(), process.getTime().getSeconds());
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		process.setTime(duration);
	}

	@Listen("onProcessMinutesChange = #processListbox")
	public void doProcessMinutesChange(ForwardEvent evt) {
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		String inputValue = inputEvent.getValue();
		if(inputValue.compareTo("") != 0) {
			origin.setValue(Integer.valueOf(inputEvent.getValue()));
			Process process = getProcessFromListbox(data);
			Duration duration = null;
			try {
				duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, process.getTime().getHours(), origin.intValue(), process.getTime().getSeconds());
			} catch (DatatypeConfigurationException e) {
				System.out.println("Error en convertir a duracion: " + e.toString());
			}
			process.setTime(duration);
		}
	}

	@Listen("onProcessSecondsChange = #processListbox")
	public void doProcessSecondsChange(ForwardEvent evt) {
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		String inputValue = inputEvent.getValue();
		if(inputValue.compareTo("") != 0) {
			origin.setValue(Integer.valueOf(inputEvent.getValue()));
			Process process = getProcessFromListbox(data);
			Duration duration = null;
			try {
				duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, process.getTime().getHours(), process.getTime().getMinutes(), origin.intValue());
			} catch (DatatypeConfigurationException e) {
				System.out.println("Error en convertir a duracion: " + e.toString());
			}
			process.setTime(duration);
		}
	}

	@Listen("onProcessWorkHourChange = #processListbox")
	public void doProcessWorkHourChange(ForwardEvent evt) {
		pieceChanged = true;
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Listbox element = (Listbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		WorkHour workHour = (WorkHour)element.getSelectedItem().getValue();
		Process process = getProcessFromListbox(data);
		process.setWorkHour(workHour);
		// modificamos el componente web para que muestre la seleccion realizada
		Bandbox bandbox = (Bandbox)element.getParent().getParent();
		bandbox.setText(workHour.getRole());
		bandbox.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onSelect = #pieceListbox")
	public void selectPiece() {
		if(pieceListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentPiece = null;
		} else {
			if(currentPiece != null) {// si no hay nada editandose
				// si existen cambios a la pieza, se pregunta para guardar antes
				if(pieceChanged) {
					Messagebox.show("Desea guardar los cambios realizados a la pieza? ", "Confirmar Guardado", Messagebox.OK | Messagebox.NO | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
						public void onEvent(Event evt) throws InterruptedException {
							if (evt.getName().equals("onOK")) {
								if(savePiece() == false) {
									// no se guardo la pieza
									return;
								}
							} else if(evt.getName().equals("onCancel")) {
								//no se guarda ni se cambia de pieza
								return;
							}
							selectPieceAndRefresh();
						}
					});
					return;
				}
			}
			selectPieceAndRefresh();
		}
	}

	private void selectPieceAndRefresh() {
		currentPiece = pieceListbox.getSelectedItem().getValue();
		// se crea un clon de la pieza para reemplazar al currentPiece en caso de que se cancele o resetee la edicion
		clonedPiece = (Piece) Piece.copy(currentPiece);
		refreshViewPiece();
		pieceChanged = false;
		pieceListModel.clearSelection();
		scrollToPieceBlock();
	}

	@Listen("onClick = #resetPieceButton")
	public void resetPiece() {
		if(currentPiece != null) {
			Piece piece = getOriginalPiece();
			pieceList.set(pieceList.indexOf(currentPiece), piece);
			currentPiece = piece;
		}
		refreshViewPiece();
		pieceCreationBlock.setVisible(true);
	}

	private Piece getOriginalPiece() {
		return (Piece) Piece.copy(clonedPiece);// se pasa solo la copia para evitar que el clon sea modificado
	}

	@Listen("onClick = #cancelPieceButton")
	public void cancelPiece() {
		if(currentPiece != null) {
			Piece piece = getOriginalPiece();
			pieceList.set(pieceList.indexOf(currentPiece), piece);
		}
		currentPiece = null;
		refreshViewPiece();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteProductButton")
	public void deleteProduct() {
		if(currentProduct != null) {
			if(orderDetailRepository.findFirstByProduct(currentProduct) != null) {
				Messagebox.show("No se puede eliminar, el producto se encuentra agregado en 1 o mas pedidos.", "Informacion", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			Messagebox.show("Esta seguro que quiere eliminar el producto?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						productRepository.delete(currentProduct);
						currentProduct = null;
						currentPiece = null;
						refreshViewProduct();
						refreshViewPiece();
						alert("Producto eliminado.");
					}
				}
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deletePieceButton")
	public void deletePieceButtonClick() {
		if(currentPiece != null) {
			Messagebox.show("Esta seguro que desea eliminar la pieza " + currentPiece.getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						//eliminamos la pieza
						deletePiece();
					}
				}
			});
		}
	}

	private void deletePiece() {
		int index = -1;
		for(Piece auxPiece:pieceList) {
			if(auxPiece.getId().equals(currentPiece.getId())) {
				index = pieceList.indexOf(auxPiece);
				break;
			}
		}
		if(index > -1) {
			pieceList.remove(index);
		}
		pieceListModel = new ListModelList<>(pieceList);
		pieceListbox.setModel(pieceListModel);
		currentPiece = null;
		refreshViewPiece();
		alert("Pieza eliminada.");
	}

	@Listen("onClick = #resetProductButton")
	public void resetProduct() {
		// busca en la bd la version que tiene sin modificar los detalles
		if(currentProduct != null) {
			currentProduct = productRepository.findOne(currentProduct.getId());
		}
		refreshViewProduct();
		currentPiece = null;
		refreshViewPiece();
	}

	@Listen("onClick = #pieceCopyButton")
	public void doPieceCopyButtonClick() {
		newPieceButtonClick();
		// mostramos el modal para seleccionar la pieza
		Window window = (Window)Executions.createComponents(
				"/piece_selection_modal.zul", null, null);
		window.doModal();
	}

	private void fillPieceCopy(Piece pieceCopy) {
		pieceNameTextbox.setText(pieceCopy.getName());
		pieceGroupCheckbox.setChecked(pieceCopy.isGroup());
		lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(pieceCopy.getLengthMeasureUnit()));
		depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(pieceCopy.getDepthMeasureUnit()));
		widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(pieceCopy.getWidthMeasureUnit()));
		BigDecimal lenght = pieceCopy.getLength();
		if(lenght != null) {
			pieceLengthDoublebox.setValue(lenght.doubleValue());
		} else {
			pieceLengthDoublebox.setValue(0);
		}
		BigDecimal depth = pieceCopy.getDepth();
		if(depth != null) {
			pieceDepthDoublebox.setValue(depth.doubleValue());
		} else {
			pieceDepthDoublebox.setValue(0);
		}
		BigDecimal width = pieceCopy.getWidth();
		if(width != null) {
			pieceWidthDoublebox.setValue(width.doubleValue());
		} else {
			pieceWidthDoublebox.setValue(0);
		}
		pieceSizeTextbox.setValue(pieceCopy.getSize());
		pieceUnitsByProductIntbox.setValue(pieceCopy.getUnits());
		fillProcessCopy(pieceCopy);
	}

	private void fillProcessCopy(Piece pieceCopy) {
		List<Process> processListCopy = pieceCopy.getProcesses();
		listboxProcessList = new ArrayList<Process>();
		for(Process processCopy : processListCopy) {
			listboxProcessList.add(new Process(null, processTypeRepository.findOne(processCopy.getType().getId()), processCopy.getDetails(), processCopy.getTime(), processCopy.getWorkHour()));
		}
		refreshViewProcess();
	}

	@Listen("onClick = #openRawMaterialListButton")
	public void openRawMaterialListButtonClick() {
		Executions.getCurrent().setAttribute("rawMaterialList", rawMaterialList);
		Executions.getCurrent().setAttribute("currentProduct", currentProduct);
		Window window = (Window)Executions.createComponents("/product_raw_material.zul", null, null);
		window.doModal();
	}

	@Listen("onClick = #openSupplyListButton")
	public void openSupplyListButtonClick() {
		Executions.getCurrent().setAttribute("supplyList", supplyList);
		Executions.getCurrent().setAttribute("currentProduct", currentProduct);
		Window window = (Window)Executions.createComponents("/product_supply.zul", null, null);
		window.doModal();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/product_list.zul");
	}

	@Listen("onClick = #deletePieceImageButton")
	public void deletePieceImageButtonClick() {
		org.zkoss.image.Image img = pieceImage.getContent();
		if(img != null) {// se borra solo  si hay una imagen
			img = null;
			pieceImage.setContent(img);
			pieceImage.setHeight("0px");
			pieceImage.setWidth("0px");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #uploadPieceImageButton")
	public void uploadPieceImageButtonClick() {
		Fileupload.get(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				UploadEvent uploadEvent = (UploadEvent) event;
				org.zkoss.util.media.Media media = uploadEvent.getMedia();
				if (media instanceof org.zkoss.image.Image) {
					org.zkoss.image.Image img = (org.zkoss.image.Image) media;
					int[] heightAndWidth = RenderElHelper.getHeightAndWidthScaled(img, 150);
					pieceImage.setHeight(heightAndWidth[0] + "px");
					pieceImage.setWidth(heightAndWidth[1] + "px");
					pieceImage.setContent(img);
					processTabpanelClick();
				} else {
					Messagebox.show("No es una imagen: "+media, "Error", Messagebox.OK, Messagebox.ERROR);
				}
			}
		});
	}

	@Listen("onClick = #processTab")
	public void processTabpanelClick() {
		processListbox.setModel(processTypeListModel);// se realiza pra que se re-renderice el scrollbar
	}

	@Listen("onChanging = #pieceNameTextbox, #pieceLengthDoublebox, #pieceDepthDoublebox, #pieceWidthDoublebox, #pieceSizeTextbox, #pieceUnitsByProductIntbox")
	public void pieceDetaisOnChanging() {
		pieceChanged = true;
	}

	@Listen("onSelect = #lengthMeasureUnitSelectbox, #depthMeasureUnitSelectbox, #widthMeasureUnitSelectbox")
	public void pieceDetaisOnSelect() {
		pieceChanged = true;
	}

	@Listen("onCheck = #pieceGroupCheckbox")
	public void pieceDetaisOnCheck() {
		pieceChanged = true;
	}

	protected void refreshTotalCostLabel() {
		BigDecimal totalCost = getTotalCost();
		totalCostLabel.setValue(totalCost.doubleValue() + "");
	}

	private BigDecimal getTotalCost() {
		BigDecimal totalCost = new BigDecimal("0");
		for(Piece each : pieceList) {
			totalCost = totalCost.add(each.getCost());
		}
		return totalCost;
	}
}

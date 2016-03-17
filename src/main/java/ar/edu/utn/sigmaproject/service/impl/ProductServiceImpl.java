package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.image.Image;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductImage;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProductImageService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.SupplyService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductServiceImpl implements ProductService {

	static List<Product> productList = new ArrayList<Product>();
	private SerializationService serializator = new SerializationService("product");

	public ProductServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Product> aux = serializator.obtenerLista();
		if(aux != null) {
			productList = aux;
		} else {
			serializator.grabarLista(productList);
		}
	}

	public synchronized List<Product> getProductList() {
		List<Product> list = new ArrayList<Product>();
		for(Product product:productList){
			if(product.isClone() == false) {// se devuelven solo los que no sean clones
				list.add(Product.clone(product));
			}
		}
		return list;
	}

	public synchronized Product getProduct(Integer id) {
		for(Product product:productList) {
			if(product.getId().equals(id)) {
				return Product.clone(product);
			}
		}
		return null;
	}

	public synchronized Product saveProduct(Product product) {
		if(product.getId() == null) {
			Integer new_id = getNewId();
			product.setId(new_id);
		}

		if(existId(product.getId())){
			throw new IllegalArgumentException("can't save product, id already used");
		}
		product = Product.clone(product);
		productList.add(product);
		serializator.grabarLista(productList);
		return product;
	}

	public synchronized Product updateProduct(Product product) {
		if(product.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id product, save it first");
		}else {
			product = Product.clone(product);
			int size = productList.size();
			for(int i = 0; i < size; i++) {
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())) {
					productList.set(i, product);
					serializator.grabarLista(productList);
					return product;
				}
			} throw new RuntimeException("Product not found "+product.getId());
		}
	}

	public synchronized void deleteProduct(Product product) {
		if(product.getId() != null) {// hay que agregar un checkeo para no eliminar el producto si esta siendo referenciado por otros objetos u enviar alguna confirmacion
			new PieceServiceImpl().deleteAll(product.getId());// se realiza una eliminacion en cascada de las piezas relacionadas al producto, los procesos se eliminan en el servicio de la pieza
			new RawMaterialServiceImpl().deleteAll(product.getId());
			new ProductImageServiceImpl().deleteAll(product.getId());
			int size = productList.size();
			for(int i = 0; i < size; i++) {
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())) {
					productList.remove(i);
					serializator.grabarLista(productList);
					return;
				}
			}
		}
	}

	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < productList.size(); i++) {
			Product aux = productList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	private boolean existId(Integer id) {
		boolean value = false;
		for(int i =0; i < productList.size(); i++) {
			Product aux = productList.get(i);
			if(aux.getId().equals(id)) {
				value = true;
			}
		}
		return value;
	}

	public synchronized Product saveProduct(Product product, List<Piece> pieceList, List<Process> processList) {
		product = saveProduct(product);// vuelve con id agregado
		PieceService pieceService = new PieceServiceImpl();
		if(pieceList != null && pieceList.isEmpty() == false) {// se guardan todas las piezas
			for(int i = 0; i < pieceList.size(); i++) {
				pieceList.get(i).setIdProduct(product.getId());// se le asigna el id del producto
				pieceService.savePiece(pieceList.get(i));
			}
		}
		ProcessService processService = new ProcessServiceImpl();
		if(processList != null && processList.isEmpty() == false) {// se guardan todos los procesos
			for(int i = 0; i < processList.size(); i++) {
				processService.saveProcess(processList.get(i));
			}
		}
		return product;
	}

	public synchronized Product saveProduct(Product product, List<Piece> pieceList, List<Process> processList, List<Supply> supplyList, List<RawMaterial> rawMaterialList) {
		product = saveProduct(product, pieceList, processList);
		SupplyService supplyService = new SupplyServiceImpl();
		if(supplyList != null && supplyList.isEmpty() == false) {// se guardan los insumos
			for(int i = 0; i < supplyList.size(); i++) {
				supplyList.get(i).setIdProduct(product.getId());// se le asigna el id del producto
				supplyService.saveSupply(supplyList.get(i));
			}
		}
		RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
		if(rawMaterialList != null && rawMaterialList.isEmpty() == false) {// se guardan las materias primas
			for(int i = 0; i < rawMaterialList.size(); i++) {
				rawMaterialList.get(i).setIdProduct(product.getId());// se le asigna el id del producto
				rawMaterialService.saveRawMaterial(rawMaterialList.get(i));
			}
		}
		return product;
	}

	public synchronized Product updateProduct(Product product, List<Piece> pieceList, List<Process> processList) {
		product = updateProduct(product);
		PieceService pieceService = new PieceServiceImpl();
		ProcessService processService = new ProcessServiceImpl();
		if(pieceList != null) {// se actualizan todas las piezas
			// primero eliminamos las piezas que estan en el service, pero que no existen mas en el producto
			List<Piece> auxPieceList = pieceService.getPieceList(product.getId());// obtenemos las piezas del producto que estan en el servicio
			for(Piece auxPiece:auxPieceList) {// recorremos todas las piezas obtenidas
				Piece aux = searchPiece(auxPiece.getId(), pieceList);// buscamos en la lista para ver si esta tambien ahi
				if(aux == null) {// si la pieza no esta en la lista se debe eliminar del service
					pieceService.deletePiece(auxPiece);// eliminamos la pieza, el servicio se encarga de eliminar los procesos relacionados a esa pieza
				}
			}
			// ahora recorremos la lista para actualizar las piezas que ya existen o agregar las que no
			for(Piece current:pieceList) {
				Piece auxPiece = pieceService.getPiece(current.getId());// se busca a ver si existe la pieza
				if(auxPiece == null) {// es una nueva pieza, se graba
					current.setIdProduct(product.getId());// se le asigna el id del producto
					pieceService.savePiece(current);
				} else {// esta pieza existe, se actualiza
					pieceService.updatePiece(current);
				}
			}
		}
		if(processList != null) {// se actualizan todos los procesos
			// primero eliminamos los procesos que estan en el service, pero que no existen mas en el producto
			List<Process> completeProcessList = new ArrayList<Process>();
			List<Piece> auxPieceList = pieceService.getPieceList(product.getId());// obtenemos las piezas del producto que estan en el servicio con los clones incluidos, pq alguna piezas q son clones aun tienen procesos referenciados q no son clones
			for(Piece auxPiece:auxPieceList) {// recorremos todas las piezas del producto
				List<Process> auxProcessList = processService.getProcessList(auxPiece.getId());// por cada pieza buscamos sus procesos, no debe contener clones
				for(Process auxProcess:auxProcessList) {
					completeProcessList.add(auxProcess);// agregamos los procesos a la lista completa
				}
			}
			for(Process auxProcess:completeProcessList) {// recorremos la lista completa de procesos que estan en el servicio
				Process aux = searchProcess(auxProcess, processList);// se busca si el proceso existe en la lista actualizada del producto
				if(aux == null) {// si el proceso no esta en la lista se debe eliminar del service
					processService.deleteProcess(auxProcess);
				}
			}
			// ahora recorremos la lista para actualizar los procesos que ya existen o agregar los que no
			for(int i = 0; i < processList.size(); i++) {
				Integer pieceId = processList.get(i).getIdPiece();
				Integer processTypeId = processList.get(i).getIdProcessType();
				Process process = processService.getProcess(pieceId, processTypeId);
				if(process == null) {// no existe, se guarda
					processService.saveProcess(processList.get(i));
				} else {// existe, se actualiza
					Process aux = processList.get(i);
					aux.setId(process.getId());// volvemos a insertar el id debido a que quizas en la edicion de producto se volvio a crear el mismo proceso con id null
					processService.updateProcess(aux);
				}
			}
		}
		return product;
	}

	public synchronized Product updateProduct(Product product, List<Piece> pieceList, List<Process> processList, List<Supply> supplyList, List<RawMaterial> rawMaterialList) {
		product = updateProduct(product, pieceList, processList);
		if(supplyList != null) {
			// primero eliminamos los insumos que estan en el service, pero que no existen mas en el producto
			SupplyService supplyService = new SupplyServiceImpl();
			List<Supply> completeSupplyList = supplyService.getSupplyList(product.getId());
			for(Supply auxSupply:completeSupplyList) {// recorremos la lista completa de insumos que estan en el servicio
				Supply aux = searchSupply(auxSupply.getId(), supplyList);
				if(aux == null) {// si el insumo no esta en la lista se debe eliminar del service
					supplyService.deleteSupply(auxSupply);
				}
			}
			// ahora recorremos la lista para actualizar los insumos que ya existen o agregar los que no
			for(int i = 0; i < supplyList.size(); i++) {
				if(supplyList.get(i).getId() == null) {// es un nuevo insumo, se guarda
					supplyList.get(i).setIdProduct(product.getId());// se le asigna el id del producto
					supplyService.saveSupply(supplyList.get(i));
				} else {// se actualiza
					supplyService.updateSupply(supplyList.get(i));
				}
			}
		}
		if(rawMaterialList != null) {
			// primero eliminamos las materias primas que estan en el service, pero que no existen mas en el producto
			RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
			List<RawMaterial> completeRawMaterialList = rawMaterialService.getRawMaterialList(product.getId());
			for(RawMaterial auxRawMaterial:completeRawMaterialList) {// recorremos la lista completa de insumos que estan en el servicio
				RawMaterial aux = searchRawMaterial(auxRawMaterial.getId(), rawMaterialList);
				if(aux == null) {// si la materia prima no esta en la lista se debe eliminar del service
					rawMaterialService.deleteRawMaterial(auxRawMaterial);
				}
			}
			// ahora recorremos la lista para actualizar las materias primas que ya existen o agregar los que no
			for(int i = 0; i < rawMaterialList.size(); i++) {
				if(rawMaterialList.get(i).getId() == null) {// es una nueva materia prima, se guarda
					rawMaterialList.get(i).setIdProduct(product.getId());// se le asigna el id del producto
					rawMaterialService.saveRawMaterial(rawMaterialList.get(i));
				} else {// se actualiza
					rawMaterialService.updateRawMaterial(rawMaterialList.get(i));
				}
			}
		}
		return product;
	}

	private synchronized Piece searchPiece(Integer idPiece, List<Piece> list) {
		int size = list.size();
		for(int i = 0; i < size; i++) {
			Piece t = list.get(i);
			if(t.getId().equals(idPiece)) {
				return Piece.clone(t);
			}
		}
		return null;
	}

	private synchronized Process searchProcess(Process other, List<Process> processList) {
		int size = processList.size();
		for(int i = 0; i < size; i++) {
			Process t = processList.get(i);
			if(t.getIdPiece().equals(other.getIdPiece()) && t.getIdProcessType().equals(other.getIdProcessType())) {
				return Process.clone(t);
			}
		}
		return null;
	}

	private synchronized Supply searchSupply(Integer idSupply, List<Supply> list) {
		int size = list.size();
		for(int i = 0; i < size; i++) {
			Supply t = list.get(i);
			if(t.getId() != null && t.getId().equals(idSupply)) {
				return Supply.clone(t);
			}
		}
		return null;
	}

	private synchronized RawMaterial searchRawMaterial(Integer idRawMaterial, List<RawMaterial> list) {
		int size = list.size();
		for(int i = 0; i < size; i++) {
			RawMaterial t = list.get(i);
			if(t.getId() != null && t.getId().equals(idRawMaterial)) {
				return RawMaterial.clone(t);
			}
		}
		return null;
	}

	public Image findImage(Product currentProduct) {
		ProductImageService productImageService = new ProductImageServiceImpl();
		ProductImage aux = productImageService.findFirstProductImage(currentProduct.getId());
		if(aux != null) {
			return aux.getImage();
		} else {
			return null;
		}
	}

	public Product saveProduct(Product currentProduct, Image image,
			List<Piece> pieceList, List<Process> processList,
			List<Supply> supplyList, List<RawMaterial> rawMaterialList) {
		currentProduct = saveProduct(currentProduct, pieceList, processList, supplyList, rawMaterialList);
		ProductImageService productImageService = new ProductImageServiceImpl();
		if(image != null) {
			productImageService.save(new ProductImage(null, currentProduct.getId(), image));
		}
		return currentProduct;
	}

	public Product updateProduct(Product currentProduct, Image image,
			List<Piece> pieceList, List<Process> processList,
			List<Supply> supplyList, List<RawMaterial> rawMaterialList) {
		currentProduct = updateProduct(currentProduct, pieceList, processList, supplyList, rawMaterialList);
		ProductImageService productImageService = new ProductImageServiceImpl();
		ProductImage aux = productImageService.findFirstProductImage(currentProduct.getId());
		if(image != null) {
			if(aux != null) {
				aux.setImage(image);
				productImageService.save(aux);
			} else {
				productImageService.save(new ProductImage(null, currentProduct.getId(), image));
			}
		} else {
			if(aux != null) {
				productImageService.deleteProductImage(aux);
			} 
		}
		return currentProduct;
	}
}

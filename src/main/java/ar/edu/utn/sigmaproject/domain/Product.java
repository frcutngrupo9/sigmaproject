package ar.edu.utn.sigmaproject.domain;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;

@Entity
@Indexed
public class Product implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany
	List<Piece> pieces;

	@OneToMany
	List<Supply> supplies;

	@OneToMany
	List<RawMaterial> rawMaterials;

	@Lob
	byte[] imageData = new byte[0];

	@Field
	String name = "";
	String details = "";
	String code = "";
	Integer stock = 0;
	Integer stockMin = 0;
	Integer stockRepo = 0;

	@ManyToOne
	ProductCategory category;

	BigDecimal price = BigDecimal.ZERO;
	boolean isClone;

	public Product() {

	}

	public Product(String code , String name, String details, ProductCategory category, BigDecimal price) {
		this.name = name;
		this.details = details;
		this.category = category;
		this.code = code;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(List<Piece> pieces) {
		this.pieces = pieces;
	}

	public List<Supply> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<Supply> supplies) {
		this.supplies = supplies;
	}

	public List<RawMaterial> getRawMaterials() {
		return rawMaterials;
	}

	public void setRawMaterials(List<RawMaterial> rawMaterials) {
		this.rawMaterials = rawMaterials;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getStockMin() {
		return stockMin;
	}

	public void setStockMin(Integer stockMin) {
		this.stockMin = stockMin;
	}

	public Integer getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(Integer stockRepo) {
		this.stockRepo = stockRepo;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getName() {
		return name;
	}

	public String getDetails() {
		return details;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getCode() {
		return code;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return id != null && other.id != null && id.equals(other.id);
	}

	public static Product clone(Product product){
		try {
			return (Product)product.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}

package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class SupplyType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	String code;
	String description;
	String details;
	String brand;
	String presentation;
	String measure;
	
	public SupplyType(Integer id, String code, String description, String details, String brand, String presentation, String measure) {
		this.id = id;
		this.code = code;
		this.description = description;
		this.details = details;
		this.brand = brand;
		this.presentation = presentation;
		this.measure = measure;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPresentation() {
		return presentation;
	}

	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
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
        SupplyType other = (SupplyType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    public static SupplyType clone(SupplyType supplyType){
        try {
            return (SupplyType)supplyType.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }
}

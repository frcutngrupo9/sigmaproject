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

package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class MaterialsOrder implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "materialsOrder", targetEntity = MaterialsOrderDetail.class)
	private List<MaterialsOrderDetail> details = new ArrayList<>();
	
	@ManyToOne
	private ProductionPlan productionPlan = null;
	
	private Integer number = 0;
	private Date date = null;
	private Date dateReception = null;
	private String receiptNumber = "";
	
	public MaterialsOrder() {
		
	}
	
	public MaterialsOrder(Integer number, Date date) {
		this.number = number;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<MaterialsOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MaterialsOrderDetail> details) {
		this.details = details;
	}

	public Date getDateReception() {
		return dateReception;
	}

	public void setDateReception(Date dateReception) {
		this.dateReception = dateReception;
	}

	public ProductionPlan getProductionPlan() {
		return productionPlan;
	}

	public void setProductionPlan(ProductionPlan productionPlan) {
		this.productionPlan = productionPlan;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	
//	public String isTotallyReceived() {
//		for(MaterialsOrderDetail each : details) {
//			if(!each.getQuantity().equals(each.getQuantityReceived())) {
//				return "NO";
//			}
//		}
//		return "SI";
//	}
	
	public boolean isTotallyReceived() {
		for(MaterialsOrderDetail each : details) {
			if(each.getQuantity().compareTo(each.getQuantityReceived()) > 0) {// cantidad necesaria mayor que cantidad recibida
				return false;
			}
		}
		return true;
	}
}

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

import org.hibernate.search.annotations.Indexed;

@Entity(name = "Orders")
@Indexed
public class Order implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "order", targetEntity = OrderDetail.class)
	private List<OrderDetail> details = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	private List<OrderState> states = new ArrayList<>();

	@ManyToOne
	private Client client;

	@ManyToOne
	private OrderStateType currentStateType = null;

	private Integer number = 0;
	private Date date = new Date();
	private Date needDate = new Date();
	private String numberBill = "";

	public Order() {
	}

	public Order(Client client, Integer number, Date date, Date needDate) {
		this.client = client;
		this.number = number;
		this.date = date;
		this.needDate = needDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}

	public OrderStateType getCurrentStateType() {
		return currentStateType;
	}

	public OrderState getCurrentState() {
		OrderState result = null;
		for(OrderState each : states) {// busca el objeto con la fecha mas reciente
			if(result != null) {
				if(result.getDate().before(each.getDate())) {
					result = each;
				}
			} else {
				result = each;
			}
		}
		if(result != null) {
			return result;
		}
		return null;
	}

	public void setState(OrderState state) {
		currentStateType = state.getType();
		states.add(state);
	}

	public List<OrderState> getStates() {
		return states;
	}

	public void setStates(List<OrderState> states) {
		this.states = states;
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

	public Date getNeedDate() {
		return needDate;
	}

	public void setNeedDate(Date needDate) {
		this.needDate = needDate;
	}

	public String getNumberBill() {
		return numberBill;
	}

	public void setNumberBill(String numberBill) {
		this.numberBill = numberBill;
	}

	public List<Product> getProductList() {
		List<Product> productList = new ArrayList<Product>();
		for(OrderDetail each : details) {
			productList.add(each.getProduct());
		}
		return productList;
	}
}
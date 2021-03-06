package com.gcaldas.conceptualmodel.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class PurchaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
	private Date orderDate;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "orderNumber")
	private Payment payment;

	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	@ManyToOne
	@JoinColumn(name = "address_id")
	private Address deliveryAddress;

	@OneToMany(mappedBy = "id.purchaseOrder")
	private Set<OrderItem> items = new HashSet<>();

	public PurchaseOrder() {

	}

	public PurchaseOrder(Integer id, Date orderDate, Client client, Address deliveryAddress) {
		super();
		this.id = id;
		this.orderDate = orderDate;
		this.client = client;
		this.deliveryAddress = deliveryAddress;
	}
	
	public double getTotalValue() {
		double sum = 0.0;
		for(OrderItem orderItem : items) {
			sum = sum + orderItem.getSubTotal();
		}
		return sum;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Set<OrderItem> getItems() {
		return items;
	}

	public void setItems(Set<OrderItem> items) {
		this.items = items;
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
		PurchaseOrder other = (PurchaseOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CANADA);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrder: ");
		builder.append(getId());
		builder.append(", Order date: ");
		builder.append(sdf.format(getOrderDate()));
		builder.append(", Client: ");
		builder.append(getClient().getName());
		builder.append(", Status payment: ");
		builder.append(getPayment().getStatus().getDescription());
		builder.append("\nDetails:\n");
		for(OrderItem orderItem : getItems()) {
			builder.append(orderItem.toString());
		}
		builder.append("Total value: ");
		builder.append(nf.format(getTotalValue()));
		return builder.toString();
	}
}

package org.example.foodappmobile;

import java.util.List;

public class DBOrders {

	private int id;
	private String number;
	private List<String> products;
	
	public int getId() {
		return id;
	}
	
	public String getNumber() {
		return number;
	}
	
	public List<String> getProducts() {
		return products;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public void setProducts(List<String> products) {
		this.products = products;
	}
}

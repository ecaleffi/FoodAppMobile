package org.example.foodappmobile;

import java.util.List;

public class Recipe {
	
	private String description;
	private List<String> products;
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setProducts(List<String> products) {
		this.products = products;
	}
	
	public List<String> getProducts() {
		return products;
	}
 
}

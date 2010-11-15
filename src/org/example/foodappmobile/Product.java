package org.example.foodappmobile;

public class Product {
	
	private String name;
	private String description;
	private String price;
	private String quantity;
	
	public void setName( String name) {
		this.name = name;
	}
	
	public void setDescription( String description) {
		this.description = description;
	}
	
	public void setPrice (String price) {
		this.price = price;
	}
	
	public void setQuantity (String quantity) {
		this.quantity = quantity;
	}
	
	public String getName() { return name;}
	
	public String getDescription() { return description;}
	
	public String getPrice() { return price; }
	
	public String getQuantity() { return quantity; }
}

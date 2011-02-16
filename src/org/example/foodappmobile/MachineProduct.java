package org.example.foodappmobile;

public class MachineProduct {
	
	private int id;
	private String name;
	private String description;
	private String price;
	private int qty;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPrice() {
		return price;
	}
	
	public int getQty() {
		return qty;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}

}

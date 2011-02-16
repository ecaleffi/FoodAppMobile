package org.example.foodappmobile;

import java.util.List;

public class Machine {

	private int id;
	private String name;
	private String address;
	private String city;
	private String province_state;
	private String postal_code;
	private String latitude;
	private String longitude;
	private List<Integer> products;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getProvinceState() {
		return province_state;
	}
	
	public String getPostalCode() {
		return postal_code;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public List<Integer> getProducts() {
		return products;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setProvinceState(String province_state) {
		this.province_state = province_state;
	}
	
	public void setPostalCode(String postal_code) {
		this.postal_code = postal_code;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public void setProducts(List<Integer> products) {
		this.products = products;
	}
}

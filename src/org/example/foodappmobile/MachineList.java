package org.example.foodappmobile;

import java.util.List;

public class MachineList {
	
	private List<Machine> machines;
	
	private List<MachineProduct> products;
	
	public List<Machine> getMachines() {
		return machines;
	}
	
	public void setMachines(List<Machine> machines) {
		this.machines = machines;
	}
	
	public List<MachineProduct> getMachineProducts() {
		return products;
	}
	
	public void setMachineProducts(List<MachineProduct> products) {
		this.products = products;
	}

}

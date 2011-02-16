package org.example.foodappmobile;

import android.app.Activity;
import android.os.Bundle;

public class MachineActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        FoodAppData fad = new FoodAppData(this);
        fad.creaAggiornaDB();
	}
}

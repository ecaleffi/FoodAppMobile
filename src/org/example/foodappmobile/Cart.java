package org.example.foodappmobile;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class Cart extends Activity{
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = getIntent().getExtras();
        ArrayList<Product> ordered = b.getParcelableArrayList("orderedProducts");
        
        for (int x=0; x < ordered.size(); x++) {
			System.out.println(ordered.get(x).getName() + " - " + ordered.get(x).getDescription() 
					+ " - " + ordered.get(x).getPrice() + " - " + ordered.get(x).getQuantity());
		}
    }

}

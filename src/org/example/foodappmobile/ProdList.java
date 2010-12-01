package org.example.foodappmobile;

import android.os.Bundle; 
import android.app.Activity;

public class ProdList extends Activity {
	
	final String url = "http://192.168.2.6:3000/rest/product";
	String result = "";
	ProductView pview;
	HttpMethods hm;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        hm = new HttpMethods();
        result = hm.callWebService(url);
        pview = new ProductView(this, result);
		setContentView(pview);
		pview.requestFocus();
    
    }

}

package org.example.foodappmobile;

import android.app.Activity;
import android.os.Bundle;

public class RecipeList extends Activity {
	
	final String url = "rest/recipe";
	String result = "";
	HttpMethods hm;
	RecipeView rv;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        hm = new HttpMethods();
        result = hm.callWebService(url);
        rv = new RecipeView(this, result);
		setContentView(rv);
		rv.requestFocus();
    
    }
    
}

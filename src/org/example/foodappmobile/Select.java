package org.example.foodappmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Select extends Activity implements OnClickListener {
	
	String strCookieName = "foodapp_session";
	String strCookieValue;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.select);
        
        /* Recupero i valori passati come parametro dall'attività chiamante*/
        Bundle b = getIntent().getExtras();
        strCookieValue = b.getString(strCookieName);
        
        View listButton = findViewById(R.id.prodlist_button);
        listButton.setOnClickListener(this);
        View orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(this);
    }
    
    public void onClick(View v) {
        switch (v.getId()) {
        	case R.id.prodlist_button:
        		Intent list = new Intent(this, ProdList.class);
        		startActivity(list);
        		break;
        	
        	case R.id.recipelist_button:
        		Intent recList = new Intent(this, RecipeList.class);
        		startActivity(recList);
        		
        	case R.id.order_button:
        		Intent order = new Intent(this, Order.class);
        		Bundle bundle = new Bundle();
				bundle.putString(strCookieName, strCookieValue);
				order.putExtras(bundle);
        		startActivity(order);
        		break;
        }
    }
}

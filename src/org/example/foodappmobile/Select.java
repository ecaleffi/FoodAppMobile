package org.example.foodappmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Select extends Activity implements OnClickListener {
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.select);
        
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
        	
        	case R.id.order_button:
        		break;
        }
    }
}

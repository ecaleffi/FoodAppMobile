package org.example.foodappmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class FoodAppMobile extends Activity implements OnClickListener{
	        
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);
        
        View loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
  
    } // end onCreate()
    
    public void onClick(View v) {
        switch (v.getId()) {
        	case R.id.login_button:
        		Intent login = new Intent(this, Login.class);
        		startActivity(login);
        		break;
        	
        	case R.id.about_button:
        		Intent i = new Intent(this, About.class);
        		startActivity(i);
        		break;
        }
    }
  
}
package org.example.foodappmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity{
	
	Cookie mycookie;
	HttpMethods hm;
	StructResp sr;
	final String url = "http://192.168.2.6:3000/people/login";
	int respCode = 0;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.login);
        
        final Button btnLogin = (Button)findViewById(R.id.submit_login);  
        btnLogin.setOnClickListener(new Button.OnClickListener(){  
            public void onClick(View v) {
            	
            	final EditText txtName = (EditText)findViewById(R.id.login_name);
            	final EditText txtPw = (EditText)findViewById(R.id.login_pw);
            	
            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
                nameValuePairs.add(new BasicNameValuePair("name", txtName.getText().toString()));  
                nameValuePairs.add(new BasicNameValuePair("password", txtPw.getText().toString()));
            	
            	hm = new HttpMethods();
            	sr = hm.postDataLogin(url, nameValuePairs);
            	respCode = sr.getHttpResponse().getStatusLine().getStatusCode();
            	mycookie = sr.getMycookie();
            	
            	System.out.println(sr.getMycookie().toString());
            	System.out.println(respCode);
            	
            	controlRespCode();	
            }  
        });
        
      
    }	// fine onCreate
    
    
    public void controlRespCode() {
    	
    	//Controllo il valore del codice della risposta
        if (respCode == 401) {
        	Intent loginFailed = new Intent(this, LoginFailed.class);
        	startActivity(loginFailed);
        }
        if (respCode == 200) {
        	Intent select = new Intent(this, Select.class);
        	Bundle bundle = new Bundle();
			bundle.putString(mycookie.getName(), mycookie.getValue());
			select.putExtras(bundle);
        	startActivity(select);
        	
        }
        if (respCode == 0) {
        	Intent postErr = new Intent(this, PostErr.class);
        	startActivity(postErr);
        }
    	
    }

}

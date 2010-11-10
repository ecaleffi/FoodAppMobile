package org.example.foodappmobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity{
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.login);
        
        final Button btnLogin = (Button)findViewById(R.id.submit_login);  
        btnLogin.setOnClickListener(new Button.OnClickListener(){  
            public void onClick(View v) {  
            	postData();  
            }  
        });  
    }	// fine onCreate
    
    public void postData(){
    	
    	//Crea un nuovo HttpClient e POST Header
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://192.168.2.6:3000/people/login");
    	
    	final EditText txtName = (EditText)findViewById(R.id.login_name);
    	final EditText txtPw = (EditText)findViewById(R.id.login_pw);
    	
    	try {  
            // Add your data  
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
            nameValuePairs.add(new BasicNameValuePair("name", txtName.getText().toString()));  
            nameValuePairs.add(new BasicNameValuePair("password", txtPw.getText().toString()));  
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
      
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost);
            System.out.println(response);
              
        } catch (ClientProtocolException e) {            
        } catch (IOException e) {  }
    }

}

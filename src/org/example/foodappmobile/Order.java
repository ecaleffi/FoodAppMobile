package org.example.foodappmobile;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;

public class Order extends Activity{
	
	final String url = "http://192.168.2.6:3000/rest/product";
	String result = "";
	OrderView ordview;
		
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        result = callWebService(url);
        System.out.println(result);
        ordview = new OrderView(this, result);
        setContentView(ordview);
        ordview.requestFocus();
    }
    
    public String callWebService(String url) {
    	
		HttpClient httpclient = new DefaultHttpClient();  
		HttpGet request = new HttpGet(url);  
		BasicResponseHandler handler = new BasicResponseHandler(); 
		try {  
			result = httpclient.execute(request, handler);  }
		catch (ClientProtocolException e) {	e.printStackTrace(); } 
		catch (IOException io) { io.printStackTrace();  } 
		httpclient.getConnectionManager().shutdown();
		
		return result;
    		
	}

}

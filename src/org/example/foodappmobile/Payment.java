package org.example.foodappmobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Payment extends Activity {
	
	String strCookieName = "foodapp_session";
	String strCookieValue;
	String selectedCard;
	HttpContext localContext = null;
	HttpResponse resp;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = getIntent().getExtras();
        strCookieValue = b.getString(strCookieName);
        
        setContentView(R.layout.payment);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.card_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
         
        
        final Button complete = (Button)findViewById(R.id.complete_order);  
        complete.setOnClickListener(new Button.OnClickListener(){  
            public void onClick(View v) {  
            	postData();  
            }  
        });  
        
    } //fine onCreate
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          //Toast.makeText(parent.getContext()), "The planet is " +
            //  parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        	selectedCard = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public void postData() {
    	
    	System.out.println(selectedCard);
    	//Serve per fare in modo che il metodo POST venga gestito tramite la 
		// versione di HTTP 1.1; in questo modo la risposta è molto più performante
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	
		//Crea un nuovo HttpClient e POST Header
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost("http://192.168.2.6:3000/checkout/payment");
		
		BasicClientCookie ck = new BasicClientCookie(strCookieName, strCookieValue);
		ck.setPath("/");
		ck.setDomain("192.168.2.6");
		ck.setExpiryDate(null);
		ck.setVersion(0);
		
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(ck);

		// Creo un context HTTP locale
		localContext = new BasicHttpContext();
		// Lego il cookie store al context locale
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		final EditText txtccname = (EditText)findViewById(R.id.ccname);
    	final EditText txtccn = (EditText)findViewById(R.id.ccn);
    	final EditText txtccm = (EditText)findViewById(R.id.ccm);
    	final EditText txtccy = (EditText)findViewById(R.id.ccy);
    	final EditText txtccvn = (EditText)findViewById(R.id.ccvn);
		
		try {  
			// Aggiungo i parametri da passare con la richiesta POST 
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
			nameValuePairs.add(new BasicNameValuePair("ccname", txtccname.getText().toString()));  
			nameValuePairs.add(new BasicNameValuePair("cctype", selectedCard));
			nameValuePairs.add(new BasicNameValuePair("ccn", txtccn.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("ccm", txtccm.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("ccy", txtccy.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("ccvn", txtccvn.getText().toString()));
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			HttpResponse response = httpclient.execute(httppost, localContext);
			resp = response;
		} catch (ClientProtocolException e) {            
		} catch (IOException e) {  }
    	
    }

}

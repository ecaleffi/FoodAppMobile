package org.example.foodappmobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Checkout extends Activity{
	
	String strCookieName = "foodapp_session";
    String strCookieValue;
    /* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	ArrayList<Product> ordered;
	HttpResponse resp;
	/* Istanza della classe MyOrder da passare all'attività Preview
	 * per visualizzare i dati dell'ordinazione*/
	MyOrder mo;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.checkout);
        
        Bundle b = getIntent().getExtras();
        ordered = b.getParcelableArrayList("orderedProducts");
        strCookieValue = b.getString(strCookieName);
        System.out.println(strCookieName + "=" + strCookieValue);
        
        final Button btnChkout = (Button)findViewById(R.id.insert);  
        btnChkout.setOnClickListener(new Button.OnClickListener(){  
            public void onClick(View v) {  
            	postData();  
            }  
        });
    }	// fine onCreate
    
    public void postData(){
    	
    	//Serve per fare in modo che il metodo POST venga gestito tramite la 
    	// versione di HTTP 1.1; in questo modo la risposta è molto più performante
    	HttpParams params = new BasicHttpParams();
    	params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    	
    	//Crea un nuovo HttpClient e POST Header
    	HttpClient httpclient = new DefaultHttpClient(params);
    	HttpPost httppost = new HttpPost("http://192.168.2.6:3000/checkout/billing");
    	
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
    	
    	final EditText txtBillToFirstName = (EditText)findViewById(R.id.billtoname);
    	final EditText txtBillToLastName = (EditText)findViewById(R.id.billtosurname);
    	final EditText txtBillToAddress1 = (EditText)findViewById(R.id.billtoaddress);
    	final EditText txtBillToCity = (EditText)findViewById(R.id.billtocity);
    	final EditText txtBillToState = (EditText)findViewById(R.id.billtostate);
    	final EditText txtBillToZip = (EditText)findViewById(R.id.billtozip);
    	final EditText txtBillToCountry = (EditText)findViewById(R.id.billtocountry);
    	final EditText txtBillToDayPhone = (EditText)findViewById(R.id.billtoday_phone);
    	final EditText txtBillToNightPhone = (EditText)findViewById(R.id.billtonight_phone);
    	final EditText txtBillToFax = (EditText)findViewById(R.id.billtofax);
    	final EditText txtBillToEmail = (EditText)findViewById(R.id.billtoemail);
    	final EditText txtShipToFirstName = (EditText)findViewById(R.id.shiptoname);
    	final EditText txtShipToLastName = (EditText)findViewById(R.id.shiptosurname);
    	final EditText txtShipToAddress1 = (EditText)findViewById(R.id.shiptoaddress);
    	final EditText txtShipToCity = (EditText)findViewById(R.id.shiptocity);
    	final EditText txtShipToState = (EditText)findViewById(R.id.shiptostate);
    	final EditText txtShipToZip = (EditText)findViewById(R.id.shiptozip);
    	final EditText txtShipToCountry = (EditText)findViewById(R.id.shiptocountry);
    	final EditText txtShipToDayPhone = (EditText)findViewById(R.id.shiptoday_phone);
    	final EditText txtShipToNightPhone = (EditText)findViewById(R.id.shiptonight_phone);
    	final EditText txtShipToFax = (EditText)findViewById(R.id.shiptofax);
    	final EditText txtShipToEmail = (EditText)findViewById(R.id.shiptoemail);
    	final EditText txtComments = (EditText)findViewById(R.id.comments);
    	
    	/* Istanzio l'oggetto MyOrder da passare*/
    	mo = new MyOrder();
    	mo.setBillToFirstName(txtBillToFirstName.getText().toString());
    	mo.setBillToLastName(txtBillToLastName.getText().toString());
    	mo.setBillToAddress1(txtBillToAddress1.getText().toString());
    	mo.setBillToCity(txtBillToCity.getText().toString());
    	mo.setBillToState(txtBillToState.getText().toString());
    	mo.setBillToZip(txtBillToZip.getText().toString());
    	mo.setBillToCountry(txtBillToCountry.getText().toString());
    	mo.setBillToDayPhone(txtBillToDayPhone.getText().toString());
    	mo.setBillToNightPhone(txtBillToNightPhone.getText().toString());
    	mo.setBillToFax(txtBillToFax.getText().toString());
    	mo.setBillToEmail(txtBillToEmail.getText().toString());
    	mo.setShipToFirstName(txtShipToFirstName.getText().toString());
    	mo.setShipToLastName(txtShipToLastName.getText().toString());
    	mo.setShipToAddress1(txtShipToAddress1.getText().toString());
    	mo.setShipToCity(txtShipToCity.getText().toString());
    	mo.setShipToState(txtShipToState.getText().toString());
    	mo.setShipToZip(txtShipToZip.getText().toString());
    	mo.setShipToCountry(txtShipToCountry.getText().toString());
    	mo.setShipToDayPhone(txtShipToDayPhone.getText().toString());
    	mo.setShipToNightPhone(txtShipToNightPhone.getText().toString());
    	mo.setShipToFax(txtShipToFax.getText().toString());
    	mo.setShipToEmail(txtShipToEmail.getText().toString());
    	mo.setComments(txtComments.getText().toString());
    	
    	try {  
            // Aggiungo i parametri da passare con la richiesta POST 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(23);  
            nameValuePairs.add(new BasicNameValuePair("billtofirstname", txtBillToFirstName.getText().toString()));  
            nameValuePairs.add(new BasicNameValuePair("billtolastname", txtBillToLastName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtoaddress1", txtBillToAddress1.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtocity", txtBillToCity.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtostate", txtBillToState.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtozip", txtBillToZip.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtocountry", txtBillToCountry.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtodayphone", txtBillToDayPhone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtonightphone", txtBillToNightPhone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtofax", txtBillToFax.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("billtoemail", txtBillToEmail.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptofirstname", txtShipToFirstName.getText().toString()));  
            nameValuePairs.add(new BasicNameValuePair("shiptolastname", txtShipToLastName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptoaddress1", txtShipToAddress1.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptocity", txtShipToCity.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptostate", txtShipToState.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptozip", txtShipToZip.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptocountry", txtShipToCountry.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptodayphone", txtShipToDayPhone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptonightphone", txtShipToNightPhone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptofax", txtShipToFax.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("shiptoemail", txtShipToEmail.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("comments", txtComments.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
      
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost, localContext);
            resp = response;
            // Se tutto va bene viene ritornato il codice 200
            System.out.println(response.getStatusLine().getStatusCode());
            //respCode = response.getStatusLine().getStatusCode();
              
        } catch (ClientProtocolException e) {            
        } catch (IOException e) {  }
        
        if (resp.getStatusLine().getStatusCode() == 200) {
        	Intent prev = new Intent(this, Preview.class);
        	Bundle b = new Bundle();
        	b.putParcelableArrayList("orderedProducts", ordered);
        	b.putParcelable("orderDetails", mo);
			b.putString(ck.getName(), ck.getValue());
			prev.putExtras(b);
        	startActivity(prev);
        }
        
    }

}

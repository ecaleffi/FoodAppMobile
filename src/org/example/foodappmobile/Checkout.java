package org.example.foodappmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Checkout extends Activity{
	
	String strCookieName = "foodapp_session";
    String strCookieValue;
    final String postUrl = "checkout/billing";
    /* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	ArrayList<Product> ordered;
	HttpResponse resp;
	/* Istanza della classe MyOrder da passare all'attività Preview
	 * per visualizzare i dati dell'ordinazione*/
	MyOrder mo;
	HttpMethods hm = new HttpMethods();
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.checkout);
        
        Bundle b = getIntent().getExtras();
        ordered = b.getParcelableArrayList("orderedProducts");
        strCookieValue = b.getString(strCookieName);
        System.out.println(strCookieName + "=" + strCookieValue);
        
        final CheckBox cb = (CheckBox)findViewById(R.id.check);
        cb.setOnClickListener(new CheckBox.OnClickListener(){
        	public void onClick(View v) {
        		
        		/* Recupero le informazioni di fatturazione */
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
            	
            	/* Recupero le informazioni di spedizione */
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
        		
            	/* Controllo se il checkbox è selezionato */
            	if (cb.isChecked()) {
                	/* Setto le informazioni di spedizione uguali a quelle di fatturazione */
                	txtShipToFirstName.setText(txtBillToFirstName.getText().toString());
                	txtShipToLastName.setText(txtBillToLastName.getText().toString());
                	txtShipToAddress1.setText(txtBillToAddress1.getText().toString());
                	txtShipToCity.setText(txtBillToCity.getText().toString());
                	txtShipToState.setText(txtBillToState.getText().toString());
                	txtShipToZip.setText(txtBillToZip.getText().toString());
                	txtShipToCountry.setText(txtBillToCountry.getText().toString());
                	txtShipToDayPhone.setText(txtBillToDayPhone.getText().toString());
                	txtShipToNightPhone.setText(txtBillToNightPhone.getText().toString());
                	txtShipToFax.setText(txtBillToFax.getText().toString());
                	txtShipToEmail.setText(txtBillToEmail.getText().toString());
        		}
        		else {
        			txtShipToFirstName.setText("");
                	txtShipToLastName.setText("");
                	txtShipToAddress1.setText("");
                	txtShipToCity.setText("");
                	txtShipToState.setText("");
                	txtShipToZip.setText("");
                	txtShipToCountry.setText("");
                	txtShipToDayPhone.setText("");
                	txtShipToNightPhone.setText("");
                	txtShipToFax.setText("");
                	txtShipToEmail.setText("");
        		}
        	}
        }); 
        	
        
        final Button btnChkout = (Button)findViewById(R.id.insert);  
        btnChkout.setOnClickListener(new Button.OnClickListener(){  
            public void onClick(View v) {  
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
            	
            	if ( checkValidation(txtBillToFirstName, txtBillToLastName, txtBillToAddress1, txtBillToCity, txtBillToState, 
            			txtBillToZip, txtBillToCountry, txtBillToDayPhone, txtBillToNightPhone, txtBillToFax, txtBillToEmail,
            			txtShipToFirstName, txtShipToLastName, txtShipToAddress1, txtShipToCity, txtShipToState, txtShipToZip, 
            			txtShipToCountry, txtShipToDayPhone, txtShipToNightPhone, txtShipToFax, txtShipToEmail, txtComments))
            	{
            		/* Se arrivo qui significa che la validazione è passata */
            	
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
            	
            		resp = hm.postData(postUrl, nameValuePairs, strCookieName, strCookieValue);
        			
            		controlRespCode();
        	
            	} //fine if
            	  
            }  
        });
    }	// fine onCreate
    
    /* Validazione dei campi inseriti dall'utente: controllo che i campi necessari per creare un ordine
	 * siano tutti presenti*/
    public boolean checkValidation(EditText txtBillToFirstName, EditText txtBillToLastName, EditText txtBillToAddress1, 
    		EditText txtBillToCity, EditText txtBillToState, EditText txtBillToZip, EditText txtBillToCountry, 
    		EditText txtBillToDayPhone, EditText txtBillToNightPhone, EditText txtBillToFax, EditText txtBillToEmail, 
    		EditText txtShipToFirstName, EditText txtShipToLastName, EditText txtShipToAddress1, EditText txtShipToCity, 
    		EditText txtShipToState, EditText txtShipToZip, EditText txtShipToCountry, EditText txtShipToDayPhone, 
    		EditText txtShipToNightPhone, EditText txtShipToFax, EditText txtShipToEmail, EditText txtComments) {
    	if (txtBillToFirstName.getText().toString().equals("") || txtBillToLastName.getText().toString().equals("") ||
    			txtBillToAddress1.getText().toString().equals("") || txtBillToCity.getText().toString().equals("") ||
    			txtBillToState.getText().toString().equals("") || txtBillToZip.getText().toString().equals("") ||
    			txtBillToCountry.getText().toString().equals("") || txtBillToEmail.getText().toString().equals("") ||
    			txtShipToFirstName.getText().toString().equals("") || txtShipToLastName.getText().toString().equals("") ||
    			txtShipToAddress1.getText().toString().equals("") || txtShipToCity.getText().toString().equals("") ||
    			txtShipToState.getText().toString().equals("") || txtShipToZip.getText().toString().equals("") ||
    			txtShipToCountry.getText().toString().equals("") || txtShipToEmail.getText().toString().equals("")) {
    		AlertDialog.Builder notBlank = new AlertDialog.Builder(this);
    		notBlank.setMessage("I campi Nome, Cognome, Indirizzo, Città, Regione, CAP, Stato ed Email sia della" +
    				" fatturazione che della spedizione non possono essere lasciati vuoti.")
    			.setCancelable(false)
    			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			});
    		notBlank.show();
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    public void controlRespCode(){
    	if (resp.getStatusLine().getStatusCode() == 200) {
    		Intent prev = new Intent(this, Preview.class);
    		Bundle b = new Bundle();
    		b.putParcelableArrayList("orderedProducts", ordered);
    		b.putParcelable("orderDetails", mo);
    		b.putString(strCookieName, strCookieValue);
    		prev.putExtras(b);
    		startActivity(prev);
    	}  
    }

}

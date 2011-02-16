package org.example.foodappmobile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	final String urlPost = "checkout/payment";
	HttpMethods hm = new HttpMethods();
	
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
    			
		final EditText txtccname = (EditText)findViewById(R.id.ccname);
    	final EditText txtccn = (EditText)findViewById(R.id.ccn);
    	final EditText txtccm = (EditText)findViewById(R.id.ccm);
    	final EditText txtccy = (EditText)findViewById(R.id.ccy);
    	final EditText txtccvn = (EditText)findViewById(R.id.ccvn);
    	
    	/* Eseguo la validazione dei campi inseriti dall'utente tramite regex*/
    	/* Creo un array di stringhe con i possibili errori*/
    	String[] err = new String[10];
    	err[0] = "Il campo Nome sulla carta di credito non pu˜ essere lasciato vuoto";
    	err[1] = "Il numero della carta di credito deve contenere solo cifre";
    	err[2] = "Il numero della carta di credito deve contenere da 12 a 16 cifre";
    	err[3] = "Il mese di scadenza della carta deve contenere solo cifre";
    	err[4] = "Il mese di scadenza della carta deve contenere 1 o 2 cifre";
    	err[5] = "Il mese di scadenza della carta deve essere un numero da 1 a 12";
    	err[6] = "L'anno di scadenza della carta deve contenere solo cifre";
    	err[7] = "L'anno di scadenza della carta deve contenere 2 cifre";
    	err[8] = "Il codice di sicurezza della carta deve contenere solo cifre";
    	err[9] = "Il codice di sicurezza della carta deve contenere 3 o 4 cifre";
    	/* Serve per verificare quali errori si sono verificati*/
    	boolean[] errId = new boolean[10];
    	/* Inizializzo l'array*/
    	for (int j=0; j<10; j++) {
    		errId[j] = false;
    	}
    	
    	Pattern patNumber = Pattern.compile("\\d{12,16}");
    	/* Fa match con una stringa che contiene un qualsiasi carattere che non sia un numero*/
    	Pattern onlyNumber = Pattern.compile("\\D");
    	Pattern patMonth = Pattern.compile("\\d{1,2}");
    	Pattern patYear = Pattern.compile("\\d{2}");
    	Pattern patVnumb = Pattern.compile("\\d{3,4}");
    	Matcher matNumber = patNumber.matcher(txtccn.getText().toString());
    	Matcher matMonth = patMonth.matcher(txtccm.getText().toString());
    	int month = 0;
    	if ( !txtccm.getText().toString().equals("")) {
    		month = Integer.parseInt(txtccm.getText().toString());
    	}
    	Matcher matYear = patYear.matcher(txtccy.getText().toString());
    	Matcher matVnumb = patVnumb.matcher(txtccvn.getText().toString());
    	Matcher onlyNumberccn = onlyNumber.matcher(txtccn.getText().toString());
    	Matcher onlyNumberccm = onlyNumber.matcher(txtccm.getText().toString());
    	Matcher onlyNumberccy = onlyNumber.matcher(txtccy.getText().toString());
    	Matcher onlyNumberccvn = onlyNumber.matcher(txtccvn.getText().toString());
    	
    	if (txtccname.getText().toString().equals("")) {
    		errId[0] = true;
    	}
    	if (onlyNumberccn.find()) {
    		errId[1] = true;
    	}
    	if (!matNumber.find()) {
    		errId[2] = true;
    	}
    	if (onlyNumberccm.find()) {
    		errId[3] = true;
    	}
    	if (!matMonth.find()) {
    		errId[4] = true;
    	}
    	if ((!(month <= 12)) || (!(month >= 1))) {
    		errId[5] = true;
    	}
    	if (onlyNumberccy.find()) {
    		errId[6] = true;
    	}
    	if(!matYear.find()) {
    		errId[7] = true;
    	}
    	if(onlyNumberccvn.find()) {
    		errId[8] = true;
    	}
    	if(!(matVnumb.find())) {
    		errId[9] = true;
    	}
    	
    	boolean trovato_err = false;
    	for (int j=0; j<10; j++) {
    		if (errId[j] == true)
    			trovato_err = true;
    	}
    	if ( trovato_err ) {
    		String errMsg = "Si sono verificati i seguenti errori: ";
    		for (int j=0; j<10; j++) {
    			if (errId[j] == true) {
    				errMsg = errMsg + "\n" + err[j];
    			}
    		}
    		AlertDialog.Builder errVal = new AlertDialog.Builder(this);
    		errVal.setMessage(errMsg)
    			.setCancelable(false)
    			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					dialog.cancel();
    				}
    			});
    		errVal.show();
    	}
    	
    	else { //Se la validazione passa, esegui la richiesta POST
		
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
    		nameValuePairs.add(new BasicNameValuePair("ccname", txtccname.getText().toString()));  
    		nameValuePairs.add(new BasicNameValuePair("cctype", selectedCard));
    		nameValuePairs.add(new BasicNameValuePair("ccn", txtccn.getText().toString()));
    		nameValuePairs.add(new BasicNameValuePair("ccm", txtccm.getText().toString()));
    		nameValuePairs.add(new BasicNameValuePair("ccy", txtccy.getText().toString()));
    		nameValuePairs.add(new BasicNameValuePair("ccvn", txtccvn.getText().toString()));
    		
			resp = hm.postData(urlPost, nameValuePairs, strCookieName, strCookieValue);
		
    		if (resp != null) {
    			Intent ordComp = new Intent(this, OrderComplete.class);
    			startActivity(ordComp);
    		}
    	}
    	
    }

}

package org.example.foodappmobile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Cart extends Activity implements OnClickListener{
	
	/* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	String strCookieName = "foodapp_session";
    String strCookieValue;
    HttpResponse resp;
    ArrayList<Product> ordered;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = getIntent().getExtras();
        ordered = b.getParcelableArrayList("orderedProducts");
        strCookieValue = b.getString(strCookieName);
        
        System.out.println(strCookieName + "=" + strCookieValue);
        
        /*for (int x=0; x < ordered.size(); x++) {
			System.out.println(ordered.get(x).getName() + " - " + ordered.get(x).getDescription() 
					+ " - " + ordered.get(x).getPrice() + " - " + ordered.get(x).getQuantity());
		} */
        
        /* ScrollView per fare in modo che la pagina si possa scorrere ed 
         * eventuali prodotti del carrello non vengano tagliati*/
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundResource(R.color.background);
        /* Definisco un LinearLayout per visualizzare i prodotti nella pagina*/
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        /* Numero di prodotti ordinati*/
        int numItems = ordered.size();
        /* Array di TextView per visualizzare i dati*/
        TextView[] sku = new TextView[numItems];
        TextView[] desc = new TextView[numItems];
        TextView[] price = new TextView[numItems];
        TextView[] qty = new TextView[numItems];
        TextView[] tot = new TextView[numItems];
        TextView subTot;
        /* Vista piccola usata come separatore*/
        View[] sep = new View[numItems];
        /* Bottone per effettuare il checkout*/
        Button chk;
        /* Stringhe e variabili per gestire totale e subtotale*/
        String strTotale;
        double totale;
        String strSubTotale;
        double subTotale = 0.0;
        
        for (int i=0; i < numItems; i++) {
        	sku[i] = new TextView(this);
        	sku[i].setText("SKU: " + ordered.get(i).getName() );
        	ll.addView(sku[i]);
        	desc[i] = new TextView(this);
        	desc[i].setText("Descrizione: " + ordered.get(i).getDescription() );
        	ll.addView(desc[i]);
        	price[i] = new TextView(this);
        	price[i].setText("Prezzo: " + ordered.get(i).getPrice() );
        	ll.addView(price[i]);
        	qty[i] = new TextView(this);
        	qty[i].setText("Quantità: " + ordered.get(i).getQuantity() );
        	ll.addView(qty[i]);
        	tot[i] = new TextView(this);
        	tot[i].setText("\nTotale: " + ordered.get(i).getTot() );
        	ll.addView(tot[i]);
        	
        	/* Codice per trasformare il totale nel giusto formato*/
        	strTotale = ordered.get(i).getTot();
        	strTotale = strTotale.replace(" EUR", "");
    		strTotale = strTotale.replace(",", ".");
    		totale = Double.parseDouble(strTotale);
    		subTotale = subTotale + totale;
        	
        	sep[i] = new View(this);
			sep[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			sep[i].setBackgroundResource(R.color.separator);
			sep[i].setMinimumHeight(3);
			ll.addView(sep[i]);
        }
        DecimalFormat twoDec = new DecimalFormat("0.00");
        strSubTotale = twoDec.format(subTotale);
        strSubTotale = "" + strSubTotale;
        strSubTotale = strSubTotale.replace(".", ",");
		strSubTotale = strSubTotale + " EUR";
        LinearLayout st = new LinearLayout(this);
        st.setOrientation(LinearLayout.HORIZONTAL);
        st.setGravity(Gravity.RIGHT);
        subTot = new TextView(this);
        subTot.setTextSize(18);
        subTot.setText("Subtotale: " + strSubTotale +"\n");
        st.addView(subTot);
        ll.addView(st);
        LinearLayout bt = new LinearLayout(this);
        bt.setOrientation(LinearLayout.HORIZONTAL);
        bt.setGravity(Gravity.CENTER);
        chk = new Button(this);
        chk.setText("Checkout");
        chk.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
        chk.setOnClickListener(this);
        bt.addView(chk);
        ll.addView(bt);
        
        sv.addView(ll);
        setContentView(sv);
    } //fine onCreate
    
    public void onClick (View v) {
    	
    	//Serve per fare in modo che il metodo POST venga gestito tramite la 
		// versione di HTTP 1.1; in questo modo la risposta è molto più performante
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	
		//Crea un nuovo HttpClient e POST Header
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost("http://192.168.2.6:3000/checkout/");
		
		BasicClientCookie ck = new BasicClientCookie(strCookieName, strCookieValue);
		ck.setPath("/");
		ck.setDomain("192.168.2.6");
		ck.setExpiryDate(null);
		ck.setVersion(0);
		
		System.out.println(ck.toString());
		
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(ck);

		// Creo un context HTTP locale
		localContext = new BasicHttpContext();
		// Lego il cookie store al context locale
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		try {
			HttpResponse response = httpclient.execute(httppost, localContext);
			resp = response;
		}	catch (ClientProtocolException e) { } 	
			catch (IOException e) { }
		
		if (resp != null)  {
			/* Creo l'intent e preparo i parametri da passare */
			Intent check = new Intent(this, Checkout.class);
			Bundle b = new Bundle();
			b.putParcelableArrayList("orderedProducts", ordered);
			b.putString(ck.getName(), ck.getValue());
			check.putExtras(b);
		
			/* Avvio l'Activity*/				
			startActivity(check);
		}
    }

}

package org.example.foodappmobile;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Preview extends Activity implements OnClickListener{

	final String url = "checkout/payment/";
	HttpMethods hm = new HttpMethods();
	/* Parametri della sessione da inserire nel cookie*/
	String strCookieName = "foodapp_session";
    String strCookieValue;
    /* Lista dei prodotti ordinati dal cliente*/
    ArrayList<Product> ordered;
    /* Parametri di fatturazione e spedizione inseriti dall'utente*/
    MyOrder mo;
    HttpContext localContext;
    HttpResponse resp;
    
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        /* Recupero i valori passati come parametro dall'attività chiamante*/
        Bundle b = getIntent().getExtras();
        ordered = b.getParcelableArrayList("orderedProducts");
        mo = b.getParcelable("orderDetails");
        strCookieValue = b.getString(strCookieName);
        System.out.println(strCookieName + "=" + strCookieValue);
        
        /* Vista ScrollView per fare in modo che la pagina continui oltre alla
         * sua fine*/
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundResource(R.color.background);
        /* Definisco un LinearLayout per visualizzare i prodotti nella pagina*/
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(this);
        title.setText("Anteprima dell'Ordine");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize((float) 24.5);
        /* Setto i parametri per avere un marginBottom*/
        MarginLayoutParams mlp = new MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT );
        mlp.setMargins(0,0,0,150);
        title.setLayoutParams(mlp);
        ll.addView(title);
        
        /* Definisco un TableLayout per visualizzare su due colonne i dettagli
         * relativi a Fatturazione e Spedizione inseriti dall'utente*/
        TableLayout tl = new TableLayout(this);
        /* Setto le colonne Shrinkable in modo che se il testo è troppo lungo
         * non vada ad occupare troppo spazio dell'altra colonna ma venga
         * scritto a capo*/
        tl.setColumnShrinkable(0, true);
        tl.setColumnShrinkable(1, true);
        
        /* Array di TableRow per visualizzare le righe della tabella*/
        TableRow[] tr = new TableRow[12];
        
        /* La prima riga conterrà i titoli per fatturazione e spedizione*/
        tr[0] = new TableRow(this);
        TextView fat = new TextView(this);
        fat.setText("Fatturazione");
        fat.setTextSize((float) 18.5);
        tr[0].addView(fat);
        TextView sped = new TextView(this);
        sped.setText("Spedizione");
        sped.setTextSize((float) 18.5);
        tr[0].addView(sped);
        tl.addView(tr[0]);
        
        /* Vista di separazione*/
        View sep1 = new View(this);
        sep1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
        sep1.setBackgroundResource(R.color.separator);
		sep1.setMinimumHeight(3);
		sep1.setLayoutParams(mlp);
		tl.addView(sep1);
        
		/* Array di TextView per mostrare sul display le stringhe con i dati
		 * inseriti dall'utente nella fase di checkout*/
        TextView[] ordDet = new TextView[23];
        
        /* Definisco una riga per ciascun campo dell'ordine*/
        tr[1] = new TableRow(this);
        ordDet[0] = new TextView(this);
        ordDet[0].setText("Nome: "+ mo.getBillToFirstName());
        tr[1].addView(ordDet[0]);
        ordDet[1] = new TextView(this);
        ordDet[1].setText("Nome: "+ mo.getShipToFirstName());
        tr[1].addView(ordDet[1]);
        tr[1].setPadding(0, 15, 0, 0);
        tl.addView(tr[1]);
        
        tr[2] = new TableRow(this);
        ordDet[2] = new TextView(this);
        ordDet[2].setText("Cognome: "+ mo.getBillToLastName());
        tr[2].addView(ordDet[2]);
        ordDet[3] = new TextView(this);
        ordDet[3].setText("Cognome: "+ mo.getShipToLastName());
        tr[2].addView(ordDet[3]);
        tl.addView(tr[2]);
        
        tr[3] = new TableRow(this);
        ordDet[4] = new TextView(this);
        ordDet[4].setText("Indirizzo: "+ mo.getBillToAddress1());
        tr[3].addView(ordDet[4]);
        ordDet[5] = new TextView(this);
        ordDet[5].setText("Indirizzo: "+ mo.getShipToAddress1());
        tr[3].addView(ordDet[5]);
        tl.addView(tr[3]);
        
        tr[4] = new TableRow(this);
        ordDet[6] = new TextView(this);
        ordDet[6].setText("Città: "+ mo.getBillToCity());
        tr[4].addView(ordDet[6]);
        ordDet[7] = new TextView(this);
        ordDet[7].setText("Città: "+ mo.getShipToCity());
        tr[4].addView(ordDet[7]);
        tl.addView(tr[4]);
        
        tr[5] = new TableRow(this);
        ordDet[8] = new TextView(this);
        ordDet[8].setText("Stato: "+ mo.getBillToState());
        tr[5].addView(ordDet[8]);
        ordDet[9] = new TextView(this);
        ordDet[9].setText("Stato: "+ mo.getShipToState());
        tr[5].addView(ordDet[9]);
        tl.addView(tr[5]);
        
        tr[6] = new TableRow(this);
        ordDet[10] = new TextView(this);
        ordDet[10].setText("CAP: "+ mo.getBillToZip());
        tr[6].addView(ordDet[10]);
        ordDet[11] = new TextView(this);
        ordDet[11].setText("CAP: "+ mo.getShipToZip());
        tr[6].addView(ordDet[11]);
        tl.addView(tr[6]);
        
        tr[7] = new TableRow(this);
        ordDet[12] = new TextView(this);
        ordDet[12].setText("Country: "+ mo.getBillToCountry());
        tr[7].addView(ordDet[12]);
        ordDet[13] = new TextView(this);
        ordDet[13].setText("Country: "+ mo.getShipToCountry());
        tr[7].addView(ordDet[13]);
        tl.addView(tr[7]);
        
        tr[8] = new TableRow(this);
        ordDet[14] = new TextView(this);
        ordDet[14].setText("Telefono Diurno: "+ mo.getBillToDayPhone());
        tr[8].addView(ordDet[14]);
        ordDet[15] = new TextView(this);
        ordDet[15].setText("Telefono Diurno: "+ mo.getShipToDayPhone());
        tr[8].addView(ordDet[15]);
        tl.addView(tr[8]);
        
        tr[9] = new TableRow(this);
        ordDet[16] = new TextView(this);
        ordDet[16].setText("Telefono Notturno: "+ mo.getBillToNightPhone());
        tr[9].addView(ordDet[16]);
        ordDet[17] = new TextView(this);
        ordDet[17].setText("Telefono Notturno: "+ mo.getShipToNightPhone());
        tr[9].addView(ordDet[17]);
        tl.addView(tr[9]);
        
        tr[10] = new TableRow(this);
        ordDet[18] = new TextView(this);
        ordDet[18].setText("Fax: "+ mo.getBillToFax());
        tr[10].addView(ordDet[18]);
        ordDet[19] = new TextView(this);
        ordDet[19].setText("Fax: "+ mo.getShipToFax());
        tr[10].addView(ordDet[19]);
        tl.addView(tr[10]);
        
        tr[11] = new TableRow(this);
        ordDet[20] = new TextView(this);
        ordDet[20].setText("Email: "+ mo.getBillToEmail());
        tr[11].addView(ordDet[20]);
        ordDet[21] = new TextView(this);
        ordDet[21].setText("Email: "+ mo.getShipToEmail());
        tr[11].addView(ordDet[21]);
        tl.addView(tr[11]);
        TextView com = new TextView(this);
        com.setText("Commenti: "+ mo.getComments());
        tl.addView(com);
        /* Aggiungo il TableLayout al LinearLayout padre*/
        ll.addView(tl);
        
        /* Visualizzo i commenti sotto alla tabella*/
        /*TextView com = new TextView(this);
        com.setText("Commenti: "+ mo.getComments());
        com.setLayoutParams(mlp);
        ll.addView(com); */
        
        TextView cart = new TextView(this);
        cart.setText("\nCarrello");
        cart.setTextSize((float) 18.5);
        cart.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.addView(cart);
        
        View sep2 = new View(this);
        sep2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
        sep2.setBackgroundResource(R.color.separator);
		sep2.setMinimumHeight(3);
		sep2.setLayoutParams(mlp);
		tl.addView(sep2);
        
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
        st.setOrientation(LinearLayout.VERTICAL);
        st.setGravity(Gravity.RIGHT);
        subTot = new TextView(this);
        subTot.setText("Subtotale: " + strSubTotale);
        subTot.setGravity(Gravity.RIGHT);
        st.addView(subTot);
        
        /* Variabili per imposte, gestione, costi di spedizione e somma di tutto*/
        double tax, handling, shipping, sum;
        tax = 0.2 * subTotale;
        handling = 0.0;
        shipping = 5.0;
        sum = subTotale + tax + handling + shipping;
        /* Stringhe per la visualizzazione delle quantità sopra indicate*/
        String strTax, strHand, strShip, strSum;
        strTax = twoDec.format(tax);
        strTax = "" + strTax;
        strTax.replace(".", ",");
        strTax = strTax + " EUR";
        strHand = twoDec.format(handling);
        strHand = "" + strHand;
        strHand.replace(".", ",");
        strHand = strHand + " EUR";
        strShip = twoDec.format(shipping);
        strShip = "" + strShip;
        strShip.replace(".", ",");
        strShip = strShip + " EUR";
        strSum = twoDec.format(sum);
        strSum = "" + strSum;
        strSum.replace(".", ",");
        strSum = strSum + " EUR";
        TextView tTax = new TextView(this);
        tTax.setText("Imposte: "+ strTax);
        tTax.setGravity(Gravity.RIGHT);
        st.addView(tTax);
        TextView tHand = new TextView(this);
        tHand.setText("Gestione: "+ strHand);
        tHand.setGravity(Gravity.RIGHT);
        st.addView(tHand);
        TextView tShip = new TextView(this);
        tShip.setText("Spedizione: "+ strShip);
        tShip.setGravity(Gravity.RIGHT);
        st.addView(tShip);
        TextView tSum = new TextView(this);
        tSum.setText("Totale: "+ strSum +"\n");
        tSum.setTextSize(18);
        tSum.setGravity(Gravity.RIGHT);
        st.addView(tSum);
        /* Aggiungo il Layout con i costi al LinearLayout principale ll*/
        ll.addView(st);
        
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        Button cont = new Button(this);
        cont.setText("Continua");
        cont.setGravity(Gravity.CENTER_HORIZONTAL);
        cont.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
        cont.setOnClickListener(this);
        ll.addView(cont);
        
        /* Aggiungo il LinearLayout alla ScrollView e la visualizzo*/
        sv.addView(ll);
        setContentView(sv);
        
    }	// fine onCreate
    
    public void onClick(View v) {
    	
    	resp = hm.postDataNoPairs(url, strCookieName, strCookieValue);
    	
		if (resp != null) {
			Intent pay = new Intent(this, Payment.class);
			Bundle b = new Bundle();
			b.putString(strCookieName, strCookieValue);
			pay.putExtras(b);
			startActivity(pay);
			finish();
		}
    }
}

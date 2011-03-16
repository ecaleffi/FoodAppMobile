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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import static org.example.foodappmobile.Constants.*;

public class Order extends Activity implements OnClickListener{
	
	final String postUrl = "cart/add";
	
	/* Array di stringhe per i nomi dei prodotti*/
	String[] name;
	/* Array di stringhe per le descrizioni dei prodotti*/
	String[] desc;
	/* Array di stringhe per i prezzi dei prodotti*/
	String[] price;
	/* Array di EditText per recuperare le quantità inserite*/
	EditText[] qty;
	/* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	/* Lista che serve per salvare i prodotti ordinati*/
	ArrayList<Product> ordered = new ArrayList<Product>();
	/* Oggetto Product che va inserito nella lista*/
	Product tmpProd;
	String strCookieName = "foodapp_session";
	String strCookieValue;
	HttpResponse resp;
	/* Istanza della classe per chiamare dei metodi Http */
	HttpMethods hm = new HttpMethods();
	/* Variabile per accedere al database*/
	private FoodAppData fad;
	
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        Bundle b = getIntent().getExtras();
        strCookieValue = b.getString(strCookieName);
        
        /* Definisco un LinearLayout per visualizzare gli elementi nella pagina*/
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.background);
        
        /* LinearLayout per visualizzare orizzontalmente una label per la quantità 
         * e l'EditText per inserire la quantità richiesta*/
        LinearLayout edit;
        /* LinearLayout per visualizzare il bottone per il carrello*/
        LinearLayout layCart;
                
        /* Definisco una vista ScrollView per 'attaccargli' il LinearLayout in modo
         *  che tutti i prodotti vengano visualizzati*/
        ScrollView sv = new ScrollView(this); 
                
        ProductList prodList = null; //Variabile per salvare la lista di prodotti
		int numProd = 0;	//Variabile per contare il numero di prodotti
		
		/* Utilizzo la classe FoodAppData per creare o aggiornare il database */
		fad = new FoodAppData(this);
		fad.creaAggiornaDB();
		prodList = fad.getProducts();
		
		for (Product p : prodList.getProds()) {
			System.out.println(p.getName());
		}
		
		/* Numero di prodotti presenti nella lista*/
		numProd = prodList.getProds().size();
		
		/* Array di stringhe per i nomi dei prodotti*/
		name = new String[numProd];
		/* Array di stringhe per le descrizioni dei prodotti*/
		desc = new String[numProd];
		/* Array di stringhe per i prezzi dei prodotti*/
		price = new String[numProd];
		/* Array di TextView per visualizzare i nomi dei prodotti*/
		TextView[] prodName = new TextView[numProd];
		/* Array di TextView per visualizzare le descrizioni dei prodotti*/
		TextView[] prodDesc = new TextView[numProd];
		/* Array di TextView per visualizzare i prezzi dei prodotti*/
		TextView[] prodPrice = new TextView[numProd];
		/* Array di TextView per visualizzare la quantità del prodotto*/
		TextView[] prodQty = new TextView[numProd];
		/* Array di EditText per inserire la quantità del prodotto*/
		EditText[] editQty = new EditText[numProd];
		/* Inizializzo l'array esterno che mi servirà per recuperare i valori
		 * di quantità inseriti*/
		qty = new EditText[numProd];
		/* Definisco una vista piccola per separare i vari prodotti*/
		View[] sep = new View[numProd];
		/* Array di Button per creare bottoni che servono per aggiungere il relativo prodotto
		 * nel carrello */
		Button[] btn = new Button[numProd];
		/* Button per visualizzare il carrello */
		Button cart;
		
		/* Ciclo per inizializzare gli array di Stringhe */
		int i=0;
		for (Product prod : prodList.getProds()) {
			name[i] = prod.getName();
			desc[i] = prod.getDescription();
			price[i] = prod.getPrice();
			i++;
		}
		
		/* Ciclo per settare i TextView da visualizzare*/
		for (i=0; i < numProd; i++) {
			prodName[i] = new TextView(this);
			prodName[i].setText(name[i]);
			ll.addView(prodName[i]);
			prodDesc[i] = new TextView(this);
			prodDesc[i].setText(desc[i]);
			ll.addView(prodDesc[i]);
			prodPrice[i] = new TextView(this);
			prodPrice[i].setText(price[i]);
			ll.addView(prodPrice[i]);
			/* Aggiungo un nuovo LinearLayout per visualizzare orizzontalmente 
			 * la label della quantità e il campo per modificarla*/
			edit = new LinearLayout(this);
	        edit.setOrientation(LinearLayout.HORIZONTAL);
			prodQty[i] = new TextView(this);
			prodQty[i].setText("Quantità: ");
			edit.addView(prodQty[i]);
			editQty[i] = new EditText(this);
			editQty[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			editQty[i].setText("1");
			edit.addView(editQty[i]);
			ll.addView(edit);
			
			btn[i] = new Button(this);
			btn[i].setText("Aggiungi al carrello");
			btn[i].setId(i);
			btn[i].setOnClickListener(this);
			btn[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
			ll.addView(btn[i]);	
			sep[i] = new View(this);
			sep[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			sep[i].setBackgroundResource(R.color.separator);
			sep[i].setMinimumHeight(3);
			ll.addView(sep[i]);
		} 
		layCart = new LinearLayout(this);
		layCart.setGravity(Gravity.CENTER);
		layCart.setPadding(5, 5, 5, 5);
		cart = new Button(this);
		cart.setText("Visualizza il carrello");
		cart.setId(999);
		cart.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		cart.setOnClickListener(this);
		layCart.addView(cart);
		ll.addView(layCart);
		
		/* Setto qty in modo che possa recuperare i valori all'esterno del metodo*/
		qty = editQty;
		
		/* Aggiungo il LinearLayout alla ScrollView e poi la visualizzo */
		sv.addView(ll);
		setContentView(sv);
		
    } //fine onCreate
    
    
    public void onClick (View v) {
    	/*Recupero l'identificativo del bottone cliccato*/
		int id = v.getId();
		
		if (id == 999) { 	// Id associato al pulsante per visualizzare il carrello
			if (ordered.size() == 0) { 	// Controllo che sia stato selezionato almeno un prodotto
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setMessage("Non hai selezionato alcun prodotto! Il carrello è vuoto.")
        			.setCancelable(false)
        			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        		builder.show();
			}
			else {
				/* Creo un nuovo Intent per avviare l'Activity associata al carrello*/
				Intent cart = new Intent(this, Cart.class);
				
				/* Preparo i parametri da passare all'Activity*/
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("orderedProducts", ordered);
				bundle.putString(strCookieName, strCookieValue);
				cart.putExtras(bundle);
				
				/* Avvio l'Activity*/				
				startActivity(cart);
				finish();
			}
		}
		
		else {
			/* Controllo che la quantità richiesta del prodotto sia disponibile */
			int qty_available = 0;
			fad = new FoodAppData(this);
			SQLiteDatabase db = fad.getReadableDatabase();
			String[] COL = {STOCK_QTY};
			String where = "name = " + "\"" + name[id] + "\"";
			Cursor c = db.query(TABLE_PRODUCTS, COL, where, null, null, null, null);
			startManagingCursor(c);
			while (c.moveToNext()) {
				qty_available = c.getInt(0);
			}
			
			/* Controllo che la quantità inserita sia in un formato accettabile*/
			int q = Integer.parseInt(qty[id].getText().toString());
			if (! (( q > 0) && (q < 1000)) ) {
				AlertDialog.Builder quant = new AlertDialog.Builder(this);
        		quant.setMessage("La quantità deve essere un numero intero maggiore di 0 e minore di 1000")
        			.setCancelable(false)
        			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        		quant.show();	
			}
			else if (q > qty_available) {
				AlertDialog.Builder qty_error = new AlertDialog.Builder(this);
        		qty_error.setMessage("La quantità di prodotto richiesta non è disponibile. La quantità massima" +
        				" che è possibile ordinare è: " + qty_available)
        			.setCancelable(false)
        			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        		qty_error.show();
			}
			else {
    	
				/* Istanzio il prodotto selezionato per inserirlo nella lista*/
				tmpProd = new Product();
				tmpProd.setName(name[id]);
				tmpProd.setDescription(desc[id]);
				tmpProd.setPrice(price[id]);
				tmpProd.setQuantity(qty[id].getText().toString());
				/* Inserisco il prodotto nella lista*/
				ordered.add(tmpProd);
    	  
				// Aggiungo i parametri da passare con la richiesta POST 
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);  
				nameValuePairs.add(new BasicNameValuePair("sku", name[id]));  
				nameValuePairs.add(new BasicNameValuePair("description", desc[id]));
				nameValuePairs.add(new BasicNameValuePair("price", price[id]));
				nameValuePairs.add(new BasicNameValuePair("quantity", qty[id].getText().toString()));
				
				resp = hm.postData(postUrl, nameValuePairs, strCookieName, strCookieValue);
            
				/* Setto un AlertDialog per visualizzare un messaggio di corretto
				 * inserimento del prodotto nel carrello*/
				if (resp != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Il prodotto è stato inserito correttamente nel carrello")
            			.setCancelable(false)
            			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            				public void onClick(DialogInterface dialog, int id) {
            					dialog.cancel();
            				}
            			});
					builder.show();
				}
            
				/*Stampa di prova dei prodotti ordinati*/
				if (ordered != null) {
					for (int x=0; x < ordered.size(); x++) {
						System.out.println(ordered.get(x).getName() + " - " + ordered.get(x).getDescription() 
								+ " - " + ordered.get(x).getPrice() + " - " + ordered.get(x).getQuantity());
					}
				}
              
			}
		}
           
	} // Fine onClick
    
}

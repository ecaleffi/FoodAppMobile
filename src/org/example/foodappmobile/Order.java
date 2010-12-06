package org.example.foodappmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
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
	
	final String url = "http://192.168.2.6:3000/rest/product";
	final String recUrl = "http://192.168.2.6:3000/rest/recipe";
	final String postUrl = "http://192.168.2.6:3000/cart/add";
	String result = "";
	String resRec = "";
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
	//private FoodAppData products;
	//private FoodAppData recipes;
	//private FoodAppData uses;
	ProductList pl;
	RecList rl;
	Product tprod;
	Recipe trec;
	ArrayList<String> recProducts;
	HashMap<String, List<String>> hmap;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        Bundle b = getIntent().getExtras();
        strCookieValue = b.getString(strCookieName);
        
        result = hm.callWebService(url);
        resRec = hm.callWebService(recUrl);
        System.out.println(result);
        System.out.println(resRec);
        
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
		
		String jsonData = result;
		String jsonRec = resRec;
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		JSONObject j;
		JSONObject j2;
		
		try {
			j = new JSONObject(jsonData);
			j2 = new JSONObject(jsonRec);
			ProductList temp = gson.fromJson(j.toString(), ProductList.class);
			RecList tempRec = gson.fromJson(j2.toString(), RecList.class);
			prodList = temp;
			pl = temp;
			rl = tempRec;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (pl == null || rl == null) {
			System.out.println("Errore nella deserializzazione JSON");
		}
		
		/* Inserisco i prodotti nel database */
		//products = new FoodAppData(this);
		fad = new FoodAppData(this);
		/* Controllo che il database non esista già */
		if (!fad.isDatabaseExist()) {
			for (Product p : pl.getProds()) {
				addProduct(p);			
			}
			
			for (Recipe r : rl.getRecipes()) {
				addRecipe(r);
			}
			
			/* Salvo i prodotti associati alle ricette in un HashMap */
			hmap = new HashMap<String, List<String>>();
			
			for (Recipe r : rl.getRecipes()) {
				recProducts = new ArrayList<String>();
				for (String t : r.getProducts()) {
					recProducts.add(t);
				}
				hmap.put(r.getDescription(), recProducts);
			}
			
			addUses(rl, hmap);
			
		}
		else {
			/* Inserisco nel database solo i prodotti che non sono già presenti */
			SQLiteDatabase db1 = fad.getReadableDatabase();
			String[] COL = {NAME};
			Cursor cursor = db1.query(TABLE_PRODUCTS, COL, null, null, null, null, null);
			startManagingCursor(cursor);
			List<String> dbProd = new ArrayList<String>();
			
			while (cursor.moveToNext()) {
				String pname = cursor.getString(0);
				dbProd.add(pname);
				//System.out.println(pname);
			}
			cursor.close();
			
			for (Product pr : pl.getProds()) {
				if (! dbProd.contains(pr.getName())) {
					addProduct(pr);
				}
			}
			
			/* Inserisco nel database solo le ricette che non sono già presenti */
			SQLiteDatabase db2 = fad.getReadableDatabase();
			String[] COL2 = {DESCRIPTION};
			Cursor cursor2 = db2.query(TABLE_RECIPES, COL2, null, null, null, null, null);
			startManagingCursor(cursor2);
			List<String> dbRec = new ArrayList<String>();
			
			while (cursor2.moveToNext()) {
				String rdesc = cursor2.getString(0);
				dbRec.add(rdesc);
			}
			cursor2.close();
			
			for (Recipe r : rl.getRecipes()) {
				if (! dbRec.contains(r.getDescription())) {
					addRecipe(r);
					
					/* Se c'è una nuova ricetta, inserisco nella relazione uses tale ricetta con i prodotti
					 * che contiene */
					addUsesSingleRecipe(r);
				}
			}
		}
		fad.close();
		
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
			}
		}
		
		else {
			
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
    
    public void addProduct(Product p) {
    	SQLiteDatabase db = fad.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(NAME, p.getName());
    	values.put(DESCRIPTION, p.getDescription());
    	values.put(PRICE, p.getPrice());
    	values.put(DURATION, "2012-01-01");
    	db.insertOrThrow(TABLE_PRODUCTS, null, values);
    }
    
    public void addRecipe(Recipe r) {
    	SQLiteDatabase db = fad.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(DESCRIPTION, r.getDescription());
    	db.insertOrThrow(TABLE_RECIPES, null, values);
    }
    
    public void addUses(RecList rl, HashMap<String, List<String>> hmap ) {
    	for (Recipe rec : rl.getRecipes()) {
			/* Ricavo dall'HashMap la lista dei prodotti associata alla ricetta */
			List<String> lsprod = new ArrayList<String>();
			lsprod = hmap.get(rec.getDescription());
			
			/* Definisco una query per ricavare l'id delle ricette data la descrizione */
			SQLiteDatabase db3 = fad.getWritableDatabase();
			String[] COL = {_ID};
			String where = "description = " + "\"" + rec.getDescription() + "\"";
			Cursor cursor = db3.query(TABLE_RECIPES, COL, where, null, null, null, null);
			startManagingCursor(cursor);
			while(cursor.moveToNext()) {
				int id = cursor.getInt(0);
				for (int i=0; i<lsprod.size(); i++) {
					/* Definisco una query per ricavare l'id dei prodotti dato il nome */
					String where2 = "name = " + "\"" + lsprod.get(i) + "\"";
					Cursor c2 = db3.query(TABLE_PRODUCTS, COL, where2, null, null, null, null);
					startManagingCursor(c2);
					while (c2.moveToNext()) {
						/* Inserisco i valori trovati nella tabella uses */
						int prod_id = c2.getInt(0);
						ContentValues values = new ContentValues();
						values.put(RECIPE_ID, id);
						values.put(PRODUCT_ID, prod_id);
						db3.insertOrThrow(TABLE_USES, null, values);
					}
					c2.close();
				}
			}
			cursor.close();
		}
    }
    
    public void addUsesSingleRecipe(Recipe r) {
    	
    	recProducts = new ArrayList<String>();
		for (String t : r.getProducts()) {
			recProducts.add(t);
		}
		
		/* Definisco una query per ricavare l'id della ricetta data la descrizione */
		SQLiteDatabase db3 = fad.getWritableDatabase();
		String[] COL = {_ID};
		String where = "description = " + "\"" + r.getDescription() + "\"";
		Cursor cursor = db3.query(TABLE_RECIPES, COL, where, null, null, null, null);
		startManagingCursor(cursor);
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);
			for (int i=0; i<recProducts.size(); i++) {
				/* Definisco una query per ricavare l'id dei prodotti dato il nome */
				String where2 = "name = " + "\"" + recProducts.get(i) + "\"";
				Cursor c2 = db3.query(TABLE_PRODUCTS, COL, where2, null, null, null, null);
				startManagingCursor(c2);
				while (c2.moveToNext()) {
					/* Inserisco i valori trovati nella tabella uses */
					int prod_id = c2.getInt(0);
					ContentValues values = new ContentValues();
					values.put(RECIPE_ID, id);
					values.put(PRODUCT_ID, prod_id);
					db3.insertOrThrow(TABLE_USES, null, values);
				}
				c2.close();
			}
		}
		cursor.close();
		
    }
    
    
}

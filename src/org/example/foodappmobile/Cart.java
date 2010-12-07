package org.example.foodappmobile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import static org.example.foodappmobile.Constants.*;

public class Cart extends Activity implements OnClickListener{
	
	/* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	String strCookieName = "foodapp_session";
    String strCookieValue;
    HttpResponse resp;
    ArrayList<Product> ordered;
    List<String> orderedString;
    final String url = "checkout/";
    FoodAppData fad;
    HashMap<Integer, List<Integer>> map;
    HashMap<Integer, Integer> count;
    /* Liste per salvare le ricette comuni ai prodotti presenti nel carrello */
    List<Integer> commonRecipe;
    HashMap<String, List<String>> recProd;
    List<String> prods;
    List<String> allProds;
    int index;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = getIntent().getExtras();
        ordered = b.getParcelableArrayList("orderedProducts");
        strCookieValue = b.getString(strCookieName);
        
        System.out.println(strCookieName + "=" + strCookieValue);
        
        /* Aggiungo alla lista orderedString tutti i nomi dei prodotti ordinati */
        orderedString = new ArrayList<String>();
        for (int c=0; c<ordered.size(); c++) {
        	orderedString.add(ordered.get(c).getName());
        }
        
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
        
        /* Codice per visualizzare le ricette */
        /* Descrizione: prendo ciascun prodotto dalla lista dei prodotti ordinati, ricavo il suo id nel
         * database, vado a vedere se nella tabella uses tale id è associato a una qualche ricetta,
         * salvo l'id della ricetta e poi vedo quali ricette sono state trovate.
         * Se prodotti diversi sono associati a ricette diverse, faccio l'intersezione fra le ricette per 
         * visualizzare solamente le ricette comuni a tutti i prodotti */
        fad = new FoodAppData(this);
        map = new HashMap<Integer, List<Integer>>();
        
        for (int k=0; k<ordered.size(); k++) {
        	SQLiteDatabase db = fad.getReadableDatabase();
        	String[] COL = {_ID};
        	String where = "name = " + "\"" + ordered.get(k).getName() + "\"";
        	Cursor cursor = db.query(TABLE_PRODUCTS, COL, where, null, null, null, null);
        	startManagingCursor(cursor);
        	while (cursor.moveToNext()) {
        		int prodId = cursor.getInt(0);
        		String where2 = "product_id = " + prodId;
        		String[] COL2 = {RECIPE_ID};
        		Cursor cursor2 = db.query(TABLE_USES, COL2, where2, null, null, null, null);
        		if (cursor2 != null) {
        			List<Integer> ids = new ArrayList<Integer>();
        			while (cursor2.moveToNext()) {
        				int recId = cursor2.getInt(0);
        				ids.add(recId);
        				System.out.println(recId);	
        			}
        			map.put(prodId, ids);
        		}
        		cursor2.close();
        	}
        	cursor.close();
        }
        fad.close();
        
        count = new HashMap<Integer, Integer>();
        
        /* Iteratore che ha la funzione di fare un ciclo per ogni chiave presente nell'HashMap */
        Iterator<Integer> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
        	String key = iterator.next().toString();
        	int keyInt = Integer.parseInt(key);
        	List<Integer> value = map.get(keyInt);
        	for (int x=0; x<value.size(); x++) {
        		System.out.println(key + " " + value.get(x));
        		/* Per ogni valore presente nell'HashMap (recipe_id) vado a contare le sue occorrenze e salvo tale valore
        		 * in un altro HashMap che ha come chiave l'id della ricetta e come valore il numero di occorrenze 
        		 * nell'HashMap che mappa prodotti e ricette associate */
        		if (count.get(value.get(x)) != null) {
        			int tempVal = count.get(value.get(x));
        			tempVal++;
        			count.put(value.get(x), tempVal);
        		}
        		else {
        			count.put(value.get(x), 1);
        		}
        	}
        }
        
        commonRecipe = new ArrayList<Integer>();
        Iterator<Integer> it = count.keySet().iterator();
        while ( it.hasNext() ) {
        	String key = it.next().toString();
        	int keyInt = Integer.parseInt(key);
        	int val = count.get(keyInt);
        	/* Controllo che il valore delle occorrenze di una ricetta sia uguale al numero di prodotti ordinati:
        	 * in caso affermativo significa che la ricetta è comune a tutti i prodotti presenti nel carrello */
        	if (val == ordered.size()) {
        		System.out.println(" La ricetta con id " + keyInt + " e\' comune a tutti i prodotti ordinati.");
        		commonRecipe.add(keyInt);
        	}
        }
        
        /* Serve per ottenere un HashMap che contiene come chiave la descrizione della ricetta e come
         * valore i nomi dei prodotti associati a tale ricetta */
        recProd = new HashMap<String, List<String>>();
        for (int recIt=0; recIt < commonRecipe.size(); recIt++ ) {
        	SQLiteDatabase db = fad.getReadableDatabase();
        	String[] COL = {DESCRIPTION};
        	String where = _ID + " = " + commonRecipe.get(recIt);
        	Cursor c = db.query(TABLE_RECIPES, COL, where, null, null, null, null);
        	startManagingCursor(c);
        	while (c.moveToNext()) {
        		String recipeDesc = c.getString(0);
        		prods =  new ArrayList<String>();
        		String[] COL2 = {PRODUCT_ID};
        		String where2 = RECIPE_ID + " = " + commonRecipe.get(recIt);
        		Cursor c2 = db.query(TABLE_USES, COL2, where2, null, null, null, null);
        		startManagingCursor(c2);
        		while (c2.moveToNext()) {
        			int prodId = c2.getInt(0);
        			String[] COL3 = {NAME};
        			String where3 = _ID + " = " + prodId;
        			Cursor c3 = db.query(TABLE_PRODUCTS, COL3, where3, null, null, null, null);
        			startManagingCursor(c3);
        			while (c3.moveToNext()) {
        				String prodName = c3.getString(0);
        				prods.add(prodName);
        			}
        			c3.close();
        		}
        		c2.close();
        		/* Scrivo nell'HashMap la ricetta corrente con i prodotti che contiene */
        		recProd.put(recipeDesc, prods);
        	}
        	c.close();
        }
        fad.close();
        
        if ( ! recProd.isEmpty()) {
        	/* Visualizzo nel Layout le ricette consigliate */
        	TextView rec = new TextView(this);
        	rec.setText("\nRicette consigliate\n");
        	rec.setGravity(Gravity.CENTER);
        	rec.setTextSize(18);
        	ll.addView(rec);
        
        	/* Definisco un TableLayout per visualizzare su una colonna le ricette consigliate
        	 * e nell'altra i prodotti che sono associati alle ricette */
        	TableLayout tl = new TableLayout(this);
        	/* Setto le colonne Shrinkable in modo che se il testo è troppo lungo
        	 * non vada ad occupare troppo spazio dell'altra colonna ma venga
        	 * scritto a capo*/
        	tl.setColumnShrinkable(0, true);
        	//tl.setColumnShrinkable(1, true);
        	tl.setColumnStretchable(1, true);
        
        	/* La prima riga conterrà i titoli per ricette e prodotti */
        	TableRow titleRow = new TableRow(this);
        	TextView titleRecipe = new TextView(this);
        	titleRecipe.setText("Ricette");
        	titleRecipe.setTextSize((float) 16.5);
        	titleRow.addView(titleRecipe);
        	TextView titleProd = new TextView(this);
        	titleProd.setText("Prodotti");
        	titleProd.setTextSize((float) 16.5);
        	titleProd.setGravity(Gravity.CENTER);
        	titleRow.addView(titleProd);
        	tl.addView(titleRow);
        
        	/* Setto i parametri per avere un marginBottom*/
        	MarginLayoutParams mlp = new MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT );
        	mlp.setMargins(0,0,0,150);
        
        	/* Vista di separazione*/
        	View sep1 = new View(this);
        	sep1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
        			LayoutParams.FILL_PARENT));
        	sep1.setBackgroundResource(R.color.separator);
        	sep1.setMinimumHeight(3);
        	sep1.setLayoutParams(mlp);
        	tl.addView(sep1);
        
        	/* Definisco un Iteratore per scorrere i valori dell'HashMap costruito prima */
        	Iterator<String> iter = recProd.keySet().iterator();
        	/* Preparo una lista per salvare tutti i prodotti che si possono ordinare */
        	allProds = new ArrayList<String>();
        	while (iter.hasNext()) {
        		String rDesc = iter.next();
        		prods = new ArrayList<String>();
        		prods = recProd.get(rDesc);
        		/*for (int i=0; i < prods.size(); i++ ) {
        			System.out.println(rDesc + " -> " + prods.get(i));
        		} 	OK */
        		TableRow tr = new TableRow(this);
        		TextView recipe = new TextView(this);
        		recipe.setText(rDesc);
        		tr.addView(recipe);
        		for (int i=0; i < prods.size(); i++ ) {
        			TextView prodName = new TextView(this);
        			allProds.add(prods.get(i));
        			prodName.setText(prods.get(i));
        			prodName.setGravity(Gravity.CENTER);
        			if (i == 0) {
        				tr.addView(prodName);
        				/*if (! orderedString.contains(prodName)) {
        					TextView add = new TextView(this);
        					add.setText("Aggiungi al carrello");
        					tr.addView(add);
        				} */
        				tl.addView(tr);
        			}
        			else {
        				TableRow tr2 = new TableRow(this);
        				TextView empty = new TextView(this);
        				tr2.addView(empty);
        				tr2.addView(prodName);
        				/*if (! orderedString.contains(prodName)) {
        					TextView add = new TextView(this);
        					add.setText("Aggiungi al carrello");
        					tr.addView(add);
        				} */
        				tl.addView(tr2);
        			}
        		}
        	
        	}
        
        	TextView tAdd = new TextView(this);
        	tAdd.setText("\nVuoi inserire uno di questi prodotti nel carrello?\n");
        	tl.addView(tAdd);
        
        	for (int index=0; index < allProds.size(); index++) {
        		if (! orderedString.contains(allProds.get(index))) {
        			TableRow tx = new TableRow(this);
        			TextView tvProd = new TextView(this);
        			tvProd.setText(allProds.get(index));
        			tx.addView(tvProd);
        			Button addCart = new Button(this);
        			addCart.setText("Aggiungi");
        			tx.addView(addCart);
        			tl.addView(tx);
        		}
        	}
        
        	ll.addView(tl);
        
        	/* Inserisco un TextView vuoto per separare il bottone dalla tabella */
        	TextView space = new TextView(this);
        	space.setHeight(40);
        	ll.addView(space);
        }
        else {
        	TextView noRec = new TextView(this);
        	noRec.setText("\nNon vi è alcuna ricetta consigliata per i prodotti presenti" +
        			" nel carrello.\n");
        	ll.addView(noRec);
        }
         
        /* Aggiungo il bottone per fare il checkout */
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
        
        /* Aggiungo il LinearLayout principale allo ScrollView e setto il contenuto della pagina */
        sv.addView(ll);
        setContentView(sv);
    } //fine onCreate
    
    public void onClick (View v) {
    	
    	HttpMethods hm = new HttpMethods();
    	resp = hm.postDataNoPairs(url, strCookieName, strCookieValue);
		
		if (resp != null)  {
			/* Creo l'intent e preparo i parametri da passare */
			Intent check = new Intent(this, Checkout.class);
			Bundle b = new Bundle();
			b.putParcelableArrayList("orderedProducts", ordered);
			b.putString(strCookieName, strCookieValue);
			check.putExtras(b);
		
			/* Avvio l'Activity*/				
			startActivity(check);
		}
    }

}

package org.example.foodappmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import static org.example.foodappmobile.Constants.*;

public class MachineActivity extends Activity{
	
	/* Oggetto della classe FoodAppData per il database */
	private FoodAppData fad;
	/* Lista di prodotti da visualizzare */
	List<String> products;
	/* Array di CheckBox per la selezione dei prodotti */
	CheckBox[] cb;
	/* Lista che contiene i prodotti selezionati */
	List<String> selected;
	/* HashMap che serve per mantenere una relazione fra prodotti e Id in modo
	 * da non dover interrogare nuovamente il database per ottenerli */
	HashMap<String,List<Integer>> map;
	/* Lista di id di distributori che contengono i prodotti selezionati */
	ArrayList<Integer> resMachine;

	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine);
        
        /* Creo o aggiorno il database per ottenere i prodotti e distributori */
        fad = new FoodAppData(this);
        fad.creaAggiornaDB();
        
        final LinearLayout ll = (LinearLayout) findViewById(R.id.ll_prods);
        
        /* Cerco nel database i prodotti da visualizzare */
        products = new ArrayList<String>();
        map = new HashMap<String,List<Integer>>();
        SQLiteDatabase db = fad.getReadableDatabase();
        String[] COL = {NAME, _ID};
        Cursor c = db.query(TABLE_MACHINE_PRODUCT, COL, null, null, null, null, null);
        startManagingCursor(c);
        while(c.moveToNext()) {
        	List<Integer> ids = new ArrayList<Integer>();
        	String pname = c.getString(0);
        	int id = c.getInt(1);
        	ids.add(id);
        	/* Aggiungo il prodotto alla lista da visualizzare */
        	if(! products.contains(pname)) {
        		products.add(pname);
        		/* Se il prodotto non era nella lista, può avere un solo id associato */
        		map.put(pname, ids);
        	}
        	else {
        		/* Se ho trovato il prodotto nella lista, allora significa che è 
        		 * presente in più distributori e ha più id associati */
        		ids = map.get(pname);
        		ids.add(id);
        		map.put(pname, ids);
        	}	
        }
        c.close();
        db.close();
        
        /* Stampa di prova dell'hashmap */
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
        	List<Integer> ids = map.get(key);
        	for (int i : ids) {
        		System.out.println(key + " -> " + i);
        	}
        }  /* Stampa OK*/
        
        /* Ora la lista products contiene i prodotti da visualizzare */
        /* Imposto un checkbox per ciascun prodotto, in modo che l'utente possa
         * selezionare i prodotti che vuole cercare nei distributori */
        int numProd = products.size();
        cb = new CheckBox[numProd];
        for (int i=0; i < numProd; i++) {
        	cb[i] = new CheckBox(this);
        	cb[i].setText(products.get(i));
        	ll.addView(cb[i]);
        }
        
        /* Predispongo un LinearLayout per la visualizzazione del pulsante */
        LinearLayout ll2 = new LinearLayout(this);
        ll2.setGravity(Gravity.CENTER);
        ll2.setPadding(5, 5, 5, 5);
        /* Visualizzo un pulsante per cercare i prodotti */
        Button search = new Button(this);
        search.setText("Cerca");
        search.setWidth(100);
        //search.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				//LayoutParams.WRAP_CONTENT));
        search.setOnClickListener(new Button.OnClickListener(){
        	public void onClick (View v) {
        		resMachine = new ArrayList<Integer>();
        		selected = new ArrayList<String>();
        		for (int i = 0; i < products.size(); i++) {
        			if (cb[i].isChecked()) {
        				System.out.println(products.get(i) + " selezionato.");
        				/* Aggiungo il prodotto selezionato alla lista */
        				selected.add(products.get(i));
        			}
        		}
        		
        		/* Prima di procedere controllo che l'utente abbia selezionato almeno
        		 * un prodotto dalla lista */
        		if (selected.size() == 0) {
        			printNoSelectionError();
        		}
        		else {
        		
        			/* Cerco nel database se esiste un distributore che contiene tutti
        			 * i prodotti selezionati, altrimenti avverto l'utente */
        			String prod = selected.get(0); /* Prendo il primo prodotto selezionato */
        			/* Ricavo gli id associati a quel prodotto (uno o più) */
        			List<Integer> ids = map.get(prod); 
        			List<Integer> machineIds = new ArrayList<Integer>(); /* Risultati delle query */
        			for (int id : ids) {
        				/* Trovo gli id del/dei distributori che contengono quel prodotto */
        				SQLiteDatabase db2 = fad.getReadableDatabase();
        				String[] COL = {MACHINE_ID};
        				String where = PRODUCT_ID + " = " + id;
        				Cursor c = db2.query(TABLE_PRODUCT_CONTAINED, COL, where, null, null, null, null);
        				startManagingCursor(c);
        				while(c.moveToNext()) {
        					int macId = c.getInt(0);
        					machineIds.add(macId);
        				}
        				c.close();
        				db2.close();
        			}
        			/* Ora ho l'id dei distributori che contengono il primo prodotto.
        			 * Trovo gli altri prodotti che contengono tali distributori */
        			HashMap<Integer, List<Integer>> macProd = new HashMap<Integer, List<Integer>>();
        			for (int id : machineIds) {
        				List<Integer> prodIds = new ArrayList<Integer>();
        				SQLiteDatabase db3 = fad.getReadableDatabase();
        				String[] COL = {PRODUCT_ID};
        				String where = MACHINE_ID + " = " + id;
        				Cursor c = db3.query(TABLE_PRODUCT_CONTAINED, COL, where, null, null, null, null);
        				startManagingCursor(c);
        				while(c.moveToNext()) {
        					int pId = c.getInt(0);
        					prodIds.add(pId);
        				}
        				macProd.put(id, prodIds);
        				c.close();
        				db3.close();
        			}
        		
        			Iterator<Integer> it1 = macProd.keySet().iterator();
        			while(it1.hasNext()) {
        				int key = it1.next();
        				List<Integer> val = macProd.get(key);
        				for (int t : val) {
        					System.out.println("id distributore: "+ key + " -> " + t);
        				}
        			} /* OK */
        		
        			/* Ora ho un hash map con chiave pari agli id dei distributori trovati e con valori gli id
        			 * dei prodotti che essi contengono. Cerco fra questi id se ci sono gli altri prodotti
        			 * ordinati dall'utente, altrimenti visualizzo un messaggio di errore. */
        			Iterator<Integer> iter = macProd.keySet().iterator();
        			while(iter.hasNext()) {
        				int key = iter.next();
        				/* Lista degli id dei prodotti della macchina con id = key */
        				List<Integer> productsId = macProd.get(key);
        				if (selected.size() > 1 ) {
        					int nFind = 1;
        					/* Prendo ciascuno degli altri prodotti selezionati */
        					for (int i = 1; i < selected.size(); i++) {
        						List<Integer> selId = map.get(selected.get(i));
        						for (int id : selId) {
        							if (productsId.contains(id)) {
        								nFind++;
        							}
        						}
        					}
        					if (nFind == selected.size()) {
        						resMachine.add(key);
        					}
        				}
        				else {
        					resMachine.add(key);
        				}
        			}
        		
        			/* Stampa di prova del risultato */
        			for (int i : resMachine) {
        				System.out.println("Risultati: Distributore -> " + i);
        			} /* OK */
        		
        			/* Controllo che i prodotti selezionati siano presenti in almeno
        			 * una macchina altrimenti avverto l'utente con un messaggio */
        			if (resMachine.size() == 0) {
        				printMessage();
        			}
        			else {
        				/* Avvio l'attività per mostrare il distributore più vicino
        				 * passandogli come parametro la lista degli id dei 
        				 * distributori trovati */
        				startActivityNearestMachine();
        			}
        		
        		}
        		
        	} // fine onClick
        });
        fad.close();
        ll2.addView(search);
        ll.addView(ll2);
	}
	
	private void printMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Non esiste nessun distributore con i prodotti che hai" +
				" selezionato. Riprova con una diversa combinazione di prodotti.")
			.setCancelable(false)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		builder.show();
	}
	
	private void printNoSelectionError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Non hai selezionato alcun prodotto. " +
				"Effettua almeno una selezione prima di procedere")
			.setCancelable(false)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		builder.show();
	}
	
	private void startActivityNearestMachine() {
		Intent near = new Intent(this, NearestMachine.class);
		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList("distributori", resMachine);
		near.putExtras(bundle);
		startActivity(near);
	}
}

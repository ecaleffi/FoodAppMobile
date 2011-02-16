package org.example.foodappmobile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static android.provider.BaseColumns._ID;
import static org.example.foodappmobile.Constants.*;

public class FoodAppData extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "foodappmobile.db";
	private static final int DATABASE_VERSION = 1;
	private HttpMethods hm; //Variabile per invocare il servizio REST
	/* URL per l'invocazione dei servizi REST */
	private final String prodUrl = "rest/product"; 
	private final String recUrl = "rest/recipe";
	private final String ordUrl = "rest/order";
	private final String macUrl = "rest/machine";
	/* Risultati ritornati dall'invocazione del servizio REST */
	private String resProd;
	private String resRec;
	private String resOrd;
	private String resMac;
	/* Istanze delle classi che conterranno le informazioni deserializzate */
	private ProductList pl;
	private RecList rl;
	private OrderList ol;
	private MachineList ml;
	/* Variabili di appoggio e HashMap per relazioni */
	ArrayList<String> recProducts;
	HashMap<String, List<String>> hmap;
	ArrayList<String> ordProducts;
	HashMap<Integer, List<String>> hmapOrd;
	
	public FoodAppData (Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" + _ID +
				" INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " CHAR(64)," +
				DESCRIPTION + " TEXT," + PRICE + " MONEY," + DURATION + " DATE, " + 
				STOCK_QTY + " INTEGER, " + STOCK_THRESHOLD + " INTEGER" + ");");
			db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" + _ID + 
				" INTEGER PRIMARY KEY AUTOINCREMENT, " + DESCRIPTION + " TEXT" + ");");
			db.execSQL("CREATE TABLE " + TABLE_USES + " (" + PRODUCT_ID + " INTEGER REFERENCES PRODUCT(_ID), " +
				RECIPE_ID + " INTEGER REFERENCES RECIPE(_ID), " + 
				"PRIMARY KEY (RECIPE_ID, PRODUCT_ID) " + ");");
			db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" + _ID + 
					" INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " CHAR(20)" + ");");
			db.execSQL("CREATE TABLE " + TABLE_ORDERS_ITEM + " (" + ORDER_ID + " INTEGER " +
					"REFERENCES ORDERS(ID), " + ITEM + " CHAR(64), " + 
					"PRIMARY KEY (ORDER_ID, ITEM) " + ");");
			db.execSQL("CREATE TABLE " + TABLE_MACHINE + " (" + _ID + " INTEGER PRIMARY KEY, " +
					NAME + " CHAR(32) NOT NULL, " + ADDRESS + " CHAR(64), " + CITY + " CHAR(32), " +
					PROVINCE_STATE + " CHAR(32), " + POSTAL_CODE + " CHAR(10), " +
					LATITUDE + " CHAR(32), " + LONGITUDE + " CHAR(32)" + ");");
			db.execSQL("CREATE TABLE " + TABLE_MACHINE_PRODUCT + " (" + _ID + " INTEGER PRIMARY KEY, " +
					NAME + " CHAR(32) NOT NULL, " + DESCRIPTION + " TEXT, " + PRICE + " MONEY, " +
					DURATION + " DATE, " + QTY + " INTEGER" + ");");
			db.execSQL("CREATE TABLE " + TABLE_PRODUCT_CONTAINED + " (" + MACHINE_ID +
					" INTEGER REFERENCES MACHINE(ID), " + PRODUCT_ID + 
					" INTEGER REFERENCES MACHINE_PRODUCT(ID), " + 
					" PRIMARY KEY (MACHINE_ID, PRODUCT_ID)"+ ");");
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e("Errore nella creazione delle tabelle", e.toString());
			throw e;
		} finally {
			db.endTransaction();
		}

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCTS);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_RECIPES);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USES);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ORDERS);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ORDERS_ITEM);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_MACHINE);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_MACHINE_PRODUCT);
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCT_CONTAINED);
			onCreate(db);
		} catch (SQLException e) {
			Log.e("Errore nell'upgrade delle tabelle", e.toString());
			throw e;
		} finally {
			db.endTransaction();
		}
	}
	
	public boolean isDatabaseExist() {
		File dbFile = new File(DB_PATH+DATABASE_NAME);
		return dbFile.exists();
	}
	
	public void creaAggiornaDB() {
		System.out.println("Creazione - Aggiornamento DB");
		hm = new HttpMethods();
		
		/* Ottengo dal servizio REST le informazioni serializzate */
		resProd = hm.callWebService(prodUrl);
		resRec = hm.callWebService(recUrl);
		resOrd = hm.callWebService(ordUrl);
		resMac = hm.callWebService(macUrl);
		
		/* Creo le variabili per la deserializzazione */
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		JSONObject j1;
		JSONObject j2;
		JSONObject j3;
		JSONObject j4;
		
		/* Effettuo la deserializzazione */
		try {
			j1 = new JSONObject(resProd);
			j2 = new JSONObject(resRec);
			j3 = new JSONObject(resOrd);
			j4 = new JSONObject(resMac);
			ProductList tempProd = gson.fromJson(j1.toString(), ProductList.class);
			RecList tempRec = gson.fromJson(j2.toString(), RecList.class);
			OrderList tempOrd = gson.fromJson(j3.toString(), OrderList.class);
			MachineList tempMac = gson.fromJson(j4.toString(), MachineList.class);
			pl = tempProd;
			rl = tempRec;
			ol = tempOrd;
			ml = tempMac;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (pl == null || rl == null || ol == null || ml == null) {
			System.out.println("Errore nella deserializzazione JSON");
		}
		
		/* Inserisco i prodotti nel database */
		//fad = new FoodAppData(c);
		/* CASO IN CUI IL DATABASE NON ESISTE */
		if (! isDatabaseExist()) {
			/* Aggiungo i prodotti al database */
			for (Product p : pl.getProds()) {
				addProduct(p);			
			}
			
			/* Aggiungo le ricette al database */
			for (Recipe r : rl.getRecipes()) {
				addRecipe(r);
			}
			
			/* Aggiungo gli ordini al database */
			for (DBOrders o : ol.getOrders()) {
				addOrder(o);
			}
			
			/* Salvo i prodotti associati alle ricette in un HashMap */
			hmap = new HashMap<String, List<String>>();
			
			/* Aggiungo i prodotti nella tabella in relazione con le ricette */
			for (Recipe r : rl.getRecipes()) {
				recProducts = new ArrayList<String>();
				for (String t : r.getProducts()) {
					recProducts.add(t);
				}
				hmap.put(r.getDescription(), recProducts);
			}
			
			addUses(rl, hmap);
			
			/* Salvo i prodotti associati agli ordini in un HashMap*/
			hmapOrd = new HashMap<Integer, List<String>>();
			
			/* Aggiungo i prodotti nella tabella in relazione con gli ordini */
			for (DBOrders o : ol.getOrders()) {
				ordProducts = new ArrayList<String>();
				for (String tt : o.getProducts()) {
					ordProducts.add(tt);
				}
				hmapOrd.put(o.getId(), ordProducts);
			}
			
			addItem(ol, hmapOrd);
			
			/* Aggiungo i distributori e i prodotti al database */
			for (Machine m : ml.getMachines()) {
				addMachine(m);
			}
			for (MachineProduct mp : ml.getMachineProducts()) {
				addMachineProduct(mp);
			}
			/* Aggiungo i prodotti nella tabella delle relazioni */
			for (Machine m2 : ml.getMachines()) {
				for (int id : m2.getProducts()) {
					addProductContained(m2.getId(), id);
				}
			}
			
		}
		/* CASO IN CUI IL DATABASE E' GIA' ESISTENTE */
		else {
			/* Inserisco nel database solo i prodotti che non sono già presenti */
			SQLiteDatabase db1 = getReadableDatabase();
			String[] COL = {NAME};
			Cursor cursor = db1.query(TABLE_PRODUCTS, COL, null, null, null, null, null);
			//startManagingCursor(cursor);
			List<String> dbProd = new ArrayList<String>();
			
			while (cursor.moveToNext()) {
				String pname = cursor.getString(0);
				dbProd.add(pname);
				//System.out.println(pname);
			}
			cursor.close();
			db1.close();
			
			for (Product pr : pl.getProds()) {
				if (! dbProd.contains(pr.getName())) {
					addProduct(pr);
				}
			}
			
			/* Inserisco nel database solo le ricette che non sono già presenti */
			SQLiteDatabase db2 = getReadableDatabase();
			String[] COL2 = {DESCRIPTION};
			Cursor cursor2 = db2.query(TABLE_RECIPES, COL2, null, null, null, null, null);
			//startManagingCursor(cursor2);
			List<String> dbRec = new ArrayList<String>();
			
			while (cursor2.moveToNext()) {
				String rdesc = cursor2.getString(0);
				dbRec.add(rdesc);
			}
			cursor2.close();
			db2.close();
			
			for (Recipe r : rl.getRecipes()) {
				if (! dbRec.contains(r.getDescription())) {
					addRecipe(r);
					
					/* Se c'è una nuova ricetta, inserisco nella relazione uses tale ricetta con i prodotti
					 * che contiene */
					addUsesSingleRecipe(r);
				}
			}
			
			/* Inserisco nel database solo gli ordini che non sono già presenti */
			SQLiteDatabase db3 = getReadableDatabase();
			String[] COL3 = {_ID};
			Cursor cursor3 = db3.query(TABLE_ORDERS, COL3, null, null, null, null, null);
			//startManagingCursor(cursor3);
			List<Integer> dbId = new ArrayList<Integer>();
			while(cursor3.moveToNext()) {
				int id = cursor3.getInt(0);
				dbId.add(id);
			}
			cursor3.close();
			db3.close();
			
			for (DBOrders o : ol.getOrders()) {
				if (! dbId.contains(o.getId())) {
					addOrder(o);
				
					/* Se c'è un nuovo ordine, inserisco i prodotti che contiene nella 
					 * tabella order_items tali prodotti */
					addItemSingleOrder(o);
				}
			}
			
			/* Inserisco nel database solamente i prodotti dei distributori che non sono presenti */
			SQLiteDatabase db4 = getReadableDatabase();
			String[] COL4 = {_ID};
			Cursor cursor4 = db4.query(TABLE_MACHINE_PRODUCT, COL4, null, null, null, null, null);
			//startManagingCursor(cursor4);
			List<Integer> pmId = new ArrayList<Integer>();
			while(cursor4.moveToNext()) {
				int id = cursor4.getInt(0);
				pmId.add(id);
			}
			cursor4.close();
			db4.close();
			
			for (MachineProduct mprod : ml.getMachineProducts()) {
				if (! pmId.contains(mprod.getId())) {
					addMachineProduct(mprod);
				}
			}
			
			/* Inserisco nel database solamente i distributori che non sono presenti */
			SQLiteDatabase db5 = getReadableDatabase();
			String[] COL5 = {_ID};
			Cursor cursor5 = db5.query(TABLE_MACHINE, COL5, null, null, null, null, null);
			//startManagingCursor(cursor5);
			List<Integer> mId = new ArrayList<Integer>();
			while(cursor5.moveToNext()) {
				int id = cursor5.getInt(0);
				mId.add(id);
			}
			cursor5.close();
			db5.close();
			
			for (Machine mac : ml.getMachines()) {
				if (! mId.contains(mac.getId())) {
					addMachine(mac);
					
					/* Inserisco nella tabella della relazione i prodotti che tale macchina contiene */
					List<Integer> prodId = mac.getProducts();
					for (int id : prodId) {
						addProductContained(mac.getId(), id);
					}
				}
			}
		}
		close();
		
	}
	
	private void addProduct(Product p) {
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(NAME, p.getName());
    	values.put(DESCRIPTION, p.getDescription());
    	values.put(PRICE, p.getPrice());
    	values.put(DURATION, "2012-01-01");
    	values.put(STOCK_QTY, Integer.parseInt(p.getStockQty()));
    	values.put(STOCK_THRESHOLD, Integer.parseInt(p.getStockThreshold()));
    	db.insertOrThrow(TABLE_PRODUCTS, null, values);
    	db.close();
    }
    
    private void addRecipe(Recipe r) {
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(DESCRIPTION, r.getDescription());
    	db.insertOrThrow(TABLE_RECIPES, null, values);
    	db.close();
    }
    
    private void addOrder(DBOrders o) {
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(_ID, o.getId());
    	values.put(NUMBER, o.getNumber());
    	db.insertOrThrow(TABLE_ORDERS, null, values);
    	db.close();
    }
    
    private void addUses(RecList rl, HashMap<String, List<String>> hmap ) {
    	for (Recipe rec : rl.getRecipes()) {
			/* Ricavo dall'HashMap la lista dei prodotti associata alla ricetta */
			List<String> lsprod = new ArrayList<String>();
			lsprod = hmap.get(rec.getDescription());
			
			/* Definisco una query per ricavare l'id delle ricette data la descrizione */
			SQLiteDatabase db3 = getWritableDatabase();
			String[] COL = {_ID};
			String where = "description = " + "\"" + rec.getDescription() + "\"";
			Cursor cursor = db3.query(TABLE_RECIPES, COL, where, null, null, null, null);
			//startManagingCursor(cursor);
			while(cursor.moveToNext()) {
				int id = cursor.getInt(0);
				for (int i=0; i<lsprod.size(); i++) {
					/* Definisco una query per ricavare l'id dei prodotti dato il nome */
					String where2 = "name = " + "\"" + lsprod.get(i) + "\"";
					Cursor c2 = db3.query(TABLE_PRODUCTS, COL, where2, null, null, null, null);
					//startManagingCursor(c2);
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
			db3.close();
		}
    }
    
    private void addItem(OrderList ol, HashMap<Integer, List<String>> hmapOrd) {
    	for (DBOrders ord : ol.getOrders()) {
    		SQLiteDatabase db = getWritableDatabase();
    		/* Ricavo dall'hashMap la lista di prodotti associata all'ordine */
    		List<String> listp = new ArrayList<String>();
    		listp = hmapOrd.get(ord.getId());
    		for (String lp : listp) {
    			ContentValues values = new ContentValues();
    			values.put(ORDER_ID, ord.getId());
    			values.put(ITEM, lp);
    			db.insertOrThrow(TABLE_ORDERS_ITEM, null, values);
    		}
    		db.close();
    	}
    }
    
    private void addMachine(Machine m) {
    	
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(_ID, m.getId());
    	values.put(NAME, m.getName());
    	values.put(ADDRESS, m.getAddress());
    	values.put(CITY, m.getCity());
    	values.put(PROVINCE_STATE, m.getProvinceState());
    	values.put(POSTAL_CODE, m.getPostalCode());
    	values.put(LATITUDE, m.getLatitude());
    	values.put(LONGITUDE, m.getLongitude());
    	db.insertOrThrow(TABLE_MACHINE, null, values);
    	db.close();
    	
    }
    
    private void addMachineProduct(MachineProduct mp) {
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(_ID, mp.getId());
    	values.put(NAME, mp.getName());
    	values.put(DESCRIPTION, mp.getDescription());
    	values.put(PRICE, mp.getPrice());
    	values.put(DURATION, "2012-01-01");
    	values.put(QTY, mp.getQty());
    	db.insertOrThrow(TABLE_MACHINE_PRODUCT, null, values);
    	db.close();
    }
    
    private void addProductContained(int mId, int pId) {
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(MACHINE_ID, mId);
    	values.put(PRODUCT_ID, pId);
    	db.insertOrThrow(TABLE_PRODUCT_CONTAINED, null, values);
    	db.close();
    }
    
    private void addUsesSingleRecipe(Recipe r) {
    	
    	recProducts = new ArrayList<String>();
		for (String t : r.getProducts()) {
			recProducts.add(t);
		}
		
		/* Definisco una query per ricavare l'id della ricetta data la descrizione */
		SQLiteDatabase db3 = getWritableDatabase();
		String[] COL = {_ID};
		String where = "description = " + "\"" + r.getDescription() + "\"";
		Cursor cursor = db3.query(TABLE_RECIPES, COL, where, null, null, null, null);
		//startManagingCursor(cursor);
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);
			for (int i=0; i<recProducts.size(); i++) {
				/* Definisco una query per ricavare l'id dei prodotti dato il nome */
				String where2 = "name = " + "\"" + recProducts.get(i) + "\"";
				Cursor c2 = db3.query(TABLE_PRODUCTS, COL, where2, null, null, null, null);
				//startManagingCursor(c2);
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
		db3.close();
		
    }
    
    private void addItemSingleOrder (DBOrders o) {
    	
    	ordProducts = new ArrayList<String>();
    	for (String p : o.getProducts()) {
    		ordProducts.add(p);
    	}
    	
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	for (String prod : ordProducts) {
    		values.put(ORDER_ID, o.getId());
    		values.put(ITEM, prod);
    		db.insertOrThrow(TABLE_ORDERS_ITEM, null, values);
    	}
    	db.close();
    }
    
    public ProductList getProducts() {
    	return pl;
    }
}

package org.example.foodappmobile;

import java.io.File;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static android.provider.BaseColumns._ID;
import static org.example.foodappmobile.Constants.*;

public class FoodAppData extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "foodappmobile.db";
	private static final int DATABASE_VERSION = 1;
	
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

}

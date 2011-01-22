package org.example.foodappmobile;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns{
	
	/* Nome della tabella che conterrà i prodotti */
	public static final String TABLE_PRODUCTS = "products";
	/* Colonne della tabella products */
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String PRICE = "price";
	public static final String DURATION = "duration";
	public static final String STOCK_QTY = "stock_qty";
	public static final String STOCK_THRESHOLD = "stock_threshold";
	
	/* Nome della tabella che conterrà le ricette */
	public static final String TABLE_RECIPES = "recipes";
	/* Nome della tabella che conterrà le associazioni fra ricette e prodotti */
	public static final String TABLE_USES = "uses";
	/* Colonne della tabella uses */
	public static final String PRODUCT_ID = "product_id";
	public static final String RECIPE_ID = "recipe_id";
	/* Tabella per la memorizzazione degli ordini */
	public static final String TABLE_ORDERS = "orders";
	public static final String NUMBER = "number";
	public static final String TABLE_ORDERS_ITEM = "orders_item";
	public static final String ORDER_ID = "order_id";
	public static final String ITEM = "item";
	
	public static final String DB_PATH = "/data/data/org.example.foodappmobile/databases/";

}

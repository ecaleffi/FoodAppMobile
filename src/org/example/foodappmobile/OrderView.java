package org.example.foodappmobile;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderView extends LinearLayout implements OnClickListener {
	
	public OrderView(Context context, String result) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		setBackgroundResource(R.color.background);
		
		ProductList prodList = null; //Variabile per salvare la lista di prodotti
		int numProd = 0;	//Variabile per contare il numero di prodotti
		
		String jsonData = result;
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		JSONObject j;
		
		try {
			j = new JSONObject(jsonData);
			ProductList temp = gson.fromJson(j.toString(), ProductList.class);
			prodList = temp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (prodList == null) {
			System.out.println("Errore nella deserializzazione JSON");
		}
		
		for (Product prod : prodList.getProds()) {
			numProd = numProd + 1;
		}
		
		String[] prods = new String[numProd];
		TextView[] prodName = new TextView[numProd];
		Button[] btn = new Button[numProd];
		int i=0;
		for (Product prod : prodList.getProds()) {
			prods[i] = prod.getName();
			i++;
		} 
		
		for (i=0; i < numProd; i++) {
			prodName[i].setText(prods[i]);
			addView(prodName[i]);
			btn[i].setText("Aggiungi al carrello");
			btn[i].setOnClickListener(new Button.OnClickListener(){
				public void onClick (View v) {
					postData();
				}
			});
			addView(btn[i]);
			
		}
	}
	public void onClick (View v) {}
	
	public void postData() {}

}

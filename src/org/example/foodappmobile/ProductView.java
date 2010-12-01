package org.example.foodappmobile;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ProductView extends ScrollView{
	
	public JSONObject jobj;
	public ProductView (Context context, String result) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		setBackgroundResource(R.color.background);
		String jsonData = result;
		String output = "";
		//String jsonstring = "{ 'prods': [ { 'name': 'Parmigiano', 'description': 'desc'}, { 'name': 'Lasagna', 'description': 'lasagna Barilla' } ] }";
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		
		JSONObject j;
				
		try
		{
			j = new JSONObject(jsonData);
			System.out.println(j.toString());
			ProductList temp = gson.fromJson(j.toString(), ProductList.class);
			String res = "";
			for (Product prod : temp.getProds()) {
				System.out.println(prod.getName() + " - " + prod.getDescription()
						+ " - " + prod.getPrice());
				res = res + prod.getName() + ": " + prod.getDescription() + " - Prezzo: " +
					prod.getPrice() + "\n\n";
			}
			output = res;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		TextView tv = new TextView(context);
		tv.setText(output);
		
		this.addView(tv);
	}

}

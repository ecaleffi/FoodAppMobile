package org.example.foodappmobile;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecipeView extends ScrollView{
	
	public JSONObject jobj;
	
	public RecipeView (Context context, String result) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		setBackgroundResource(R.color.background);
		String jsonData = result;
		String output = "";
		
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		
		JSONObject j;
		
		try
		{
			j = new JSONObject(jsonData);
			System.out.println(j.toString());
			RecList rl = gson.fromJson(j.toString(), RecList.class);
			String res = "";
			for (Recipe rec : rl.getRecipes()) {
				System.out.println(rec.getDescription());
				for (int x=0; x < rec.getProducts().size(); x++) {
					System.out.println(rec.getProducts().get(x).toString());
				}
				res = res + rec.getDescription() + "\n" + "Ingredienti: \n";
				for (int x=0; x < rec.getProducts().size(); x++) {
					res = res + "\t" + rec.getProducts().get(x).toString() + "\n";
				}
				res = res + "\n";
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

package org.example.foodappmobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import static org.example.foodappmobile.Constants.*;

public class NearestMachine extends Activity {
	
	ArrayList<Integer> resMachines;
	private FoodAppData fad;
	private LocationManager lm;
	/* Coordinate attuali ottenute dal GPS */
	double Lat;
	double Lng;
	/* Coordinate dei distributori trovati*/
	double[] Mlat;
	double[] Mlng;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        resMachines = b.getIntegerArrayList("distributori");
        
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        /*
        String provider = LocationManager.GPS_PROVIDER;
        Location location = lm.getLastKnownLocation(provider);
        if (location != null) {
        	double lat = location.getLatitude();
        	double lng = location.getLongitude();
        	System.out.println("Latitudine " + lat + ", Longitudine " + lng);
        }
        else {
        	System.out.println("Posizione sconosciuta");
        } */
        
        LocationListener locationListener = new LocationListener() {
        	
        	public void onLocationChanged(Location location) {
        		double geoLat = location.getLatitude();
        		double geoLng = location.getLongitude();
        		System.out.println("posizione: " + geoLat + " , " + geoLng);
        		Lat = geoLat;
        		Lng = geoLng;
        	}
        	
        	public void onProviderDisabled(String provider) {}
        	
        	public void onProviderEnabled(String provider) {}
        	
        	public void onStatusChanged(String provider, int status, Bundle extras) {}
        	
        };
        
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, locationListener);
        
        /* Reperisco dal database le coordinate di latitudine e longitudine dei
         * distributori trovati */
        fad = new FoodAppData(this);
        Mlat = new double[resMachines.size()];
        Mlng = new double[resMachines.size()];
        for (int i = 0; i < resMachines.size(); i++ ) {
        	SQLiteDatabase db = fad.getReadableDatabase();
        	String[] COL = {LATITUDE, LONGITUDE};
        	String where = _ID + " = " + resMachines.get(i);
        	Cursor c = db.query(TABLE_MACHINE, COL, where, null, null, null, null);
        	startManagingCursor(c);
        	while(c.moveToNext()) {
        		String lat = c.getString(0);
        		String lng = c.getString(1);
        		Mlat[i] = Double.parseDouble(lat);
        		Mlng[i] = Double.parseDouble(lng);
        	}
        }
        
        for (int i = 0; i < resMachines.size(); i++ ) {
        	System.out.println("lat: " + Mlat[i] + ", long: " + Mlng[i]);
        }

	}
	

}

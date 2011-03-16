package org.example.foodappmobile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import static org.example.foodappmobile.Constants.*;

public class NearestMachine extends MapActivity {
	
	private MapView mapview;
	ArrayList<Integer> resMachines;
	private FoodAppData fad;
	private LocationManager lm;
	/* Coordinate attuali ottenute dal GPS */
	double Lat;
	double Lng;
	/* Coordinate dei distributori trovati*/
	double[] Mlat;
	double[] Mlng;
	/* Costanti e variabili per la formula della distanza geodetica */
	/* Variabili per salvare le coordinate in radianti */
	double latAlfa;
	double lngAlfa;
	double[] MlatBeta;
	double[] MlngBeta;
	/* Variabili per la formula */
	double[] fi; //Serve per l'angolo compreso fra i due punti
	double[] p; //Salva il terzo lato del triangolo sferico
	double[] dist; //Salva il valore della distanza
	/* Raggio terrestre (in km) */
	double RT = 6371;
	/* Risultati delle distanze */
	double[] resDist;
	
	/* Id, distanza e coordinate del distributore pi vicino */
	int nearest;
	double minDist;
	double latNearest;
	double lngNearest;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapmachine);
        Bundle b = getIntent().getExtras();
        resMachines = b.getIntegerArrayList("distributori");
        
        mapview = (MapView) findViewById(R.id.mapview);
        
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
        		resDist = new double[resMachines.size()];
        		resDist = findDistance();
        		
        		/* Stampa di prova */
        		int i = 1;
        		for (double d : resDist) {
        			System.out.println("Distanza " + i + " = " + d + " km ");
        			i++;
        		}
        		
        		/* Salvo il distributore pi vicino */
        		minDist = resDist[0];
        		nearest = resMachines.get(0);
        		latNearest = Mlat[0];
        		lngNearest = Mlng[0];
        		if (resMachines.size() > 1) {
        			for (int k = 1; k < resMachines.size(); k++) {
        				if (resDist[k] < minDist) {
        					minDist = resDist[k];
        					nearest = resMachines.get(k);
        					latNearest = Mlat[k];
        					lngNearest = Mlng[k];
        				}
        			}
        		}
        		
        		/* Stampa di prova del distributore pi vicino */
        		System.out.println("Distributore pi vicino: id -> " + nearest + 
        				", distanza: " + minDist + " km");
        		
        		GeoPoint srcGeoPoint = new GeoPoint((int) (Lat * 1E6), (int) (Lng * 1E6));
        		GeoPoint destGeoPoint = new GeoPoint((int) (latNearest * 1E6), 
        				(int) (lngNearest * 1E6));
        		DrawPath(srcGeoPoint, destGeoPoint, Color.GREEN, mapview);
        		mapview.getController().animateTo(srcGeoPoint);
        		mapview.getController().setZoom(15);
        		mapview.setBuiltInZoomControls(true);
        		mapview.setClickable(true);
        		
        	}
        	
        	public void onProviderDisabled(String provider) {}
        	
        	public void onProviderEnabled(String provider) {}
        	
        	public void onStatusChanged(String provider, int status, Bundle extras) {}
        	
        };
        
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 50, locationListener);
        
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
	
	/* Metodo per calcolare la distanza geodetica fra la posizione attuale e quella dei
	 * distributori trovati come risultato dall'attivitˆ precedente */
	private double[] findDistance() {
		/* Calcolo il distributore pi vicino con la formula della distanza geodetica */
        /* Inizializzazione variabili: calcolo gli angoli in radianti */
        latAlfa = Math.PI * Lat / 180;
        lngAlfa = Math.PI * Lng / 180;
        MlatBeta = new double[resMachines.size()];
        MlngBeta = new double[resMachines.size()];
        fi = new double[resMachines.size()];
        p = new double[resMachines.size()];
        dist = new double[resMachines.size()];
        for (int i = 0; i < resMachines.size(); i++) {
        	MlatBeta[i] = Math.PI * Mlat[i] / 180;
        	MlngBeta[i] = Math.PI * Mlng[i] / 180;
        	/* Calcola l'angolo compreso fi */
        	fi[i] = Math.abs(lngAlfa - MlngBeta[i]);
        	/* Calcola il terzo lato del triangolo sferico */
        	p[i] = Math.acos(Math.sin(MlatBeta[i]) * Math.sin(latAlfa) + 
        			Math.cos(MlatBeta[i]) * Math.cos(latAlfa) * Math.cos(fi[i]));
        	/* Calcola la distanza sulla superficie terrestre */
        	dist[i] = p[i] * RT;
        }
        return dist;
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	protected boolean isLocationDisplayed() {
		return false;
	}
	
	private void DrawPath(GeoPoint src, GeoPoint dest, int color, MapView mapview) {
		/* Mi connetto al servizio di Google Map */
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=it");
		urlString.append("&saddr="); // Coordinate sorgente
		urlString.append( Double.toString(Lat)); //Latitudine sorgente
		urlString.append(",");
		urlString.append( Double.toString(Lng)); //Longitudine sorgente
		urlString.append("&daddr="); // Coordinate destinazione
		urlString.append( Double.toString(latNearest)); //Latitudine sorgente
		urlString.append(",");
		urlString.append( Double.toString(lngNearest)); //Longitudine sorgente
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		
		/* Prende il documento KML e lo parsa per ottenere le coordinate */
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		
		try {
			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
			
			if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
				String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue();
				String[] pairs = path.split(" ");
				String[] lngLat = pairs[0].split(","); //lngLat[0]=longitude , lngLat[1]=latitude
				
				GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6), (int)
						(Double.parseDouble(lngLat[0])*1E6));
				mapview.getOverlays().add(new MyOverlay(startGP, startGP, 1));
				
				GeoPoint gp1;
				GeoPoint gp2 = startGP;
				for (int i = 1; i<pairs.length; i++) {
					lngLat = pairs[i].split(",");
					gp1 = gp2;
					gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6), (int)
							(Double.parseDouble(lngLat[0])*1E6));
					mapview.getOverlays().add(new MyOverlay(gp1, gp2, 2, color));
				}
				mapview.getOverlays().add(new MyOverlay(dest, dest, 3));
			}
		}
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) { e.printStackTrace(); }
		
	}
	

}

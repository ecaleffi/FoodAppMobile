package org.example.foodappmobile;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpMethods {
	
	String result;
	HttpContext localContext = null;
	HttpResponse resp;
	Cookie mycookie;
	
	public HttpMethods () {}
	
	public String callWebService(String url) {
		
		url = "http://192.168.2.6:3000/" + url;
    	HttpClient httpclient = new DefaultHttpClient();  
		HttpGet request = new HttpGet(url);  
		BasicResponseHandler handler = new BasicResponseHandler(); 
		try {  
			result = httpclient.execute(request, handler);  }
		catch (ClientProtocolException e) {	e.printStackTrace(); } 
		catch (IOException io) { io.printStackTrace();  } 
		httpclient.getConnectionManager().shutdown();
		
		return result;
    		
	}
	
	public HttpResponse postData(String url, List<NameValuePair> nvp, String cookieName, String cookieValue) {
		
		url = "http://192.168.2.6:3000/" + url;
		//Serve per fare in modo che il metodo POST venga gestito tramite la 
		// versione di HTTP 1.1; in questo modo la risposta è molto più performante
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		//Crea un nuovo HttpClient e POST Header
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost(url);
		
		BasicClientCookie ck = new BasicClientCookie(cookieName, cookieValue);
		ck.setPath("/");
		ck.setDomain("192.168.2.6");
		ck.setExpiryDate(null);
		ck.setVersion(0);
		
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(ck);
		
		// Creo un context HTTP locale
		localContext = new BasicHttpContext();
		// Lego il cookie store al context locale
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvp));
			
			// Execute HTTP Post Request
			if (localContext != null) {
				HttpResponse response = httpclient.execute(httppost, localContext);
				resp = response;
			}
			else {
				HttpResponse response = httpclient.execute(httppost);
				resp = response;
			}
		} catch (ClientProtocolException e) {            
		} catch (IOException e) {  }
		
		return resp;
	}
	
	public StructResp postDataLogin (String url, List<NameValuePair> nvp) {
		
		url = "http://192.168.2.6:3000/" + url;
		//Serve per fare in modo che il metodo POST venga gestito tramite la 
		// versione di HTTP 1.1; in questo modo la risposta è molto più performante
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		//Crea un nuovo HttpClient e POST Header
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost(url);
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvp));
			
			HttpResponse response = httpclient.execute(httppost);
			resp = response;
		} catch (ClientProtocolException e) {            
		} catch (IOException e) {  }
		
		StructResp sr = new StructResp();
		sr.setHttpResponse(resp);
		
		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			sr.setMycookie(null);
		} else {
			mycookie = cookies.get(0);
			sr.setMycookie(mycookie);
		}
		
		return sr;
	}
	
	public HttpResponse postDataNoPairs(String url, String cookieName, String cookieValue) {
		
		url = "http://192.168.2.6:3000/" + url;
		
		//Serve per fare in modo che il metodo POST venga gestito tramite la 
		// versione di HTTP 1.1; in questo modo la risposta è molto più performante
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		//Crea un nuovo HttpClient e POST Header
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httppost = new HttpPost(url);
		
		BasicClientCookie ck = new BasicClientCookie(cookieName, cookieValue);
		ck.setPath("/");
		ck.setDomain("192.168.2.6");
		ck.setExpiryDate(null);
		ck.setVersion(0);
		
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(ck);
		
		// Creo un context HTTP locale
		localContext = new BasicHttpContext();
		// Lego il cookie store al context locale
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		try {
			
			// Execute HTTP Post Request
			if (localContext != null) {
				HttpResponse response = httpclient.execute(httppost, localContext);
				resp = response;
			}
			else {
				HttpResponse response = httpclient.execute(httppost);
				resp = response;
			}
		} catch (ClientProtocolException e) {            
		} catch (IOException e) {  }
		
		return resp;
	}
	
}

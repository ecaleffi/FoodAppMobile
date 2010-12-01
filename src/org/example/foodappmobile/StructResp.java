package org.example.foodappmobile;

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;

public class StructResp {
	
	private HttpResponse response;
	private Cookie mycookie;
	
	public StructResp () {}
	
	public HttpResponse getHttpResponse () {
		return response;
	}
	
	public void setHttpResponse (HttpResponse response) {
		this.response = response;
	}
	
	public Cookie getMycookie () {
		return mycookie;
	}
	
	public void setMycookie (Cookie mycookie) {
		this.mycookie = mycookie;
	}
	
}

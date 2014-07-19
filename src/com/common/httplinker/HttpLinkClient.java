package com.common.httplinker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.os.Message;

public class HttpLinkClient {
	private HttpClient httpClient ;
	
	public HttpLinkClient(){
		HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),80));
        schReg.register(new Scheme("https",PlainSocketFactory.getSocketFactory(),433));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params,schReg);
        httpClient = new DefaultHttpClient(conMgr,params);
	}
	
	public String synGet(String baseUrl,List<NameValuePair> nameValuePairs){
		return getConnection(baseUrl, nameValuePairs);
	}	
	public String synPost(String baseUrl,List<NameValuePair> nameValuePairs){
		return postConnection(baseUrl, nameValuePairs);
	}
	public void asyGet(final String baseUrl,final List<NameValuePair> nameValuePairs,final HttpHandler handler){
		new Thread(new Runnable() {
			@Override
			public void run() {
	    		String out = getConnection(baseUrl, nameValuePairs);
	    		if(handler != null)  {
	    			Message msg = new Message();
	    			Bundle b = new Bundle();
	    			b.putString("result", out);
	    			msg.setData(b);
	    			handler.sendMessage(msg);
	    		}
			}
		}).start();
	}	
	
	public void asyPost(final String baseUrl,final List<NameValuePair> nameValuePairs,final HttpHandler handler){
		new Thread(new Runnable() {
			@Override
			public void run() {
	    		String out = postConnection(baseUrl, nameValuePairs);
	    		if(handler != null)  {
	    			Message msg = new Message();
	    			Bundle b = new Bundle();
	    			b.putString("result", out);
	    			msg.setData(b);
	    			handler.sendMessage(msg);
	    		}
			}
		}).start();
	}

	private String getConnection(String baseUrl,List<NameValuePair> nameValuePairs){
    	String url;
    	baseUrl=baseUrl.trim();
    	if(baseUrl.endsWith("?")) baseUrl.substring(0, baseUrl.length()-1);
		if(nameValuePairs != null && nameValuePairs.size()>0){
    		url = baseUrl + "?" + URLEncodedUtils.format(nameValuePairs, "UTF-8");
        }else{
    		url = baseUrl;
    	}

		HttpGet httpGet = new HttpGet(url);
        InputStream inputStream = null;
        String result = null;
        
		try {
			inputStream = httpClient.execute(httpGet).getEntity().getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        String line;result="";
	        while((line = reader.readLine()) != null){
	            result = result + "\n" + line;
	        }
		} catch (Exception e) {
			result = null;
		} finally{
        	try {
        		if(inputStream != null) inputStream.close(); 
        	} catch (IOException e) {}
        }
		
		return result;
	}
	private String postConnection(String baseUrl,List<NameValuePair> nameValuePairs){
    	baseUrl=baseUrl.trim();
		HttpPost httpPost = new HttpPost(baseUrl);
     
        String result = null;
        InputStream inputStream = null;
		try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs==null?new ArrayList<NameValuePair>():nameValuePairs));

	        inputStream = httpClient.execute(httpPost).getEntity().getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        String line;result = "";
	        while((line = reader.readLine()) != null){
	            result = result + line;
	        }
		
		} catch (Exception e) {
			result = null;
		} finally{
        	try {
        		if(inputStream != null) inputStream.close(); 
        	} catch (IOException e) {}
		}
		
		return result;
	}
}

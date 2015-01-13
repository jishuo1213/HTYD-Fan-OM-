package com.htyd.fan.om.util.https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	
	public static String sendHttpRequest(final String address) {
				HttpURLConnection connection = null;
				try {
					Log.d("fanjishuo____sendHttpRequest","URL:" + address);
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if(TextUtils.isEmpty(response)){
						Log.d("fanjishuo____sendHttpRequest","URL:" + address);
						Log.d("fanjishuo____sendHttpRequest", "result:"+response.toString()+"empty");
						return null;
					}else{
						Log.d("fanjishuo____sendHttpRequest","URL:" + address);
						Log.d("fanjishuo____sendHttpRequest", "result:"+response.toString());
						return response.toString();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					Log.i("fanjishuo____sendHttpRequest", "catch ioe");
					return null;
				} 
			 finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
	}


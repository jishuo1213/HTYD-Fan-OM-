package com.htyd.fan.om.util.https;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	
	public static String sendHttpRequest(final String address) {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					Log.i("fanjishuo____sendHttpRequest", address);
					Log.i("fanjishuo____sendHttpRequest", url.toString());
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
						Log.i("fanjishuo___sendHttpRequest", "isEmpty(response)");
						return null;
					}else{
						return response.toString();
					}
				} catch (Exception e) {
					Log.i("fanjishuo___sendHttpRequest", e.getLocalizedMessage());
					return null;
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
	}


package com.htyd.fan.om.util.https;

import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.htyd.fan.om.model.AttendBean;

public class NetOperating {

	/**
	 * 向服务器插入一条签到记录
	 * 
	 * @param context
	 * @param mBean
	 * @return
	 * @throws JSONException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static boolean saveAttendToSever(Context context, AttendBean mBean)
			throws JSONException, InterruptedException, ExecutionException {
		JSONObject param = new JSONObject();
		param.put("KQID", -1);
		param.put("YHID", -1);
		param.put("QDRQ", "2014-11-26 12:12:12");
		param.put("QDJD", 10);
		param.put("QDWD", 10);
		param.put("QDWZ", "jn");
		param.put("TXR", "Fan");
		NameValuePair params = new BasicNameValuePair("Params",
				param.toString());
		String result = "";
		result = HttpHelper.GetResponse(context, Urls.LOGINURL
				+ "Operate=saveKqxx", params);
		if (result.equals("false") || result.length() == 0) {
			return false;
		}
		JSONObject resultJson = new JSONObject(result);
		return resultJson.getBoolean("RESULT");
	}

	public static boolean login(Context context, String userName,
			String password) throws JSONException, InterruptedException,
			ExecutionException {
		JSONObject param = new JSONObject();
		param.put("DLZH", userName);
		param.put("DLMM", password);
		NameValuePair params = new BasicNameValuePair("Params",
				param.toString());
		String result = "";
		result = HttpHelper.GetResponse(context, Urls.LOGINURL
				+ "Operate=login", params);
		Log.i("fanjishuo____login", result);
		if (result.equals("false") || result.length() == 0) {
			return false;
		}
		JSONObject resultJson = new JSONObject(result);
		if(resultJson.getString("YHID") != null && !resultJson.getString("YHID").equals("")){
			return true;
		}
		return resultJson.getBoolean("RESULT");
	}

	public static String getResultFromNet(Context context, JSONObject param,
			String Url, String operate) throws Exception {
		String result = "";
		if (param == null) {
			result = HttpHelper.GetResponse(context, Url + operate);
		} else {
			NameValuePair params = new BasicNameValuePair("Params",
					param.toString());
			result = HttpHelper.GetResponse(context, Url + operate, params);
		}
		Log.i("fanjishuo_____getResultFromNet", result);
		if (result.equals("false") || result.length() == 0) {
			throw new Exception("请求数据失败");
		}
		return result;
	}
}

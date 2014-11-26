package com.htyd.fan.om.attendmanage;

import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.util.https.HttpHelper;
import com.htyd.fan.om.util.https.Urls;

public class AttendNetOperating {

	public static boolean saveAttendToSever(Context context, AttendBean mBean)
			throws JSONException, InterruptedException, ExecutionException {
		JSONObject param = new JSONObject();
		param.put("KQID", -1);
		param.put("YHID", -1);
		param.put("QDRQ","2014-11-26 12:12:12");
		param.put("QDJD", 10);
		param.put("QDWD", 10);
		param.put("QDWZ", "jn");
		param.put("TXR", "Fan");
		NameValuePair params = new BasicNameValuePair("Params",
				param.toString());
		String result = "";
		result = HttpHelper.GetResponse(context, Urls.saveAttendBeanUrl
				+ "Operate=saveKqxx", params);
		if (result.equals("false") || result.length() == 0) {
			return false;
			
		}
		JSONObject resultJson = new JSONObject(result);
		return resultJson.getBoolean("RESULT");
	}
}

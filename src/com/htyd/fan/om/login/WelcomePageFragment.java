package com.htyd.fan.om.login;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htyd.fan.om.R;
import com.htyd.fan.om.main.MainActivity;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class WelcomePageFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.welcome_page, container, false);
		checkAutoLogin();
		return v;
	}

	private void checkAutoLogin() {
		if (Preferences.getIsAutoLogin(getActivity())) {
			startTask(Preferences.getLastLoginAccount(getActivity()),
					Preferences.getLastLoginPassword(getActivity()));
		} else {
			Intent i = new Intent(getActivity(), LoginActivity.class);
			startActivity(i);
			getActivity().finish();
		}
	}

	private class AutoLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			JSONObject resultJson = null;
			
			try {
				resultJson = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (result.length() == 0 || resultJson.has("MESSAGE")) {
				UItoolKit.showToastShort(getActivity(), "登录出错，请重新登录");
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivity(i);
				getActivity().finish();
				stopTask(this);
				return;
			}

			/*if (resultJson.has("MESSAGE")) {
				try {
					UItoolKit.showToastShort(getActivity(),
							resultJson.getString("MESSAGE") + "请重新登录");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivity(i);
				getActivity().finish();
				stopTask(this);
				return;
			}*/
			Preferences.setUserInfo(getActivity(), result);
			try {
				Preferences.setUserId(getActivity(),
						resultJson.getString("YHID"));
				Preferences.setUserName(getActivity(),
						resultJson.getString("YHMC"));
			} catch (NumberFormatException | JSONException e) {
				Log.d("fanjishuo___AutoLoginTask", "catch e");
				e.printStackTrace();
			}
			Intent i = new Intent(getActivity(), MainActivity.class);
			startActivity(i);
			getActivity().finish();
			stopTask(this);
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("DLZH", params[0]);
				param.put("DLMM", params[1]);
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.LOGINURL, "Operate=login");
				return result;
			} catch (JSONException e1) {
				e1.printStackTrace();
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return result;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return result;
			}
		}
	}

	private void startTask(String userName, String password) {
		new AutoLoginTask().execute(userName, password);
	}

	protected void stopTask(AutoLoginTask task) {
		task.cancel(false);
	}
}

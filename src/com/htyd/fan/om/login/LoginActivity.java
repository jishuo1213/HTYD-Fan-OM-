package com.htyd.fan.om.login;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.htyd.fan.om.R;
import com.htyd.fan.om.main.MainActivity;
import com.htyd.fan.om.util.base.DES;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class LoginActivity extends Activity {

	protected EditText userNameEditText, passwordEditText;
	private CheckBox checkBox, checkRemberPwd;
	protected Button loginButton;
	Context context;
	protected String passWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		setContentView(R.layout.activity_login_layout);
		initView();
	}

	private void initView() {
		context = this;
		userNameEditText = (EditText) findViewById(R.id.edit_login_username);
		passwordEditText = (EditText) findViewById(R.id.edit_login_password);
		checkBox = (CheckBox) findViewById(R.id.check_auto_login);
		checkRemberPwd = (CheckBox) findViewById(R.id.check_remember_pasword);
		loginButton = (Button) findViewById(R.id.btn_log_in);
		loginButton.setOnClickListener(LoginListener);
		userNameEditText.setText(Preferences.getLastLoginAccount(getBaseContext()));
		if(Preferences.getIsRemPwd(getBaseContext())){
			passwordEditText.setText(Preferences.getLastLoginPassword(getBaseContext()));
			checkRemberPwd.setChecked(true);
		}
	}

	private OnClickListener LoginListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!checkCanLogin()) {
				return;
			}
			startTask(userNameEditText.getText().toString(), passwordEditText
					.getText().toString());
			loginButton.setEnabled(false);
			passWord = passwordEditText.getText().toString();
			/*
			 * Intent i = new Intent(getBaseContext(),MainActivity.class);
			 * startActivity(i); finish();
			 */
		}
	};

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setTitle("登录");
		actionBar.setDisplayShowHomeEnabled(false);
	}

	protected boolean checkCanLogin() {
		if (TextUtils.isEmpty(userNameEditText.getText())
				|| TextUtils.isEmpty(passwordEditText.getText())) {
			UItoolKit.showToastShort(getBaseContext(), "用户名或密码不能为空");
			return false;
		}
		return true;
	}

	private class LoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				UItoolKit.showToastShort(getBaseContext(), "登录失败，请检查网络");
				stopTask(this);
				loginButton.setEnabled(true);
				return;
			}
			JSONObject resultJson = null;
			try {
				resultJson = new JSONObject(result);
				Preferences.setUserInfo(getBaseContext(), result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resultJson.has("MESSAGE")) {
				try {
					UItoolKit.showToastShort(getBaseContext(),
							resultJson.getString("MESSAGE"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				stopTask(this);
				loginButton.setEnabled(true);
				return;
			}
			try {
				Preferences.setUserId(getBaseContext(),
						resultJson.getString("YHID"));
				Preferences.setUserName(getBaseContext(),
						resultJson.getString("YHMC"));
				Preferences.setLastLoginAccount(getBaseContext(),
						resultJson.getString("DLZH"));
				Preferences.setLastLoginPassword(getBaseContext(),
						DES.encryptDES(passWord, "19911213"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (checkBox.isChecked()) {
				Preferences.setAutoLogin(getBaseContext(), true);
			} else {
				Preferences.setAutoLogin(getBaseContext(), false);
			}
			if (checkRemberPwd.isChecked()) {
				Preferences.setRememberPwd(getBaseContext(), true);
			} else {
				Preferences.setRememberPwd(getBaseContext(), false);
			}
			loginButton.setEnabled(true);
			Intent i = new Intent(getBaseContext(), MainActivity.class);
			startActivity(i);
			finish();
			stopTask(this);
		}

		@Override
		protected String doInBackground(String... params) {

			JSONObject param = new JSONObject();
			try {
				param.put("DLZH", params[0]);
				param.put("DLMM", params[1]);
				return NetOperating.getResultFromNet(getBaseContext(), param,
						Urls.LOGINURL, "Operate=login");
			} catch (JSONException e1) {
				e1.printStackTrace();
				return null;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	protected void startTask(String userName, String password) {
		new LoginTask().execute(userName, password);
	}

	protected void stopTask(LoginTask task) {
		task.cancel(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (checkBox.isChecked()) {
				Preferences.setAutoLogin(getBaseContext(), true);
			} else {
				Preferences.setAutoLogin(getBaseContext(), false);
			}
			if (checkRemberPwd.isChecked()) {
				Preferences.setRememberPwd(getBaseContext(), true);
			} else {
				Preferences.setRememberPwd(getBaseContext(), false);
			}
			finish();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
	
}

package com.htyd.fan.om.login;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.htyd.fan.om.R;
import com.htyd.fan.om.main.MainActivity;

public class LoginActivity extends Activity {
	
	private EditText userNameEditText,passwordEditText;
	private CheckBox checkBox;
	private Button loginButton;
     Context context;
	
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
		checkBox = (CheckBox) findViewById(R.id.check_remember_pasword);
		loginButton = (Button) findViewById(R.id.btn_log_in);
		loginButton.setOnClickListener(LoginListener);
	}

	private OnClickListener LoginListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			/**
			 * 验证用户名和密码
			 */
			Intent i = new Intent(getBaseContext(),MainActivity.class);
			startActivity(i);
			finish();
		}
	};
	
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setTitle("登录");
		actionBar.setDisplayShowHomeEnabled(false);
	}
}

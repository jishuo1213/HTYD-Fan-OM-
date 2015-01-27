package com.htyd.fan.om.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import android.app.Activity;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class TestActivity extends Activity implements TestListener{

	private TextView testStatus,testResult,testError,testFailure;
	private int errorNum,failureNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity_layout);
		Button startTestButton =  (Button) findViewById(R.id.btn_start_test);
		testStatus = (TextView) findViewById(R.id.tv_test_status);
		testResult = (TextView) findViewById(R.id.tv_test_result);
		testError = (TextView) findViewById(R.id.tv_error_result);
		testFailure = (TextView) findViewById(R.id.tv_failure_result);
		final AndroidTestRunner testRunner = new AndroidTestRunner();
		testRunner.setContext(TestActivity.this);
		testRunner.setTest(new OMTestSuite());
		testRunner.addTestListener(this);
		startTestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testRunner.runTest();
			}
		});
	}

	@Override
	public void addError(Test test, Throwable t) { 
		errorNum++;
		testStatus.setText("error");
		testError.setText(errorNum+"-"+t.getMessage()+"/n");
	}

	@Override
	public void addFailure(Test test, AssertionFailedError t) {
		failureNum++;
		testStatus.setText("failure");
		testFailure.setText(failureNum+"-"+t.getMessage()+"/n");
	}

	@Override
	public void endTest(Test test) {
		testStatus.setText("end");
		if(errorNum == 0 && failureNum == 0){
			testResult.setText("成功，无错误");
		}else{
			testResult.setText("有"+errorNum+"个error，"+failureNum+"个failure");
		}
	}

	@Override
	public void startTest(Test test) {
		testStatus.setText("start");
	}

}

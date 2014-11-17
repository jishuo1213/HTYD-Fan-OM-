package com.htyd.fan.om.util.https;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}

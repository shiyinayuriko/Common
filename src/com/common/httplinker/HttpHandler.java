package com.common.httplinker;

import android.os.Handler;
import android.os.Message;

public abstract class HttpHandler extends Handler{
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		String result = msg.getData().getString("result");
		onSucceed(result);
	}
	public abstract void onSucceed(String result);
}

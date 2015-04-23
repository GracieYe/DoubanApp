package com.gracie.douban;

import android.app.Application;
import android.content.Intent;

/**
 * 用软引用存放数据如果依然内存溢出，则重写Application方法(杀手锏)
 * @author Gracie
 *
 */
public class MyApp extends Application{

	//Java虚拟机内存不足时调用的方法
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		//发送广播，关闭掉一些activity service
		Intent intent=new Intent();
		intent.setAction("kill_activity_action");
		sendBroadcast(intent);
	}
}

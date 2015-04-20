package com.gracie.douban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity{

	private TextView versionNumber;
	private LinearLayout mLinearLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);	
		mLinearLayout=(LinearLayout) findViewById(R.id.LinearLayout01);
		versionNumber=(TextView) findViewById(R.id.versionNumber);
		versionNumber.setText(getVersion());
		
		//判断当前网络状态是否可用
		if(isNetworkConnected()){
			//splash做一个动画(透明度)，进入主界面
			AlphaAnimation aa=new AlphaAnimation(0.0f, 1.0f);
			aa.setDuration(2000);
			mLinearLayout.setAnimation(aa);
			mLinearLayout.startAnimation(aa);
			
			//延迟2秒进行r任务
			new Handler().postDelayed(new LoadMainTabTask(), 2000);
		}else{
			showSetNetworkDialog();
		}
	}
	
	private class LoadMainTabTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent=new Intent(SplashActivity.this,MainTabActivity.class);
			startActivity(intent);
			finish();
		}		
	}
	
	private void showSetNetworkDialog() {
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("设置网络");
		builder.setMessage("网络错误检查网络状态");
		builder.setPositiveButton("设置网络", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//跳转到设置网络的页面	,android版本不同时名称不同	
				if(android.os.Build.VERSION.SDK_INT>10){
					startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));  
				}else{
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));  
				}
				finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();				
			}
		});
		
		builder.create().show();
	}

	/**
	 * 获取版本号，以显示在屏幕上
	 * @return
	 */
	private String getVersion(){
		try {
			PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);
			return "Version"+info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Version";
		}
	}
	
	/**
	 * 判断网络状态,注意加入访问网络的权限
	 */
	private boolean isNetworkConnected(){
		ConnectivityManager manager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();

		//知识拓展，判断是否连着wifi		
//		WifiManager wifiManager=(WifiManager) getSystemService(WIFI_SERVICE);
//		wifiManager.isWifiEnabled();//true-连着wifi		
		
		return (info!=null&&info.isConnected());
	}
}

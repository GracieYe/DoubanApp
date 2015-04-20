package com.gracie.douban;

import com.google.gdata.client.douban.DoubanService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity{

	public TextView title; //每个条目的标题
	public RelativeLayout loading; //正在加载的布局
	public DoubanService doubanService;
	public ImageButton mImgBack; //返回按钮
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String apiKey = "0c51c1ba21ad8cfd24f5452e6508a6f7";
		String secret = "359e16e5e5c62b6e";

		doubanService = new DoubanService("黑马小瓣瓣", apiKey,secret);
		
		//把密钥设置给doubanService
		SharedPreferences sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		String accesstoken=sp.getString("accesstoken", null);
		String tokensecret=sp.getString("tokensecret", null);
		doubanService.setAccessToken(accesstoken, tokensecret);
		
		setupView();
		setListener();
		fillData();
	}
	
	public abstract void setupView();
	public abstract void setListener();
	public abstract void fillData();
	
	public void showLoading(){
		//设置加载框可见，并添加渐变动画
		loading.setVisibility(View.VISIBLE);
		AlphaAnimation aa=new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(1000);
		ScaleAnimation sa=new ScaleAnimation(0.0f, 1.0f,0.0f,1.0f);
		sa.setDuration(1000);
						
		AnimationSet set=new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(sa);
						
		loading.setAnimation(set);
		loading.startAnimation(set);
	}
	
	public void hideLoading(){
		AlphaAnimation aa=new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(1000);
		ScaleAnimation sa=new ScaleAnimation(1.0f, 0.0f,1.0f,0.0f);
		sa.setDuration(1000);
				
		AnimationSet set=new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(sa);
				
		loading.setAnimation(set);
		loading.startAnimation(set);
		
		loading.setVisibility(View.INVISIBLE);
		
	}
	
	public void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}	
}

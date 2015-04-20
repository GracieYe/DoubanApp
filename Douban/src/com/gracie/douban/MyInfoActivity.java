package com.gracie.douban;

import com.google.gdata.data.Link;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.UserEntry;
import com.gracie.douban.util.LoadImageAsynTask;
import com.gracie.douban.util.LoadImageAsynTask.LoadImageAsynTaskCallback;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyInfoActivity extends BaseActivity{
	
	private TextView tv_name;
	private TextView tv_location;
	private TextView tv_info;
	private ImageView iv_icon;
	
	String name;
	String location;
	String content;
	String iconUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//保证首先设置相关页面再去调用父类方法，否则找不到相应控件
		setContentView(R.layout.my_info);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setupView() {
		tv_name=(TextView) findViewById(R.id.txtUserName);
		tv_location=(TextView) findViewById(R.id.txtUserAddress);
		tv_info=(TextView) findViewById(R.id.txtUserDescription);
		loading=(RelativeLayout) findViewById(R.id.loading);
		title=(TextView) findViewById(R.id.myTitle);
		mImgBack=(ImageButton) findViewById(R.id.back_button);
		iv_icon=(ImageView) findViewById(R.id.imgUser);
	}

	@Override
	public void setListener() {
		mImgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void fillData() {
		showLoading();
		
		//创建子线程获取数据
		new AsyncTask<Void, Void, Void>() {

			//在异步任务执行之前调用，运行在主线程里
			//可以放初始化UI等方法
			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			//在异步任务(后台任务)执行之后调用，运行在主线程里
			//可以放清理相关的方法
			@Override
			protected void onPostExecute(Void result) {
				hideLoading();
				super.onPostExecute(result);
				
				tv_info.setText(content);
				tv_location.setText(location);
				tv_name.setText(name);
				
				//设置用户头像
				LoadImageAsynTask task=new LoadImageAsynTask(new LoadImageAsynTaskCallback() {
					
					@Override
					public void beforeLoadImage() {
						//设置一个默认头像
						iv_icon.setImageResource(R.drawable.ic_launcher);					
					}
					
					@Override
					public void afterLoadImage(Bitmap bitmap) {
						if(bitmap!=null){
							//说明加载图片成功，则把图片设置成头像
							iv_icon.setImageBitmap(bitmap);
						}else{
							//说明加载失败,则设置一个默认头像
							iv_icon.setImageResource(R.drawable.ic_launcher);
						}					
					}
				});
				task.execute(iconUrl);
			}

			//后台执行的任务，运行在子线程中
			//可执行一些耗时的操作
			@Override
			protected Void doInBackground(Void... params) {
				//从服务器读取用户信息
				try {
					UserEntry ue=doubanService.getAuthorizedUser();
					name=ue.getTitle().getPlainText();
					location=ue.getLocation();
					content = ((TextContent) ue.getContent()).getContent().getPlainText();
					
					//获取用户头像图片所在的地址
					for(Link link:ue.getLinks()){
						if("icon".equals(link.getRel())){
							iconUrl=link.getHref();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

}

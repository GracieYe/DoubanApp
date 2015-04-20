package com.gracie.douban;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainTabActivity extends FragmentActivity{
	private LinearLayout mTabMe;
	private LinearLayout mTabNewBook;
	
	private ImageButton mImgMe;
	private ImageButton mImgNewBook;
	
	private Fragment tabme;
	private Fragment tabbook;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		init();
		setSelected(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_tab_menu, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.main_tab_menu_clear_user:
			SharedPreferences sp=getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor editor=sp.edit();
			editor.putString("accesstoken", "");
			editor.putString("tokensecret", "");
			editor.commit();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void init(){
		mTabMe=(LinearLayout) findViewById(R.id.tab_nav_me);
		mTabNewBook=(LinearLayout) findViewById(R.id.tab_nav_book);
		
		mImgMe=(ImageButton) findViewById(R.id.tab_nav_me_img);
		mImgNewBook=(ImageButton) findViewById(R.id.tab_nav_book_img);
		
		mTabMe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSelected(0);							
			}
		});
		
		mTabNewBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSelected(1);					
			}
		});
	}	
	
	private void setSelected(int i){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch(i){
		case 0:
			tabme=new MeFragment();
			transaction.replace(R.id.id_content, tabme);
			transaction.commit(); 
			mTabMe.setBackgroundResource(R.drawable.tab_main_nav_on);
			mTabNewBook.setBackgroundResource(R.drawable.tab_main_nav_off);
			break;
		case 1:
			tabbook=new NewBookFragment();
			transaction.replace(R.id.id_content, tabbook);
			transaction.commit();
			mTabMe.setBackgroundResource(R.drawable.tab_main_nav_off);
			mTabNewBook.setBackgroundResource(R.drawable.tab_main_nav_on);
			break;
		}
	}
}

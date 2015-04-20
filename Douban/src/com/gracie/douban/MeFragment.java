package com.gracie.douban;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MeFragment extends Fragment{

	private ListView lv;
	private SharedPreferences sp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v= inflater.inflate(R.layout.tabme, container, false);
		lv=(ListView) v.findViewById(R.id.melv);

		sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		String[] values=new String[]{"我读","我听","我评","我看","我的日记","我的资料","小组"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
                R.layout.me_item,R.id.fav_title,values);
		lv.setAdapter(adapter);
		
		//为每个条目添加点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(isUserAuthoroized()){
					//进入到每个条目对应的界面
					switch(position){
					case 0:
						Intent myReadIntent=new Intent(getActivity(),MyReadActivity.class);
						startActivity(myReadIntent);
						break;
					case 5:
						Intent myInfoIntent=new Intent(getActivity(),MyInfoActivity.class);
						startActivity(myInfoIntent);
						break;
					}
				}else{
					//定向到登录界面
					Intent intent=new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}			
			}
		});
		return v;
	}
	
	/**
	 * 判断用户是否获取到了授权
	 * @return
	 */
	private boolean isUserAuthoroized(){
		String accesstoken=sp.getString("accesstoken", null);
		String tokensecret=sp.getString("tokensecret", null);
		
		if(accesstoken==null||tokensecret==null||accesstoken.isEmpty()||tokensecret.isEmpty()){
			return false;
		}else{
			return true;
		}	
	}
}

package com.gracie.douban;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.data.Link;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.Subject;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.extensions.Rating;
import com.gracie.douban.domain.Book;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyReadActivity extends BaseActivity implements OnItemClickListener{

	private ListView subjectList;
	MyReadAdapter adapter;
	Map<String,SoftReference<Bitmap>> iconCache; //key是图片的URL,value是一个软引用类型的Bitmap,把图片缓存在内存中
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.subject);
		super.onCreate(savedInstanceState);
		
		//初始化内存缓存
		iconCache=new HashMap<String,SoftReference<Bitmap>>();
	}
	
	@Override
	public void setupView() {
		loading=(RelativeLayout) findViewById(R.id.loading);
		subjectList=(ListView) findViewById(R.id.subjectlist);
	}

	@Override
	public void setListener() {
		subjectList.setOnItemClickListener(this);		
	}

	@Override
	public void fillData() {
		//通过异步任务获取数据然后显示到页面上
		new AsyncTask<Void, Void, List<Book>>() {
			
			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Book> result) {
				hideLoading();
				super.onPostExecute(result);
				if(result!=null){
					if(adapter==null){
						adapter=new MyReadAdapter(MyReadActivity.this, result);
						subjectList.setAdapter(adapter);
					}
				}else{
					showToast("获取数据失败");
				}
			}

			@Override
			protected List<Book> doInBackground(Void... params) {
				try {
					UserEntry ue=doubanService.getAuthorizedUser();
					String userId=ue.getUid();
					
					//获取用户所以收集的信息
					CollectionFeed feeds=doubanService.getUserCollections(userId, "book", null, null, 1, 4);
					List<Book> books=new ArrayList<Book>();
					
					for(CollectionEntry ce:feeds.getEntries()){
						Subject se = ce.getSubjectEntry();
						Book book = new Book();
						
						if(se!=null){
							
							//设置书名
							String title = se.getTitle().getPlainText();
							book.setName(title);
							
							//设置对于书的描述
							StringBuilder sb = new StringBuilder();
							for (Attribute attr : se.getAttributes()) {
								if ("author".equals(attr.getName())) {
									sb.append(attr.getContent());
									sb.append("/");
								} else if ("publisher".equals(attr.getName())) {
									sb.append(attr.getContent());
									sb.append("/");
								} else if ("pubdate".equals(attr.getName())) {
									sb.append(attr.getContent());
									sb.append("/");
								} else if ("isbn10".equals(attr.getName())) {
									sb.append(attr.getContent());
									sb.append("/");
								}
							}
							book.setDescription(sb.toString());
							
							//设置价格和书的链接(书的具体内容)
							Rating rating = se.getRating();
							if (rating != null) {
								book.setRating(rating.getAverage());

							}
							for (Link link : se.getLinks()) {
								if ("image".equals(link.getRel())) {
									book.setBookUrl(link.getHref());
								}
							}
							
							books.add(book);
						}
					}
					return books;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

}

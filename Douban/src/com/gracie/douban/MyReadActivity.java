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
import com.gracie.douban.util.LoadImageAsynTask;
import com.gracie.douban.util.LoadImageAsynTask.LoadImageAsynTaskCallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyReadActivity extends BaseActivity implements OnItemClickListener{

	private ListView subjectList;
	MyReadAdapter adapter;
	Map<String, SoftReference<Bitmap>> iconCache;
	int startIndex; //获取信息的起始位置
	int count; //每次获取信息的数目
	int max=20;
	boolean isLoading=false;
	IntentFilter filter;
	KillReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.subject);
		super.onCreate(savedInstanceState);
		startIndex=1;
		count=5;
		
		// 初始化内存缓存
		iconCache = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	@Override
	public void setupView() {
		loading=(RelativeLayout) findViewById(R.id.loading);
		subjectList=(ListView) findViewById(R.id.subjectlist);
		
		filter=new IntentFilter();
		filter.addAction("kill_activity_action");
		receiver=new KillReceiver();
		this.registerReceiver(receiver, filter);
	}

	@Override
	public void setListener() {
		subjectList.setOnItemClickListener(this);
		
		//设置滚动监听器
		subjectList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState){
				case OnScrollListener.SCROLL_STATE_IDLE:
					//如果当前滚动状态为静止状态
					//并且最后一个用户可见的条目为适配器中最后一个条目，则表示滚动到最后需要加载新数据
					
					//获取最后一个用户可见条目的位置
					int position=view.getLastVisiblePosition();
					int count=adapter.getCount();
					
					if(position==(count-1)){//代表拖动在了最下方
						//获取新的数据
						startIndex+=count;
						if(startIndex>max){
							showToast("数据已经加载到最大条目");
							return;
						}
						
						if(isLoading){
							return;
						}
						fillData();
					}
					break;
					
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void fillData() {
		//通过异步任务获取数据然后显示到页面上
		new AsyncTask<Void, Void, List<Book>>() {
			
			@Override
			protected void onPreExecute() {
				showLoading();	
				isLoading=true;
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Book> result) {
				hideLoading();
				super.onPostExecute(result);
				if(result!=null){
					if(adapter==null){
						adapter=new MyReadAdapter(result);
						subjectList.setAdapter(adapter);
					}else{
						//把获取的新数据加到适配器中，通知界面更新内容
						adapter.addMoreBook(result);
						
						//通知数据适配器更新数据
						adapter.notifyDataSetChanged();
					}
				}else{
					showToast("获取数据失败");
				}
				isLoading=false;
			}

			@Override
			protected List<Book> doInBackground(Void... params) {
				try {
					UserEntry ue=doubanService.getAuthorizedUser();
					String userId=ue.getUid();
					
					//获取用户所以收集的信息
					CollectionFeed feeds=doubanService.getUserCollections(userId, "book", null, null, startIndex, count);
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
	
	private class MyReadAdapter extends BaseAdapter {

		private List<Book> books;

		public MyReadAdapter(List<Book> books) {
			this.books = books;
		}

		public void addMoreBook(List<Book> books) {
			for (Book book : books) {
				this.books.add(book);
			}
		}

		public int getCount() {

			return books.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return books.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(MyReadActivity.this, R.layout.book_item,
					null);
			final ImageView iv_book = (ImageView) view
					.findViewById(R.id.book_img);
			RatingBar rb = (RatingBar) view.findViewById(R.id.ratingbar);
			TextView tv_title = (TextView) view.findViewById(R.id.book_title);
			TextView tv_description = (TextView) view
					.findViewById(R.id.book_description);
			Book book = books.get(position);
			if (book.getRating() != 0) {
				rb.setRating(book.getRating());
			} else {
				rb.setVisibility(View.INVISIBLE);
			}
			tv_description.setText(book.getDescription());
			tv_title.setText(book.getName());
			// 判断 图片是否在sd卡上存在

			String iconpath = book.getBookUrl();
			final String iconname = iconpath.substring(
					iconpath.lastIndexOf("/") + 1, iconpath.length());
			/*
			 * File file = new File("/sdcard/" + iconname); if (file.exists()) {
			 * iv_book.setImageURI(Uri.fromFile(file));
			 * System.out.println("使用sd卡缓存"); } else {
			 */

			if (iconCache.containsKey(iconname)) {
				SoftReference<Bitmap> softref = iconCache.get(iconname);
				if (softref != null) {
					Bitmap bitmap = softref.get();
					if (bitmap != null) {
						System.out.println("使用内存缓存 ");
						iv_book.setImageBitmap(bitmap);
					} else {
						loadimage(iv_book, book, iconname);
					}

				}

			} else {

				loadimage(iv_book, book, iconname);
			}
			return view;
		}

		private void loadimage(final ImageView iv_book, Book book,final String iconname) {
			
			LoadImageAsynTask task = new LoadImageAsynTask(
					new LoadImageAsynTaskCallback() {
						public void beforeLoadImage() {

							iv_book.setImageResource(R.drawable.book);
						}

						public void afterLoadImage(Bitmap bitmap) {
							if (bitmap != null) {
								System.out.println("下载服务器图片");
								iv_book.setImageBitmap(bitmap);
								/*
								 * // 把bitmap存放到sd卡上 try { File file = new
								 * File("/sdcard/" + iconname); FileOutputStream
								 * stream = new FileOutputStream( file);
								 * bitmap.compress(CompressFormat.JPEG, 100,
								 * stream); } catch (Exception e) {
								 * e.printStackTrace(); }
								 */
								// 把图片存放到内存缓存里面
								iconCache.put(iconname,
										new SoftReference<Bitmap>(bitmap));

							} else {
								iv_book.setImageResource(R.drawable.book);
							}

						}
					});
			task.execute(book.getBookUrl());
		}
	}

	private class KillReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			iconCache=null;
			showToast("内存不足activity退出");
			finish();			
		}		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}

package com.gracie.douban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.gracie.douban.domain.Book;
import com.gracie.douban.util.LoadImageAsynTask;
import com.gracie.douban.util.LoadImageAsynTask.LoadImageAsynTaskCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyReadAdapter extends BaseAdapter{

	private List<Book> books;
	private Context context;
	
	public MyReadAdapter(Context context,List<Book> books) {
		this.context=context;
		this.books = books;
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Object getItem(int position) {	
		return books.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view=View.inflate(context, R.layout.book_item, null);
		
		final ImageView iv_book=(ImageView) view.findViewById(R.id.book_img);
		RatingBar rb=(RatingBar) view.findViewById(R.id.ratingbar);
		TextView tv_title=(TextView) view.findViewById(R.id.book_title);
		TextView tv_description=(TextView) view.findViewById(R.id.book_description);
		
		Book book=books.get(position);
		
		if(book.getRating()!=0){
			rb.setRating(book.getRating());
		}else{
			rb.setVisibility(View.INVISIBLE);
		}
		
		tv_title.setText(book.getName());
		tv_description.setText(book.getDescription());
			
		String iconPath=book.getBookUrl();
		final String iconName=iconPath.substring(iconPath.lastIndexOf("/")+1, iconPath.length());
		File file=new File("/sdcard/Douban/img/"+iconName);
		
		//判断图片是否在SD卡上存在，若存在则不再从网络上下载
		if(file.exists()){
			iv_book.setImageURI(Uri.fromFile(file));
		}
		else{
			LoadImageAsynTask task=new LoadImageAsynTask(new LoadImageAsynTaskCallback() {
				
				@Override
				public void beforeLoadImage() {
					//设置默认图片
					iv_book.setImageResource(R.drawable.book);				
				}
				
				@Override
				public void afterLoadImage(Bitmap bitmap) {
					if(bitmap!=null){
						iv_book.setImageBitmap(bitmap);
						//把下载好的图片存放到SD卡
						try {
							File file=new File("/sdcard/Douban/img/"+iconName);
							FileOutputStream stream=new FileOutputStream(file);
							/**
							 * 第一个参数：保存的格式。第二个参数：压缩率，若为30表示压缩70%; 如果不压缩是100，表示压缩率为0  
							 */
							bitmap.compress(CompressFormat.JPEG,100,stream);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}else{
						iv_book.setImageResource(R.drawable.book);
					}			
				}
			});
			task.execute(book.getBookUrl());}
		return view;
	}

}

package com.gracie.douban.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * 使用异步任务从网络上下载图片
 * 第一个参数是图片下载路径的url
 * 第二个参数是下载的进度
 * 第三个参数是执行完成后的返回值
 * @author Gracie
 *
 */
public class LoadImageAsynTask extends AsyncTask<String, Void, Bitmap>{

	LoadImageAsynTaskCallback loadImageAsynTaskCallback;
	
	
	public LoadImageAsynTask(LoadImageAsynTaskCallback loadImageAsynTaskCallback) {
		super();
		this.loadImageAsynTaskCallback = loadImageAsynTaskCallback;
	}

	public interface LoadImageAsynTaskCallback{
		public void beforeLoadImage();
		public void afterLoadImage(Bitmap bitmap);
	}
	
	/**
	 * 异步任务执行之前调用,做些初始化的操作
	 */
	@Override
	protected void onPreExecute() {
		
		//初始化的操作具体如何实现由调用者实现
		loadImageAsynTaskCallback.beforeLoadImage();
		super.onPreExecute();
	}

	/**
	 * 异步任务执行之后调用
	 */
	@Override
	protected void onPostExecute(Bitmap result) {
		loadImageAsynTaskCallback.afterLoadImage(result);
		super.onPostExecute(result);
	}

	/**
	 * 后台子线程运行的异步任务
	 */
	@Override
	protected Bitmap doInBackground(String... params) {
		String path=params[0];
		
		try {
			URL url=new URL(path);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			InputStream is=conn.getInputStream();
			return BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}

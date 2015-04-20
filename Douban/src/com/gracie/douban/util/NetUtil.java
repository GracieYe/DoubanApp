package com.gracie.douban.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gdata.client.douban.DoubanService;
import com.gracie.douban.R;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetUtil {

	/**
	 * 判断是否需要输入验证码
	 * 需要则返回验证码的ID,不需要则返回空
	 */
	public static String isNeedCaptcha(Context context)throws Exception{
		String loginurl=context.getResources().getString(R.string.loginurl);
		URL url=new URL(loginurl);
		
		URLConnection conn=url.openConnection();
		Source source=new Source(conn);
		List<Element> elements=source.getAllElements("input");
		
		for(Element element:elements){
			String result=element.getAttributeValue("name");
			if("captcha-id".equals(result)){
				return element.getAttributeValue("value");
			}
		}
		return null;
	}
	
	/**
	 * 根据路径下载图片
	 * @param path
	 * @return
	 */
	public static Bitmap getImage(String path)throws Exception{
		URL url=new URL(path);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		InputStream is=conn.getInputStream();
		//把流的内容转化成图片
		return BitmapFactory.decodeStream(is);
	}
	
	public static boolean Login(String name,String pwd,String captcha ,String captchaid, Context context) throws Exception{
		//填写api key,secret. 
		String apiKey = "0c51c1ba21ad8cfd24f5452e6508a6f7";
		String secret = "359e16e5e5c62b6e";

		DoubanService myService = new DoubanService("黑马小瓣瓣", apiKey,
				secret);

		// 2.获取到授权的链接地址 
		String url = myService.getAuthorizationUrl(null);

		//用httpclient 打开 登陆界面  保存登陆成功的cookie http://www.douban.com/accounts/login
		//点击登陆的按钮的时候  实际上是往服务器发送了一个post的信息 
		HttpPost httppost = new HttpPost("http://www.douban.com/accounts/login");
		//设置http post请求提交的数据 
		List<NameValuePair> namevaluepairs  = new ArrayList<NameValuePair>();
		namevaluepairs.add(new BasicNameValuePair("source", "simple"));
		namevaluepairs.add(new BasicNameValuePair("redir", "http://www.douban.com"));
		namevaluepairs.add(new BasicNameValuePair("form_email", name));
		namevaluepairs.add(new BasicNameValuePair("form_password", pwd));
		namevaluepairs.add(new BasicNameValuePair("captcha-solution",captcha));
		namevaluepairs.add(new BasicNameValuePair("captcha-id",captchaid));
		
		namevaluepairs.add(new BasicNameValuePair("user_login", "登录"));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(namevaluepairs,"utf-8");
		httppost.setEntity(entity);
		//  创建一个浏览器 
		DefaultHttpClient client = new DefaultHttpClient();
		// 完成了用户登陆豆瓣的操作 
		HttpResponse response = client.execute(httppost);
	    System.out.println(response.getStatusLine().getStatusCode());
	    Source source  = new Source(response.getEntity().getContent());
	    System.out.println( source.toString());
	       
		// 获取登陆成功的cookie 
	    CookieStore  cookie =  client.getCookieStore();    
	    
	    //带着cookie访问豆瓣认证网站 模拟用户点击 同意按钮 
		
	    HttpPost post1 = new HttpPost(url);

	    String oauth_token =  url.substring(url.lastIndexOf("=")+1, url.length()); 
	    System.out.println(oauth_token);
	    List<NameValuePair> namevaluepairs1  = new ArrayList<NameValuePair>();
	    namevaluepairs1.add(new BasicNameValuePair("ck","Rw1e"));
	    namevaluepairs1.add(new BasicNameValuePair("oauth_token",oauth_token));
	    namevaluepairs1.add(new BasicNameValuePair("oauth_callback",""));
	    namevaluepairs1.add(new BasicNameValuePair("ssid","9d9af9b0"));
	    namevaluepairs1.add(new BasicNameValuePair("confirm","同意"));
	    UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(namevaluepairs1,"utf-8");
	    post1.setEntity(entity1);
	    DefaultHttpClient client2 = new DefaultHttpClient();
	    client2.setCookieStore(cookie);
	    HttpResponse  respose1 =   client2.execute(post1);
	    InputStream is = respose1.getEntity().getContent();
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len= 0;
	    while((len = is.read(buffer))!=-1){
	    	bos.write(buffer, 0, len);
	    }
	    is.close();
	    System.out.println(new String( bos.toByteArray()));
	    
		//3. 获取到授权后的令牌和密钥
		ArrayList<String>  tokens = myService.getAccessToken();
		String accesstoken = tokens.get(0);
		String tokensecret = tokens.get(1);
		System.out.println(accesstoken);
		System.out.println(tokensecret);
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("accesstoken", accesstoken);
		editor.putString("tokensecret", tokensecret);
		editor.commit();
		return true;
	}
}

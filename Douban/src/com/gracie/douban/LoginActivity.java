package com.gracie.douban;

import com.gracie.douban.util.NetUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	protected static final int NEED_CAPTCHA = 10;
	protected static final int NOT_NEED_CAPTCHA = 11;
	protected static final int GET_CAPTCHA_ERROR = 12;
	protected static final int LOGIN_SUCCESS = 13;
	protected static final int LOGIN_FAIL = 14;
	
	private EditText emial;
	private EditText psw;
	private Button loginBtn;
	private Button existBtn;
	private LinearLayout captchaLinearLayout;
	private TextView captchaValue;
	private ImageView captchaImg;
	
	ProgressDialog pd;
	String result=null;
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			pd.dismiss();
			
			switch(msg.what){
			case NEED_CAPTCHA:
				captchaLinearLayout.setVisibility(View.VISIBLE);
				Bitmap bitmap=(Bitmap) msg.obj;
				captchaImg.setImageBitmap(bitmap);
				break;
			case NOT_NEED_CAPTCHA:
				break;
			case GET_CAPTCHA_ERROR:
				Toast.makeText(getApplicationContext(), "查询验证码失败", Toast.LENGTH_SHORT).show();
				break;
			case LOGIN_SUCCESS:
				finish();
				break;
			case LOGIN_FAIL:
				Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
				break;
			}
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		init();
		setListener();
	}
	
	private void init(){
		emial=(EditText) findViewById(R.id.EditTextEmail);
		psw=(EditText) findViewById(R.id.EditTextPassword);
		loginBtn=(Button) findViewById(R.id.btnLogin);
		existBtn=(Button) findViewById(R.id.btnExit);
		captchaLinearLayout=(LinearLayout) findViewById(R.id.ll_captcha);
		captchaValue=(TextView) findViewById(R.id.EditTextCaptchaValue);
		captchaImg=(ImageView) findViewById(R.id.ImageViewCaptcha);
		
		getCaptcha();
	}

	private void getCaptcha() {
		pd=new ProgressDialog(this);
		pd.setMessage("正在查询验证码");
		pd.show();
		//判断是否需要输入验证码
		new Thread(){
			public void run() {
				try {
					result = NetUtil.isNeedCaptcha(getApplicationContext());
					if(result!=null){						
						//下载验证码获取所对应的图片
						String imgPath=getResources().getString(R.string.captchaurl)+result+"&amp;size=s";
						Bitmap bitmap=NetUtil.getImage(imgPath);
						Message msg=new Message();
						msg.what=NEED_CAPTCHA;
						msg.obj=bitmap;
						handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						msg.what=NOT_NEED_CAPTCHA;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {				
					e.printStackTrace();
					Message msg=new Message();
					msg.what=GET_CAPTCHA_ERROR;
					handler.sendMessage(msg);
				}
				
				
			};
		}.start();
	}
	
	private void setListener(){
		loginBtn.setOnClickListener(this);
		existBtn.setOnClickListener(this);
		captchaImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			final String name = emial.getText().toString();
			final String pwd = psw.getText().toString();
			if("".equals(name)||"".equals(pwd)){
				Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				if(result!=null){
					//表明需要输入验证码
					final String captchavalue = captchaValue.getText().toString();
					if("".equals(captchavalue)){
						Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
						return ;
					}else{
						login(name,pwd,captchavalue);
					}
				}else{
					login(name,pwd,"");
				}
			}
			break;
		case R.id.btnExit:
			finish();
			break;
		case R.id.ImageViewCaptcha:
			getCaptcha();
			break;
		}
		
	}
	
	private void login(final String name,final String pwd,final String captchavalue){
		pd.setMessage("正在登录");
		pd.show();
		new Thread(){
			
			@Override
			public void run() {
				try {
					boolean flag=NetUtil.Login(name, pwd, captchavalue, result, getApplicationContext());
					if(flag){
						Message msg=new Message();
						msg.what=LOGIN_SUCCESS;
						handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						msg.what=LOGIN_FAIL;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
					msg.what=LOGIN_FAIL;
					handler.sendMessage(msg);
				}
				
			};
		}.start();
	}
}

package com.gracie.douban;

import java.util.ArrayList;
import java.util.List;

import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.NoteFeed;
import com.google.gdata.data.douban.UserEntry;
import com.gracie.douban.domain.Note;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyNoteActivity extends BaseActivity implements OnClickListener{

	private ListView subjectList;
	private Button bt_next,bt_pre;
	private int startIndex=1;
	private int count=10;
	private boolean isloading=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_note);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void setupView() {
		loading=(RelativeLayout) findViewById(R.id.loading);
		subjectList=(ListView) findViewById(R.id.subjectlist);
		bt_next=(Button) findViewById(R.id.bt_next);
		bt_pre=(Button)findViewById(R.id.bt_pre);
	}

	@Override
	public void setListener() {
		bt_next.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
	}
	
	@Override
	public void fillData() {
		if(isloading){
			showToast("正在下载数据中");
			return;
		}
		
		new AsyncTask<Void, Void, List<Note>>() {

			@Override
			protected void onPreExecute() {
				showLoading();
				isloading = true;
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Note> result) {
				hideLoading();
				super.onPostExecute(result);
				if (result != null) {
					// 设置到数据适配器里面
					MyNoteAdapter adapter = new MyNoteAdapter(result);
					subjectList.setAdapter(adapter);
				} else {
					showToast("下载数据发生异常");
				}
				isloading =false;
			}

			@Override
			protected List<Note> doInBackground(Void... params) {
				try {
					UserEntry ue = doubanService.getAuthorizedUser();
					String uid = ue.getUid();
					// 首先获取用户的 所有收集的信息
					NoteFeed noteFeed = doubanService.getUserNotes(uid, startIndex,
							count);
					List<Note> notes = new ArrayList<Note>();
					for (NoteEntry ne : noteFeed.getEntries()) {
						Note note = new Note();
//						note.setNoteEntry(ne);
						
						if (ne.getContent() != null){
							note.setContent(((TextContent) ne.getContent()).getContent().getPlainText());
						}		
						note.setTitle(ne.getTitle().getPlainText());

						for (Attribute attr : ne.getAttributes()) {

							if ("can_reply".equals(attr.getName())) {
								note.setCan_reply(attr.getContent());
							} else if ("privacy".equals(attr.getName())) {
								note.setPrivacy(attr.getContent());
							}
						}
						note.setPubDate(ne.getPublished().toString());
						notes.add(note);
					}
					return notes;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}.execute();

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_next:
			startIndex+=count;
			if(startIndex>100){
				showToast("最多获取一百条");
			}
			break;
		case R.id.bt_pre:
			if(startIndex>(count+1)){
				startIndex-=count;
				fillData();
			}else{
				showToast("已经是第一页");
			}
			break;
		}		
	}
	
	private class MyNoteAdapter extends BaseAdapter{
		
		private List<Note> userNotes;
		
		public MyNoteAdapter(List<Note> userNotes){
			this.userNotes=userNotes;
		}
		
		@Override
		public int getCount() {
			return userNotes.size();
		}

		@Override
		public Object getItem(int position) {
			return userNotes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			
			if(convertView==null){
				view=View.inflate(getApplicationContext(), R.layout.note_item, null);
			}else{
				view=convertView;
			}
			
			TextView tv=(TextView) view.findViewById(R.id.fav_title);
			tv.setText(userNotes.get(position).getTitle());
		
			return view;
		}
		
	}
}

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyNoteActivity extends BaseActivity implements OnClickListener{

	private ListView subjectList;
	private Button bt_next,bt_pre;
	
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
		new AsyncTask<Void, Void, List<Note>>() {
			
			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Note> result) {
				hideLoading();
				super.onPostExecute(result);
			}

			@Override
			protected List<Note> doInBackground(Void... params) {
				try {
					UserEntry ue = doubanService.getAuthorizedUser();
					String uid = ue.getUid();
					// 首先获取用户的 所有收集的信息
					NoteFeed noteFeed = doubanService.getUserNotes(uid, 1,10);
					List<Note> notes = new ArrayList<Note>();
					for (NoteEntry ne : noteFeed.getEntries()) {
						Note note=new Note();
						if(ne.getContent()!=null){
							note.setContent(((TextContent) ne.getContent()).getContent().getPlainText());
							note.setTitle(ne.getTitle().getPlainText());
							
							for(Attribute attr:ne.getAttributes()){
								if("can_reply".equals(attr.getName())){
									note.setCan_reply(attr.getContent());
								}else if("privacy".equals(attr.getName())){
									note.setPrivacy(attr.getContent());
								}
							}
						}
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
			break;
		case R.id.bt_pre:
			break;
		}		
	}
}

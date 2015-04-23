package com.gracie.douban.domain;

public class Note {
	
	private String title; //日记标题
	private String content; //日记内容
	private String can_reply; //回复数量
	private String privacy; //权限
	private String pubDate; //发表时间
	
	public Note(){}
	
	public Note(String title, String content, String can_reply, String privacy,
			String pubDate) {
		this.title = title;
		this.content = content;
		this.can_reply = can_reply;
		this.privacy = privacy;
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCan_reply() {
		return can_reply;
	}

	public void setCan_reply(String can_reply) {
		this.can_reply = can_reply;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
}

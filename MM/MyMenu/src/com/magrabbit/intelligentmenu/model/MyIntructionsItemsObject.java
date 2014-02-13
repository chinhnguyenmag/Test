package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

public class MyIntructionsItemsObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int mId;

	public String mName;

	public String mTime;

	public String mYoutubeId;

	public MyIntructionsItemsObject(int mId, String mName, String mTime,
			String youtubeId) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mTime = mTime;
		this.mYoutubeId = youtubeId;
	}

	public MyIntructionsItemsObject() {
	}

	public String getYoutubeId() {
		return mYoutubeId;
	}

	public void setYoutubeId(String mYoutubeId) {
		this.mYoutubeId = mYoutubeId;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}
}

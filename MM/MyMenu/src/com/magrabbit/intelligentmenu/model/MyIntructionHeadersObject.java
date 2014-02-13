package com.magrabbit.intelligentmenu.model;

public class MyIntructionHeadersObject {

	private int mId;

	private int mIcon;

	private String mName;


	public MyIntructionHeadersObject(int mId, int mIcon, String mName) {
		this.mId = mId;
		this.mIcon = mIcon;
		this.mName = mName;
	}

	public MyIntructionHeadersObject() {
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getIcon() {
		return mIcon;
	}

	public void setIcon(int mIcon) {
		this.mIcon = mIcon;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}
}

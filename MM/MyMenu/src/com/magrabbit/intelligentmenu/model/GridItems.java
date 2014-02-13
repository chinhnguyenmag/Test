package com.magrabbit.intelligentmenu.model;


public class GridItems {

	private int mId;
	private String mImage;
	private boolean mIsSelect;

	public GridItems(int mId, String mImage,boolean mIsSelect) {
		this.mId = mId;
		this.mImage = mImage;
		this.mIsSelect=mIsSelect;
	}
	public GridItems(int mId, String mImage) {
		this.mId = mId;
		this.mImage = mImage;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getImage() {
		return mImage;
	}

	public void setImage(String mImage) {
		this.mImage = mImage;
	}

	public boolean ismIsSelect() {
		return mIsSelect;
	}

	public void setmIsSelect(boolean mIsSelect) {
		this.mIsSelect = mIsSelect;
	}
}

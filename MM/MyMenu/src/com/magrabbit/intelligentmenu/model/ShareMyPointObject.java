package com.magrabbit.intelligentmenu.model;

public class ShareMyPointObject {
	private int mLogo;
	private String mNameRestaurant;
	private int mPoints;
	private String mDateMostRecent;
	private boolean mSelected;

	public ShareMyPointObject() {
		super();
	}

	public ShareMyPointObject(int mLogo, String mNameRestaurant, int mPoints,
			String mDateMostRecent, boolean mSelected) {
		super();
		this.mLogo = mLogo;
		this.mNameRestaurant = mNameRestaurant;
		this.mPoints = mPoints;
		this.mDateMostRecent = mDateMostRecent;
		this.mSelected = mSelected;
	}

	public int getLogo() {
		return mLogo;
	}

	public void setLogo(int mLogo) {
		this.mLogo = mLogo;
	}

	public String getNameRestaurant() {
		return mNameRestaurant;
	}

	public void setNameRestaurant(String mNameRestaurant) {
		this.mNameRestaurant = mNameRestaurant;
	}

	public int getPoints() {
		return mPoints;
	}

	public void setPoints(int mPoints) {
		this.mPoints = mPoints;
	}

	public String getDateMostRecent() {
		return mDateMostRecent;
	}

	public void setDateMostRecent(String mDateMostRecent) {
		this.mDateMostRecent = mDateMostRecent;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean mSelected) {
		this.mSelected = mSelected;
	}

}

package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class RestaurantGlobalEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("id")
	public int mId;
	@SerializedName("name")
	public String mRestaurantName;
	@SerializedName("total_point")
	public int mTotalPoint;
	@SerializedName("most_recent")
	public String mDate;
	@SerializedName("logo")
	public String mLogo;
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public String getRestaurantName() {
		if(this.mRestaurantName == null)
			return "";
		else			
			return this.mRestaurantName;
	}
	public void setRestaurantName(String mRestaurantName) {
		this.mRestaurantName = mRestaurantName;
	}
	public int getTotalPoint() {
		return mTotalPoint;
	}
	public void setTotalPoint(int mTotalPoint) {
		this.mTotalPoint = mTotalPoint;
	}
	public String getDate() {
		if(this.mDate == null)
			return "";
		else			
			return this.mDate;
	}
	public void setDate(String mDate) {
		this.mDate = mDate;
	}
	public String getLogo() {
		if(this.mLogo == null)
			return "";
		else			
			return this.mLogo;
	}
	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}
	
	
	
}

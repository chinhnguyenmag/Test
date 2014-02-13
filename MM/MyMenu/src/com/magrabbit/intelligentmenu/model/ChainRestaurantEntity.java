package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ChainRestaurantEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("id")
	private int  mId;
	@SerializedName("name")
	private String mChainName;
	@SerializedName("address")
	private String mAddress;
	@SerializedName("city")
	private String mCity;
	@SerializedName("state")
	private String mState;
	@SerializedName("country")
	private String mCountry;
	@SerializedName("zip")
	private String mZip;
	@SerializedName("total")
	private int mPoint;
	@SerializedName("most_recent")
	private String mDate;
	@SerializedName("logo")
	private String mLogo;
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public String getChainName() {
		if(this.mChainName == null)
			return "";
		else			
			return this.mChainName;
	}
	public void setChainName(String mChainName) {
		this.mChainName = mChainName;
	}
	public String getAddress() {
		if(this.mAddress == null)
			return "";
		else			
			return this.mAddress;
	}
	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	public String getCity() {
		if(this.mCity == null)
			return "";
		else			
			return this.mCity;
	}
	public void setCity(String mCity) {
		this.mCity = mCity;
	}
	public String getState() {
		if(this.mState == null)
			return "";
		else			
			return this.mState;
	}
	public void setState(String mState) {
		this.mState = mState;
	}
	public String getCountry() {
		if(this.mCountry == null)
			return "";
		else			
			return this.mCountry;
	}
	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}
	public String getZip() {
		if(this.mZip == null)
			return "";
		else			
			return this.mZip;
	}
	public void setZip(String mZip) {
		this.mZip = mZip;
	}
	public int getPoint() {
		return mPoint;
	}
	public void setPoint(int mPoint) {
		this.mPoint = mPoint;
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

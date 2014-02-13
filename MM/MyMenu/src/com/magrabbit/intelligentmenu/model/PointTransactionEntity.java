package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PointTransactionEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("id")
	private int mId;
	@SerializedName("user_id")
	private int mUserId;
	@SerializedName("point_type")
	private String mPointType;
	@SerializedName("points")
	private int mPointNumber;
	@SerializedName("status")
	private int mStatus;// =1: add point and =2: sub point
	@SerializedName("updated_at")
	private String mDateUpdated;
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public int getUserId() {
		return mUserId;
	}
	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}
	public String getPointType() {
		if(this.mPointType == null)
			return "";
		else 
			return this.mPointType;
	}
	public void setPointType(String mPointType) {
		this.mPointType = mPointType;
	}
	public int getPointNumber() {
		return mPointNumber;
	}
	public void setPointNumber(int mPointNumber) {
		this.mPointNumber = mPointNumber;
	}
	public int getStatus() {
		return mStatus;
	}
	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}
	public String getDateUpdated() {
		if(this.mDateUpdated == null)
			return "";
		else 
			return this.mDateUpdated;
	}
	public void setDateUpdated(String mDateUpdated) {
		this.mDateUpdated = mDateUpdated;
	}
	
	

}

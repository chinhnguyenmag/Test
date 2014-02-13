package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyPointRestaurantListPointEntity {

	@SerializedName("id")
	public int mId;

	@SerializedName("user_id")
	public int mUserId;

	@SerializedName("point_type")
	public String mPointType;

	@SerializedName("points")
	public float mPoint;

	@SerializedName("most_recent")
	public String mMostRecent;

	@SerializedName("status")
	public int mStatus;

	public String getPointType() {
		if (mPointType != null) {
			return mPointType;
		} else
			return "";
	}

	public String getMostRecent() {
		if (mMostRecent != null) {
			return mMostRecent;
		} else
			return "";
	}

	public void setPointType(String mPointType) {
		this.mPointType = mPointType;
	}

	public void setMostRecent(String mMostRecent) {
		this.mMostRecent = mMostRecent;
	}

}

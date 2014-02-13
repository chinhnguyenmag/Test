package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserGlobalEntity implements Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("points")
	private float mPointNumber;

	@SerializedName("dinner_status")
	private String mDinnerStatus;

	public float getPointNumber() {
		return mPointNumber;
	}

	public void setPointNumber(float mPointNumber) {
		this.mPointNumber = mPointNumber;
	}

	public String getStatus() {
		if (this.mDinnerStatus == null)
			return "";
		else
			return this.mDinnerStatus;
	}

	public void setStatus(String mStatus) {
		this.mDinnerStatus = mStatus;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPointNumber + "");
		dest.writeString(mDinnerStatus);
	}

}

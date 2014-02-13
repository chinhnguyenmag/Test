package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyPointListLocationEntity implements Parcelable {

	@SerializedName("chain_name")
	public String mChainName = "";

	@SerializedName("total_point")
	public float mTotalPoint;

	@SerializedName("logo")
	public String mLogo = "";

	public String getChainName() {
		if (mChainName != null) {
			return mChainName;
		} else
			return "";
	}

	public String getLogo() {
		if (mLogo != null) {
			return mLogo;
		} else
			return "";
	}

	public void setChainName(String mChainName) {
		this.mChainName = mChainName;
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public static final Parcelable.Creator<MyPointListLocationEntity> CREATOR = new Parcelable.Creator<MyPointListLocationEntity>() {

		@Override
		public MyPointListLocationEntity createFromParcel(Parcel source) {
			return new MyPointListLocationEntity(source);
		}

		@Override
		public MyPointListLocationEntity[] newArray(int size) {
			return new MyPointListLocationEntity[size];
		}
	};

	public MyPointListLocationEntity() {
	}

	public MyPointListLocationEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mChainName);
		dest.writeFloat(mTotalPoint);
		dest.writeString(mLogo);
	}

	public void readFromParcel(Parcel source) {
		mChainName = source.readString();
		mTotalPoint = source.readFloat();
		mLogo = source.readString();
	}
}

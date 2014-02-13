package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyNotificationChainListEntity implements Parcelable {

	@SerializedName("location_id")
	public int mLocationId;

	@SerializedName("name")
	public String mName;

	@SerializedName("address")
	public String mAddress;

	@SerializedName("city")
	public String mCity;

	@SerializedName("state")
	public String mState;

	@SerializedName("zip")
	public String mZip;

	@SerializedName("logo")
	public String mLogo;

	@SerializedName("unread")
	public int mUnread;

	public String getName() {
		if (mName != null) {
			return mName;
		} else
			return "";
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getAddress() {
		if (mAddress != null) {
			return mAddress;
		} else
			return "";
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getCity() {
		if (mCity != null) {
			return mCity;
		} else
			return "";
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getState() {
		if (mState != null) {
			return mState;
		} else
			return "";
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public String getZip() {
		if (mZip != null) {
			return mZip;
		} else
			return "";
	}

	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	public String getLogo() {
		if (mLogo != null) {
			return mLogo;
		} else
			return "";
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public static final Parcelable.Creator<MyNotificationChainListEntity> CREATOR = new Parcelable.Creator<MyNotificationChainListEntity>() {

		@Override
		public MyNotificationChainListEntity createFromParcel(Parcel source) {
			return new MyNotificationChainListEntity(source);
		}

		@Override
		public MyNotificationChainListEntity[] newArray(int size) {
			return new MyNotificationChainListEntity[size];
		}
	};

	public MyNotificationChainListEntity() {
	}

	public MyNotificationChainListEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mLocationId);
		dest.writeString(mName);
		dest.writeString(mAddress);
		dest.writeString(mCity);
		dest.writeString(mState);
		dest.writeString(mZip);
		dest.writeString(mLogo);
		dest.writeInt(mUnread);
	}

	public void readFromParcel(Parcel source) {
		mLocationId = source.readInt();
		mName = source.readString();
		mAddress = source.readString();
		mCity = source.readString();
		mState = source.readString();
		mZip = source.readString();
		mLogo = source.readString();
		mUnread = source.readInt();
	}

}

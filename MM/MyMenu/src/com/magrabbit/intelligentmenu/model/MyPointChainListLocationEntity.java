package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointChainListLocationEntity implements Parcelable {

	@SerializedName("id")
	public int mId;

	@SerializedName("name")
	public String mName;

	@SerializedName("address")
	public String mAddress;

	@SerializedName("city")
	public String mCity;

	@SerializedName("state")
	public String mState;

	@SerializedName("country")
	public String mCountry;

	@SerializedName("zip")
	public String mZip;

	@SerializedName("total")
	public float mTotal;

	@SerializedName("most_recent")
	public String mMostRecent;

	@SerializedName("logo")
	public String mLogo;
	
	

	public String getName() {
		return mName;
	}

	public String getAddress() {
		return mAddress;
	}

	public String getCity() {
		return mCity;
	}

	public String getState() {
		return mState;
	}

	public String getCountry() {
		return mCountry;
	}

	public String getZip() {
		return mZip;
	}

	public String getMostRecent() {
		return mMostRecent;
	}

	public String getLogo() {
		return mLogo;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	public void setMostRecent(String mMostRecent) {
		this.mMostRecent = mMostRecent;
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public static final Parcelable.Creator<MyPointChainListLocationEntity> CREATOR = new Parcelable.Creator<MyPointChainListLocationEntity>() {

		@Override
		public MyPointChainListLocationEntity createFromParcel(Parcel source) {
			return new MyPointChainListLocationEntity(source);
		}

		@Override
		public MyPointChainListLocationEntity[] newArray(int size) {
			return new MyPointChainListLocationEntity[size];
		}
	};

	public MyPointChainListLocationEntity() {
	}

	public MyPointChainListLocationEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mName);
		dest.writeString(mAddress);
		dest.writeString(mCity);
		dest.writeString(mState);
		dest.writeString(mCountry);
		dest.writeString(mZip);
		dest.writeFloat(mTotal);
		dest.writeString(mMostRecent);
		dest.writeString(mLogo);

	}

	public void readFromParcel(Parcel source) {
		mId = source.readInt();
		mName = source.readString();
		mAddress = source.readString();
		mCity = source.readString();
		mState = source.readString();
		mCountry = source.readString();
		mZip = source.readString();
		mTotal = source.readFloat();
		mMostRecent = source.readString();
		mLogo = source.readString();
	}
}

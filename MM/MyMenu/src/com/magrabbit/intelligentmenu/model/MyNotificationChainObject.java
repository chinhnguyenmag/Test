package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vule
 *
 */
public class MyNotificationChainObject implements Parcelable {

	private int mLogo;

	private String mChainName;

	private String mStreet;

	private String mCountry;

	private String mCity;

	private String mState;

	private int mZipCode;

	private int mTotalMsg;

	public MyNotificationChainObject(int mLogo, String mChainName,
			String mStreet, String mCountry, String mCity, String mState,
			int mZipCode, int totalMsg) {
		super();
		this.mLogo = mLogo;
		this.mChainName = mChainName;
		this.mStreet = mStreet;
		this.mCountry = mCountry;
		this.mCity = mCity;
		this.mState = mState;
		this.mZipCode = mZipCode;
		this.mTotalMsg = totalMsg;
	}

	public static final Parcelable.Creator<MyNotificationChainObject> CREATOR = new Parcelable.Creator<MyNotificationChainObject>() {

		@Override
		public MyNotificationChainObject createFromParcel(Parcel source) {
			return new MyNotificationChainObject(source);
		}

		@Override
		public MyNotificationChainObject[] newArray(int size) {
			return new MyNotificationChainObject[size];
		}
	};

	public MyNotificationChainObject() {
	}

	public MyNotificationChainObject(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mLogo);
		dest.writeString(mChainName);
		dest.writeString(mStreet);
		dest.writeString(mCountry);
		dest.writeString(mCity);
		dest.writeString(mState);
		dest.writeInt(mZipCode);
		dest.writeInt(mTotalMsg);
	}

	public void readFromParcel(Parcel source) {
		mLogo = source.readInt();
		mChainName = source.readString();
		mStreet = source.readString();
		mCountry = source.readString();
		mCity = source.readString();
		mState = source.readString();
		mZipCode = source.readInt();
		mTotalMsg = source.readInt();
	}

	public int getTotakMsg() {
		return mTotalMsg;
	}

	public void setTotakMsg(int mTotakMsg) {
		this.mTotalMsg = mTotakMsg;
	}

	public int getLogo() {
		return mLogo;
	}

	public void setLogo(int mLogo) {
		this.mLogo = mLogo;
	}

	public String getChainName() {
		return mChainName;
	}

	public void setChainName(String mChainName) {
		this.mChainName = mChainName;
	}

	public String getStreet() {
		return mStreet;
	}

	public void setStreet(String mStreet) {
		this.mStreet = mStreet;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getState() {
		return mState;
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public int getZipCode() {
		return mZipCode;
	}

	public void setZipCode(int mZipCode) {
		this.mZipCode = mZipCode;
	}

}

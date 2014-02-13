package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vule
 *
 */
public class MyFavoriteChainObject implements Parcelable {

	private int mId;

	private int mlogo;

	private String mName;

	private String mStreet;

	private String mCity;

	private String mState;

	private String mZipcode;

	private int mNumberFavorite;

	private String mMostRecent;

	public MyFavoriteChainObject() {
	}

	public MyFavoriteChainObject(int mId, int mlogo, String mName,
			String mStreet, String mCity, String mState, String mZipcode,
			int mNumberFavorite, String mMostRecent) {
		super();
		this.mId = mId;
		this.mlogo = mlogo;
		this.mName = mName;
		this.mStreet = mStreet;
		this.mCity = mCity;
		this.mState = mState;
		this.mZipcode = mZipcode;
		this.mNumberFavorite = mNumberFavorite;
		this.mMostRecent = mMostRecent;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getlogo() {
		return mlogo;
	}

	public void setlogo(int mlogo) {
		this.mlogo = mlogo;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getStreet() {
		return mStreet;
	}

	public void setStreet(String mStreet) {
		this.mStreet = mStreet;
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

	public String getZipcode() {
		return mZipcode;
	}

	public void setZipcode(String mZipcode) {
		this.mZipcode = mZipcode;
	}

	public int getNumberFavorite() {
		return mNumberFavorite;
	}

	public void setNumberFavorite(int mNumberFavorite) {
		this.mNumberFavorite = mNumberFavorite;
	}

	public String getMostRecent() {
		return mMostRecent;
	}

	public void setMostRecent(String mMostRecent) {
		this.mMostRecent = mMostRecent;
	}

	public static final Parcelable.Creator<MyFavoriteChainObject> CREATOR = new Parcelable.Creator<MyFavoriteChainObject>() {

		@Override
		public MyFavoriteChainObject createFromParcel(Parcel source) {
			return new MyFavoriteChainObject(source);
		}

		@Override
		public MyFavoriteChainObject[] newArray(int size) {
			return new MyFavoriteChainObject[size];
		}
	};

	public MyFavoriteChainObject(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeInt(mlogo);
		dest.writeString(mName);
		dest.writeString(mStreet);
		dest.writeString(mState);
		dest.writeString(mCity);
		dest.writeString(mZipcode);
		dest.writeInt(mNumberFavorite);
		dest.writeString(mMostRecent);
	}

	public void readFromParcel(Parcel source) {
		mId = source.readInt();
		mlogo = source.readInt();
		mName = source.readString();
		mStreet = source.readString();
		mCity = source.readString();
		mState = source.readString();
		mZipcode = source.readString();
		mNumberFavorite = source.readInt();
		mMostRecent = source.readString();
	}

}

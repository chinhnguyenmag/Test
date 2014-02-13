package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyFavoriteChainListEntity implements Parcelable {

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
	public String mZipcode;

	@SerializedName("most_recent")
	public String mMostRecent;

	@SerializedName("logo")
	public String mLogo;

	@SerializedName("total_favourites")
	public int mTotalFavorites;

	public MyFavoriteChainListEntity(int mLocationId, String mName,
			String mAddress, String mCity, String mState, String mZipcode,
			String mMostRecent, String mLogo, int mTotalFavorites) {
		super();
		this.mLocationId = mLocationId;
		this.mName = mName;
		this.mAddress = mAddress;
		this.mCity = mCity;
		this.mState = mState;
		this.mZipcode = mZipcode;
		this.mMostRecent = mMostRecent;
		this.mLogo = mLogo;
		this.mTotalFavorites = mTotalFavorites;
	}

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

	public String getZipcode() {
		if (mZipcode != null) {
			return mZipcode;
		} else
			return "";
	}

	public void setZipcode(String mZipcode) {
		this.mZipcode = mZipcode;
	}

	public String getMostRecent() {
		if (mMostRecent != null) {
			return mMostRecent;
		} else
			return "";
	}

	public void setMostRecent(String mMostRecent) {
		this.mMostRecent = mMostRecent;
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

	public static final Parcelable.Creator<MyFavoriteChainListEntity> CREATOR = new Parcelable.Creator<MyFavoriteChainListEntity>() {

		@Override
		public MyFavoriteChainListEntity createFromParcel(Parcel source) {
			return new MyFavoriteChainListEntity(source);
		}

		@Override
		public MyFavoriteChainListEntity[] newArray(int size) {
			return new MyFavoriteChainListEntity[size];
		}
	};

	public MyFavoriteChainListEntity() {
	}

	public MyFavoriteChainListEntity(Parcel source) {
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
		dest.writeString(mZipcode);
		dest.writeString(mMostRecent);
		dest.writeString(mLogo);
		dest.writeInt(mTotalFavorites);
	}

	public void readFromParcel(Parcel source) {
		mLocationId = source.readInt();
		mName = source.readString();
		mAddress = source.readString();
		mCity = source.readString();
		mState = source.readString();
		mZipcode = source.readString();
		mMostRecent = source.readString();
		mLogo = source.readString();
		mTotalFavorites = source.readInt();
	}

}

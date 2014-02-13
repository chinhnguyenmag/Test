package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyFavoriteGlobalListEntity implements Parcelable {

	@SerializedName("chain_name")
	public String mChainName;

	@SerializedName("logo")
	public String mLogo;

	@SerializedName("total_favourites")
	public int mTotalFavorites;

	public MyFavoriteGlobalListEntity(String mChainName, String mLogo,
			int mTotalFavorites) {
		super();
		this.mChainName = mChainName;
		this.mLogo = mLogo;
		this.mTotalFavorites = mTotalFavorites;
	}

	public static final Parcelable.Creator<MyFavoriteGlobalListEntity> CREATOR = new Parcelable.Creator<MyFavoriteGlobalListEntity>() {

		@Override
		public MyFavoriteGlobalListEntity createFromParcel(Parcel source) {
			return new MyFavoriteGlobalListEntity(source);
		}

		@Override
		public MyFavoriteGlobalListEntity[] newArray(int size) {
			return new MyFavoriteGlobalListEntity[size];
		}
	};

	public String getChainName() {
		if (mChainName != null) {
			return mChainName;
		} else
			return "";
	}

	public void setChainName(String mChainName) {
		this.mChainName = mChainName;
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

	public MyFavoriteGlobalListEntity() {
	}

	public MyFavoriteGlobalListEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mChainName);
		dest.writeString(mLogo);
		dest.writeInt(mTotalFavorites);
	}

	public void readFromParcel(Parcel source) {
		mChainName = source.readString();
		mLogo = source.readString();
		mTotalFavorites = source.readInt();
	}

}

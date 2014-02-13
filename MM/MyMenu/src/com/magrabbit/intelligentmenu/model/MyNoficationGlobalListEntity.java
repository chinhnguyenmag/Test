package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyNoficationGlobalListEntity implements Parcelable {

	@SerializedName("chain_name")
	public String mChainName;

	@SerializedName("most_recent")
	public String mMostRecent;

	@SerializedName("logo")
	public String mLogo;

	@SerializedName("unread")
	public int mUnread;

	public String getChainName() {
		if (mChainName != null) {
			return mChainName;
		} else
			return "";
	}

	public void setChainName(String mChainName) {
		this.mChainName = mChainName;
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

	public static final Parcelable.Creator<MyNoficationGlobalListEntity> CREATOR = new Parcelable.Creator<MyNoficationGlobalListEntity>() {

		@Override
		public MyNoficationGlobalListEntity createFromParcel(Parcel source) {
			return new MyNoficationGlobalListEntity(source);
		}

		@Override
		public MyNoficationGlobalListEntity[] newArray(int size) {
			return new MyNoficationGlobalListEntity[size];
		}
	};

	public MyNoficationGlobalListEntity() {
	}

	public MyNoficationGlobalListEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mChainName);
		dest.writeString(mMostRecent);
		dest.writeString(mLogo);
		dest.writeInt(mUnread);
	}

	public void readFromParcel(Parcel source) {
		mChainName = source.readString();
		mMostRecent = source.readString();
		mLogo = source.readString();
		mUnread = source.readInt();
	}

}

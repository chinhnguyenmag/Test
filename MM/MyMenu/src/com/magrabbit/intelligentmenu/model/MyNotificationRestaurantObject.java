package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vule
 *
 */
public class MyNotificationRestaurantObject implements Parcelable {
	private int mLogo;

	private String mMsgSubject;

	private String mContent;

	private String mDate;

	private String mType;

	public MyNotificationRestaurantObject(int mLogo, String mMsgSubject,
			String mContent, String mDate, String mType) {
		super();
		this.mLogo = mLogo;
		this.mMsgSubject = mMsgSubject;
		this.mContent = mContent;
		this.mDate = mDate;
		this.mType = mType;
	}

	public static final Parcelable.Creator<MyNotificationRestaurantObject> CREATOR = new Parcelable.Creator<MyNotificationRestaurantObject>() {

		@Override
		public MyNotificationRestaurantObject createFromParcel(Parcel source) {
			return new MyNotificationRestaurantObject(source);
		}

		@Override
		public MyNotificationRestaurantObject[] newArray(int size) {
			return new MyNotificationRestaurantObject[size];
		}
	};

	public MyNotificationRestaurantObject() {
	}

	public MyNotificationRestaurantObject(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mLogo);
		dest.writeString(mMsgSubject);
		dest.writeString(mContent);
		dest.writeString(mDate);
		dest.writeString(mType);
	}

	public void readFromParcel(Parcel source) {
		mLogo = source.readInt();
		mMsgSubject = source.readString();
		mContent = source.readString();
		mDate = source.readString();
		mType = source.readString();
	}

	public int getLogo() {
		return mLogo;
	}

	public void setLogo(int mLogo) {
		this.mLogo = mLogo;
	}

	public String getMsgSubject() {
		return mMsgSubject;
	}

	public void setMsgSubject(String mMsgSubject) {
		this.mMsgSubject = mMsgSubject;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getType() {
		return mType;
	}

	public void setType(String mType) {
		this.mType = mType;
	}

}

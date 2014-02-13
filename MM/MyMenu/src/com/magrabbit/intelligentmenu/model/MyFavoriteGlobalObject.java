package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vule
 *
 */
public class MyFavoriteGlobalObject implements Parcelable {

	private int mId;

	private int mlogo;

	private String mName;

	private int mNumberFavorite;

	public MyFavoriteGlobalObject() {
	}

	public MyFavoriteGlobalObject(int mId, int mlogo, String mName,
			int mNumberFavorite) {
		super();
		this.mId = mId;
		this.mlogo = mlogo;
		this.mName = mName;
		this.mNumberFavorite = mNumberFavorite;
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

	public int getNumberFavorite() {
		return mNumberFavorite;
	}

	public void setNumberFavorite(int mNumberFavorite) {
		this.mNumberFavorite = mNumberFavorite;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public static final Parcelable.Creator<MyFavoriteGlobalObject> CREATOR = new Parcelable.Creator<MyFavoriteGlobalObject>() {

		@Override
		public MyFavoriteGlobalObject createFromParcel(Parcel source) {
			return new MyFavoriteGlobalObject(source);
		}

		@Override
		public MyFavoriteGlobalObject[] newArray(int size) {
			return new MyFavoriteGlobalObject[size];
		}
	};

	public MyFavoriteGlobalObject(Parcel source) {
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
		dest.writeInt(mNumberFavorite);
	}

	public void readFromParcel(Parcel source) {
		mId = source.readInt();
		mlogo = source.readInt();
		mName = source.readString();
		mNumberFavorite = source.readInt();
	}

}

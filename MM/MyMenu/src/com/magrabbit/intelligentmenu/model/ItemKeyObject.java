package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class ItemKeyObject implements Serializable {

	@SerializedName("id")
	private int mId;

	@SerializedName("name")
	private String mName;

	@SerializedName("image")
	private String mImg;

	@SerializedName("description")
	private String mDescription = "";

	public ItemKeyObject(int mId, String mName, String mDescription, String mImg) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mImg = mImg;
		this.mDescription = mDescription;
	}

	public ItemKeyObject(Parcel in) {
		this.mId = in.readInt();
		this.mName = in.readString();
		this.mImg = in.readString();
		this.mDescription = in.readString();
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public int getId() {
		return mId;
	}

	public void setIdd(int mIdd) {
		this.mId = mIdd;
	}

	public String getImg() {
		return mImg;
	}

	public void setImg(String mImg) {
		this.mImg = mImg;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel parcel, int flags) {
//		parcel.writeInt(mId);
//		parcel.writeString(mName);
//		parcel.writeString(mImg);
//		parcel.writeString(mDescription);
//	}
//
//	public static final Parcelable.Creator<ItemKeyObject> CREATOR = new Parcelable.Creator<ItemKeyObject>() {
//		public ItemKeyObject createFromParcel(Parcel in) {
//			return new ItemKeyObject(in);
//		}
//
//		public ItemKeyObject[] newArray(int size) {
//			return new ItemKeyObject[size];
//		}
//	};
}

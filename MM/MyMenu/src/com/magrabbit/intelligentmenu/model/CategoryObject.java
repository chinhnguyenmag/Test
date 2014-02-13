package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Hung Hoang
 * 
 */
public class CategoryObject implements Parcelable {

	@SerializedName("id")
	public int mId;

	@SerializedName("name")
	public String mNameCategory;
	
	@SerializedName("number_item")
	public int mNumberItem;
	
	@SerializedName("is_favourite")
	public int mIsFavorite;
	
	@SerializedName("is_nexttime")
	public int mIsNextTime;

	public CategoryObject() {
		super();
	}

	public CategoryObject(int mId, String mNameCategory, int mNumberItem, int mIsFavorite, int mIsNextTime) {
		super();
		this.mId = mId;
		this.mNameCategory = mNameCategory;
		this.mNumberItem = mNumberItem;
		this.mIsFavorite = mIsFavorite;
		this.mIsNextTime = mIsNextTime;
	}

	public CategoryObject(Parcel in) {
		this.mId = in.readInt();
		this.mNameCategory = in.readString();
		this.mNumberItem = in.readInt();
	}

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public String getmNameCategory() {
		return mNameCategory;
	}

	public void setmNameCategory(String mNameCategory) {
		this.mNameCategory = mNameCategory;
	}

	public int getNumberItem() {
		return mNumberItem;
	}

	public void setNumberItem(int mNumberItem) {
		this.mNumberItem = mNumberItem;
	}

	public int isIsFavorite() {
		return mIsFavorite;
	}

	public void setIsFavorite(int mIsFavorite) {
		this.mIsFavorite = mIsFavorite;
	}

	public int isIsNextTime() {
		return mIsNextTime;
	}

	public void setIsNextTime(int mIsNextTime) {
		this.mIsNextTime = mIsNextTime;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(mId);
		parcel.writeInt(mNumberItem);
		parcel.writeString(mNameCategory);
	}

	public static final Parcelable.Creator<CategoryObject> CREATOR = new Parcelable.Creator<CategoryObject>() {
		public CategoryObject createFromParcel(Parcel in) {
			return new CategoryObject(in);
		}

		public CategoryObject[] newArray(int size) {
			return new CategoryObject[size];
		}
	};
}

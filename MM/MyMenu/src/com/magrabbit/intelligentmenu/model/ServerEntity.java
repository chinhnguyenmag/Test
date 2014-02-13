package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class ServerEntity {

	@SerializedName("id")
	private int mId;
	@SerializedName("name")
	private String mName;
	@SerializedName("avatar")
	private String mAvatar;
	@SerializedName("is_favorite")
	private int mIsFavourite;
	@SerializedName("rating")
	private float mRating;
	@SerializedName("is_chosen")
	private int mIsChosen = 0;

	public ServerEntity(int mId, String mName, String mAvatar,
			int mIsFavourite, float mRating) {
		this.mId = mId;
		this.mName = mName;
		this.mAvatar = mAvatar;
		this.mIsFavourite = mIsFavourite;
		this.mRating = mRating;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		if (this.mName != null) {
			return mName;
		} else {
			return "";
		}
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getAvatar() {
		if (this.mAvatar != null) {
			return mAvatar;
		} else {
			return "";
		}
	}

	public void setAvatar(String mAvatar) {
		this.mAvatar = mAvatar;
	}

	public int getIsFavourite() {
		return mIsFavourite;
	}

	public void setIsFavourite(int isFavourite) {
		this.mIsFavourite = isFavourite;
	}

	public float getRating() {
		return mRating;
	}

	public void setRating(float mRating) {
		this.mRating = mRating;
	}

	public int getIsChosen() {
		return mIsChosen;
	}

	public void setIsChosen(int mIsChosen) {
		this.mIsChosen = mIsChosen;
	}

	@Override
	public String toString() {
		return "This is my server: \n" + "Name: " + mName;
	}
}

package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class ServerDetailsEntity {

	@SerializedName("id")
	private int mId;
	@SerializedName("name")
	private String mName;
	@SerializedName("avatar")
	private String mAvatar;
	@SerializedName("is_favorite")
	private int mIsFavourite;
	@SerializedName("rating")
	private String mRating;

	public ServerDetailsEntity(int mId, String mName, String mAvatar,
			int mIsFavourite, String mRating) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mAvatar = mAvatar;
		this.mIsFavourite = mIsFavourite;
		this.mRating = mRating;
	}

	public ServerDetailsEntity(int mId, String mName, String mAvatar,
			int mIsFavourite) {
		this.mId = mId;
		this.mName = mName;
		this.mAvatar = mAvatar;
		this.mIsFavourite = mIsFavourite;
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

	public void setIsFavourite(int mIsFavourite) {
		this.mIsFavourite = mIsFavourite;
	}

	public String getRating() {
		return mRating;
	}

	public void setRating(String mRating) {
		this.mRating = mRating;
	}

}

package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MyInstructionItemEntity {

	@SerializedName("id")
	public int mId;

	@SerializedName("item_name")
	public String mItemName;

	@SerializedName("youtube_id")
	public String mYoutubeId;

	@SerializedName("times")
	public String mTime;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getItemName() {
		if (mItemName != null) {
			return mItemName;
		} else
			return "";
	}

	public void setItemName(String mItemName) {
		this.mItemName = mItemName;
	}

	public String getYoutubeId() {
		if (mYoutubeId != null) {
			return mYoutubeId;
		} else
			return "";
	}

	public void setYoutubeId(String mYoutubeId) {
		this.mYoutubeId = mYoutubeId;
	}

	public String getTime() {
		if (mTime != null) {
			return mTime;
		} else
			return "";
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

}

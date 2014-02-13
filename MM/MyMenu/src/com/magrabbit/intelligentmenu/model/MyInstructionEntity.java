package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MyInstructionEntity {

	@SerializedName("id")
	public int mId;

	@SerializedName("name")
	public String mName;

	@SerializedName("icon")
	public String mIcon;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
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

	public String getIcon() {
		if (mIcon != null) {
			return mIcon;
		} else
			return "";
	}

	public void setIcon(String mIcon) {
		this.mIcon = mIcon;
	}

}

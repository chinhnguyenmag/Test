package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class SharePointViaFriendListEntity {

	@SerializedName("status")
	private String mStatus;

	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;

	public String getStatus() {
		if (this.mStatus != null) {
			return this.mStatus;
		} else {
			return "";
		}
	}

	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getError() {
		if (this.mError != null) {
			return this.mError;
		} else {
			return "";
		}
	}

	public void setError(String mError) {
		this.mError = mError;
	}

}

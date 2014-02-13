package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class SharePointViaSMSEntity {

	@SerializedName("url")
	private String mUrl;

	@SerializedName("status")
	private String mStatus;

	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;

	public String getUrl() {
		if (this.mUrl != null) {
			return this.mUrl;
		} else {
			return "";
		}
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

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

	public String getMessage() {
		if (this.mMessage != null) {
			return this.mMessage;
		} else {
			return "";
		}
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

}

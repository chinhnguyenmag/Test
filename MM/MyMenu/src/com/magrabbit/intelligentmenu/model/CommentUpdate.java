package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @Description Entity to get result from Server after editing the rating item
 */
public class CommentUpdate {

	@SerializedName("status")
	private String mStatus;
	
	@SerializedName("rating")
	private String mRating;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;

	public String getStatus() {
		if (this.mStatus != null) {
			return mStatus;
		} else {
			return "";
		}
	}

	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getRating() {
		if (this.mRating != null) {
			return mRating;
		} else {
			return "";
		}
	}

	public void setRating(String mRating) {
		this.mRating = mRating;
	}

}

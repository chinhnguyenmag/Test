package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class CommentsAddServerRating {

	@SerializedName("status")
	public String mStatus;

	@SerializedName("rating")
	public float mRating;

	@SerializedName("message")
	public String mMessage;

	@SerializedName("error")
	public String mError;
}

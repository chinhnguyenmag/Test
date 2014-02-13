package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MenuItemCommentEntity {
	
	@SerializedName("status")
	public String mStatus;
	@SerializedName("review")
	public int mReview;
	@SerializedName("rating")
	public float mRating;

}

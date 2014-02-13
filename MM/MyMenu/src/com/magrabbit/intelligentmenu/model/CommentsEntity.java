package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class CommentsEntity {

	@SerializedName("user_id")
	public int mUserId;

	@SerializedName("item_id")
	public int mItemId;

	@SerializedName("text")
	public String mRatingContent;

	@SerializedName("rating")
	public float mRating;

	@SerializedName("created")
	public String mDateCreated;

	@SerializedName("updated")
	public String mDateUpdated;

	@SerializedName("user_avatar")
	public String mUserAvatar;

	@SerializedName("item_name")
	public String mNameItem;

	@SerializedName("username")
	public String mUserName;

	@SerializedName("comment_id")
	public int mMessageId;
}

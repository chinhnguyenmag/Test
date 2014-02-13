package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class CommentsRestaurantEntity {

	@SerializedName("user_id")
	public String mUserId;

	@SerializedName("location_id")
	public int mLocationId;

	@SerializedName("text")
	public String mRatingContent;

	@SerializedName("rating")
	public int mRating;

	@SerializedName("created")
	public String mDateCreated;

	@SerializedName("updated")
	public String mDateUpdated;

	@SerializedName("user_avatar")
	public String mUserAvatar;

	@SerializedName("server_name")
	public String mSeverName;

	@SerializedName("location_name")
	public String mLocationName;

	@SerializedName("username")
	public String mUserName;
	
	@SerializedName("message_id")
	public int mMessageId;
}

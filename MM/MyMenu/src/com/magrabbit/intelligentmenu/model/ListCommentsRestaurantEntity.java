package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ListCommentsRestaurantEntity {

	@SerializedName("rating")
	public double mRatings;
	
	@SerializedName("ratings_count")
	public int mRatingCount;
	
	@SerializedName("comments_count")
	public int mCommentCount;
	
	@SerializedName("comments")
	public List<CommentsRestaurantEntity> mListComments;
	
	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;
}

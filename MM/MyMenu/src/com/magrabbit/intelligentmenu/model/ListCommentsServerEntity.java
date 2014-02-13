package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ListCommentsServerEntity {

	@SerializedName("rating")
	public float mRatings;
	
	@SerializedName("ratings_count")
	public int mRatingCount;
	
	@SerializedName("comments_count")
	public int mCommentCount;
	
	@SerializedName("comments")
	public List<CommentsServerObject> mListComments;
	
	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;
	
}

package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CommentsServerEntity {

	@SerializedName("comments")
	public List<CommentsServerObject> mListComments;
	
	@SerializedName("status")
	public String mStatus;
	
	@SerializedName("message")
	public String mMessage;
	
	@SerializedName("error")
	public String mError;
}

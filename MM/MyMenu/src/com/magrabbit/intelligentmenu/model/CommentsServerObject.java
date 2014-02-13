package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class CommentsServerObject {

	@SerializedName("id")
	private int mId;

	@SerializedName("server_id")
	private int mServerId;

	@SerializedName("rating")
	private int mRating;

	@SerializedName("comment")
	private String mComment;

	@SerializedName("username")
	private String mUserName;

	@SerializedName("user_avatar")
	private String mUserAvatar;

	@SerializedName("created")
	private String mDateCreated;

	@SerializedName("updated")
	private String mDateUpdated;

	@SerializedName("server_avatar")
	private String mServerAvatar;

	@SerializedName("server_name")
	private String mSeverName;

	@SerializedName("message_id")
	private int mMessageId;
	

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getServerId() {
		return mServerId;
	}

	public void setServerId(int mServerId) {
		this.mServerId = mServerId;
	}

	public String getUserName() {
		if (this.mUserName != null) {
			return mUserName;
		} else {
			return "";
		}
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getUserAvatar() {
		if (this.mUserAvatar != null) {
			return mUserAvatar;
		} else {
			return "";
		}
	}

	public void setUserAvatar(String mUserAvatar) {
		this.mUserAvatar = mUserAvatar;
	}

	public String getComment() {
		if (this.mComment != null) {
			return mComment;
		} else {
			return "";
		}
	}
	public String getDateCreated() {
		if (this.mDateCreated != null) {
			return mDateCreated;
		} else {
			return "";
		}
	}

	public void setDateCreated(String mDateCreated) {
		this.mDateCreated = mDateCreated;
	}

	public String getDateUpdated() {
		if (this.mDateUpdated != null) {
			return mDateUpdated;
		} else {
			return "";
		}
	}

	public void setDateUpdated(String mDateUpdated) {
		this.mDateUpdated = mDateUpdated;
	}

	public void setComment(String mComment) {
		this.mComment = mComment;
	}

	public int getRating() {
		return mRating;
	}

	public void setRating(int mRating) {
		this.mRating = mRating;
	}

	public String getServerAvatar() {
		if (this.mServerAvatar != null) {
			return mServerAvatar;
		} else {
			return "";
		}
	}

	public void setServerAvatar(String mServerAvatar) {
		this.mServerAvatar = mServerAvatar;
	}

	public String getSeverName() {
		if (this.mSeverName != null) {
			return mSeverName;
		} else {
			return "";
		}
	}

	public void setSeverName(String mSeverName) {
		this.mSeverName = mSeverName;
	}

	public int getMessageId() {
		return mMessageId;
	}

	public void setMessageId(int mMessageId) {
		this.mMessageId = mMessageId;
	}
}

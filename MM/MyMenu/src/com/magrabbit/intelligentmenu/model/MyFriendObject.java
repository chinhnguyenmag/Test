package com.magrabbit.intelligentmenu.model;

/**
 * @author Vu Le
 * 
 */
public class MyFriendObject {
	private int mUserId;
	private String mUserName = "";
	private int mAvatar;
	private int mStatus;
	private String mDateInvited = "";
	private String mDateRegistered = "";
	private String mDateLastRating = "";
	private int mTotalRatings;
	private int mTotalPoints;
	private int mTotalVisited;

	public MyFriendObject(int mUserId, String mName, int mImage, int mStatus,
			String mDateInvited, String mDateRegistered,
			String mDateLastRating, int mTotalRatings, int mTotalPoints,
			int mTotalVisited) {
		this.mUserId = mUserId;
		this.mUserName = mName;
		this.mAvatar = mImage;
		this.mStatus = mStatus;
		this.mDateInvited = mDateInvited;
		this.mDateRegistered = mDateRegistered;
		this.mDateLastRating = mDateLastRating;
		this.mTotalRatings = mTotalRatings;
		this.mTotalPoints = mTotalPoints;
		this.mTotalVisited = mTotalVisited;
	}

	public MyFriendObject() {
	}

	public int getUserId() {
		return mUserId;
	}

	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public int getAvatar() {
		return mAvatar;
	}

	public void setAvatar(int mAvatar) {
		this.mAvatar = mAvatar;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public String getDateInvited() {
		return mDateInvited;
	}

	public void setDateInvited(String mDateInvited) {
		this.mDateInvited = mDateInvited;
	}

	public String getDateRegistered() {
		return mDateRegistered;
	}

	public void setDateRegistered(String mDateRegistered) {
		this.mDateRegistered = mDateRegistered;
	}

	public String getDateLastRating() {
		return mDateLastRating;
	}

	public void setDateLastRating(String mDateLastRating) {
		this.mDateLastRating = mDateLastRating;
	}

	public int getTotalRatings() {
		return mTotalRatings;
	}

	public void setTotalRatings(int mTotalRatings) {
		this.mTotalRatings = mTotalRatings;
	}

	public int getTotalPoints() {
		return mTotalPoints;
	}

	public void setTotalPoints(int mTotalPoints) {
		this.mTotalPoints = mTotalPoints;
	}

	public int getTotalVisited() {
		return mTotalVisited;
	}

	public void setTotalVisited(int mTotalVisited) {
		this.mTotalVisited = mTotalVisited;
	}

}

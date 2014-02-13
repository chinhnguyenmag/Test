package com.magrabbit.intelligentmenu.model;

/**
 * @author vule
 *
 */
import com.google.gson.annotations.SerializedName;

/**
 * @author vu le
 * 
 */
public class MyFriendListEntity {

	@SerializedName("id")
	private int id;

	@SerializedName("first_name")
	private String mFirstName = "";

	@SerializedName("last_name")
	private String mLastName = "";

	@SerializedName("date_invited")
	private String mDateInvited = "";

	@SerializedName("date_registered")
	private String mDateRegistered = "";

	@SerializedName("friend_status")
	private String mStatus = "";

	@SerializedName("last_rating")
	private String mLastRating = "";

	@SerializedName("number_rating")
	private float mNumberRatings;

	@SerializedName("point_received")
	private float mPointReceived;

	@SerializedName("restaurant_visited")
	private int mResVisited;

	@SerializedName("userPhotoImageURL")
	private String mAvatar = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		if (this.mFirstName != null) {
			return this.mFirstName;
		} else {
			return "";
		}
	}

	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	public String getLastName() {
		if (this.mLastName != null) {
			return this.mLastName;
		} else {
			return "";
		}
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	public String getDateInvited() {
		if (this.mDateInvited != null) {
			return this.mDateInvited;
		} else {
			return "";
		}
	}

	public void setDateInvited(String mDateInvited) {
		this.mDateInvited = mDateInvited;
	}

	public String getDateRegistered() {
		if (this.mDateRegistered != null) {
			return this.mDateRegistered;
		} else {
			return "";
		}
	}

	public void setDateRegistered(String mDateRegistered) {
		this.mDateRegistered = mDateRegistered;
	}

	public String getStatus() {
		if (this.mStatus != null) {
			return this.mStatus;
		} else {
			return "";
		}
	}

	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getLastRating() {
		if (this.mLastRating != null) {
			return this.mLastRating;
		} else {
			return "";
		}
	}

	public void setLastRating(String mLastRating) {
		this.mLastRating = mLastRating;
	}

	public float getNumberRatings() {
		return mNumberRatings;
	}

	public void setNumberRatings(float mNumberRatings) {
		this.mNumberRatings = mNumberRatings;
	}

	public float getPointReceived() {
		return mPointReceived;
	}

	public void setPointReceived(float mPointReceived) {
		this.mPointReceived = mPointReceived;
	}

	public int getResVisited() {
		return mResVisited;
	}

	public void setResVisited(int mResVisited) {
		this.mResVisited = mResVisited;
	}

	public String getAvatar() {
		if (this.mAvatar != null) {
			return this.mAvatar;
		} else {
			return "";
		}
	}

	public void setAvatar(String mAvatar) {
		this.mAvatar = mAvatar;
	}

}

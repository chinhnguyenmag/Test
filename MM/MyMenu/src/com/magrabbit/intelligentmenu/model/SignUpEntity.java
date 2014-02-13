package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class SignUpEntity {
	@SerializedName("access_token")
	public String mAccessToken;

	@SerializedName("id")
	public String mId;

	@SerializedName("status")
	public String mStatus;

	@SerializedName("errors")
	public ErrorSignUpEntity mError;

	@SerializedName("city")
	public String mCity;

	@SerializedName("state")
	public String mState;

	@SerializedName("points")
	public String mPoints;

	@SerializedName("dinner_status")
	public String mDinnerStatus;
}

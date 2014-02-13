package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class UserEntity {
	@SerializedName("status")
	public String mStatus;

	@SerializedName("access_token")
	public String mAccessToken;

	@SerializedName("error")
	public String mError;

	@SerializedName("exist")
	public int mExist = -1;

	@SerializedName("user")
	public UserObject mUser;

	@SerializedName("message")
	public String mMessage;
}

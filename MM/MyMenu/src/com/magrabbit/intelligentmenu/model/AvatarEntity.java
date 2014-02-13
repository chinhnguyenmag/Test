package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class AvatarEntity {
	@SerializedName("id")
	public int mId;

	@SerializedName("avatar")
	public String mAvatar;

	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;
}

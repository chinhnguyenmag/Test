package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class InviteFriendViaSMSEntity {
	@SerializedName("url")
	public String mUrl;
	@SerializedName("error")
	public String mError;
	@SerializedName("status")
	public String mStatus;
	@SerializedName("message")
	public String mMessage;
}

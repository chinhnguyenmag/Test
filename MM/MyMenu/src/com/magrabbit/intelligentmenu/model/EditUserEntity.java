package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class EditUserEntity {
	
	@SerializedName("status")
	public String mStatus;

	@SerializedName("error")
	public String mError;
	
	@SerializedName("state")
	public String mState;
	
	@SerializedName("city")
	public String mCity;
	
	@SerializedName("message")
	public String mMessage;
	
	@SerializedName("errors")
	public ErrorSignUpEntity mErrors;
}

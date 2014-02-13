package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class UpdateOrderEntity {
	@SerializedName("status")
	public String mStatus;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

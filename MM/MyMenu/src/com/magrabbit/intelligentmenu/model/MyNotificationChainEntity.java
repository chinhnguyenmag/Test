package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyNotificationChainEntity {

	@SerializedName("restaurants")
	public List<MyNotificationChainListEntity> mRestaurants;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

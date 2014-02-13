package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyNotificationRestaurantEntity {

	@SerializedName("messages")
	public List<MyNotificationRestaurantListEntity> mMessages;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyNotificationMessageEntity {

	@SerializedName("message")
	public List<MyNotificationMessageListEntity> mMessage;

	@SerializedName("error")
	public String mError;
}

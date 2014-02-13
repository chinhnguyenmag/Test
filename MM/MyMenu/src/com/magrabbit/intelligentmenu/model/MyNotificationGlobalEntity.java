package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyNotificationGlobalEntity {
	
	@SerializedName("restaurants")
	public List<MyNoficationGlobalListEntity> mRestaurant;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

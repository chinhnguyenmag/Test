package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyFavoriteRestaurantEntity {

	@SerializedName("items")
	public List<MyFavoriteRestaurantListEntity> mListItem;

	@SerializedName("servers")
	public List<ServerDetailsEntity> mListServer;

	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

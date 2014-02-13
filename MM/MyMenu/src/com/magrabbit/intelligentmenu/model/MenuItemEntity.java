package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MenuItemEntity {
	
	@SerializedName("items")
	public List<MenuItem> mListMenuItems;
	
	@SerializedName("current_order")
	public List<CurrentOrderObject> mListCurrentOrderObject;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

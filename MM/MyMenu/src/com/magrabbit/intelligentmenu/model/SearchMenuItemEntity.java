package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SearchMenuItemEntity implements Serializable {

	@SerializedName("items")
	public List<MenuItem> mListMenuItem;

	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;

	@SerializedName("current_order")
	public List<CurrentOrderObject> mListCurrentOrderObject;
}

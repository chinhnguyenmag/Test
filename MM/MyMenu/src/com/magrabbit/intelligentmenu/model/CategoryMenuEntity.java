package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CategoryMenuEntity {
	
	@SerializedName("categories")
	public List<CategoryObject> mListCategory;
	
	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;
}

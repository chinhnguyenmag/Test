package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyInstructionListItemEntity {
	
	@SerializedName("items")
	public List<MyInstructionItemEntity> mListItems;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

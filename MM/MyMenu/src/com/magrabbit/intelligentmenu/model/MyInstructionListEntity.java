package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyInstructionListEntity {
	
	@SerializedName("categories")
	public List<MyInstructionEntity> mListCategories;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

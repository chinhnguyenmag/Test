package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ItemImageEntity implements Serializable {
	
	@SerializedName("item_id")
	public int mId;
	
	@SerializedName("image")
	public String mImage;
}

package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MyFavoriteListItemImgsEntity {

	@SerializedName("image")
	public String mImage = "";

	public MyFavoriteListItemImgsEntity(String mImage) {
		super();
		this.mImage = mImage;
	}

	public String getmImage() {
		return mImage;
	}

	public void setmImage(String mImage) {
		this.mImage = mImage;
	}

}

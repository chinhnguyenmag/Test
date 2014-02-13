package com.magrabbit.intelligentmenu.social.facebook.model;

import com.google.gson.annotations.SerializedName;

public class ImageProfileEntity {
	@SerializedName("id")
	public String mId;
	@SerializedName("picture")
	public ImageProfileObject mProfile;
}

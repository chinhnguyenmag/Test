package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ImageEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("location_id")
	public int mLocationId;
	@SerializedName("image")
	public String mImageUrl;

}

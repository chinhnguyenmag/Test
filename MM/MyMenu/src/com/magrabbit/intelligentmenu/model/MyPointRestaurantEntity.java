package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointRestaurantEntity {

	@SerializedName("location")
	public MyPointRestaurantLocation mLocation;
	
	@SerializedName("points")
	public List<MyPointRestaurantListPointEntity> mListPoints;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RestaurantEntity {

	@SerializedName("locations")
	private List<RestaurantObject> mListRestaurants;

	public List<RestaurantObject> getListRestaurants() {
		return mListRestaurants;
	}

	public void setListRestaurants(List<RestaurantObject> mListRestaurants) {
		this.mListRestaurants = mListRestaurants;
	}
	
	
}

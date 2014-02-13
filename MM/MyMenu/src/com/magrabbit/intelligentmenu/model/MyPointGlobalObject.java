package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @Description: Show all information about all restaurant points
 */

public class MyPointGlobalObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("user")
	private List<UserGlobalEntity> mListUserInfos;
	@SerializedName("locations")
	private List<RestaurantGlobalEntity> mListRestaurantGlobals;

	public MyPointGlobalObject(List<UserGlobalEntity> mListUserInfos,
			List<RestaurantGlobalEntity> mListChains) {
		// Add list of chains
		if (this.mListRestaurantGlobals == null) {
			this.mListRestaurantGlobals = new ArrayList<RestaurantGlobalEntity>();
		}
		this.mListRestaurantGlobals.clear();
		this.mListRestaurantGlobals.addAll(mListChains);

		// Add list of user info
		if (this.mListUserInfos == null) {
			this.mListUserInfos = new ArrayList<UserGlobalEntity>();
		}
		this.mListUserInfos.clear();
		this.mListUserInfos.addAll(mListUserInfos);
	}

	public List<RestaurantGlobalEntity> getListRestaurant() {
		return mListRestaurantGlobals;
	}

	public void setListRestaurant(
			List<RestaurantGlobalEntity> mListRestaurantGlobals) {
		if (this.mListRestaurantGlobals == null) {
			this.mListRestaurantGlobals = new ArrayList<RestaurantGlobalEntity>();
		}
		this.mListRestaurantGlobals.clear();
		this.mListRestaurantGlobals.addAll(mListRestaurantGlobals);
	}
}
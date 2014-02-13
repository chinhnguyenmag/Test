package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointRestaurantLocation {
	@SerializedName("dinner_status")
	private String mDinnerStatus;

	public String getDinnerStatus() {
		if (this.mDinnerStatus != null) {
			return this.mDinnerStatus;
		} else {
			return "";
		}
	}

	public void setDinnerStatus(String mDinnerStatus) {
		this.mDinnerStatus = mDinnerStatus;
	}

}

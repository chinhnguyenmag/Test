package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointLocationGlobal {

	@SerializedName("dinner_status")
	private String mDinnerStatus;

	public String getDinnerStatus() {
		if (this.mDinnerStatus != null) {
			return mDinnerStatus;
		} else {
			return "";
		}
	}

	public void setDinnerStatus(String mDinnerStatus) {
		this.mDinnerStatus = mDinnerStatus;
	}

}

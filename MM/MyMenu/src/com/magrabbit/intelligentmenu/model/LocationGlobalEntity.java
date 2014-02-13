package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class LocationGlobalEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("dinner_status")
	private String mDinnerStatus;
	
	public String getmDinnerStatus() {
		if(this.mDinnerStatus == null)
			return "";
		else
			return this.mDinnerStatus;
	}
	public void setmDinnerStatus(String mDinnerStatus) {
		this.mDinnerStatus = mDinnerStatus;
	}
}

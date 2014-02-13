package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class LocationTransactionEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("dinner_status")
	private String mDinnerStatus;

	public String getDinnerStatus() {
		if(this.mDinnerStatus == null)
			return "";
		else 
			return this.mDinnerStatus;
	}

	public void setDinnerStatus(String mDinnerStatus) {
		this.mDinnerStatus = mDinnerStatus;
	}
	
	
	
	
}

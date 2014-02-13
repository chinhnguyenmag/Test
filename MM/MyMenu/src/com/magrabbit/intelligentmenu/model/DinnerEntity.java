package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class DinnerEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("total")
	private float mTotal;
	@SerializedName("dinner_status")
	private String mDinnerStatus;

	public float getTotal() {
		return mTotal;
	}

	public void setTotal(float mTotal) {
		this.mTotal = mTotal;
	}

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

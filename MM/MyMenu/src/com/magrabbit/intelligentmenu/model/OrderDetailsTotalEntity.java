package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetailsTotalEntity {

	public OrderDetailsTotalEntity(float mTotal){
		this.mTotal = mTotal;
	}
	@SerializedName("total")
	private float mTotal;

	public float getTotal() {
		return mTotal;
	}

	public void setTotal(float mTotal) {
		this.mTotal = mTotal;
	}
	
	
}

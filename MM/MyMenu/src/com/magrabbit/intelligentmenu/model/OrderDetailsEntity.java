package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class OrderDetailsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("id")
	private int mId;
	@SerializedName("price")
	private float mPrice;
	@SerializedName("order_date")
	private String mOrderDate;
	@SerializedName("paid_date")
	private String mPaidDate;

	public OrderDetailsEntity(int mId, float mPrice, String mOrderDate, String mPaidDate) {
		this.mId = mId;
		this.mPrice = mPrice;
		this.mOrderDate = mOrderDate;
		this.mPaidDate = mPaidDate;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}

	public String getDate() {
		if (this.mOrderDate != null) {
			return mOrderDate;
		} else {
			return "";
		}
	}

	public void setDate(String mDate) {
		this.mOrderDate = mDate;
	}

	public String getName() {
		if (this.mPaidDate != null) {
			return mPaidDate;
		} else {
			return "";
		}
	}

	public void setName(String mName) {
		this.mPaidDate = mName;
	}

}

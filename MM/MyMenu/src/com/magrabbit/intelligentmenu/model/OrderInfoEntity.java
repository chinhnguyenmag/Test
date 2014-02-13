package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class OrderInfoEntity {

	@SerializedName("store_id")
	private int mStoreId;
	@SerializedName("receipt")
	private int mReceipt;
	@SerializedName("date")
	private String mDate;
	@SerializedName("time")
	private String mTime;
	@SerializedName("sub")
	private float mSub;
	@SerializedName("tax")
	private float mTax;
	@SerializedName("tip")
	private float mTip;
	@SerializedName("tip_percent")
	private int mTipPercent;
	@SerializedName("total")
	private float mTotal;
	@SerializedName("price")
	private float mPrice;

	public OrderInfoEntity(int mStoreId, int mReceipt, String mDate,
			String mTime, float mSub, float mTax, float mTip, int mTipPercent,
			float mTotal, float mPrice) {
		this.mStoreId = mStoreId;
		this.mReceipt = mReceipt;
		this.mDate = mDate;
		this.mTime = mTime;
		this.mSub = mSub;
		this.mTax = mTax;
		this.mTip = mTip;
		this.mTipPercent = mTipPercent;
		this.mTotal = mTotal;
		this.mPrice = mPrice;
	}

	public float getTotal() {
		return mTotal;
	}

	public void setTotal(float mTotal) {
		this.mTotal = mTotal;
	}

	public float getTip() {
		return mTip;
	}

	public void setTip(float mTip) {
		this.mTip = mTip;
	}

	public int getStoreId() {
		return mStoreId;
	}

	public void setStoreId(int mStoreId) {
		this.mStoreId = mStoreId;
	}

	public int getTipPercent() {
		return mTipPercent;
	}

	public void setTipPercent(int mTipPercent) {
		this.mTipPercent = mTipPercent;
	}

	public float getTax() {
		return mTax;
	}

	public void setTax(float mTax) {
		this.mTax = mTax;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}

	public int getReceipt() {
		return mReceipt;
	}

	public void setReceipt(int mReceipt) {
		this.mReceipt = mReceipt;
	}

	public float getSub() {
		return mSub;
	}

	public void setSub(float mSub) {
		this.mSub = mSub;
	}

	public String getDate() {
		if (this.mDate != null) {
			return this.mDate;
		} else {
			return "";
		}
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getTime() {
		if (this.mTime != null) {
			return this.mTime;
		} else {
			return "";
		}
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

}

package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class OrderByLocationEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("id")
	private int mId;
	@SerializedName("total_price")
	private float mPrice;
	@SerializedName("order_date")
	private String mOrderDate;
	@SerializedName("paid_date")
	private String mPaidDate;
	@SerializedName("is_paid")
	private int mIsPaid;
	@SerializedName("read")
	private int mIsRead;

	public OrderByLocationEntity(int mId, float mPrice, String mOrderDate, String mPaidDate, int mIsPaid, int mIsRead) {
		this.mId = mId;
		this.mPrice = mPrice;
		this.mOrderDate = mOrderDate;
		this.mPaidDate = mPaidDate;
		this.mIsPaid = mIsPaid;
		this.mIsRead = mIsRead;
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

	
	public String getOrderDate() {
		if (this.mOrderDate != null) {
			return mOrderDate;
		} else {
			return "";
		}
	}

	public void setOrderDate(String mOrderDate) {
		this.mOrderDate = mOrderDate;
	}

	public String getPaidDate() {
		if (this.mPaidDate != null) {
			return mPaidDate;
		} else {
			return "";
		}
	}

	public void setPaidDate(String mPaidDate) {
		this.mPaidDate = mPaidDate;
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

	public int getIsPaid() {
		return mIsPaid;
	}

	public void setIsPaid(int mIsPaid) {
		this.mIsPaid = mIsPaid;
	}

	public int getIsRead() {
		return mIsRead;
	}

	public void setIsRead(int mIsRead) {
		this.mIsRead = mIsRead;
	}

}

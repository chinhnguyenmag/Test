package com.magrabbit.intelligentmenu.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyOrderDetailsEntity {

	// General information
	@SerializedName("id")
	private int mId;
	@SerializedName("total_tip")
	private float mTip;
	@SerializedName("total_tax")
	private float mTax;
	@SerializedName("receipt_no")
	private int mReceipt;
	@SerializedName("store_no")
	private int mStoreId;
	@SerializedName("is_paid")
	private int mIsPaid;
	@SerializedName("sub_price")
	private float mSubTotal;
	@SerializedName("total_price")
	private float mTotal;
	@SerializedName("status")
	private int mStatus;
	@SerializedName("order_date")
	private String mOrderDate;
	@SerializedName("paid_date")
	private String mPaidDate;

	// Items
	@SerializedName("items")
	public List<OrderItemEntity> mListItems;

	@SerializedName("server")
	public List<ServerDetailsEntity> mServerInfos;

	@SerializedName("error")
	public String mError;

	@SerializedName("message")
	public String mMessage;

	public MyOrderDetailsEntity(int mId, float mTip, float mTax, int mReceipt,
			int mStoreId, int mIsPaid,float mSubTotal, float mTotal, int mStatus, String mOrderDate,
			String mPaidDate, List<OrderItemEntity> mListItems,
			List<ServerDetailsEntity> mServerInfos) {
		super();
		this.mId = mId;
		this.mTip = mTip;
		this.mTax = mTax;
		this.mReceipt = mReceipt;
		this.mStoreId = mStoreId;
		this.mIsPaid = mIsPaid;
		this.mSubTotal = mSubTotal;
		this.mTotal = mTotal;
		this.mStatus = mStatus;
		this.mOrderDate = mOrderDate;
		this.mPaidDate = mPaidDate;
		// initialize list of items
		if (this.mListItems == null) {
			this.mListItems = new ArrayList<OrderItemEntity>();
		}
		this.mListItems.clear();
		this.mListItems.addAll(mListItems);

		if (this.mServerInfos == null) {
			this.mServerInfos = new ArrayList<ServerDetailsEntity>();
		}
		this.mServerInfos.clear();
		this.mServerInfos.addAll(mServerInfos);
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public float getTip() {
		return mTip;
	}

	public void setTip(float mTip) {
		this.mTip = mTip;
	}

	public float getTax() {
		return mTax;
	}

	public void setTax(float mTax) {
		this.mTax = mTax;
	}

	public int getReceipt() {
		return mReceipt;
	}

	public void setReceipt(int mReceipt) {
		this.mReceipt = mReceipt;
	}

	public int getStoreId() {
		return mStoreId;
	}

	public void setStoreId(int mStoreId) {
		this.mStoreId = mStoreId;
	}

	public int getIsPaid() {
		return mIsPaid;
	}

	public void setIsPaid(int mIsPaid) {
		this.mIsPaid = mIsPaid;
	}
	
	public float getSubTotal() {
		return mSubTotal;
	}

	public void setSubTotal(float mSubTotal) {
		this.mSubTotal = mSubTotal;
	}

	public float getTotal() {
		return mTotal;
	}

	public void setTotal(float mTotal) {
		this.mTotal = mTotal;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
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

}

package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyOrderEntity {
	
	@SerializedName("id")
	public int mId;
	
	@SerializedName("order_date")
	public String mOrderDate;
	
	@SerializedName("total_tip")
	public float mTip;
	
	@SerializedName("location_id")
	public int mLocationId;
	
	@SerializedName("is_paid")
	public int mIsPaid;
	
	@SerializedName("store_no")
	public int mStoreNo;
	
	@SerializedName("receipt_no")
	public int mReceiptNo;
	
	@SerializedName("server_id")
	public int mServerId;
	
	@SerializedName("paid_date")
	public String mPaidDate;
	
	@SerializedName("tax")
	public float tax;
	
	@SerializedName("items")
	public List<MyOrderObject> mListItem;
	
	@SerializedName("server")
	public List<ServerEntity> mListServer;
	
	@SerializedName("status")
	public int mStatus;
}

package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
public class CurrentOrderItem implements Serializable {
	@SerializedName("item_id")
	private int mItemId;

	@SerializedName("quantity")
	private int mQuantity;
	
	@SerializedName("status")
	private int mStatus;
	
	@SerializedName("order_item_id")
	private int mOrderItemId;
	
	@SerializedName("menu_id")
	private int mMenuId;
	
	@SerializedName("category_id")
	private int mCategoryId;
	
	public int getItemId() {
		return mItemId;
	}

	public void setItemId(int mItemId) {
		this.mItemId = mItemId;
	}

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public int getOrderItemId() {
		return mOrderItemId;
	}

	public void setOrderItemId(int mOrderItemId) {
		this.mOrderItemId = mOrderItemId;
	}

	public int getmMenuId() {
		return mMenuId;
	}

	public void setmMenuId(int mMenuId) {
		this.mMenuId = mMenuId;
	}

	public int getmCategoryId() {
		return mCategoryId;
	}

	public void setmCategoryId(int mCategoryId) {
		this.mCategoryId = mCategoryId;
	}
}

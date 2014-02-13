package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class OrderItemEntity {

	@SerializedName("id")
	private int mId;
	@SerializedName("name")
	private String mItemName;
	@SerializedName("price")
	private float mPrice;
	@SerializedName("status")
	private int mStatus;
	@SerializedName("quantity")
	private int mQuantity;
	@SerializedName("use_point")
	private float mUsePoint;
	@SerializedName("note")
	private String mNote;
	@SerializedName("redemption_value")
	private int mRedemptionValue;
	@SerializedName("rating")
	private float mRating;
	@SerializedName("menu_id")
	private int mMenuId;
	@SerializedName("category_id")
	private int mCategoryId;


	
	public OrderItemEntity(int mId, String mItemName,float mPrice,int mStatus, int mQuantity,
			float mUsePoint, String mNote, int mPaidType,int mRedemptionValue, float mRating,
			int mMenuId, int mCategoryId) {
		this.mId = mId;
		this.mItemName = mItemName;
		this.mPrice = mPrice;
		this.mStatus = mStatus;
		this.mQuantity = mQuantity;
		this.mUsePoint = mUsePoint;
		this.mNote = mNote;
		this.mRedemptionValue = mRedemptionValue;
		this.mRating = mRating;
		this.mMenuId = mMenuId;
		this.mCategoryId = mCategoryId;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getItemName() {
		if (this.mItemName != null) {
			return this.mItemName;
		} else {
			return "";
		}
	}

	public void setItemName(String mItemName) {
		this.mItemName = mItemName;
	}

	
	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}

	public float getUsePoint() {
		return mUsePoint;
	}

	public void setUsePoint(float mUsePoint) {
		this.mUsePoint = mUsePoint;
	}

	public String getNote() {
		if (this.mNote != null) {
			return this.mNote;
		} else {
			return "";
		}
	}

	public void setNote(String mNote) {
		this.mNote = mNote;
	}

	
	public int getRedemptionValue() {
		return mRedemptionValue;
	}

	public void setRedemptionValue(int mRedemptionValue) {
		this.mRedemptionValue = mRedemptionValue;
	}

	public float getRating() {
		return mRating;
	}

	public void setRating(float mRating) {
		this.mRating = mRating;
	}

	public int getMenuId() {
		return mMenuId;
	}

	public void setMenuId(int mMenuId) {
		this.mMenuId = mMenuId;
	}

	public int getCategoryId() {
		return mCategoryId;
	}

	public void setCategoryId(int mCategoryId) {
		this.mCategoryId = mCategoryId;
	}

}

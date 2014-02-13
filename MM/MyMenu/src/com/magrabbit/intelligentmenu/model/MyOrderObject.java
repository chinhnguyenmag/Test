package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MyOrderObject {
	@SerializedName("id")
	private int mIdMenuItem;

	@SerializedName("category_id")
	private int mIdCategory;

	@SerializedName("menu_id")
	private int mIdMenu;

	@SerializedName("quantity")
	private int mQuantity;
	
	@SerializedName("quantity")
	private int mQuantityTemp;
	
	@SerializedName("note")
	private String mComment;

	@SerializedName("price")
	private float mPrice;

	@SerializedName("use_point")
	private float mUsePoint;

	@SerializedName("status")
	private int mStatus;

	@SerializedName("rating")
	private float mRate;

	@SerializedName("name")
	private String mNameItem;
	
	@SerializedName("redemption_value")
	private float mRedemption;
	
	@SerializedName("order_item_id")
	private int mOrderItemId;
	
	private int mDelete; //0 update/1 update
	
	private boolean isChecked;

	public void MyFriendObject() {

	}

	public MyOrderObject(int mIdMenuItem, int mIdCategory, int mIdMenu,
			int mQuantity, String mComment, float mPrice, float mUsePoint,
			int mStatus, int mRate, String mNameItem,
			float mRedemption, int mDelete) {
		super();
		this.mIdMenuItem = mIdMenuItem;
		this.mIdCategory = mIdCategory;
		this.mIdMenu = mIdMenu;
		this.mQuantity = mQuantity;
		this.mComment = mComment;
		this.mPrice = mPrice;
		this.mUsePoint = mUsePoint;
		this.mStatus = mStatus;
		this.mRate = mRate;
		this.mNameItem = mNameItem;
		this.mRedemption = mRedemption;
		this.mDelete = mDelete;
	}

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}

	public String getNameItem() {
		return mNameItem;
	}

	public void setNameItem(String mNameItem) {
		this.mNameItem = mNameItem;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}

	public float getRate() {
		return mRate;
	}

	public void setRate(float mRate) {
		this.mRate = mRate;
	}

	public int getIdMenuItem() {
		return mIdMenuItem;
	}

	public void setIdMenuItem(int mIdMenuItem) {
		this.mIdMenuItem = mIdMenuItem;
	}

	public int getIdCategory() {
		return mIdCategory;
	}

	public void setIdCategory(int mIdCategory) {
		this.mIdCategory = mIdCategory;
	}

	public int getIdMenu() {
		return mIdMenu;
	}

	public void setIdMenu(int mIdMenu) {
		this.mIdMenu = mIdMenu;
	}

	public float getUsePoint() {
		return mUsePoint;
	}

	public void setUsePoint(float mPoint) {
		this.mUsePoint = mPoint;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public String getComment() {
		return mComment;
	}

	public void setComment(String mComment) {
		this.mComment = mComment;
	}
	
	public int isDelete() {
		return mDelete;
	}

	public void setDelete(int mDelete) {
		this.mDelete = mDelete;
	}

	public float getRedemption() {
		return mRedemption;
	}

	public void setRedemption(float mRedemption) {
		this.mRedemption = mRedemption;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getmQuantityTemp() {
		return mQuantityTemp;
	}

	public void setmQuantityTemp(int mQuantityTemp) {
		this.mQuantityTemp = mQuantityTemp;
	}

	public int getOrderItemId() {
		return mOrderItemId;
	}

	public void setOrderItemId(int mOrderItemId) {
		this.mOrderItemId = mOrderItemId;
	}
}

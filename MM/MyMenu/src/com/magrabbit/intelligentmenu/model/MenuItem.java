package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author VuLe
 * 
 *         This model use to description for menu of item
 * 
 */
public class MenuItem implements Serializable {

	@SerializedName("id")
	private int mId;

	@SerializedName("name")
	private String mName = "";

	@SerializedName("price")
	private float mPrice;

	@SerializedName("rating")
	private String mRating;

	@SerializedName("description")
	private String mDescription = "";

	@SerializedName("reward_points")
	private int mReward_point;

	@SerializedName("calories")
	private double mCalories;

	@SerializedName("category_id")
	private int mCategory_id;

	@SerializedName("category_name")
	private String mCategoryName;

	@SerializedName("special_message")
	private String mSpecialMesssage = "";

	@SerializedName("item_images")
	private List<ItemImageEntity> mImage;

	@SerializedName("review")
	private int mReview;

	@SerializedName("item_keys")
	private List<ItemKeyObject> mLisItemKey;

	@SerializedName("is_nexttime")
	private int mTryNextTime;

	@SerializedName("is_favourite")
	private int mIsFavorite;

	@SerializedName("menu_id")
	private int mMenuId;

	private boolean mIsAddOrder;

	// =============Waiting service======================

	private int mQuantity;

	private int mOrderId;
	
	private int mStatus;
	
	private int mOrderItemId;

	@SerializedName("redemption_value")
	private int mRedemptionValue;

	public int getRedemptionValue() {
		return mRedemptionValue;
	}

	public void setRedemptionValue(int mRedemptionValue) {
		this.mRedemptionValue = mRedemptionValue;
	}

	public int getOrderId() {
		if (this.mOrderId <= 0) {
			return 0;
		}
		return mOrderId;
	}

	public void setOrderId(int mOrder_id) {
		this.mOrderId = mOrder_id;
	}

	public int getQuantity() {
		if (this.mQuantity <= 0) {
			return 0;
		} else {
			return mQuantity;
		}
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}

	public MenuItem() {
		super();
	}

	public MenuItem(int mId, String mName, float mPrice, String mRating,
			String mDescription, int mReward_point, double mCalories,
			int mCategory_id, String mCategoryName, String mSpecialMesssage,
			List<ItemImageEntity> mImage, int mReview,
			List<ItemKeyObject> mLisItemKey, int mTryNextTime, int mIsFavorite,
			int mMenuId, boolean mIsAddOrder, int quantity) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mPrice = mPrice;
		this.mRating = mRating;
		this.mDescription = mDescription;
		this.mReward_point = mReward_point;
		this.mCalories = mCalories;
		this.mCategory_id = mCategory_id;
		this.mCategoryName = mCategoryName;
		this.mSpecialMesssage = mSpecialMesssage;
		this.mImage = mImage;
		this.mReview = mReview;
		this.mLisItemKey = mLisItemKey;
		this.mTryNextTime = mTryNextTime;
		this.mIsFavorite = mIsFavorite;
		this.mMenuId = mMenuId;
		this.mIsAddOrder = mIsAddOrder;
		this.mQuantity = quantity;
	}

//	public MenuItem(Parcel in) {
//		this.mId = in.readInt();
//		this.mName = in.readString();
//		this.mPrice = in.readFloat();
//		this.mDescription = in.readString();
//		this.mReward_point = in.readInt();
//		this.mCalories = in.readDouble();
//		this.mRating = in.readFloat();
//		this.mCategory_id = in.readInt();
//		this.mSpecialMesssage = in.readString();
//		this.mReview = in.readInt();
//		this.mTryNextTime = in.readInt();
//		this.mIsFavorite = in.readInt();
//		this.mQuantity = in.readInt();
//		this.mIsAddOrder = in.readInt() != 0;
//		if (mLisItemKey == null)
//			mLisItemKey = new ArrayList<ItemKeyObject>();
//		in.readTypedList(mLisItemKey, ItemKeyObject.CREATOR);
//	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public int getReward_point() {
		return mReward_point;
	}

	public void setReward_point(int mReward_point) {
		this.mReward_point = mReward_point;
	}

	public double getCalories() {
		return mCalories;
	}

	public void setCalories(double mCalories) {
		this.mCalories = mCalories;
	}

	public String getRating() {
		return mRating;
	}

	public void setRating(String mRating) {
		this.mRating = mRating;
	}

	public int getCategory_id() {
		return mCategory_id;
	}

	public void setCategory_id(int mCategory_id) {
		this.mCategory_id = mCategory_id;
	}

	public String getSpecialMesssage() {
		if (mSpecialMesssage == null) {
			return "";
		}
		return mSpecialMesssage;
	}

	public void setSpecialMesssage(String mSpecialMesssage) {
		this.mSpecialMesssage = mSpecialMesssage;
	}

	public List<ItemImageEntity> getImage() {
		return mImage;
	}

	public void setImage(List<ItemImageEntity> mImage) {
		this.mImage = mImage;
	}

	public int getReview() {
		return mReview;
	}

	public void setReview(int mReview) {
		this.mReview = mReview;
	}

	public List<ItemKeyObject> getLisItemKey() {
		if (mLisItemKey == null) {
			mLisItemKey = new ArrayList<ItemKeyObject>();
		}
		return mLisItemKey;
	}

	public void setLisItemKey(List<ItemKeyObject> mLisItemKey) {
		this.mLisItemKey = mLisItemKey;
	}

	public int isTryNextTime() {
		return mTryNextTime;
	}

	public void setTryNextTime(int mTryNextTime) {
		this.mTryNextTime = mTryNextTime;
	}

	public int isFavorite() {
		return mIsFavorite;
	}

	public void setIsFavorite(int mIsFavorite) {
		this.mIsFavorite = mIsFavorite;
	}

	public boolean isAddOrder() {
		return mIsAddOrder;
	}

	public void setIsAddOrder(boolean mIsAddOrder) {
		this.mIsAddOrder = mIsAddOrder;
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void setCategoryName(String mCategoryName) {
		this.mCategoryName = mCategoryName;
	}

	public int getMenuId() {
		return mMenuId;
	}

	public void setMenuId(int mMenuId) {
		this.mMenuId = mMenuId;
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
	
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel parcel, int flags) {
//		parcel.writeInt(mCategory_id);
//		parcel.writeInt(mId);
//		parcel.writeInt(mReview);
//		parcel.writeInt(mReward_point);
//		parcel.writeString(mName);
//		parcel.writeString(mDescription);
//		parcel.writeString(mCategoryName);
//		parcel.writeDouble(mPrice);
//		parcel.writeDouble(mCalories);
//		parcel.writeDouble(mRating);
//		parcel.writeList(mLisItemKey);
//		parcel.writeByte((byte) (isAddOrder() ? 1 : 0));
//		parcel.writeInt(mIsFavorite);
//		parcel.writeInt(mTryNextTime);
//		parcel.writeInt(mQuantity);
//	}
//
//	public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>() {
//		public MenuItem createFromParcel(Parcel in) {
//			return new MenuItem(in);
//		}
//
//		public MenuItem[] newArray(int size) {
//			return new MenuItem[size];
//		}
//	};
}

package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @description Model SearchProfile for Advanced Search feature
 * 
 */
public class SearchProfile implements Parcelable {
	
	@SerializedName("id")
	private int mId;
	@SerializedName("name")
	private String mNameProfile;
	@SerializedName("location_rating")
	private String mRestaurantRatings;
	@SerializedName("item_price")
	private String mItemPrices;
	@SerializedName("point_offered")
	private String mPointOffered;
	@SerializedName("item_rating")
	private String mItemRatings;
	@SerializedName("radius")
	private String mRadius;
	@SerializedName("item_type")
	private String mItemTypes;
	@SerializedName("menu_type")
	private String mMenuTypes;
	@SerializedName("keyword")
	private String mKeyword;
	@SerializedName("server_rating")
	private String mServerRatings;
	@SerializedName("isdefault")
	private int mIsDefault;

	// Variables to process locally

	// Instance

	public SearchProfile() {

	}

	public SearchProfile(int mId, String mNameProfile,
			String mRestaurantRatings, String mItemPrices,
			String mPointOffered, String mItemRatings, String mRadius,
			String mItemTypes, String mMenuTypes, String mKeyword,
			String mServerRatings, int mIsDefault) {
		this.mId = mId;
		this.mNameProfile = mNameProfile;
		this.mRestaurantRatings = mRestaurantRatings;
		this.mItemPrices = mItemPrices;
		this.mPointOffered = mPointOffered;
		this.mItemRatings = mItemRatings;
		this.mRadius = mRadius;
		this.mItemTypes = mItemTypes;
		this.mMenuTypes = mMenuTypes;
		this.mKeyword = mKeyword;
		this.mServerRatings = mServerRatings;
		this.mIsDefault = mIsDefault;
	}

	public static final Parcelable.Creator<SearchProfile> CREATOR = new Parcelable.Creator<SearchProfile>() {

		@Override
		public SearchProfile createFromParcel(Parcel source) {
			return new SearchProfile(source);
		}

		@Override
		public SearchProfile[] newArray(int size) {
			return new SearchProfile[size];
		}
	};

	public SearchProfile(Parcel source) {
		readFromParcel(source);
	}

	private void readFromParcel(Parcel source) {
		this.mId = source.readInt();
		this.mNameProfile = source.readString();
		this.mRestaurantRatings = source.readString();
		this.mItemPrices = source.readString();
		this.mPointOffered = source.readString();
		this.mItemRatings = source.readString();
		this.mRadius = source.readString();
		this.mItemTypes = source.readString();
		this.mMenuTypes = source.readString();
		this.mKeyword = source.readString();
		this.mServerRatings = source.readString();
		this.mIsDefault = source.readInt();

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.mId);
		dest.writeString(this.mNameProfile);
		dest.writeString(this.mRestaurantRatings);
		dest.writeString(this.mItemPrices);
		dest.writeString(this.mPointOffered);
		dest.writeString(this.mItemRatings);
		dest.writeString(this.mRadius);
		dest.writeString(this.mItemTypes);
		dest.writeString(this.mMenuTypes);
		dest.writeString(this.mKeyword);
		dest.writeString(this.mServerRatings);
		dest.writeInt(this.mIsDefault);
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getNameProfile() {
		if (this.mNameProfile == null)
			return "";
		else
			return mNameProfile;
	}

	public void setNameProfile(String mNameProfile) {
		this.mNameProfile = mNameProfile;
	}

	public String getRestaurantRatings() {
		if (this.mRestaurantRatings == null)
			return "";
		else
			return mRestaurantRatings;
	}

	public void setRestaurantRatings(String mRestaurantRatings) {

		this.mRestaurantRatings = mRestaurantRatings;
	}

	public String getItemPrices() {
		if (this.mItemPrices == null)
			return "";
		else
			return mItemPrices;
	}

	public void setItemPrices(String mItemPrices) {
		this.mItemPrices = mItemPrices;
	}

	public String getPointOffered() {
		if (this.mPointOffered == null)
			return "";
		else
			return mPointOffered;
	}

	public void setPointOffered(String mPointOffered) {
		this.mPointOffered = mPointOffered;
	}

	public String getItemRatings() {
		if (this.mItemRatings == null)
			return "";
		else
			return mItemRatings;
	}

	public void setItemRatings(String mItemRatings) {
		this.mItemRatings = mItemRatings;
	}

	public String getRadius() {
		if (this.mRadius == null)
			return "";
		else
			return mRadius;
	}

	public void setRadius(String mRadius) {
		this.mRadius = mRadius;
	}

	public String getItemTypes() {
		if (this.mItemTypes == null)
			return "";
		else
			return mItemTypes;
	}

	public void setItemTypes(String mItemTypes) {
		this.mItemTypes = mItemTypes;
	}

	public String getMenuTypes() {
		if (this.mMenuTypes == null)
			return "";
		else
			return mMenuTypes;
	}

	public void setMenuTypes(String mMenuTypes) {
		this.mMenuTypes = mMenuTypes;
	}

	public String getKeywords() {
		if (this.mKeyword == null)
			return "";
		else
			return mKeyword;
	}

	public void setKeywords(String mKeyword) {
		this.mKeyword = mKeyword;
	}

	public String getServerRatings() {
		if (this.mServerRatings == null)
			return "";
		else
			return mServerRatings;
	}

	public void setServerRatings(String mServerRatings) {
		this.mServerRatings = mServerRatings;
	}

	public int getIsDefault() {
		return mIsDefault;
	}

	public void setIsDefault(int mIsDefault) {
		this.mIsDefault = mIsDefault;
	}

	public SearchProfile clone() {
		return new SearchProfile(mId, mNameProfile, mRestaurantRatings,
				mItemPrices, mPointOffered, mItemRatings, mRadius, mItemTypes,
				mMenuTypes, mKeyword, mServerRatings, mIsDefault);
	}

}

package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Hung Hoang.
 * @Description: All information of a restaurant and it's ready to pass data
 *               from server
 */
public class LocationEntity implements Serializable {
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public int mRestaurantId;
	@SerializedName("name")
	public String mNameRestaurant;
	@SerializedName("address")
	public String mAddress;
	@SerializedName("city")
	public String mCity;
	@SerializedName("state")
	public String mState;
	@SerializedName("country")
	public String mCountry;
	@SerializedName("created_at")
	public String mCreatedAt;
	@SerializedName("lat")
	public double mLatitude;
	@SerializedName("long")
	public double mLongitude;
	@SerializedName("phone")
	public String mPhoneNumber;
	@SerializedName("url")
	public String mUrl;
	@SerializedName("rating")
	public float mRating;
	@SerializedName("redeemption_password")
	public String mRedeemptionPassword;
	@SerializedName("slug")
	public String mSlug;
	@SerializedName("subscription_type")
	public String mSubscriptionType;
	@SerializedName("updated_at")
	public String mUpdatedAt;
	@SerializedName("zip")
	public String mZip;
	@SerializedName("tax")
	public float mTax;
	@SerializedName("hour_of_operation")
	public String mHourOfOperation;
	@SerializedName("bio")
	public String mBio;
	@SerializedName("owner_id")
	public int mOwnerId;
	@SerializedName("logo")
	public String mLogo;
	@SerializedName("isFavourite")
	public LocationFavouriteEntity mFavourites;
	@SerializedName("images")
	public List<ImageEntity> mListImages;
	@SerializedName("dinner")
	public DinnerEntity mDinnerInfos;
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;

	public LocationEntity() {
		super();
	}

	// Creator for Restaurant Object
	public LocationEntity(int mRestaurantId, String mNameRestaurant,
			String mAddress, String mCity, String mState, String mCountry,
			String mCreatedAt, double mLatitude, double mLongitude,
			String mPhoneNumber, int mOwnerId, String mUrl, int mRating,
			String mRedeemptionPassword, String mSlug,
			String mSubscriptionType, String mUpdatedAt, String mZip,
			float mTax, String mHourOfOperation, String mBio, String mLogo,
			LocationFavouriteEntity mFavourites, List<ImageEntity> mListImages,
			DinnerEntity mDinner) {

		this.mRestaurantId = mRestaurantId;
		this.mNameRestaurant = mNameRestaurant;
		this.mAddress = mAddress;
		this.mCity = mCity;
		this.mState = mState;
		this.mCountry = mCountry;
		this.mCreatedAt = mCreatedAt;
		this.mLatitude = mLatitude;
		this.mLongitude = mLongitude;
		this.mPhoneNumber = mPhoneNumber;
		this.mOwnerId = mOwnerId;
		this.mUrl = mUrl;
		this.mRating = mRating;
		this.mRedeemptionPassword = mRedeemptionPassword;
		this.mSlug = mSlug;
		this.mSubscriptionType = mSubscriptionType;
		this.mUpdatedAt = mUpdatedAt;
		this.mZip = mZip;
		this.mTax = mTax;
		this.mHourOfOperation = mHourOfOperation;
		this.mBio = mBio;
		this.mLogo = mLogo;
		this.mFavourites = mFavourites;
		if (this.mListImages == null)
			this.mListImages = new ArrayList<ImageEntity>();
		this.mListImages.clear();
		this.mListImages.addAll(mListImages);

		this.mDinnerInfos = mDinner;

	}

	public int getRestaurantId() {
		return mRestaurantId;
	}

	public void setRestaurantId(int mRestaurantId) {
		this.mRestaurantId = mRestaurantId;
	}

	public String getNameRestaurant() {
		if (mNameRestaurant != null)
			return mNameRestaurant;
		else
			return "";
	}

	public void setNameRestaurant(String mNameRestaurant) {
		this.mNameRestaurant = mNameRestaurant;
	}

	public String getAddress() {
		if (mAddress != null)
			return mAddress;
		else
			return "";
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getCity() {
		if (mCity != null)
			return mCity;
		else
			return "";
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getState() {
		if (mState != null)
			return mState;
		else
			return "";
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public String getCountry() {
		if (mCountry != null)
			return mCountry;
		else
			return "";
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getCreatedAt() {
		if (mCreatedAt != null)
			return mCreatedAt;
		else
			return "";
	}

	public void setCreatedAt(String mCreatedAt) {
		this.mCreatedAt = mCreatedAt;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public String getPhoneNumber() {
		if (mPhoneNumber != null)
			return mPhoneNumber;
		else
			return "";
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public int getOwnerId() {
		return mOwnerId;
	}

	public void setOwnerId(int mOwnerId) {
		this.mOwnerId = mOwnerId;
	}

	public String getUrl() {
		if (mUrl != null) {
			return mUrl;
		} else {
			return "";
		}
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public float getRating() {
		return mRating;
	}

	public void setRating(float mRating) {
		this.mRating = mRating;
	}

	public String getRedeemptionPassword() {
		if (mRedeemptionPassword != null)
			return mRedeemptionPassword;
		else
			return "";
	}

	public void setRedeemptionPassword(String mRedeemptionPassword) {
		this.mRedeemptionPassword = mRedeemptionPassword;
	}

	public String getSlug() {
		if (mSlug != null)
			return mSlug;
		else
			return "";
	}

	public void setSlug(String mSlug) {
		this.mSlug = mSlug;
	}

	public String getSubscriptionType() {
		if (mSubscriptionType != null)
			return mSubscriptionType;
		else
			return "";
	}

	public void setSubscriptionType(String mSubscriptionType) {
		this.mSubscriptionType = mSubscriptionType;
	}

	public String getUpdatedAt() {
		if (mUpdatedAt != null)
			return mUpdatedAt;
		else
			return "";
	}

	public void setUpdatedAt(String mUpdatedAt) {
		this.mUpdatedAt = mUpdatedAt;
	}

	public String getZip() {
		if (mZip != null)
			return mZip;
		else
			return "";
	}

	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	public float getTax() {
		return mTax;
	}

	public void setTax(float mTax) {
		this.mTax = mTax;
	}

	public String getHourOfOperation() {
		if (mHourOfOperation != null)
			return mHourOfOperation;
		else
			return "";
	}

	public void setHourOfOperation(String mHourOfOperation) {
		this.mHourOfOperation = mHourOfOperation;
	}

	public String getBio() {
		if (mBio != null)
			return mBio;
		else
			return "";
	}

	public void setBio(String mBio) {
		this.mBio = mBio;
	}

	public String getLogo() {
		if (mLogo != null)
			return mLogo;
		else
			return "";
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public LocationFavouriteEntity getFavourites() {
		if (this.mFavourites == null) {
			this.mFavourites = new LocationFavouriteEntity();
			this.mFavourites.mFavourite = 0;
		}
		return mFavourites;
	}

	public void setFavourites(LocationFavouriteEntity mFavourites) {
		this.mFavourites = mFavourites;
	}

	public List<ImageEntity> getListImages() {
		return mListImages;
	}

	public void setListImages(List<ImageEntity> mListImages) {
		if (this.mListImages == null)
			this.mListImages = new ArrayList<ImageEntity>();
		this.mListImages.clear();
		this.mListImages.addAll(mListImages);
	}

	public DinnerEntity getDinnerInfos() {
		return mDinnerInfos;
	}

	public void setDinnerInfos(DinnerEntity mListDinner) {
		if (this.mDinnerInfos == null)
			this.mDinnerInfos = new DinnerEntity();
		this.mDinnerInfos = mListDinner;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
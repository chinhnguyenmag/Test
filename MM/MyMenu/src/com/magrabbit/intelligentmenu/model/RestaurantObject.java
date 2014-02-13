package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author baonguyen
 * @Description: All information of a restaurant and it's ready to pass data
 *               from server
 */
public class RestaurantObject implements Parcelable {

	@SerializedName("id")
	private int mRestaurantId;
	@SerializedName("name")
	private String mNameRestaurant;
	@SerializedName("address")
	private String mAddress;
	@SerializedName("city")
	private String mCity;
	@SerializedName("state")
	private String mState;
	@SerializedName("country")
	private String mCountry;
	@SerializedName("lat")
	private double mLatitude;
	@SerializedName("long")
	private double mLongitude;
	@SerializedName("distance")
	private float mDistance;
	@SerializedName("logo")
	private String mLogo;

	public RestaurantObject() {
		super();
	}

	public static final Parcelable.Creator<RestaurantObject> CREATOR = new Parcelable.Creator<RestaurantObject>() {

		@Override
		public RestaurantObject createFromParcel(Parcel source) {
			return new RestaurantObject(source);
		}

		@Override
		public RestaurantObject[] newArray(int size) {
			return new RestaurantObject[size];
		}
	};

	public RestaurantObject(Parcel source) {
		readFromParcel(source);
	}

	private void readFromParcel(Parcel source) {
		this.mRestaurantId = source.readInt();
		this.mNameRestaurant = source.readString();
		this.mAddress = source.readString();
		this.mCity = source.readString();
		this.mState = source.readString();
		this.mCountry = source.readString();
		this.mLatitude = source.readDouble();
		this.mLongitude = source.readDouble();
		this.mDistance = source.readFloat();
		this.mLogo = source.readString();

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mRestaurantId);
		dest.writeString(mNameRestaurant);
		dest.writeString(mAddress);
		dest.writeString(mCity);
		dest.writeString(mState);
		dest.writeString(mCountry);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
		dest.writeFloat(mDistance);
		dest.writeString(mLogo);
	}

	// Creator for Restaurant Object
	public RestaurantObject(int mRestaurantId, String mNameRestaurant,
			String mAddress, String mCity, String mState, String mCountry,
			double mLatitude, double mLongitude, float mDistance, String mLogo) {

		this.mRestaurantId = mRestaurantId;
		this.mNameRestaurant = mNameRestaurant;
		this.mAddress = mAddress;
		this.mCity = mCity;
		this.mState = mState;
		this.mCountry = mCountry;
		this.mLatitude = mLatitude;
		this.mLongitude = mLongitude;
		this.mDistance = mDistance;
		this.mLogo = mLogo;
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

	public float getDistance() {
		return mDistance;
	}

	public void setDistance(float mDistance) {
		this.mDistance = mDistance;
	}

	public String getLogo() {
		if (this.mLogo != null)
			return this.mLogo;
		else
			return "";
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

}
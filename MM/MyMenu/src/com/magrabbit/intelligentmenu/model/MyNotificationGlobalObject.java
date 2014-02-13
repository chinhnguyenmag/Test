package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vule
 *
 */
public class MyNotificationGlobalObject implements Parcelable {

	private int mRestaurantLogo;
	private String mRestaurantName = "";
	private int mTotalMessage;
	private String mDatetimeRecent = "";
	private double mDistance;

	public MyNotificationGlobalObject(int restaurantLogo,
			String restaurantName, int totalMessage, String datetimeRecent,
			double distance) {
		super();
		this.mRestaurantLogo = restaurantLogo;
		this.mRestaurantName = restaurantName;
		this.mTotalMessage = totalMessage;
		this.mDatetimeRecent = datetimeRecent;
		this.mDistance = distance;
	}

	public static final Parcelable.Creator<MyNotificationGlobalObject> CREATOR = new Parcelable.Creator<MyNotificationGlobalObject>() {

		@Override
		public MyNotificationGlobalObject createFromParcel(Parcel source) {
			return new MyNotificationGlobalObject(source);
		}

		@Override
		public MyNotificationGlobalObject[] newArray(int size) {
			return new MyNotificationGlobalObject[size];
		}
	};

	public MyNotificationGlobalObject() {
	}

	public MyNotificationGlobalObject(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mRestaurantLogo);
		dest.writeString(mRestaurantName);
		dest.writeInt(mTotalMessage);
		dest.writeString(mDatetimeRecent);
		dest.writeDouble(mDistance);
	}

	public void readFromParcel(Parcel source) {
		mRestaurantLogo = source.readInt();
		mRestaurantName = source.readString();
		mTotalMessage = source.readInt();
		mDatetimeRecent = source.readString();
		mDistance = source.readDouble();
	}

	public int getRestaurantLogo() {
		return mRestaurantLogo;
	}

	public void setRestaurantLogo(int restaurantLogo) {
		this.mRestaurantLogo = restaurantLogo;
	}

	public String getRestaurantName() {
		return mRestaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.mRestaurantName = restaurantName;
	}

	public int getTotalMessage() {
		return mTotalMessage;
	}

	public void setTotalMessage(int totalMessage) {
		this.mTotalMessage = totalMessage;
	}

	public String getDatetimeRecent() {
		return mDatetimeRecent;
	}

	public void setDatetimeRecent(String datetimeRecent) {
		this.mDatetimeRecent = datetimeRecent;
	}

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double distance) {
		this.mDistance = distance;
	}

}

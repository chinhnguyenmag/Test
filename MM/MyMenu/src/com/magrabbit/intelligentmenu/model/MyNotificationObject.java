package com.magrabbit.intelligentmenu.model;

/**
 * @author vule
 *
 */
public class MyNotificationObject {

	private int mRestaurantLogo;
	private String mRestaurantName = "";
	private int mTotalMessage;
	private String mDatetimeRecent = "";
	private double mDistance;

	public MyNotificationObject(int restaurantLogo, String restaurantName,
			int totalMessage, String datetimeRecent, double distance) {
		super();
		this.mRestaurantLogo = restaurantLogo;
		this.mRestaurantName = restaurantName;
		this.mTotalMessage = totalMessage;
		this.mDatetimeRecent = datetimeRecent;
		this.mDistance = distance;
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

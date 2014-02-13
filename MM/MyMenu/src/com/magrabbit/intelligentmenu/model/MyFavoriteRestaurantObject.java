package com.magrabbit.intelligentmenu.model;

/**
 * @author vule
 *
 */
public class MyFavoriteRestaurantObject {

	private int mId;
	private int mLogo;
	private String mName;
	private String mRating;
	private int mIdRestaurant;
	private int mIdItemRestaurant;
	private int mIdCategory;

	public MyFavoriteRestaurantObject() {
	}

	public MyFavoriteRestaurantObject(int mId, int mLogo, String mName,
			String mRating, int mIdRestaurant, int mIdItemRestaurant,
			int mIdCategory) {
		super();
		this.mId = mId;
		this.mLogo = mLogo;
		this.mName = mName;
		this.mRating = mRating;
		this.mIdRestaurant = mIdRestaurant;
		this.mIdItemRestaurant = mIdItemRestaurant;
		this.mIdCategory = mIdCategory;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getLogo() {
		return mLogo;
	}

	public void setLogo(int mLogo) {
		this.mLogo = mLogo;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getRating() {
		return mRating;
	}

	public void setRating(String mRating) {
		this.mRating = mRating;
	}

	public int getIdRestaurant() {
		return mIdRestaurant;
	}

	public void setIdRestaurant(int mIdRestaurant) {
		this.mIdRestaurant = mIdRestaurant;
	}

	public int getIdItemRestaurant() {
		return mIdItemRestaurant;
	}

	public void setIdItemRestaurant(int mIdItemRestaurant) {
		this.mIdItemRestaurant = mIdItemRestaurant;
	}

	public int getIdCategory() {
		return mIdCategory;
	}

	public void setIdCategory(int mIdCategory) {
		this.mIdCategory = mIdCategory;
	}

}

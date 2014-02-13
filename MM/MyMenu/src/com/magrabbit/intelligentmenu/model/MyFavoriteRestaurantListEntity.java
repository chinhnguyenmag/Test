package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyFavoriteRestaurantListEntity {

	@SerializedName("category_id")
	public int mCategoryId;

	@SerializedName("id")
	public int mId;

	@SerializedName("name")
	public String mName;

	@SerializedName("rating")
	public String mRating;

	@SerializedName("category_name")
	public String mCategoryName = "";

	@SerializedName("menu_id")
	public int mMenuId;

	@SerializedName("type")
	public int mType;

	@SerializedName("item_images")
	public List<MyFavoriteListItemImgsEntity> mListItemImgs;

	public MyFavoriteRestaurantListEntity(int mId, String mName,
			String mRating, int mType,
			List<MyFavoriteListItemImgsEntity> mListItemImgs) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mRating = mRating;
		this.mType = mType;
		this.mListItemImgs = mListItemImgs;
	}

	public String getName() {
		if (mName != null) {
			return mName;
		} else
			return "";
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getCategoryName() {
		if (mCategoryName != null) {
			return mCategoryName;
		} else
			return "";
	}

	public void setCategoryName(String mCategoryName) {
		this.mCategoryName = mCategoryName;
	}

	public int getType() {
		return mType;
	}

	public void setType(int mType) {
		this.mType = mType;
	}
}

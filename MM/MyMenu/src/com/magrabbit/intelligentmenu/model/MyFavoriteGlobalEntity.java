package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyFavoriteGlobalEntity {
	
	@SerializedName("favourites")
	public List<MyFavoriteGlobalListEntity> mListFavorites;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

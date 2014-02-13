package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyFavoriteChainEntity {
	
	@SerializedName("favourites")
	public List<MyFavoriteChainListEntity> mListFavorites;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}

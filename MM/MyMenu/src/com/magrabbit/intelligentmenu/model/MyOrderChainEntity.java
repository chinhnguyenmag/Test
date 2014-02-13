package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @Description This model is used by both screen MyOrder Global and Chain to
 *              get the list of orders
 */
public class MyOrderChainEntity {

	@SerializedName("locations")
	public List<MyOrderChainObject> mListChainOrders;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

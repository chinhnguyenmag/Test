package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MenuItemOrderEntity {
	
	@SerializedName("status")
	public String mStatus;
	
	@SerializedName("order_id")
	public int mOrderId;
	
	@SerializedName("order_item_id")
	public int mOrderItemId;
	
	@SerializedName("order_item_status")
	public int mOrderItemStatus;
}

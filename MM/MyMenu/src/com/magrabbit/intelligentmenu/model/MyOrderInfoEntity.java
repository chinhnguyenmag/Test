package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyOrderInfoEntity {

	@SerializedName("order_info")
	public List<OrderInfoEntity> mListOrderInfos;
	@SerializedName("items")
	public List<OrderItemEntity> mListItems;
}

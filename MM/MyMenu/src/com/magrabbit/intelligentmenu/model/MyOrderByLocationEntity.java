package com.magrabbit.intelligentmenu.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyOrderByLocationEntity {

//	@SerializedName("order_total")
//	public List<OrderDetailsTotalEntity> mListOrderTotals;
	
	@SerializedName("orders")
	public List<OrderByLocationEntity> mListOrderDetails;
	
	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}

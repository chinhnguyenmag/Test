package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CurrentOrderObject implements Serializable {
	@SerializedName("id")
	private int mId;

	@SerializedName("items")
	private List<CurrentOrderItem> mListCurrentOrderItem;

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public List<CurrentOrderItem> getmListCurrentOrderItem() {
		if (mListCurrentOrderItem == null) {
			mListCurrentOrderItem = new ArrayList<CurrentOrderItem>();
		}
		return mListCurrentOrderItem;
	}

	public void setmListCurrentOrderItem(
			List<CurrentOrderItem> mListCurrentOrderItem) {
		this.mListCurrentOrderItem = mListCurrentOrderItem;
	}
}

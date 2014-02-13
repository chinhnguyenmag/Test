package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

public class MenuTypeObject {
	@SerializedName("id")
	public int mID;
	@SerializedName("name")
	public String mName = "";
	@SerializedName("created_at")
	public String mDateCreate = "";
	@SerializedName("updated_at")
	public String mDateUpdate = "";

	public boolean mIsChecked = false;

	public MenuTypeObject() {
		super();
	}

	public MenuTypeObject(int mID, String mName, String mDateCreate,
			String mDateUpdate, boolean mIsChecked) {
		super();
		this.mID = mID;
		this.mName = mName;
		this.mDateCreate = mDateCreate;
		this.mDateUpdate = mDateUpdate;
		this.mIsChecked = mIsChecked;
	}

	public MenuTypeObject clone() {
		return new MenuTypeObject(mID, mName, mDateCreate, mDateUpdate,
				mIsChecked);
	}

}

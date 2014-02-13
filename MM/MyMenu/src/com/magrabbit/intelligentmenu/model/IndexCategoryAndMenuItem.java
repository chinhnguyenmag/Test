package com.magrabbit.intelligentmenu.model;


public class IndexCategoryAndMenuItem  {

	private int mIndexCategory;
	private int mIndexMenuItem;

	public IndexCategoryAndMenuItem(int mIndexCategory, int mIndexMenuItem) {
		super();
		this.mIndexCategory = mIndexCategory;
		this.mIndexMenuItem = mIndexMenuItem;
	}

	public int getIndexCategory() {
		return mIndexCategory;
	}

	public void setIndexCategory(int mIndexCategory) {
		this.mIndexCategory = mIndexCategory;
	}

	public int getIndexMenuItem() {
		return mIndexMenuItem;
	}

	public void setIndexMenuItem(int mIndexMenuItem) {
		this.mIndexMenuItem = mIndexMenuItem;
	}

}

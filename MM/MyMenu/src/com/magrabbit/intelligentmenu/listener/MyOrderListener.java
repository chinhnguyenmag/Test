package com.magrabbit.intelligentmenu.listener;

public interface MyOrderListener {
	void ChangeSubTotal(float total,boolean isOrderChange);
	void startActivityForResult();
	void totalPoint(float point);
	void deleteItem(int position);
}
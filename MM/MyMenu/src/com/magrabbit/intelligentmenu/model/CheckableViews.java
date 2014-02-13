package com.magrabbit.intelligentmenu.model;

import android.widget.TextView;

public class CheckableViews {
	boolean checked;
	TextView nameRestaurant;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public TextView getNameRestaurant() {
		return nameRestaurant;
	}
	public void setNameRestaurant(TextView nameRestaurant) {
		this.nameRestaurant = nameRestaurant;
	}
}

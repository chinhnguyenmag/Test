package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magrabbit.intelligentmenu.activity.GridFragment;

public class PagerAdapterCustom extends FragmentStatePagerAdapter {
	private List<GridFragment> fragments;

	public PagerAdapterCustom(FragmentManager fm, List<GridFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}

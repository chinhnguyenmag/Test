package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magrabbit.intelligentmenu.activity.GridMenuItemFragment;

/**
 * @author Vu Le
 *
 */
public class PagerCustomGridAdapter extends FragmentStatePagerAdapter {
	private List<GridMenuItemFragment> mFragments;

	public PagerCustomGridAdapter(FragmentManager fm, List<GridMenuItemFragment> fragments) {
		super(fm);
		this.mFragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.mFragments.get(position);
	}

	@Override
	public int getCount() {
		return this.mFragments.size();
	}
}

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magrabbit.intelligentmenu.activity.GridFragmentMenuItemHorizontal;

/**
 * @author Vu Le
 *
 */
public class PagerCustomHorizontalGridAdapter extends FragmentStatePagerAdapter {
	private List<GridFragmentMenuItemHorizontal> mFragments;

	public PagerCustomHorizontalGridAdapter(FragmentManager fm, List<GridFragmentMenuItemHorizontal> fragments) {
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

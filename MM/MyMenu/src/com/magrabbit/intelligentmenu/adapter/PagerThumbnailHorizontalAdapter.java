package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magrabbit.intelligentmenu.activity.GridFragmentThumbnailHorizontal;

public class PagerThumbnailHorizontalAdapter extends FragmentStatePagerAdapter {
	private List<GridFragmentThumbnailHorizontal> fragments;

	public PagerThumbnailHorizontalAdapter(FragmentManager fm, List<GridFragmentThumbnailHorizontal> fragments) {
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

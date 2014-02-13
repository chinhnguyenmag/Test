package com.magrabbit.intelligentmenu.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.GridAdapter;
import com.magrabbit.intelligentmenu.listener.RestaurantDetailsListener;
import com.magrabbit.intelligentmenu.model.GridItems;

/**
 * @author Hung Hoang
 * @description Layout for thumbnail restaurant.
 */
@SuppressLint("ValidFragment")
public class GridFragment extends Fragment {

	private GridView mGridView;
	private GridAdapter mGridAdapter;
	private GridItems[] gridItems = {};
	private Activity activity;
	private RestaurantDetailsListener mListener;

	public GridFragment(GridItems[] gridItems, Activity activity,
			RestaurantDetailsListener mListener) {
		this.gridItems = gridItems;
		this.activity = activity;
		this.mListener = mListener;
	}

	public GridFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		if (container != null) {
			view = inflater.inflate(R.layout.grid_thumbnail, container, false);
			mGridView = (GridView) view.findViewById(R.id.gridView);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			if (activity != null) {

				mGridAdapter = new GridAdapter(activity, gridItems,true);
				if (mGridView != null) {
					mGridView.setAdapter(mGridAdapter);
				}

				mGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onGridItemClick((GridView) parent, view, position, id);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param g
	 * @param v
	 * @param position
	 * @param id
	 */
	public void onGridItemClick(GridView g, View v, int position, long id) {
		try {
			mListener.isShowButton(true,
					(int) mGridAdapter.getItemId(position), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
package com.magrabbit.intelligentmenu.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
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
import com.magrabbit.intelligentmenu.adapter.ViewPageAdapter;
import com.magrabbit.intelligentmenu.listener.MenuItemStatusListener;
import com.magrabbit.intelligentmenu.listener.MenuItemThumbnailListener;
import com.magrabbit.intelligentmenu.model.GridItems;
import com.magrabbit.intelligentmenu.utils.Constants;

/**
 * @author Vule
 * @Description This class use to view grid view item item when the user chose
 *              view thumbnail, user can single tap or double tap to view food
 *              item.
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("ValidFragment")
public class GridMenuItemFragment extends Fragment {

	private GridView mGridView;
	private GridAdapter mGridAdapter;
	private GridItems[] mGridItems = {};
	private MenuItemThumbnailListener mListener;
	private MenuItemStatusListener mBtListener;
	@SuppressLint("ValidFragment")
	private Activity mActivity;
	private static long mPressed;
	private List<String> mPhotos;
	private int mPosition = -1;

	public GridMenuItemFragment(GridItems[] gridItems, Activity activity,
			List<String> mPhotos, MenuItemThumbnailListener mListener,
			MenuItemStatusListener mButtonListener) {
		this.mGridItems = gridItems;
		this.mActivity = activity;
		this.mListener = mListener;
		this.mPhotos = mPhotos;
		this.mBtListener = mButtonListener;
	}

	public GridMenuItemFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.grid_thumbnail, container, false);
		mGridView = (GridView) view.findViewById(R.id.gridView);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			mGridAdapter = new GridAdapter(mActivity, mGridItems,false);
			// && mGridItems != null
			if (mGridView != null) {
				mGridView.setAdapter(mGridAdapter);
				// mGridAdapter.notifyDataSetChanged();
			}
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// view.setBackgroundResource(0);
					if (mPosition == -1) {
						mPosition = position;
					}
					if (mPressed + 500 > System.currentTimeMillis()
							&& mPosition == position) {
						onGridItemDoubleClick(position);
						mPosition = -1;
					} else if (mPressed + 500 <= System.currentTimeMillis()) {
						Constants.mIdSelected=mGridItems[position].getId();
						onGridItemClick((GridView) parent, view, position, id);
						mPressed = System.currentTimeMillis();
						mPosition = -1;
					}
					mGridAdapter.notifyDataSetChanged();
				}
				
			});
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
			mListener.PostionItem((int) mGridAdapter.getItemId(position));
			v.setBackgroundResource(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param position
	 */
	public void onGridItemDoubleClick(int position) {
		MenuItemActivity.mViewAdapter = new ViewPageAdapter(mActivity, mPhotos,
				true);
		MenuItemActivity.mVpPhotos.setAdapter(MenuItemActivity.mViewAdapter);
		MenuItemActivity.mCirIndicator.setViewPager(MenuItemActivity.mVpPhotos);
		MenuItemActivity.mCirIndicator.setCurrentItem((int) mGridAdapter
				.getItemId(position));
		mBtListener.ChangeStatus(true);
	}

	@Override
	public void onResume() {
		mGridAdapter = new GridAdapter(mActivity, mGridItems,false);
		// && mGridItems != null
		if (mGridView != null) {
			mGridView.setAdapter(mGridAdapter);
		}
		super.onResume();
	}
}
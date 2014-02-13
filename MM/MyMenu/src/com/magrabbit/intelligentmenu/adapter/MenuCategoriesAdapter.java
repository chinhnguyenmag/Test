package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogMenu.ProcessDialogMenu;
import com.magrabbit.intelligentmenu.model.CategoryObject;

/**
 * @description Adapter for MenuCategory.
 * @author Hung Hoang
 * 
 */
public class MenuCategoriesAdapter extends ArrayAdapter<CategoryObject> {

	private Context mContext;
	private int layoutResourceId;
	private List<CategoryObject> mData = null;
	private ArrayList<CategoryObject> mListCategoryFilter;
	private List<CategoryObject> mDisplayedValues = new ArrayList<CategoryObject>();
	private ProcessDialogMenu pro;
	private String logo;
	private String website;
	private String dinnerStatus;
	private float pointNumber;
	private int idRestaurant;
	private boolean isActivityFavorite;

	public MenuCategoriesAdapter(Context context, int layoutResourceId,
			List<CategoryObject> data, ProcessDialogMenu pro, String logo,
			String website, String dinnerStatus, float pointNumber,
			int IdRestaurant, boolean isActivityFavorite) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = context;
		this.mData = data;
		this.mListCategoryFilter = new ArrayList<CategoryObject>();
		this.mListCategoryFilter.addAll(mData);
		this.pro = pro;
		this.logo = logo;
		this.website = website;
		this.isActivityFavorite = isActivityFavorite;
		this.dinnerStatus = dinnerStatus;
		this.idRestaurant = IdRestaurant;
		mDisplayedValues.addAll(data);
	}

	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		CategoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new CategoryHolder();
			holder.mTvNameCategory = (TextView) row
					.findViewById(R.id.menu_item_tv_name_item);
			holder.mTvTotalMenuItem = (TextView) row
					.findViewById(R.id.menu_item_tv_total_item);
			holder.mIvFavorite = (ImageView) row
					.findViewById(R.id.menu_item_iv_favorite_categories);
			holder.mIvNextTime = (ImageView) row
					.findViewById(R.id.menu_item_iv_nextime_categories);
			row.setTag(holder);
		} else {
			holder = (CategoryHolder) row.getTag();
		}

		CategoryObject category = mData.get(position);
		holder.mTvNameCategory.setText(category.getmNameCategory());
		holder.mTvTotalMenuItem.setText("("
				+ mData.get(position).getNumberItem() + ")");

		if (mData.get(position).isIsFavorite() == 1) {
			holder.mIvFavorite.setVisibility(View.VISIBLE);
		} else {
			holder.mIvFavorite.setVisibility(View.GONE);
		}
		if (mData.get(position).isIsNextTime() == 1) {
			holder.mIvNextTime.setVisibility(View.VISIBLE);
		} else {
			holder.mIvNextTime.setVisibility(View.GONE);
		}
		holder.mIvFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pro.click_Dialog(logo, mData.get(position).getmId(),
						mData.get(position).getmNameCategory(), website,
						dinnerStatus, pointNumber, idRestaurant, true, false,
						isActivityFavorite);
			}
		});
		holder.mIvNextTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pro.click_Dialog(logo, mData.get(position).getmId(),
						mData.get(position).getmNameCategory(), website,
						dinnerStatus, pointNumber, idRestaurant, false, true,
						isActivityFavorite);
			}
		});
		return row;
	}

	static class CategoryHolder {
		TextView mTvNameCategory;
		TextView mTvTotalMenuItem;
		ImageView mIvFavorite;
		ImageView mIvNextTime;
	}

	public void resetData() {
		mData.clear();
		mData.addAll(mListCategoryFilter);
	}
}

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyOrderGlobalObject;
import com.magrabbit.intelligentmenu.server.ServerURL;

public class MyOrderGlobalAdapter extends ArrayAdapter<MyOrderGlobalObject> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<MyOrderGlobalObject> mListRestaurantGlobals;

	// Loader for images
	private ImageLoader mImageLoader;

	public MyOrderGlobalAdapter(Context mContext, int resource,
			List<MyOrderGlobalObject> mListRestaurants) {
		super(mContext, resource, mListRestaurants);
		this.mContext = mContext;
		this.mListRestaurantGlobals = mListRestaurants;
		mLayoutInflater = LayoutInflater.from(mContext);

		// Loader Initializing
		this.mImageLoader = new ImageLoader(this.mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.myorder_global_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mIvIcon = (ImageView) convertView
					.findViewById(R.id.myorder_global_item_restaurant_icon);
			viewHolder.mTvNameRestaurant = (TextView) convertView
					.findViewById(R.id.myorder_global_item_restaurant_name);
			viewHolder.mTvNumberOrder = (TextView) convertView
					.findViewById(R.id.myorder_global_item_restaurant_number);
			/*
			 * viewHolder.mTvDate = (TextView) convertView
			 * .findViewById(R.id.mypoint_restaurant_date);
			 */
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL
					+ mListRestaurantGlobals.get(position).getLogo(),
					viewHolder.mIvIcon);
			viewHolder.mTvNameRestaurant.setText(mListRestaurantGlobals.get(
					position).getName());
			viewHolder.mTvNumberOrder.setText("("
					+ mListRestaurantGlobals.get(position).getNumber() + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView mIvIcon;
		TextView mTvNameRestaurant;
		TextView mTvNumberOrder;

	}
}

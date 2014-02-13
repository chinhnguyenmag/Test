package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.RestaurantObject;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @description Control the list of restaurants to apply into List View
 */
public class RestaurantListViewAdapter extends ArrayAdapter<RestaurantObject> {

	private List<RestaurantObject> mListRestaurants;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	// private ArrayList<RestaurantDetail> filtered = null;
	// private Filter filter;
	private AdapterControlListener mListener;
	private ImageLoader mImageLoader;

	public RestaurantListViewAdapter(Context context,
			List<RestaurantObject> listData, AdapterControlListener listener) {
		super(context, R.id.listview_restaurant_item_name, listData);
		setListData(listData);
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.mListener = listener;

		// Load images from Server and set default sizes
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setSizeBitmap(60, 60);
	}

	private void setListData(List<RestaurantObject> listData) {
		if (this.mListRestaurants == null)
			this.mListRestaurants = new ArrayList<RestaurantObject>();
		this.mListRestaurants.clear();
		this.mListRestaurants.addAll(listData);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		try {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.listview_restaurant_item, parent, false);
				holder = new ViewHolder();

				holder.mIvResLogo = (ImageView) convertView
						.findViewById(R.id.listview_restaurant_item_logo);
				holder.mTvResName = (TextView) convertView
						.findViewById(R.id.listview_restaurant_item_name);
				holder.mTvResDistance = (TextView) convertView
						.findViewById(R.id.listview_restaurant_item_distance);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final RestaurantObject obj = getItem(position);

			if (obj != null) {
				holder.mIvResLogo.setScaleType(ScaleType.FIT_XY);
				mImageLoader.DisplayImage(ServerURL.URL + obj.getLogo(),
						holder.mIvResLogo);
				holder.mTvResName.setText(obj.getNameRestaurant());
				// Distances will be given by Server/ Google Places
				holder.mTvResDistance.setText(Utils.formatDecimal(obj.getDistance(), "0.00") + " mi");
			}

			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (Utils.isNetworkConnected(mContext)) {
						mListener.clickItem(obj);
					} else {
						Toast.makeText(mContext, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView mIvResLogo;
		TextView mTvResName;
		TextView mTvResDistance;
	}

	public static abstract class AdapterControlListener {

		public abstract void clickItem(RestaurantObject obj);
	}

	/*
	 * @Override public Filter getFilter() { if (filter == null) filter = new
	 * ComplexFilter(); return filter; }
	 * 
	 * private class ComplexFilter extends Filter {
	 * 
	 * @Override protected FilterResults performFiltering(CharSequence
	 * constraint) { FilterResults retval = new FilterResults();
	 * 
	 * if (constraint != null && constraint.toString().length() > 0) {
	 * constraint = constraint.toString().toUpperCase();
	 * ArrayList<RestaurantDetail> filt = new ArrayList<RestaurantDetail>();
	 * 
	 * for (int i = 0; i < items.size(); i++) { RestaurantDetail it =
	 * items.get(i); if (it.name.toUpperCase().contains(constraint)) {
	 * filt.add(it); }
	 * 
	 * } retval.count = filt.size(); retval.values = filt; } else { synchronized
	 * (this) { retval.values = items; retval.count = items.size(); } } return
	 * retval; }
	 * 
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override protected void publishResults(CharSequence constraint,
	 * FilterResults results) { filtered = (ArrayList<RestaurantDetail>)
	 * results.values; notifyDataSetChanged(); clear(); for (int i = 0, l =
	 * filtered.size(); i < l; i++) add(filtered.get(i));
	 * notifyDataSetInvalidated(); } }
	 */

}

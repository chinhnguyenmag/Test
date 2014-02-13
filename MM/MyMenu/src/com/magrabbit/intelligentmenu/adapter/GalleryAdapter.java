package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.RestaurantObject;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @description Adapter for sliding images
 */
public class GalleryAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private static ViewHolder holder;
	private Context mContext;
	private List<RestaurantObject> mListRestaurants;

	// For loading images from Server
	private ImageLoader mImageLoader;

	public GalleryAdapter(Context context, List<RestaurantObject> listRestaurants) {

		this.mContext = context;
		this.mListRestaurants = listRestaurants;

		mLayoutInflater = LayoutInflater.from(context);

		// Load images from server and set default sizes
		this.mImageLoader = new ImageLoader(mContext);
		this.mImageLoader.setSizeBitmap(60, 60);
	}

	/**
	 * @description return number of photos in the list
	 */
	@Override
	public int getCount() {
		return mListRestaurants.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			convertView = mLayoutInflater.inflate(R.layout.gallery_item,
					parent, false);
			holder = new ViewHolder();

			holder.mIvLogo = (ImageView) convertView
					.findViewById(R.id.gallery_item_resImage);
			holder.mTvDistance = (TextView) convertView
					.findViewById(R.id.gallery_item_resDistance);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		mImageLoader
				.DisplayImage(ServerURL.URL + mListRestaurants.get(position).getLogo(),
						holder.mIvLogo);
		holder.mTvDistance.setText(Utils.formatDecimal(mListRestaurants.get(position).getDistance(), "0.00") + " mi");

		return convertView;
	}

	public static class ViewHolder {
		ImageView mIvLogo;
		TextView mTvDistance;
	}

}
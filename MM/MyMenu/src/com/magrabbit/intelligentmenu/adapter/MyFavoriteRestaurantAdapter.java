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
import com.magrabbit.intelligentmenu.model.MyFavoriteRestaurantListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * 
 */
public class MyFavoriteRestaurantAdapter extends
		ArrayAdapter<MyFavoriteRestaurantListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyFavoriteRestaurantListEntity> mData;
	private ImageLoader mImageLoader;

	public MyFavoriteRestaurantAdapter(Context context, int resourceId,
			List<MyFavoriteRestaurantListEntity> listFavoriteRestaurants) {
		super(context, resourceId, listFavoriteRestaurants);
		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(context);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
		mData = listFavoriteRestaurants;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (viewHolder == null) {
			convertView = mLiInflater.inflate(
					R.layout.myfavorite_restaurant_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.myfavorite_restaurant_item_iv_avatar);
			viewHolder.mName = (TextView) convertView
					.findViewById(R.id.myfavorite_restaurant_item_tv_name);
			viewHolder.mRating = (TextView) convertView
					.findViewById(R.id.myfavorite_restaurant_tv_rating);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			if (mData.get(position).mListItemImgs != null
					&& mData.get(position).mListItemImgs.size() > 0) {
				mImageLoader.DisplayImage(ServerURL.URL
						+ mData.get(position).mListItemImgs.get(0).mImage,
						viewHolder.mIvlogo);
			} else {
				viewHolder.mIvlogo
						.setBackgroundResource(R.drawable.ic_load_img_150);
			}

			viewHolder.mName.setText(mData.get(position).getName());
			if (Double.parseDouble(mData.get(position).mRating) > 0) {
				viewHolder.mRating.setText(""
						+ Utils.exchangeRateGetExtra(Float.parseFloat(mData
								.get(position).mRating)));
			} else {
				viewHolder.mRating.setText("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	/**
	 * @author Vu Le This method will keep the value when the user swipe list
	 *         view
	 * 
	 */
	private class ViewHolder {
		ImageView mIvlogo;
		TextView mName;
		TextView mRating;
	}

}

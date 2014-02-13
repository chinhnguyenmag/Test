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
import com.magrabbit.intelligentmenu.model.MyFavoriteGlobalListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author vule
 *
 */
public class MyFavoriteGlobalAdapter extends
		ArrayAdapter<MyFavoriteGlobalListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyFavoriteGlobalListEntity> mData;
	private ImageLoader mImageLoader;

	public MyFavoriteGlobalAdapter(Context context, int resourceId,
			List<MyFavoriteGlobalListEntity> listFavorites) {
		super(context, resourceId, listFavorites);
		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
		mData = listFavorites;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (viewHolder == null) {
			convertView = mLiInflater.inflate(R.layout.myfavorite_global_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.myfavorite_global_item_iv_avatar);
			viewHolder.mTvRestaurantName = (TextView) convertView
					.findViewById(R.id.myfavorite_global_item_tv_name);
			viewHolder.mTvTotalFavorite = (TextView) convertView
					.findViewById(R.id.myfavorite_global_tv_total_favorite);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageLoader.DisplayImage(
					ServerURL.URL + mData.get(position).mLogo,
					viewHolder.mIvlogo);
			viewHolder.mTvRestaurantName.setText(mData.get(position).mChainName);
			viewHolder.mTvTotalFavorite.setText("("
					+ mData.get(position).mTotalFavorites + ")");

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
		TextView mTvRestaurantName;
		TextView mTvTotalFavorite;
	}

}

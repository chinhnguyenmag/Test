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
import com.magrabbit.intelligentmenu.model.MyFavoriteChainListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author vule
 *
 */
public class MyFavoriteChainAdapter extends ArrayAdapter<MyFavoriteChainListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyFavoriteChainListEntity> mData;
	private ImageLoader mImageLoader;

	public MyFavoriteChainAdapter(Context context, int resource,
			List<MyFavoriteChainListEntity> listChainFavorites) {
		super(context, resource, listChainFavorites);
		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(context);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
		mData = listChainFavorites;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (viewHolder == null) {
			convertView = mLiInflater.inflate(R.layout.myfavorite_chain_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.myfavorite_chain_icon);
			viewHolder.mTvName = (TextView) convertView
					.findViewById(R.id.myfavorite_chain_name);
			viewHolder.mStreet = (TextView) convertView
					.findViewById(R.id.myfavorite_chain_street);
			viewHolder.mState = (TextView) convertView
					.findViewById(R.id.myfavorite_chain_state);
			viewHolder.mTvTotalFavorite = (TextView) convertView
					.findViewById(R.id.myfavorite_tv_total_favorite);
			viewHolder.mMostRecent = (TextView) convertView
					.findViewById(R.id.myfavorite_tv_most_recent);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			 mImageLoader.DisplayImage(
			 ServerURL.URL + mData.get(position).getLogo(),
			 viewHolder.mIvlogo);
			viewHolder.mTvName.setText(mData.get(position).getName());
			viewHolder.mStreet.setText(mData.get(position).getAddress());
			viewHolder.mState.setText(mData.get(position).getCity()+", "+mData.get(position).getState()+" "+mData.get(position).getZipcode());
			viewHolder.mTvTotalFavorite.setText("("
					+ mData.get(position).mTotalFavorites + ")");
			viewHolder.mMostRecent.setText("Most Recent: "
					+ mData.get(position).getMostRecent());
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
		TextView mTvName;
		TextView mStreet;
		TextView mState;
		TextView mTvTotalFavorite;
		TextView mMostRecent;
	}

}

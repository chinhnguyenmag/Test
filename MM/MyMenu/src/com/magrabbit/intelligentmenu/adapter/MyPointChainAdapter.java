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
import com.magrabbit.intelligentmenu.model.MyPointChainListLocationEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 *
 */
public class MyPointChainAdapter extends
		ArrayAdapter<MyPointChainListLocationEntity> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<MyPointChainListLocationEntity> mListChains;

	// Loader for images
	private ImageLoader mImageLoader;

	public MyPointChainAdapter(Context mContext, int resource,
			List<MyPointChainListLocationEntity> mListChains) {
		super(mContext, resource, mListChains);
		this.mContext = mContext;
		this.mListChains = mListChains;
		mLayoutInflater = LayoutInflater.from(mContext);
		// Loader Initializing
		mImageLoader = new ImageLoader(this.mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.mypoint_chain_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mIvIcon = (ImageView) convertView
					.findViewById(R.id.mypoint_store_icon);
			viewHolder.mTvNameChain = (TextView) convertView
					.findViewById(R.id.mypoint_store_name);
			viewHolder.mTvStreet = (TextView) convertView
					.findViewById(R.id.mypoint_store_street);
			viewHolder.mTvState = (TextView) convertView
					.findViewById(R.id.mypoint_store_state);
			viewHolder.mTvPoint = (TextView) convertView
					.findViewById(R.id.mypoint_store_point);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL
					+ mListChains.get(position).getLogo(), viewHolder.mIvIcon);
			viewHolder.mTvNameChain.setText(mListChains.get(position).getName());
			viewHolder.mTvStreet.setText(mListChains.get(position).getAddress());
			viewHolder.mTvState.setText(mListChains.get(position).getCity() + ", "
					+ mListChains.get(position).getState() + " "
					+ mListChains.get(position).getZip());
			if (mListChains.get(position).mTotal > 1) {
				viewHolder.mTvPoint.setText(Utils.formatPointNumbers(
						mListChains.get(position).mTotal)
						+ " Points");
			} else {
				viewHolder.mTvPoint.setText(Utils.formatPointNumbers(
						mListChains.get(position).mTotal)
						+ " Point");
			}
			/*
			 * viewHolder.mTvDate.setText(mListChains.get(position).mMostRecent);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView mIvIcon;
		TextView mTvNameChain;
		TextView mTvStreet;
		TextView mTvState;
		TextView mTvPoint;
		// TextView mTvDate;

	}
}

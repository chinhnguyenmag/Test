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
import com.magrabbit.intelligentmenu.model.MyNotificationChainListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author Vule
 * 
 */
public class MyNotificationChainAdapter extends
		ArrayAdapter<MyNotificationChainListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyNotificationChainListEntity> mData;
	private ImageLoader mImageloader;

	public MyNotificationChainAdapter(Context context, int resourceId,
			List<MyNotificationChainListEntity> lstObject) {
		super(context, resourceId, lstObject);

		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
		mImageloader = new ImageLoader(context);
		mImageloader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageloader.setSizeBitmap(60, 60);
		mData = lstObject;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = mLiInflater.inflate(
					R.layout.mynotification_chain_item_detail, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.mynotification_chain_icon);
			viewHolder.mTvChainName = (TextView) convertView
					.findViewById(R.id.mynotification_chain_name);
			viewHolder.mTvStreet = (TextView) convertView
					.findViewById(R.id.mynotification_chain_street);
			viewHolder.mTvState = (TextView) convertView
					.findViewById(R.id.mynotification_chain_state);
			viewHolder.mTvTotalMessage = (TextView) convertView
					.findViewById(R.id.mynotification_tv_total_message);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageloader.DisplayImage(ServerURL.URL
					+ mData.get(position).getLogo(), viewHolder.mIvlogo);
			viewHolder.mTvChainName.setText(mData.get(position).getName());
			viewHolder.mTvStreet.setText(mData.get(position).getAddress());
			viewHolder.mTvState.setText(mData.get(position).getCity() + ", "
					+ mData.get(position).getState() + " "
					+ mData.get(position).getZip());
			if (mData.get(position).mUnread != 0) {
				viewHolder.mTvTotalMessage.setVisibility(View.VISIBLE);
				viewHolder.mTvTotalMessage.setText("("
						+ mData.get(position).mUnread + ")");
			} else {
				viewHolder.mTvTotalMessage.setVisibility(View.INVISIBLE);
				 viewHolder.mTvTotalMessage.setText("(0)");
				// viewHolder.mTvTotalMessage.setTextColor(mContext.getResources()
				// .getColor(R.color.White));
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
		TextView mTvChainName;
		TextView mTvStreet;
		TextView mTvState;
		TextView mTvTotalMessage;
	}

}

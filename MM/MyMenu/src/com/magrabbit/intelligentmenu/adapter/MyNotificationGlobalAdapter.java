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
import com.magrabbit.intelligentmenu.model.MyNoficationGlobalListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author Vu le
 * 
 */
public class MyNotificationGlobalAdapter extends
		ArrayAdapter<MyNoficationGlobalListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyNoficationGlobalListEntity> mData;
	private ImageLoader mImageLoader;

	public MyNotificationGlobalAdapter(Context context, int resource,
			List<MyNoficationGlobalListEntity> listMyNotification) {
		super(context, resource, listMyNotification);
		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(context);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
		mData = listMyNotification;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = mLiInflater.inflate(
					R.layout.mynotification_global_item_detail, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.mynotification_item_iv_avatar);
			viewHolder.mTvRestaurantName = (TextView) convertView
					.findViewById(R.id.mynotification_item_tv_name);
			viewHolder.mTvTotalMessage = (TextView) convertView
					.findViewById(R.id.mynotification_tv_total_message);
			viewHolder.mTvDateTime = (TextView) convertView
					.findViewById(R.id.mynotification_tv_most_recent);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL
					+ mData.get(position).getLogo(), viewHolder.mIvlogo);
			viewHolder.mTvRestaurantName.setText(mData.get(position)
					.getChainName());
			if (mData.get(position).mUnread != 0) {
				viewHolder.mTvTotalMessage.setVisibility(View.VISIBLE);
				viewHolder.mTvTotalMessage.setText("("
						+ mData.get(position).mUnread + ")");
			} else {
				viewHolder.mTvTotalMessage.setVisibility(View.GONE);
				// viewHolder.mTvTotalMessage.setTextColor(mContext.getResources()
				// .getColor(R.color.White));
			}

			viewHolder.mTvDateTime.setText("Most Recent: "
					+ mData.get(position).getMostRecent());
		} catch (Exception e) {

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
		TextView mTvTotalMessage;
		TextView mTvDateTime;
	}

}

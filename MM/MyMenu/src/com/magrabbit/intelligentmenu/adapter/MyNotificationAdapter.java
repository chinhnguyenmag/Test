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
import com.magrabbit.intelligentmenu.model.MyNotificationObject;

/**
 * @author Vu le
 *
 */
public class MyNotificationAdapter extends ArrayAdapter<MyNotificationObject> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyNotificationObject> mData;

	public MyNotificationAdapter(Context context, int resource,
			List<MyNotificationObject> listMyNotification) {
		super(context, resource, listMyNotification);
		mContext = context;
		mLiInflater = LayoutInflater.from(mContext);
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
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			viewHolder.mIvlogo.setBackgroundResource(mData.get(position).getRestaurantLogo());
			viewHolder.mTvRestaurantName.setText(mData.get(position).getRestaurantName());
			viewHolder.mTvTotalMessage.setText(mData.get(position).getTotalMessage()+"");
			viewHolder.mTvDateTime.setText(mData.get(position).getDatetimeRecent());
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

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyNotificationRestaurantListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author vule
 *
 */
public class MyNotificationRestaurantAdapter extends
		ArrayAdapter<MyNotificationRestaurantListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyNotificationRestaurantListEntity> mData;
	private ImageLoader mImageloader;

	public MyNotificationRestaurantAdapter(Context context, int resourceId,
			List<MyNotificationRestaurantListEntity> lstObject) {
		super(context, resourceId, lstObject);
		this.mContext = context;
		this.mLiInflater = LayoutInflater.from(mContext);
		mImageloader = new ImageLoader(context);
		mImageloader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageloader.setSizeBitmap(60, 60);
		this.mData = lstObject;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLiInflater.inflate(
					R.layout.mynotification_restaurant_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvlogo = (ImageView) convertView
					.findViewById(R.id.mynotification_restaurant_logo_msg);
			viewHolder.mTvSubName = (TextView) convertView
					.findViewById(R.id.mynotification_restaurant_msg_subject);
			viewHolder.mTvSubContent = (TextView) convertView
					.findViewById(R.id.mynotification_restaurant_msg);
			viewHolder.mTvDate = (TextView) convertView
					.findViewById(R.id.mynotification_restaurant_tv_date_message);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			mImageloader.DisplayImage(ServerURL.URL
					+ mData.get(position).getAlertLogo(), viewHolder.mIvlogo);

			viewHolder.mTvSubName.setText(mData.get(position).getMsgSubject());
			viewHolder.mTvDate.setText(mData.get(position).getmUpdateDate());
			if (mData.get(position).mStatus == 0) {
				viewHolder.mTvSubName.setTextColor(mContext.getResources()
						.getColor(R.color.mynotification_unread));
				viewHolder.mTvDate.setTextColor(mContext.getResources()
						.getColor(R.color.mynotification_unread));
			} else {
				viewHolder.mTvSubName.setTextColor(Color.WHITE);
				viewHolder.mTvDate.setTextColor(Color.WHITE);
			}
			viewHolder.mTvSubContent.setText(Html.fromHtml(mData.get(position).getMessage()));
			
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
		TextView mTvSubName;
		TextView mTvSubContent;
		TextView mTvDate;
	}

}

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyNotificationMessageListEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * 
 */
public class MyNotificationMessageAdapter extends
		ArrayAdapter<MyNotificationMessageListEntity> {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private List<MyNotificationMessageListEntity> mData;
	private ImageLoader mImageLoader;

	public MyNotificationMessageAdapter(Context context,
			int textViewResourceId,
			List<MyNotificationMessageListEntity> mesObject) {
		super(context, textViewResourceId, mesObject);
		this.mContext = context;
		this.mLiInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(context);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		this.mData = mesObject;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		final MyNotificationMessageListEntity mynotificationMessage = getItem(position);

		if (mynotificationMessage == null) {
			return null;
		}

		viewHolder = new ViewHolder();
		if (!mynotificationMessage.mFromUser
				.trim()
				.toUpperCase()
				.equals(UserObject.getInstance(mContext).getUserID().trim()
						.toUpperCase())) {
			convertView = mLiInflater.inflate(
					R.layout.mynotification_message_item_left, null);
			viewHolder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.mynotification_message_item_left_iv_avatar);
			viewHolder.mTvMessage = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_left_tv_message);
			viewHolder.mTvTime = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_left_tv_time);
			viewHolder.mTvDate = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_left_tv_date);
		} else {
			convertView = mLiInflater.inflate(
					R.layout.mynotification_message_item_right, null);
			viewHolder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.mynotification_message_item_right_iv_avatar);
			viewHolder.mTvMessage = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_right_tv_message);
			viewHolder.mTvTime = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_right_tv_time);
			viewHolder.mTvDate = (TextView) convertView
					.findViewById(R.id.mynotification_message_item_right_tv_date);
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL
					+ mynotificationMessage.mAvatar, viewHolder.mIvAvatar);
			viewHolder.mTvMessage.setText(Html
					.fromHtml(mynotificationMessage.mMessage.trim()));
			Linkify.addLinks(viewHolder.mTvMessage, Linkify.ALL);
			viewHolder.mTvTime.setText(Utils
					.formatTime(mynotificationMessage.mUpdateTime.trim()));
			viewHolder.mTvDate.setText(mynotificationMessage.mUpdateDay.trim());
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
		ImageView mIvAvatar;
		TextView mTvMessage;
		TextView mTvTime;
		TextView mTvDate;
	}

}

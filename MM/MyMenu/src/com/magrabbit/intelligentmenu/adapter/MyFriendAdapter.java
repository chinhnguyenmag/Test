package com.magrabbit.intelligentmenu.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFriendListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author Vu Le
 * 
 */
public class MyFriendAdapter extends ArrayAdapter<MyFriendListEntity> {

	/** the context contain this adapter */
	private Context mContext;
	private LayoutInflater mLiInflater;
	// private DialogDetailMyFriend mDialogDetailProfile;
	private List<MyFriendListEntity> mDisplayedValues = new ArrayList<MyFriendListEntity>();
	private List<MyFriendListEntity> mData;
	// Loader for images
	private ImageLoader mImageLoader;

	public MyFriendAdapter(Context context, int resource,
			List<MyFriendListEntity> lstObject) {
		super(context, resource, lstObject);
		mContext = context;
		this.mImageLoader = new ImageLoader(this.mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		this.mImageLoader.setSizeBitmap(60, 60);
		mData = lstObject;
		mDisplayedValues.addAll(lstObject);
		mLiInflater = LayoutInflater.from(mContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLiInflater.inflate(R.layout.my_friend_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvName = (TextView) convertView
					.findViewById(R.id.my_friend_item_tv_username);
			viewHolder.mIvavatar = (ImageView) convertView
					.findViewById(R.id.my_friend_item_iv_avatar);
			viewHolder.mTvDateTime = (TextView) convertView
					.findViewById(R.id.my_friend_item_tv_date);
			viewHolder.mTvStatus = (TextView) convertView
					.findViewById(R.id.my_friend_item_tv_status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// get the item from the list

		try {
			viewHolder.mTvName.setText(mData.get(position).getFirstName() + " "
					+ mData.get(position).getLastName());
			viewHolder.mTvName.setTextColor(Color.WHITE);
			mImageLoader.DisplayImage(ServerURL.URL
					+ mData.get(position).getAvatar(), viewHolder.mIvavatar);

			Date dateToFormat = new Date(mData.get(position).getDateInvited());
			SimpleDateFormat ft = new SimpleDateFormat("MM.dd.yy");
			viewHolder.mTvDateTime.setText(ft.format(dateToFormat) + "");

			if (mData.get(position).getStatus().toLowerCase().trim()
					.equals("Registered".toLowerCase())) {
				viewHolder.mTvStatus.setText("Registered");
				viewHolder.mTvDateTime.setTextColor(mContext.getResources()
						.getColor(R.color.myfriend_list_registered));
				viewHolder.mTvStatus.setTextColor(mContext.getResources()
						.getColor(R.color.myfriend_list_registered));
			} else if (mData.get(position).getStatus().toLowerCase().trim()
					.equals("Pending".toLowerCase())) {
				// TODO
				viewHolder.mTvStatus.setText("Pending");
				viewHolder.mTvDateTime.setTextColor(mContext.getResources()
						.getColor(R.color.myfriend_list_pending));
				viewHolder.mTvStatus.setTextColor(mContext.getResources()
						.getColor(R.color.myfriend_list_pending));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return convertView;
	}

	/**
	 * This method use to filter when user search my friend by input text or use
	 * the voice
	 * 
	 * @param charText
	 *            is a string to compare with item of list
	 */
	public void filterName(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		mData.clear();

		List<MyFriendListEntity> list2 = new ArrayList<MyFriendListEntity>();
		if (charText.length() == 0) {
			mData.addAll(mDisplayedValues);
		} else {
			for (MyFriendListEntity mf : mDisplayedValues) {
				String textToCompare = mf.getFirstName() + " "
						+ mf.getLastName();

				if (textToCompare.trim().toLowerCase(Locale.getDefault())
						.startsWith(charText.trim())) {
					mData.add(mf);
				}
				if (textToCompare.trim().toLowerCase(Locale.getDefault())
						.contains(charText.trim())) {
					list2.add(mf);
				}
			}
			for (MyFriendListEntity mf : mDisplayedValues) {
				String textToCompare = mf.getFirstName() + " "
						+ mf.getLastName();
				if (textToCompare.trim().toLowerCase(Locale.getDefault())
						.contains(charText.trim())
						&& !textToCompare.trim()
								.toLowerCase(Locale.getDefault())
								.startsWith(charText.trim())) {
					mData.add(mf);
				}
			}

		}
		notifyDataSetChanged();
	}

	/**
	 * @author Vu Le This method will keep the value when the user swipe list
	 *         view
	 * 
	 */
	private class ViewHolder {
		ImageView mIvavatar;
		TextView mTvName;
		TextView mTvDateTime;
		TextView mTvStatus;
	}

}

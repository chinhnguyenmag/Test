package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Chinhnd
 * 
 */
public class ItemAdapter extends ArrayAdapter<Item> {

	/** the context contain this adapter */
	private Context context;
	private LayoutInflater mLiInflater;

	public ItemAdapter(Context context, int textViewResourceId,
			List<Item> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		mLiInflater = LayoutInflater.from(this.context);
	}

	/**
	 * Custom the view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		// if (convertView == null) {
		// convertView = mLiInflater.inflate(R.layout.item_in_list, null);
		// viewHolder = new ViewHolder();
		// viewHolder.mTvName = (TextView) convertView
		// .findViewById(R.id.item_in_list_tv_name);
		// viewHolder.mTvTime = (TextView) convertView
		// .findViewById(R.id.item_in_list_tv_time);
		// convertView.setTag(viewHolder);
		// } else {
		// viewHolder = (ViewHolder) convertView.getTag();
		// }
		//
		// // get the item from the list
		// Item it = getItem(position);
		// if (it == null)
		// return null;
		// viewHolder.mTvName.setText("");
		//
		// try {
		// // Parse the date format Constant.DATE_TIME_FORMAT to the locale
		// // date format
		// viewHolder.mTvTime.setText("");
		// } catch (Exception e) {
		// viewHolder.mTvTime.setText("");
		// }
		return convertView;
	}

	private class ViewHolder {
		TextView mTvName;
		TextView mTvTime;
	}

}

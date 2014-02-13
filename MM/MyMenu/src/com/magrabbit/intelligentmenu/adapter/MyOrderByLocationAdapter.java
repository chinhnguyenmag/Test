package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.OrderByLocationEntity;
import com.magrabbit.intelligentmenu.utils.Utils;

public class MyOrderByLocationAdapter extends
		ArrayAdapter<OrderByLocationEntity> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<OrderByLocationEntity> mListOrderChains;

	public MyOrderByLocationAdapter(Context mContext, int resource,
			List<OrderByLocationEntity> mListTransactions) {
		super(mContext, resource, mListTransactions);
		this.mContext = mContext;
		this.mListOrderChains = mListTransactions;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.myorder_by_location_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvDate = (TextView) convertView
					.findViewById(R.id.myorder_by_location_date);
			viewHolder.mTvOrderName = (TextView) convertView
					.findViewById(R.id.myorder_by_location_name);
			viewHolder.mTvOrderPrice = (TextView) convertView
					.findViewById(R.id.myorder_by_location_price);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {

			String date = (mListOrderChains.get(position).getOrderDate());

			viewHolder.mTvDate.setText(splitDate(date).replace("-", "."));
			viewHolder.mTvOrderName.setText("Order Details");
			if (mListOrderChains.get(position).getIsRead() == 1) {
				// Read
				viewHolder.mTvOrderName.setTextColor(mContext.getResources()
						.getColor(R.color.White));
			}else{
				//Unread
				viewHolder.mTvOrderName.setTextColor(mContext.getResources()
						.getColor(R.color.mynotification_unread));
			}

			viewHolder.mTvOrderPrice.setText("$"
					+ Utils.formatNumber(mListOrderChains.get(position)
							.getPrice(), "0.00"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	/**
	 * 
	 * @param date
	 *            Ex:11.22.2013 06:07:30
	 * @return Get the string of 11.22.2013
	 */
	public String splitDate(String date) {
		return date.substring(0, 10);
	}

	private class ViewHolder {
		TextView mTvDate;
		TextView mTvOrderName;
		TextView mTvOrderPrice;

	}
}

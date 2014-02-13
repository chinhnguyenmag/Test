package com.magrabbit.intelligentmenu.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.MyPointRestaurantListPointEntity;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 *
 */
public class MyPointTransactionAdapter extends
		ArrayAdapter<MyPointRestaurantListPointEntity> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<MyPointRestaurantListPointEntity> mListTransactions;

	public MyPointTransactionAdapter(Context mContext, int resource,
			List<MyPointRestaurantListPointEntity> mListTransactions) {
		super(mContext, resource, mListTransactions);
		this.mContext = mContext;
		this.mListTransactions = mListTransactions;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.mypoint_transaction_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvDate = (TextView) convertView
					.findViewById(R.id.mypoint_transaction_date);
			viewHolder.mTvTransactionName = (TextView) convertView
					.findViewById(R.id.mypoint_transaction_name);
			viewHolder.mTvPoint = (TextView) convertView
					.findViewById(R.id.mypoint_transaction_point);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {

			Date dateToFormat = new Date(
					mListTransactions.get(position).getMostRecent());
			SimpleDateFormat ft = new SimpleDateFormat("MM.dd.yyyy");

			viewHolder.mTvDate.setText(ft.format(dateToFormat) + "");
			viewHolder.mTvTransactionName.setText(mListTransactions
					.get(position).getPointType());

			if (mListTransactions.get(position).mStatus == 1) {
				if (mListTransactions.get(position).mPoint > 1) {
					viewHolder.mTvPoint
							.setText(Utils.formatPointNumbers(mListTransactions.get(position).mPoint)
									+ " Points");
				} else {
					viewHolder.mTvPoint
							.setText(Utils.formatPointNumbers(mListTransactions.get(position).mPoint)
									+ " Point");
				}
				viewHolder.mTvPoint.setTextColor(mContext.getResources()
						.getColor(R.color.mypoint_transaction_add_point));
			} else if (mListTransactions.get(position).mStatus != 1) {
				if (mListTransactions.get(position).mPoint > 1) {
					viewHolder.mTvPoint.setText("("
							+ Utils.formatPointNumbers(mListTransactions.get(position).mPoint)
							+ " Points)");
				} else {
					viewHolder.mTvPoint.setText("("
							+ Utils.formatPointNumbers(mListTransactions.get(position).mPoint)
							+ " Point)");
				}
				viewHolder.mTvPoint.setTextColor(mContext.getResources()
						.getColor(R.color.mypoint_transaction_sub_point));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	private class ViewHolder {
		TextView mTvDate;
		TextView mTvTransactionName;
		TextView mTvPoint;

	}
}

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.OrderItemEntity;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @description Adapter for MyOrderDetails.
 * @author baonguyen
 * 
 */
public class MyOrderDetailsAdapter extends ArrayAdapter<OrderItemEntity> {
	private LayoutInflater mLiInflater;
	private List<OrderItemEntity> mData;

	public MyOrderDetailsAdapter(Context mContext, int resource,
			List<OrderItemEntity> mData) {
		super(mContext, resource, mData);
		this.mData = mData;
		mLiInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLiInflater.inflate(R.layout.myorder_details_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mTvQuantity = (TextView) convertView
					.findViewById(R.id.myorder_details_item_tv_quantity);
			viewHolder.mTvNameItem = (TextView) convertView
					.findViewById(R.id.myorder_details_item_tv_item_name);
			viewHolder.mTvPrice = (TextView) convertView
					.findViewById(R.id.myorder_details_item_tv_price_or_point);
			viewHolder.mTvNotes = (TextView) convertView
					.findViewById(R.id.myorder_details_item_edt_notes);
			viewHolder.mTvPaidType = (TextView) convertView
					.findViewById(R.id.myorder_details_item_tv_paid_type);
			viewHolder.mTvRating = (TextView) convertView
					.findViewById(R.id.myorder_details_item_tv_rating);
			viewHolder.mIvRating = (ImageView) convertView
					.findViewById(R.id.myorder_details_item_iv_rating);
			viewHolder.mLayoutRating = (LinearLayout) convertView
					.findViewById(R.id.myorder_details_item_layout_rating);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// get the item from the list

		try {
			viewHolder.mTvNameItem.setText(mData.get(position).getItemName());
			viewHolder.mTvQuantity.setText("("
					+ mData.get(position).getQuantity() + ")");
			viewHolder.mTvNotes.setText(mData.get(position).getNote());
			if (viewHolder.mTvNotes.getText().toString().trim().equals("")) {
				viewHolder.mTvNotes.setVisibility(View.INVISIBLE);
			}

			if (mData.get(position).getUsePoint() == 0) {
				// price > 0 && use_point = 0: paid by Cash money
				viewHolder.mTvPaidType.setText("       $");
				viewHolder.mTvPrice.setText(" "
						+ Utils.formatNumber(mData.get(position).getPrice(),
								"0.00"));
			} else {
				// use_point > 0: paid by Point

				viewHolder.mTvPaidType.setText("Points:");
				viewHolder.mTvPaidType.setTextColor(Color.RED);
				viewHolder.mTvPrice.setText(" "
						+ Utils.formatPointNumbers(mData.get(position)
								.getUsePoint()));
			}
			if (mData.get(position).getRating() == 0) {
				viewHolder.mIvRating.setVisibility(View.VISIBLE);
				viewHolder.mTvRating.setVisibility(View.GONE);
			} else {
				viewHolder.mIvRating.setVisibility(View.GONE);
				viewHolder.mTvRating.setVisibility(View.VISIBLE);
				viewHolder.mTvRating.setText(Utils.exchangeRateGetExtra(mData
						.get(position).getRating()));
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return convertView;
	}

	private class ViewHolder {
		TextView mTvNameItem;
		TextView mTvQuantity;
		TextView mTvNotes;
		TextView mTvPrice;
		TextView mTvPaidType;
		TextView mTvRating;
		ImageView mIvRating;
		LinearLayout mLayoutRating;
	}

}

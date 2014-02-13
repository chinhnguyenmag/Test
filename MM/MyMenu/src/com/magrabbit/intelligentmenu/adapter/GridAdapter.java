package com.magrabbit.intelligentmenu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.GridItems;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;

/**
 * @author Hung Hoang
 * @description Adapter for view thumbnail
 */
public class GridAdapter extends BaseAdapter {

	private Context mContext;
	private ImageLoader mImageLoader;
	private boolean mIsRestaurant;

	public class ViewHolder {
		public ImageView mImageView;
	}

	private GridItems[] mItems;
	private LayoutInflater mInflater;

	public GridAdapter(Context context, GridItems[] locations,
			boolean isRestaurant) {

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mItems = locations;
		mIsRestaurant = isRestaurant;
		// Load images from server and set default sizes
		mImageLoader = new ImageLoader(mContext);
		// mImageLoader.setSizeBitmap(w, h);

	}

	public GridItems[] getItems() {
		return mItems;
	}

	public void setItems(GridItems[] items) {
		this.mItems = items;
	}

	@Override
	public int getCount() {
		if (mItems != null) {
			return mItems.length;
		}
		return 0;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		if (mItems != null && position >= 0 && position < getCount()) {
			return mItems[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mItems != null && position >= 0 && position < getCount()) {
			return mItems[position].getId();
		}
		return 0;
	}

	public void setItemsList(GridItems[] locations) {
		this.mItems = locations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		ViewHolder viewHolder;

		if (view == null) {

			view = mInflater.inflate(R.layout.grid_item_thumbnail, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView) view
					.findViewById(R.id.grid_item_image);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.mImageView.setTag(position);
		GridItems gridItems = mItems[position];
		setCatImage(position, viewHolder, gridItems.getImage());

		if (gridItems.getId() == Constants.mIdSelected && !mIsRestaurant) {
			view.setBackgroundResource(R.drawable.border_gridview);
		} else {
			view.setBackgroundResource(0);
		}
		return view;
	}

	private void setCatImage(int pos, ViewHolder viewHolder, String image) {
		viewHolder.mImageView.setScaleType(ScaleType.FIT_XY);
		mImageLoader.DisplayImage(ServerURL.URL + image, viewHolder.mImageView);
	}
}

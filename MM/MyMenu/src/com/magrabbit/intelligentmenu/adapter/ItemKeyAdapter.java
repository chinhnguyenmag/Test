package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.ItemKeyObject;
import com.magrabbit.intelligentmenu.server.ServerURL;

public class ItemKeyAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private ViewHolder holder;
	private Context mContext;
	private List<ItemKeyObject> mListItemKey;
	private ImageLoader mImageLoader;

	public ItemKeyAdapter(Context mContext, List<ItemKeyObject> mListItemKey) {
		super();
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mListItemKey = mListItemKey;
		// Load images from server and set default sizes
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60,60);
		
	}

	@Override
	public int getCount() {
		return mListItemKey.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.menuitem_key_gallary_item, parent, false);
			holder = new ViewHolder();

			holder.mIvKey = (ImageView) convertView
					.findViewById(R.id.menuitem_key_iv_gallary);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mImageLoader.DisplayImage(ServerURL.URL
				+ mListItemKey.get(position).getImg(), holder.mIvKey);
		return convertView;
	}

	public class ViewHolder {
		ImageView mIvKey;
	}

}

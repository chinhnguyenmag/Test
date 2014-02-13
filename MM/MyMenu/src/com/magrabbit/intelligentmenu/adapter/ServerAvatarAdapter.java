package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.ServerEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * @author baonguyen
 * @Description Adapter for Server avatars.
 */
public class ServerAvatarAdapter extends PagerAdapter {
	private Context mContext;
	private List<ServerEntity> mListServers;
	private ImageLoader mImageLoader;
	private LayoutInflater mInflater;

	public ServerAvatarAdapter(Activity context, List<ServerEntity> mListServers) {

		this.mContext = context;
		this.mListServers = new ArrayList<ServerEntity>();
		this.mListServers.clear();
		this.mListServers.addAll(mListServers);
		// Load images from server and set default sizes
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
	}

	@Override
	public int getCount() {
		return mListServers.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView mIvServerAvatar;
		TextView mTvServerName;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = mInflater.inflate(R.layout.server_item, container,false);
		mIvServerAvatar = (ImageView)itemView.findViewById(R.id.server_item_resImage);
		mIvServerAvatar.setScaleType(ScaleType.FIT_XY);
		mImageLoader.DisplayImage(ServerURL.URL
				+ mListServers.get(position).getAvatar(), mIvServerAvatar);
		mTvServerName = (TextView)itemView.findViewById(R.id.server_item_resDistance);
		mTvServerName.setText(mListServers.get(position).getName());
		
		// Add viewpager_item.xml to ViewPager
		((ViewPager) container).addView(itemView);
		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);
	}
}

package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.SystemConfigs;

/**
 * @author HungHoang
 * @description Adapter for PhotoRestaurant.
 */
public class PhotosRestaurantAdapter extends PagerAdapter {
	private Context mContext;
	private List<String> mListPhotosImages;
	private ImageLoader mImageLoader;
	private boolean mIsPortrait = true;

	public PhotosRestaurantAdapter(Activity context,
			List<String> mListPhotosImages, boolean portrait) {

		this.mContext = context;
		this.mListPhotosImages = mListPhotosImages;
		this.mIsPortrait = portrait;
		// Load images from server and set default sizes
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_500);
		try {
			DisplayMetrics displayMetrics = SystemConfigs.Instance()
					.getDisplayMetrics();

			if (displayMetrics == null) {
				DisplayMetrics displayMetrics1 = new DisplayMetrics();
				((WindowManager) mContext
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay().getMetrics(displayMetrics1);
				SystemConfigs.Instance().setDisplayMetrics(displayMetrics1);
				displayMetrics = null;
			}

			if (displayMetrics != null) {
				mImageLoader.setSizeBitmap(displayMetrics.widthPixels,
						displayMetrics.heightPixels);
			} else {
				mImageLoader.setSizeBitmap(480, 480);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return mListPhotosImages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(mContext);
		if (mIsPortrait) {
			imageView.setScaleType(ScaleType.FIT_XY);
		} else {
			imageView.setScaleType(ScaleType.FIT_CENTER);
		}

		mImageLoader.DisplayImage(
				ServerURL.URL + mListPhotosImages.get(position), imageView);
		// imageView.setImageBitmap(ProcessBitmap.decodeFile(
		// context.getResources(), photosImages.get(position), 250, 250));
		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}

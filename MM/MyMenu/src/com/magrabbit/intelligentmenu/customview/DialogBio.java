package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;

/**
 * 
 * @author Hung Hoang
 * 
 */
public class DialogBio extends BaseDialog {
	private TextView mTvBio;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogBio(Context context, String mBio) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_bio_restaurant);
		mTvBio = (TextView) findViewById(R.id.details_restaurant_tv_bio);
		mTvBio.setText(mBio);
	}
}

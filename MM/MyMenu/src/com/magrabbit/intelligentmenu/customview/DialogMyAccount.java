package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.activity.AccountSettingActivity;
import com.magrabbit.intelligentmenu.activity.MyFriendsActivity;
import com.magrabbit.intelligentmenu.activity.MyNotificationGlobalActivity;
import com.magrabbit.intelligentmenu.activity.MyPointGlobalActivity;

/**
 * Dialog My Account
 * 
 * @author ChinhNguyen
 * 
 */
public class DialogMyAccount extends BaseDialog implements OnClickListener {
	private ImageView mIvResLogo;
	private ImageView mIvUserLogo;
	private TextView mTvStatus;
	private TextView mTvPoint;
	private RelativeLayout mRlMyPoint, mRlMyNotification, mRlMySetting,
			mRlSearch, mRlMyFriend, mRlMyWallet, mRlMyFavorites, mRlMyCalories;
	private ProcessDialogMyAccount mProcess;

	public DialogMyAccount(Context context, ProcessDialogMyAccount pro) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setBackgroundDrawable(
		// new ColorDrawable(android.graphics.Color.TRANSPARENT));

		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_myaccount);

		try {
			this.mContext = context;
			this.mProcess = pro;
			mIvResLogo = (ImageView) findViewById(R.id.activity_expandable_restaurantLogo);
			//mIvResLogo.setImageResource(RestaurantObject.getInstance().getLogoRestaurant());
			mIvUserLogo = (ImageView) findViewById(R.id.activity_expandable_userLogo);
			// Get Avatar from UserObject
			// UserObject.getInstance().getProfileAvatar();
			mTvStatus = (TextView) findViewById(R.id.activity_expandable_status);
			// mTvStatus.setText(UserObject.getInstance().getUserStatus());
			mTvPoint = (TextView) findViewById(R.id.activity_expandable_point);
			// mTvPoint.setText(Integer.toString(UserObject.getInstance().getPoints()));

			mRlMyPoint = (RelativeLayout) findViewById(R.id.dialog_myaccount_mypoint);
			mRlMyPoint.setOnClickListener(this);
			mRlMyNotification = (RelativeLayout) findViewById(R.id.dialog_myaccount_mynotification);
			mRlMyNotification.setOnClickListener(this);
			mRlMySetting = (RelativeLayout) findViewById(R.id.dialog_myaccount_mysetting);
			mRlMySetting.setOnClickListener(this);
			mRlSearch = (RelativeLayout) findViewById(R.id.dialog_myaccount_mysearch);
			mRlSearch.setOnClickListener(this);
			mRlMyFriend = (RelativeLayout) findViewById(R.id.dialog_myaccount_myfriend);
			mRlMyFriend.setOnClickListener(this);
			mRlMyWallet = (RelativeLayout) findViewById(R.id.dialog_myaccount_mywallet);
			mRlMyWallet.setOnClickListener(this);
			mRlMyFavorites = (RelativeLayout) findViewById(R.id.dialog_myaccount_myfavorites);
			mRlMyFavorites.setOnClickListener(this);
			mRlMyCalories = (RelativeLayout) findViewById(R.id.dialog_myaccount_mycalories);
			mRlMyCalories.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		Intent intent = null;
		if (v == this.mRlMyPoint) {
			intent = new Intent(mContext, MyPointGlobalActivity.class);
		} else if (v == this.mRlMyNotification) {
			intent = new Intent(mContext, MyNotificationGlobalActivity.class);
		} else if (v == this.mRlMySetting) {
			intent = new Intent(mContext, AccountSettingActivity.class);
		} else if (v == this.mRlSearch) {

		} else if (v == this.mRlMyFriend) {

			intent = new Intent(mContext, MyFriendsActivity.class);
		} else if (v == this.mRlMyWallet) {

		} else if (v == this.mRlMyFavorites) {

		} else if (v == this.mRlMyCalories) {

		}

		this.mProcess.click_Dialog(intent);
		dismiss();
	}

	public static abstract class ProcessDialogMyAccount {
		public abstract void click_Dialog(Intent intent);
	}

}
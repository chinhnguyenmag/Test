package com.magrabbit.intelligentmenu.customview;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFriendListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Vu Le
 * 
 */
public class DialogMyFriendProfile extends BaseDialog implements
		android.view.View.OnClickListener {

	private ProcessDialogConfirm_MyFriend mProcessDialogMove;
	private Button mBtDelete;
	private Button mBtCancel;
	MyFriendListEntity mMyFriendObject;
	private boolean mIsRemove = false;
	private ImageView mIvAvartar;
	private TextView mTvUserName;

	private TextView mTvTotalRatings;
	private TextView mTvTotalPoints;
	private TextView mTvVisited;

	private TextView mDateInvited;
	private TextView mDateRegistered;
	private TextView mDateLastRating;
	private TextView mUserId;
	private Context mContext;
	// Loader for images
	private ImageLoader mImageLoader;

	public DialogMyFriendProfile(Context context,
			MyFriendListEntity myFriendObject,
			ProcessDialogConfirm_MyFriend processMove) {
		super(context);
		mContext = context;
		this.mImageLoader = new ImageLoader(this.mContext);
		this.mImageLoader.setSizeBitmap(60, 60);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * this.getWindow().setBackgroundDrawableResource(
		 * android.R.color.transparent);
		 */
		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(50);
		this.getWindow().setBackgroundDrawable(d);

		setContentView(R.layout.dialog_myfriend_profile);
		mProcessDialogMove = processMove;
		mBtDelete = (Button) findViewById(R.id.my_friend_item_btn_delete);
		mIvAvartar = (ImageView) findViewById(R.id.my_friend_iv_avatar);
		mTvUserName = (TextView) findViewById(R.id.my_friend_item_tv_username);
		mTvTotalPoints = (TextView) findViewById(R.id.my_friend_item_tv_total_point);
		mTvTotalRatings = (TextView) findViewById(R.id.my_friend_item_tv_total_rating);
		mTvVisited = (TextView) findViewById(R.id.my_friend_item_tv_total_visited);
		mDateInvited = (TextView) findViewById(R.id.my_friend_item_tv_date_invite);
		mDateRegistered = (TextView) findViewById(R.id.my_friend_item_tv_date_resgistered);
		mDateLastRating = (TextView) findViewById(R.id.my_friend_item_tv_rating);
		mBtCancel = (Button) findViewById(R.id.my_friend_item_btn_cancel);
		// mUserId = (TextView) findViewById(R.id.my_friend_tv_userid);

		// mImageLoader.DisplayImage(ServerURL.URL + myFriendObject.mAvatar,
		// mIvAvartar);

		try {
			if (Utils.isNetworkConnected(mContext)) {
				new DownloadImageTask(mIvAvartar).execute(ServerURL.URL
						+ myFriendObject.getAvatar());
			} else {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.mess_error_network), Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mTvUserName.setText(myFriendObject.getFirstName() + " "
				+ myFriendObject.getLastName());

		mTvTotalPoints.setText(Utils.formatPointNumbers(
				myFriendObject.getPointReceived())
				+ "");
		mTvTotalRatings.setText(Utils.formatPointNumbers(
				myFriendObject.getNumberRatings())
				+ "");
		mTvVisited.setText(myFriendObject.getResVisited() + "");
		mTvVisited.setTextColor(Color.YELLOW);
		mTvTotalPoints.setTextColor(Color.YELLOW);
		mTvTotalRatings.setTextColor(Color.YELLOW);
		try {
			Date formatDateInvite = new Date(myFriendObject.getDateInvited());
			SimpleDateFormat ft = new SimpleDateFormat("MM.dd.yy");
			mDateInvited.setText(ft.format(formatDateInvite) + "");

			Date dormatDateReg = new Date(myFriendObject.getDateRegistered());
			mDateRegistered.setText(ft.format(dormatDateReg) + "");

			Date dormatDateRat = new Date(myFriendObject.getLastRating());
			mDateLastRating.setText(ft.format(dormatDateRat) + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		mBtDelete.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtDelete) {
			DialogConfirm dialog = new DialogConfirm(this.mContext,
					android.R.drawable.ic_dialog_alert, "Delete friend",
					"Are you sure you want to delete?",true,
					new ProcessDialogConfirm() {

						@Override
						public void click_Ok() {
							mIsRemove = true;
							mProcessDialogMove.click_btn_remove(
									mMyFriendObject, mIsRemove);
							dismiss();
						}

						@Override
						public void click_Cancel() {
						}
					});
			dialog.show();
		} else if (v == mBtCancel) {
			dismiss();
		}

	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		InputStream in;
		Bitmap mIcon11;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected void onPreExecute() {
			bmImage.setImageResource(R.drawable.ic_load_img_150);
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			mIcon11 = null;
			try {
				in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());

				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			try {
				if (result == null) {
					bmImage.setImageResource(R.drawable.ic_load_img_150);
				} else {
					bmImage.setImageBitmap(result);
				}
				if (in != null) {
					in.close();
					mIcon11 = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author Thanh Vu
	 * 
	 */
	public static abstract class ProcessDialogConfirm_MyFriend {
		public abstract void click_btn_remove(
				MyFriendListEntity myFriendObject, boolean isRemove);

		public abstract void click_btn_cancel();

	}

}

package com.magrabbit.intelligentmenu.customview;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyPointGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * 
 * @author baognguyen
 * @Description : Dialog for showing Saving Profile Option
 */
public class DialogStatInfo extends BaseDialog implements OnClickListener {
	private Context mContext;
	private ImageView mIvResLogo;
	private ImageView mIvUserLogo;
	private TextView mTvStatus;
	private TextView mTvPoint;
	private TextView mTvPointTitle;
	private ProgressBar mProStatus;
	private ProgressBar mProPoint;

	private ImageLoader mImageLoader;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogStatInfo(Context context, String restaurantLogo,
			String status, float point, boolean isFromGlobal) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// /** Design the dialog in dialog_stat_info.xml file */
		setContentView(R.layout.dialog_stat_info);
		// Progress Bar inflate
		mProStatus = (ProgressBar) findViewById(R.id.dialog_stat_info_status_progressBar);
		mProPoint = (ProgressBar) findViewById(R.id.dialog_stat_info_point_progressBar);
		this.mContext = context;
		mImageLoader = new ImageLoader(this.mContext);

		// Set whether picture is restaurant logo or MyMenu logo
		mIvResLogo = (ImageView) findViewById(R.id.dialog_stat_info_restaurantLogo);
		if (restaurantLogo == null) {
			mIvResLogo.setImageResource(R.drawable.ic_logo);
		} else {
			new DownloadImageTask(mIvResLogo).execute(ServerURL.URL
					+ restaurantLogo);
		}

		// Set Avatar
		mIvUserLogo = (ImageView) findViewById(R.id.dialog_stat_info_userLogo);
		mIvUserLogo.setScaleType(ScaleType.FIT_XY);

		new DownloadImageTask(mIvUserLogo).execute(ServerURL.URL
				+ UserObject.getInstance(mContext).getProfileAvatar());

		// Set status of diner
		mTvStatus = (TextView) findViewById(R.id.dialog_stat_info_status);
		// Set point number of diner in each restaurant or total point
		mTvPoint = (TextView) findViewById(R.id.dialog_stat_info_tv_point_value);
		mTvPointTitle = (TextView) findViewById(R.id.dialog_stat_info_tv_point_title);
		if (point > 1) {
			mTvPointTitle.setText("Points:");
		} else {
			mTvPointTitle.setText("Point:");
		}
		if (!isFromGlobal) {
			// Turn off ProgressBar
			mProStatus.setVisibility(View.GONE);
			mProPoint.setVisibility(View.GONE);
			mTvStatus.setText(" " + status);
			mTvPoint.setText(" " + Utils.formatPointNumbers(point));
		} else {
			// Load service to get Dinner's status and point number if not
			// transferring from Restaurant screen
			if (Utils.isNetworkConnected(mContext)) {
				mProStatus.setVisibility(View.VISIBLE);
				mProPoint.setVisibility(View.VISIBLE);
				new ListGlobalPoints().execute();
			} else {
				showToastMessage(mContext.getResources().getString(
						R.string.mess_error_network));
			}
		}
	}

	// Clear cache of ImageLoader when dismissing dialog
	@Override
	public void dismiss() {
		super.dismiss();
		if (mImageLoader != null)
			mImageLoader.clearCache();
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of text box
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.isShowing()) {
			dismiss();
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Service for loading Dinner's status and total point
	 */
	public class ListGlobalPoints extends
			AsyncTask<String, Void, MyPointGlobalEntity> {
		@Override
		protected void onPreExecute() {
			// showProgress(R.string.mess_waitting);
		}

		@Override
		protected MyPointGlobalEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGlobalPoint(UserObject
								.getInstance(mContext).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyPointGlobalEntity response = gson.fromJson(json,
						MyPointGlobalEntity.class);

				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyPointGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					// dismissProgress();
					mProStatus.setVisibility(View.GONE);
					mProPoint.setVisibility(View.GONE);
					showToastMessage(mContext.getResources().getString(
							R.string.mess_error_server));
					return;
				} else {
					UserGlobalEntity mUserGlobal = new UserGlobalEntity();
					mUserGlobal = result.mUserGlobal;
					if (mUserGlobal.getPointNumber() > 1) {
						mTvPointTitle.setText("Points:");
					} else {
						mTvPointTitle.setText("Point:");
					}
					mTvPoint.setText(" "
							+ Utils.formatPointNumbers(
									mUserGlobal.getPointNumber()));
					if (!result.mUserGlobal.getStatus().equals("")) {
						mTvStatus.setText(" " + mUserGlobal.getStatus());
					}
					// dismissProgress();
					mProStatus.setVisibility(View.GONE);
					mProPoint.setVisibility(View.GONE);
				}

			} catch (Exception e) {
				e.toString();
			}
		}
	}

	/**
	 * 
	 * @Description This task will download images from Server in a Dialog
	 *              specifically
	 */
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
	 * Show Toast message template
	 * 
	 * @param message
	 */
	void showToastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}

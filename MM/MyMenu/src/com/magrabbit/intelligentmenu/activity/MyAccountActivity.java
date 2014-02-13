package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.LocationEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyPointGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

public class MyAccountActivity extends BaseActivity implements OnClickListener,
		OptionMenuClickListener {
	private boolean mLoadFirstTime = false;
	private boolean mIsFromRestaurant = false;
	private ImageView mIvResLogo;
	private ImageView mIvUserLogo;
	private TextView mTvStatus;
	private TextView mTvPoint;
	private TextView mTvPointTitle;
	private ProgressBar mProgressStatus;
	private ProgressBar mProgressPoint;

	private RelativeLayout mRlMyPoint, mRlMyNotification, mRlMySetting,
			mRlSearch, mRlMyFriend, mRlMyWallet, mRlMyFavorites, mRlMyCalories,
			mRlMyOrderHistory;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private int resultCode = -1;
	private int mRestaurantId = -1;

	// Image Loader
	private ImageLoader mImageLoader;
	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myaccount);

		// Progress Bar inflate
		mProgressStatus = (ProgressBar) findViewById(R.id.activity_myaccount_status_progressbar);
		mProgressPoint = (ProgressBar) findViewById(R.id.activity_myaccount_point_progressbar);
		// Load the first time
		mLoadFirstTime = true;
		// Add Optional Menu to layout
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, false, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);
		// Loader for images
		mImageLoader = new ImageLoader(this);
		mImageLoader.setSizeBitmap(60, 60);

		// Inflate from layout
		mIvResLogo = (ImageView) findViewById(R.id.activity_myaccount_restaurantLogo);
		mIvResLogo.setImageResource(R.drawable.ic_logo);

		mIvUserLogo = (ImageView) findViewById(R.id.activity_myaccount_userLogo);
		mIvUserLogo.setScaleType(ScaleType.FIT_XY);
		// Get Avatar
		mImageLoader.DisplayImage(
				ServerURL.URL
						+ UserObject.getInstance(MyAccountActivity.this)
								.getProfileAvatar(), mIvUserLogo);

		// Set content for status and point of user
		mTvStatus = (TextView) findViewById(R.id.activity_myaccount_status);
		mTvPoint = (TextView) findViewById(R.id.activity_myaccount_tv_point_value);
		mTvPoint.setVisibility(View.GONE);
		mTvPointTitle = (TextView) findViewById(R.id.activity_myaccount_tv_point_title);

		Bundle bundle = getIntent().getExtras();
		try {
			if (bundle != null) {
				String logo = bundle
						.getString(StringExtraUtils.KEY_LOGO_RESTAURANT_MYACCOUNT);
				if (logo != null) {
					mImageLoader.DisplayImage(ServerURL.URL + logo, mIvResLogo);
				}

				String s = bundle
						.getString(StringExtraUtils.KEY_STATUS_RESTAURANT_MYACCOUNT);
				// Check whether it comes from Restaurant, MenuItem or other
				// Activity
				mIsFromRestaurant = bundle
						.getBoolean(StringExtraUtils.KEY_RECOGNIZE_RESTAURANT_MYACCOUNT);
				mRestaurantId = bundle
						.getInt(StringExtraUtils.KEY_ID_RESTAURANT_MYACCOUNT);
				if (s != null) {
					// Turn off ProgressBar
					mProgressStatus.setVisibility(View.GONE);
					mProgressPoint.setVisibility(View.GONE);
					mTvStatus.setText(" " + s);
					float point = getIntent().getExtras().getFloat(
							StringExtraUtils.KEY_POINT_RESTAURANT_MYACCOUNT);
					mTvPoint.setVisibility(View.VISIBLE);
					mTvPoint.setText(" " + Utils.formatPointNumbers(point));
					if (point > 1) {
						mTvPointTitle.setText("Points:");
					} else {
						mTvPointTitle.setText("Point :");
					}
				} else {
					// Load service to get Dinner's status and point number
					mProgressStatus.setVisibility(View.VISIBLE);
					mProgressPoint.setVisibility(View.VISIBLE);
					new ListGlobalPoints().execute();
				}
			} else {
				// Load service to get Dinner's status and point number
				mProgressStatus.setVisibility(View.VISIBLE);
				mProgressPoint.setVisibility(View.VISIBLE);
				new ListGlobalPoints().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mRlMyPoint = (RelativeLayout) findViewById(R.id.activity_myaccount_mypoint);
		mRlMyPoint.setOnClickListener(this);
		mRlMyNotification = (RelativeLayout) findViewById(R.id.activity_myaccount_mynotification);
		mRlMyNotification.setOnClickListener(this);
		mRlMySetting = (RelativeLayout) findViewById(R.id.activity_myaccount_mysetting);
		mRlMySetting.setOnClickListener(this);
		mRlSearch = (RelativeLayout) findViewById(R.id.activity_myaccount_mysearch);
		mRlSearch.setOnClickListener(this);
		mRlMyFriend = (RelativeLayout) findViewById(R.id.activity_myaccount_myfriend);
		mRlMyFriend.setOnClickListener(this);
		mRlMyWallet = (RelativeLayout) findViewById(R.id.activity_myaccount_mywallet);
		mRlMyWallet.setOnClickListener(this);
		mRlMyFavorites = (RelativeLayout) findViewById(R.id.activity_myaccount_myfavorites);
		mRlMyFavorites.setOnClickListener(this);
		mRlMyCalories = (RelativeLayout) findViewById(R.id.activity_myaccount_mycalories);
		mRlMyCalories.setOnClickListener(this);
		mRlMyOrderHistory = (RelativeLayout) findViewById(R.id.activity_myaccount_myorderhistory);
		mRlMyOrderHistory.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	/**
	 * @Description Load the status and point again when coming back from other
	 *              Activity. For example: When click on Back press from
	 *              MyNotificationActivity
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (!mLoadFirstTime) {
			mTvPoint.setVisibility(View.GONE);
			// Load service to get Dinner's status and point number again
			mProgressStatus.setVisibility(View.VISIBLE);
			mProgressPoint.setVisibility(View.VISIBLE);
			if (mIsFromRestaurant) {
				// Load point,status from restaurants
				new LoadPointStatusAgainTask().execute(mRestaurantId);
			} else {
				// Load point,status from global point
				new ListGlobalPoints().execute();
			}
		}
		mLoadFirstTime = false;
	}

	@Override
	public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (!Utils.isNetworkConnected(this)) {
			showToastMessage(getString(R.string.mess_error_network));
			return;
		}

		Intent intent = null;
		if (v == this.mRlMyPoint) {
			intent = new Intent(this, MyPointGlobalActivity.class);
		} else if (v == this.mRlMyNotification) {
			intent = new Intent(this, MyNotificationGlobalActivity.class);

		} else if (v == this.mRlMySetting) {
			intent = new Intent(this, AccountSettingActivity.class);
		} else if (v == this.mRlSearch) {
			// Change to Advanced Search
			intent = new Intent(this, MySearchActivity.class);
		} else if (v == this.mRlMyFriend) {
			intent = new Intent(this, MyFriendsActivity.class);
		} else if (v == this.mRlMyWallet) {

		} else if (v == this.mRlMyFavorites) {
			intent = new Intent(this, MyFavoriteGlobalActivity.class);
		} else if (v == this.mRlMyCalories) {

		} else if (v == this.mRlMyOrderHistory) {
			intent = new Intent(this, MyOrderGlobalActivity.class);
		}

		if (intent != null) {
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(CodeResult.CODE_RESULT_MYACCOUNT);
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageLoader != null)
			mImageLoader.clearCache();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYACCOUNT) {
			// Update Avatar after changing it from EditInfo
			mImageLoader.clearCache();
			mIvUserLogo = (ImageView) findViewById(R.id.activity_myaccount_userLogo);
			mImageLoader.DisplayImage(
					ServerURL.URL
							+ UserObject.getInstance(MyAccountActivity.this)
									.getProfileAvatar(), mIvUserLogo);
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			} else if (resultCode == CodeRequest.CODE_RESULT_START_OVER) {
				setResult(CodeRequest.CODE_RESULT_START_OVER);
				finish();
			} else if (resultCode == CodeRequest.CODE_RESULT_GO_HOME) {
				this.resultCode = resultCode;
			}
		}
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
		}

		@Override
		protected MyPointGlobalEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGlobalPoint(UserObject
								.getInstance(MyAccountActivity.this).mAccess_token));
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
					mProgressStatus.setVisibility(View.GONE);
					mProgressPoint.setVisibility(View.GONE);
					if (Utils.isNetworkConnected(MyAccountActivity.this)) {
						showToastMessage(MyAccountActivity.this.getResources()
								.getString(R.string.mess_error_server));
					} else {
						showToastMessage(MyAccountActivity.this.getResources()
								.getString(R.string.mess_error_network));
					}
					return;
				}
				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}
				UserGlobalEntity mUserGlobal = new UserGlobalEntity();
				mUserGlobal = result.mUserGlobal;
				if (mUserGlobal.getPointNumber() > 1) {
					mTvPointTitle.setText("Points:");
				} else {
					mTvPointTitle.setText("Point :");
				}
				mTvPoint.setVisibility(View.VISIBLE);
				mTvPoint.setText(" "
						+ Utils.formatPointNumbers(mUserGlobal.getPointNumber()));
				if (!result.mUserGlobal.getStatus().equals("")) {
					mTvStatus.setText(" " + mUserGlobal.getStatus());
				}
				mProgressStatus.setVisibility(View.GONE);
				mProgressPoint.setVisibility(View.GONE);

			} catch (Exception e) {
				e.toString();
			}

		}

	}

	/**
	 * 
	 * @Description This task to load restaurant detailed information again to
	 *              get Diner and Point infos
	 * 
	 */
	public class LoadPointStatusAgainTask extends
			AsyncTask<Integer, Void, LocationEntity> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected LocationEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlLocationByID(params[0],
								UserObject.getInstance(MyAccountActivity.this)
										.getAccess_token()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				LocationEntity response = gson.fromJson(json,
						LocationEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
			}

			return null;
		}

		@Override
		protected void onPostExecute(LocationEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					Toast.makeText(MyAccountActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					finish();
					return;
				}
				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}
				if (result.mError != null) {
					Toast.makeText(MyAccountActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					LocationEntity mLocation = result;
					float point = mLocation.getDinnerInfos().getTotal();
					if (point > 1) {
						mTvPointTitle.setText("Points:");
					} else {
						mTvPointTitle.setText("Point :");
					}
					mTvPoint.setVisibility(View.VISIBLE);
					mTvPoint.setText(" " + Utils.formatPointNumbers(point));
					if (!mLocation.getDinnerInfos().getDinnerStatus()
							.equals("")) {
						mTvStatus.setText(" "
								+ mLocation.getDinnerInfos().getDinnerStatus());
					}
					// dismissProgress();
					mProgressStatus.setVisibility(View.GONE);
					mProgressPoint.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.toString();
			}
		}
	}

	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			if (mLayoutMenu != null) {
				if (mLayoutMenu.isShown()) {
					mLayoutMenu.setVisibility(View.GONE);
				} else {
					mLayoutMenu.setVisibility(View.VISIBLE);
					mLayoutMenu.startAnimation(mAnimationShow);
				}
			}

		}
		if (keycode == KeyEvent.KEYCODE_BACK) {
			if (resultCode == CodeRequest.CODE_RESULT_GO_HOME) {
				setResult(CodeRequest.CODE_RESULT_START_OVER);
				onBackPressed();
			} else {
				onBackPressed();
			}
		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYACCOUNT);
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyAccountActivity.this,
				null, "", 0, true);
		dialog.show();
	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);

		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		} else {
			showToastMessage(getString(R.string.mess_error_network));
			return;
		}

	}

	@Override
	public void onThumbnailClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onSearchClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onFeedbackClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		myFeedback();
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(MyAccountActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyAccountActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyAccountActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyAccountActivity.this)) {
														new PostMyFeedBackAsynTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														showToastMessage(getResources()
																.getString(
																		R.string.mess_error_network));
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

											@Override
											public void click_Cancel() {
												mDialogConfirmRating.dismiss();
											}
										},
										Utils.exchangeRateGetExtra(mDialogMyFeedback
												.rateScore()),
										mDialogMyFeedback.rateComment());

								mDialogConfirmRating.show();

							} else {
								showToastMessage(getResources().getString(
										R.string.mess_error_network));
							}
						}

						@Override
						public void click_Cancel() {
							mDialogMyFeedback.dismiss();
						}
					}, 13, getResources().getString(
							R.string.custom_seebar_rate_tittle_menuitem));

		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialogMyFeedback.show();

	}

	public class PostMyFeedBackAsynTask extends
			AsyncTask<String, Void, MyFeedBackEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MyFeedBackEntity doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server.requestPost(ServerURL.getUrlMyFeedback(),
						ServerURL.getEntityMyFeedBack(
								UserObject.getInstance(MyAccountActivity.this)
										.getAccess_token(), mDialogMyFeedback
										.rateScore(), mDialogMyFeedback
										.rateComment()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFeedBackEntity response = gson.fromJson(json,
						MyFeedBackEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFeedBackEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MyAccountActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					showToastMessage(getString(R.string.dialog_my_feedback_mess_success));
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}

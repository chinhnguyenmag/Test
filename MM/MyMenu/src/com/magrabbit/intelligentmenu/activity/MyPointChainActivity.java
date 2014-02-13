package com.magrabbit.intelligentmenu.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyPointChainAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyPointChainEntity;
import com.magrabbit.intelligentmenu.model.MyPointChainListLocationEntity;
import com.magrabbit.intelligentmenu.model.MyPointListLocationEntity;
import com.magrabbit.intelligentmenu.model.MyPointLocationGlobal;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author vule
 * @Description: View all chains of specific restaurants
 */
public class MyPointChainActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListView;
	private MyPointChainAdapter mAdapter;
	private List<MyPointChainListLocationEntity> mListChainRestaurants;
	private ImageLoader mImageLoader;
	private MyPointListLocationEntity mGlobalObject;
	private List<MyPointLocationGlobal> mLocationGlobal;

	/* Layout elements */
	private ImageButton mImbtSortPoint;
	private ImageButton mImbtSortAZ;
	private ImageView mLogo;
	private TextView mPoint;
	private TextView mStatus;
	private String mBundleStatus;
	private LinearLayout mLlSortAz;
	private LinearLayout mLlSortAmount;
	private LinearLayout mLlLogoBackPress;
	private LinearLayout mLlShape;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mIsFirstTimeToLoad = false;

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
		setContentView(R.layout.activity_mypoint_chain);
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.activity_mypoint_chain_listview);
		mImbtSortAZ = (ImageButton) findViewById(R.id.activity_mypoint_chain_imbt_sortAZ);
		mImbtSortPoint = (ImageButton) findViewById(R.id.activity_mypoint_chain_imbt_sortPoint);
		mLogo = (ImageView) findViewById(R.id.activity_mypoint_chain_logo);
		mPoint = (TextView) findViewById(R.id.mypoint_chain_points);
		mStatus = (TextView) findViewById(R.id.mypoint_chain_status);
		mLlSortAz = (LinearLayout) findViewById(R.id.activity_mypoint_chain_layout_az);
		mLlSortAmount = (LinearLayout) findViewById(R.id.activity_mypoint_chain_layout_amount);
		mImageLoader = new ImageLoader(this);
		mLlLogoBackPress = (LinearLayout) findViewById(R.id.activity_mypoint_chain_layout_icon);
		mLlShape = (LinearLayout) findViewById(R.id.mypoint_chain_shape);
		mIsFirstTimeToLoad = true;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mGlobalObject = getIntent().getExtras().getParcelable(
					StringExtraUtils.KEY_MYPOINT_GLOBAL);

		}

		new ListChainPoints().execute();

		mLlSortAz.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortByAZ();
				} else if (count % 2 == 0) {
					onClick_sortByZa();
				}

			}
		});

		mLlLogoBackPress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mLlSortAmount.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortAmountHightToLow();
				} else if (count % 2 == 0) {
					onClick_sortAmountLowToHigh();
				}

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapt, View view, int pos,
					long id) {
				if (!Utils.isNetworkConnected(MyPointChainActivity.this)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_network));
					return;
				}
				Intent intent = new Intent(MyPointChainActivity.this,
						MyPointTransactionActivity.class);
				intent.putExtra(StringExtraUtils.KEY_MYPOINT_CHAIN,
						mListChainRestaurants.get(pos));
				intent.putExtra(StringExtraUtils.KEY_MYPOINT_CHAIN_STATUS,
						mBundleStatus);
				// startActivity(intent);

				if (intent != null) {
					startActivityForResult(intent,
							CodeRequest.CODE_REQUEST_MYPOINTCHAIN);
				}

			}
		});
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYPOINTCHAIN) {
			// Update Avatar after changing it from EditInfo
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	public class ListChainPoints extends
			AsyncTask<String, Void, MyPointChainEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyPointChainEntity doInBackground(String... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlChainPoint(
								UserObject
										.getInstance(MyPointChainActivity.this).mAccess_token,
								mGlobalObject.getChainName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Gson gson = new Gson();
			String json = Server.inputStreamToString(source).toString();
			MyPointChainEntity response = gson.fromJson(json,
					MyPointChainEntity.class);
			return response;
		}

		@Override
		protected void onPostExecute(MyPointChainEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MyPointChainActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(getString(R.string.mess_error_server));
					}
					dismissProgress();
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
					Toast.makeText(MyPointChainActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {

					if (mListChainRestaurants == null) {
						mListChainRestaurants = new ArrayList<MyPointChainListLocationEntity>();
					}

					mImageLoader.DisplayImage(
							ServerURL.URL + mGlobalObject.getLogo(), mLogo);

					mBundleStatus = result.mListLocationGlobal.get(0)
							.getDinnerStatus();
					mLocationGlobal = new ArrayList<MyPointLocationGlobal>();
					mLocationGlobal.addAll(result.mListLocationGlobal);
					mListChainRestaurants.clear();
					mListChainRestaurants.addAll(result.mLocationChain);

					float totalPoint = 0;

					for (int i = 0; i < mListChainRestaurants.size(); i++) {
						totalPoint += mListChainRestaurants.get(i).mTotal;
					}

					mLlShape.setVisibility(View.VISIBLE);
					if (mGlobalObject.mTotalPoint == 0
							&& result.mListLocationGlobal.get(0)
									.getDinnerStatus().equals("")) {
						mPoint.setText("0 points");
						mStatus.setText("Status:");
					} else {

						if (totalPoint > 1) {
							mPoint.setText(Utils.formatPointNumbers(totalPoint)
									+ " Points");
						} else {
							mPoint.setText(Utils.formatPointNumbers(totalPoint)
									+ " Point");
						}
						mStatus.setText("Status: "
								+ result.mListLocationGlobal.get(0)
										.getDinnerStatus());
					}

					mAdapter = new MyPointChainAdapter(
							MyPointChainActivity.this,
							R.layout.mypoint_chain_item, mListChainRestaurants);
					mListView.setAdapter(mAdapter);
					onClick_sortAmountHightToLow();
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (!mIsFirstTimeToLoad) {
//			new ListChainPoints().execute();
//		}
//		mIsFirstTimeToLoad = false;
	}

	/**
	 * 
	 * @param v
	 * @Description: Sort list followed by alphabet order
	 */
	public void onClick_sortByAZ() {
		try {
			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortPoint.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyPointChainListLocationEntity>() {
						@Override
						public int compare(MyPointChainListLocationEntity lhs,
								MyPointChainListLocationEntity rhs) {
							if (lhs.getName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getName()
													.toString()
													.trim()
													.toUpperCase(Locale.ENGLISH)) > 0)
								return 1;
							else if (lhs
									.getName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getName()
													.toString()
													.trim()
													.toUpperCase(Locale.ENGLISH)) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
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
		return super.onKeyDown(keycode, event);
	}

	public void onClick_sortByZa() {
		try {
			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortPoint.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyPointChainListLocationEntity>() {
						@Override
						public int compare(MyPointChainListLocationEntity lhs,
								MyPointChainListLocationEntity rhs) {
							if (lhs.getName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getName()
													.toString()
													.trim()
													.toUpperCase(Locale.ENGLISH)) > 0)
								return -1;
							else if (lhs
									.getName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getName()
													.toString()
													.trim()
													.toUpperCase(Locale.ENGLISH)) < 0)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @param v
	 * @Description: Sort list followed by number of points order
	 */
	public void onClick_sortAmountHightToLow() {
		try {
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			mImbtSortPoint.setVisibility(View.VISIBLE);
			mImbtSortPoint.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyPointChainListLocationEntity>() {
						@Override
						public int compare(MyPointChainListLocationEntity lhs,
								MyPointChainListLocationEntity rhs) {
							if (lhs.mTotal > rhs.mTotal)
								return -1;
							else if (lhs.mTotal < rhs.mTotal)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	public void onClick_sortAmountLowToHigh() {
		try {
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			mImbtSortPoint.setVisibility(View.VISIBLE);
			mImbtSortPoint.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyPointChainListLocationEntity>() {
						@Override
						public int compare(MyPointChainListLocationEntity lhs,
								MyPointChainListLocationEntity rhs) {
							if (lhs.mTotal > rhs.mTotal)
								return 1;
							else if (lhs.mTotal < rhs.mTotal)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTCHAIN);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		/*
		 * DialogMyAccount dialog = new DialogMyAccount(this, new
		 * ProcessDialogMyAccount() {
		 * 
		 * @Override public void click_Dialog(Intent intent) { if (intent !=
		 * null) { startActivityForResult(intent,
		 * CodeRequest.CODE_REQUEST_MENUITEM); } } }); dialog.show();
		 */
		startActivityForResult(new Intent(MyPointChainActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYPOINTCHAIN);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyPointChainActivity.this,
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

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(MyPointChainActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyPointChainActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyPointChainActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyPointChainActivity.this)) {
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
								UserObject.getInstance(
										MyPointChainActivity.this)
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
					if (!Utils.isNetworkConnected(MyPointChainActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
					}
					dismissProgress();
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
					Toast.makeText(MyPointChainActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
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

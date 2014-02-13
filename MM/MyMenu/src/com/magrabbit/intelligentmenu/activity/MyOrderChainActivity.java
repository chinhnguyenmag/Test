package com.magrabbit.intelligentmenu.activity;

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
import com.magrabbit.intelligentmenu.adapter.MyOrderChainAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyOrderChainEntity;
import com.magrabbit.intelligentmenu.model.MyOrderChainObject;
import com.magrabbit.intelligentmenu.model.MyOrderGlobalObject;
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
 * @author baonguyen
 * @Description: View all order chains of a global restaurant
 */
public class MyOrderChainActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListView;
	private MyOrderChainAdapter mAdapter;
	private List<MyOrderChainObject> mListChainRestaurants;
	private ImageLoader mImageLoader;
	private MyOrderGlobalObject globalObject;

	/* Layout elements */
	private ImageButton mIbtSortOrderNumber;
	private ImageButton mIbtSortAZ;
	private ImageView mLogo;
	private TextView mTvOrderNumber;
	private TextView mTvStatus;
	private LinearLayout mLayoutSortAz;
	private LinearLayout mLayoutSortNumberOrder;
	private LinearLayout mLayoutLogoBackPress;
	private LinearLayout mLayoutShape;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	// For loading data first time
	private boolean mIsFirstTimeToLoad = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_chain);
		this.mIsFirstTimeToLoad = true;
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.activity_myorder_chain_listview);
		mIbtSortAZ = (ImageButton) findViewById(R.id.activity_myorder_chain_imbt_sortAZ);
		mIbtSortOrderNumber = (ImageButton) findViewById(R.id.activity_myorder_chain_imbt_sortOrderNumber);
		mLogo = (ImageView) findViewById(R.id.activity_myorder_chain_logo);
		mTvOrderNumber = (TextView) findViewById(R.id.activity_myorder_chain_tv_order_number);
		mTvStatus = (TextView) findViewById(R.id.activity_myorder_chain_tv_status);
		mLayoutSortAz = (LinearLayout) findViewById(R.id.activity_myorder_chain_layout_az);
		mLayoutSortNumberOrder = (LinearLayout) findViewById(R.id.activity_myorder_chain_layout_amount);
		mImageLoader = new ImageLoader(this);
		mLayoutLogoBackPress = (LinearLayout) findViewById(R.id.activity_myorder_chain_layout_icon);
		mLayoutShape = (LinearLayout) findViewById(R.id.myorder_chain_shape);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			globalObject = (MyOrderGlobalObject) bundle
					.getSerializable(StringExtraUtils.KEY_MYORDER_GLOBAL);
		}

		if (globalObject.getNumber() > 1) {
			mTvOrderNumber.setText(globalObject.getNumber() + " Orders");
		} else {
			mTvOrderNumber.setText(globalObject.getNumber() + " Order");
		}
		if (globalObject.getStatus() != null) {
			mTvStatus.setText("Status: " + UserObject.getInstance(MyOrderChainActivity.this).mUserStatus);
		} else {
			mTvStatus.setText("Status: ");
		}

		// Call service to load list of chain orders
		new ListChainOrders().execute();

		mLayoutSortAz.setOnClickListener(new OnClickListener() {
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

		mLayoutLogoBackPress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mLayoutSortNumberOrder.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortAmountLowToHigh();
				} else if (count % 2 == 0) {
					onClick_sortAmountHightToLow();
				}

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapt, View view, int pos,
					long id) {
				if (!Utils.isNetworkConnected(MyOrderChainActivity.this)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_network));
					return;
				}
				Intent intent = new Intent(MyOrderChainActivity.this,
						MyOrderByLocationActivity.class);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_CHAIN,
						mListChainRestaurants.get(pos));
				intent.putExtra(StringExtraUtils.KEY_MYORDER_CHAIN_STATUS,
						UserObject.getInstance(MyOrderChainActivity.this).mUserStatus);

				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MYORDER_CHAIN);

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
		if (requestCode == CodeRequest.CODE_REQUEST_MYORDER_CHAIN) {
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

	public class ListChainOrders extends
			AsyncTask<String, Void, MyOrderChainEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyOrderChainEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGetMyOrderChain(
								UserObject
										.getInstance(MyOrderChainActivity.this).mAccess_token,
								globalObject.getName()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyOrderChainEntity response = gson.fromJson(json,
						MyOrderChainEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyOrderChainEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyOrderChainActivity.this)) {
						showToastMessage(MyOrderChainActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyOrderChainActivity.this
								.getResources().getString(
										R.string.mess_error_network));
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

				if (mListChainRestaurants == null) {
					mListChainRestaurants = new ArrayList<MyOrderChainObject>();
				}

				mImageLoader.DisplayImage(
						ServerURL.URL + globalObject.getLogo(), mLogo);

				mLayoutShape.setVisibility(View.VISIBLE);

				mListChainRestaurants.clear();
				mListChainRestaurants.addAll(result.mListChainOrders);
				mAdapter = new MyOrderChainAdapter(MyOrderChainActivity.this,
						R.layout.myorder_chain_item, mListChainRestaurants);
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				onClick_sortAmountHightToLow();
				dismissProgress();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		 if (!mIsFirstTimeToLoad) {
		 new ListChainOrders().execute();
		 }
		 mIsFirstTimeToLoad = false;
	}

	/**
	 * 
	 * @param v
	 * @Description: Sort list followed by alphabet order
	 */
	public void onClick_sortByAZ() {
		try {
			mIbtSortAZ.setVisibility(View.VISIBLE);
			mIbtSortOrderNumber.setVisibility(View.INVISIBLE);
			mIbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyOrderChainObject>() {
						@Override
						public int compare(MyOrderChainObject lhs,
								MyOrderChainObject rhs) {
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
			mIbtSortAZ.setVisibility(View.VISIBLE);
			mIbtSortOrderNumber.setVisibility(View.INVISIBLE);
			mIbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyOrderChainObject>() {
						@Override
						public int compare(MyOrderChainObject lhs,
								MyOrderChainObject rhs) {
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
			mIbtSortAZ.setVisibility(View.INVISIBLE);
			mIbtSortOrderNumber.setVisibility(View.VISIBLE);
			mIbtSortOrderNumber.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyOrderChainObject>() {
						@Override
						public int compare(MyOrderChainObject lhs,
								MyOrderChainObject rhs) {
							if (lhs.getNumber() > rhs.getNumber())
								return -1;
							else if (lhs.getNumber() < rhs.getNumber())
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
			mIbtSortAZ.setVisibility(View.INVISIBLE);
			mIbtSortOrderNumber.setVisibility(View.VISIBLE);
			mIbtSortOrderNumber.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListChainRestaurants,
					new Comparator<MyOrderChainObject>() {
						@Override
						public int compare(MyOrderChainObject lhs,
								MyOrderChainObject rhs) {
							if (lhs.getNumber() > rhs.getNumber())
								return 1;
							else if (lhs.getNumber() < rhs.getNumber())
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
				CodeRequest.CODE_REQUEST_MYORDER_CHAIN);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyOrderChainActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYPOINTCHAIN);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyOrderChainActivity.this,
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
			mDialogMyFeedback = new DialogMyFeedback(MyOrderChainActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyOrderChainActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyOrderChainActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyOrderChainActivity.this)) {
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
										MyOrderChainActivity.this)
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
					if (!Utils.isNetworkConnected(MyOrderChainActivity.this)) {
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

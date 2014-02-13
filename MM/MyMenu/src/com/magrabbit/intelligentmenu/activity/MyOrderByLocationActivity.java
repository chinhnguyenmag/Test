package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import com.magrabbit.intelligentmenu.adapter.MyOrderByLocationAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyOrderChainObject;
import com.magrabbit.intelligentmenu.model.OrderByLocationEntity;
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
 * @Description: View all the transaction history of one chain of specific
 *               restaurant included sharing point, reward point
 */
public class MyOrderByLocationActivity extends BaseActivity implements
		OptionMenuClickListener {

	private MyOrderByLocationAdapter mAdapter;
	// private List<OrderDetailsTotalEntity> mOrderInfo;
	private List<OrderByLocationEntity> mListOrderInfos;
	// Total Spent and Status information
	private float mTotalSpent = 0.00f;
	private String mStatus;
	private ListView mListView;
	private ImageView mLogo;
	private TextView mTvChainName;
	private TextView mTvAddress;
	private TextView mTvCityState;
	private TextView mTvTotalPrice;
	private TextView mTvDinnerStatus;
	private ImageLoader mImageLoader;
	private MyOrderChainObject mChainObject;
	private ImageButton mIbSortPoint;
	private ImageButton mIbSorDate;
	private LinearLayout mLlSortDate;
	private LinearLayout mLlSortAmount;
	private LinearLayout mLlLogoBackPress;
	private LinearLayout mLlShape;
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
		setContentView(R.layout.activity_myorder_by_location);
		this.mIsFirstTimeToLoad = true;

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.activity_myorder_by_location_listview);
		mLogo = (ImageView) findViewById(R.id.activity_myorder_by_location_restaurant_logo);
		mTvChainName = (TextView) findViewById(R.id.activity_myorder_by_location_tv_nameRestaurant);
		mTvAddress = (TextView) findViewById(R.id.activity_myorder_by_location_tv_addressRestaurant);
		mTvCityState = (TextView) findViewById(R.id.activity_myorder_by_location_tv_stateRestaurant);
		mTvTotalPrice = (TextView) findViewById(R.id.activity_myorder_by_location_tv_dinnerPoint);
		mTvDinnerStatus = (TextView) findViewById(R.id.activity_myorder_by_location_tv_dinnerStatus);
		mIbSortPoint = (ImageButton) findViewById(R.id.activity_myorder_by_location_ib_sortAmount);
		mIbSorDate = (ImageButton) findViewById(R.id.activity_myorder_by_location_ib_sortdate);
		mLlSortDate = (LinearLayout) findViewById(R.id.activity_myorder_by_location_layout_date);
		mLlSortAmount = (LinearLayout) findViewById(R.id.activity_myorder_by_location_layout_amount);
		mLlLogoBackPress = (LinearLayout) findViewById(R.id.activity_myorder_by_location_layout_icon);
		mLlShape = (LinearLayout) findViewById(R.id.activity_myorder_by_location_shape);
		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mChainObject = (MyOrderChainObject) getIntent().getExtras()
					.getSerializable(StringExtraUtils.KEY_MYORDER_CHAIN);
			mStatus = bundle
					.getString(StringExtraUtils.KEY_MYORDER_CHAIN_STATUS);
		}
		if (mStatus != null) {
			mTvDinnerStatus.setText("Status: " + mStatus);
		} else {
			mTvDinnerStatus.setText("Status: ");
		}
		mImageLoader
				.DisplayImage(ServerURL.URL + mChainObject.getLogo(), mLogo);

		mTvChainName.setText(mChainObject.getName());

		mLlShape.setVisibility(View.VISIBLE);
		mTvDinnerStatus
				.setText("Status: "
						+ UserObject
								.getInstance(MyOrderByLocationActivity.this).mUserStatus);

		mTvAddress.setText(mChainObject.getAddress());
		mTvCityState.setText(mChainObject.getCity() + ", "
				+ mChainObject.getState() + " " + mChainObject.getZip());

		/************ Call service to load the list of order by location **************/
		new ListOrderByLocation().execute();

		mLlLogoBackPress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mLlSortDate.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortDateRecent();
				} else if (count % 2 == 0) {
					onClick_sortDateFar();
				}
			}
		});

		mLlSortAmount.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortAmoutHighToLow();
				} else if (count % 2 == 0) {
					onClick_sortAmoutLowToHigh();
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapt, View view, int pos,
					long id) {

				Intent intent = new Intent(MyOrderByLocationActivity.this,
						MyOrderDetailsActivity.class);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_BYLOCATION,
						mChainObject);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_DETAILS,
						mListOrderInfos.get(pos));
				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MYORDER_BYLOCATION);
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYORDER_BYLOCATION) {
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

	public void onClick_sortAmoutHighToLow() {
		try {
			mIbSortPoint.setVisibility(View.VISIBLE);
			mIbSorDate.setVisibility(View.INVISIBLE);
			mIbSortPoint.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListOrderInfos,
					new Comparator<OrderByLocationEntity>() {
						@Override
						public int compare(OrderByLocationEntity lhs,
								OrderByLocationEntity rhs) {
							if (lhs.getPrice() > rhs.getPrice())
								return -1;
							else if (lhs.getPrice() < rhs.getPrice())
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

	public void onClick_sortAmoutLowToHigh() {
		try {
			mIbSortPoint.setVisibility(View.VISIBLE);
			mIbSorDate.setVisibility(View.INVISIBLE);
			mIbSortPoint.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListOrderInfos,
					new Comparator<OrderByLocationEntity>() {
						@Override
						public int compare(OrderByLocationEntity lhs,
								OrderByLocationEntity rhs) {
							if (lhs.getPrice() > rhs.getPrice())
								return 1;
							else if (lhs.getPrice() < rhs.getPrice())
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

	public void onClick_sortDateRecent() {
		try {
			mIbSortPoint.setVisibility(View.INVISIBLE);
			mIbSorDate.setVisibility(View.VISIBLE);
			mIbSorDate.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListOrderInfos,
					new Comparator<OrderByLocationEntity>() {

						@Override
						public int compare(OrderByLocationEntity lhs,
								OrderByLocationEntity rhs) {
							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yyyy hh:mm:ss");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getOrderDate()
										.replace("-", "."));
								d2 = form.parse(rhs.getOrderDate()
										.replace("-", "."));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (d1.compareTo(d2) > 0) {
								return -1;
							} else if (d1.compareTo(d2) < 0) {
								return 1;
							} else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	public void onClick_sortDateFar() {
		try {
			mIbSortPoint.setVisibility(View.INVISIBLE);
			mIbSorDate.setVisibility(View.VISIBLE);
			mIbSorDate.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListOrderInfos,
					new Comparator<OrderByLocationEntity>() {

						@Override
						public int compare(OrderByLocationEntity lhs,
								OrderByLocationEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yyyy hh:mm:ss");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getOrderDate()
										.replace("-", "."));
								d2 = form.parse(rhs.getOrderDate()
										.replace("-", "."));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (d1.compareTo(d2) > 0) {
								return 1;
							} else if (d1.compareTo(d2) < 0) {
								return -1;
							} else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	public class ListOrderByLocation extends
			AsyncTask<Void, Void, OrderByLocationEntity[]> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected OrderByLocationEntity[] doInBackground(Void... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGetMyOrderByLocation(
								UserObject
										.getInstance(MyOrderByLocationActivity.this).mAccess_token,
								mChainObject.getId()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				OrderByLocationEntity[] response = gson.fromJson(json,
						OrderByLocationEntity[].class);

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(OrderByLocationEntity[] result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					dismissProgress();
					if (Utils
							.isNetworkConnected(MyOrderByLocationActivity.this)) {
						showToastMessage(MyOrderByLocationActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyOrderByLocationActivity.this
								.getResources().getString(
										R.string.mess_error_network));
					}
					if (Server.mStatus != 200) {
						dismissProgress();
						Toast.makeText(getApplicationContext(),
								getString(R.string.mess_invalid_accesstoken),
								Toast.LENGTH_LONG).show();
						setResult(CodeRequest.CODE_RESULT_LOGOUT);
						finish();
					}
					return;
				}
				if (mListOrderInfos == null) {
					mListOrderInfos = new ArrayList<OrderByLocationEntity>();
				}

				mListOrderInfos.clear();
				for (int i = 0; i < result.length; i++) {
					mListOrderInfos.add(result[i]);
				}

				// calculate Total Spent by adding all order item price
				mTotalSpent = 0.00f;
				for (OrderByLocationEntity order : mListOrderInfos) {
					mTotalSpent += order.getPrice();
				}
				mTvTotalPrice.setText("Total Spent: $"
						+ Utils.formatNumber(mTotalSpent, "0.00"));
				mAdapter = new MyOrderByLocationAdapter(
						MyOrderByLocationActivity.this,
						R.layout.myorder_by_location_item, mListOrderInfos);
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				onClick_sortDateRecent();
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
			new ListOrderByLocation().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_BYLOCATION);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyOrderByLocationActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_BYLOCATION);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyOrderByLocationActivity.this, null, "", 0, true);
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
			mDialogMyFeedback = new DialogMyFeedback(
					MyOrderByLocationActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyOrderByLocationActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyOrderByLocationActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyOrderByLocationActivity.this)) {
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

	/**
	 * 
	 * @param date
	 *            Ex:2013-12-21 06:07:30
	 * @return Get the string of 2013-12-21
	 */
	public String splitDate(String date) {
		return date.substring(0, 10);
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
										MyOrderByLocationActivity.this)
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
					if (!Utils
							.isNetworkConnected(MyOrderByLocationActivity.this)) {
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

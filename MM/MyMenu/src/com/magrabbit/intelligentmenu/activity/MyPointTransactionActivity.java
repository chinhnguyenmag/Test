package com.magrabbit.intelligentmenu.activity;

import java.io.IOException;
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
import com.magrabbit.intelligentmenu.adapter.MyPointTransactionAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyPointChainListLocationEntity;
import com.magrabbit.intelligentmenu.model.MyPointRestaurantEntity;
import com.magrabbit.intelligentmenu.model.MyPointRestaurantListPointEntity;
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
 * @Description: View all the transaction history of one chain of specific
 *               restaurant included sharing point, reward point
 */
public class MyPointTransactionActivity extends BaseActivity implements
		OptionMenuClickListener {

	private MyPointTransactionAdapter mAdapter;
	private List<MyPointRestaurantEntity> mListTransactions;
	private List<MyPointRestaurantListPointEntity> mListTransactionInfos;
	private ListView mListView;
	private ImageView mLogo;
	private TextView mChainName;
	private TextView mAddress;
	private TextView mCityState;
	private TextView mDinnerPoint;
	private TextView mDinnerStatus;
	private ImageLoader mImageLoader;
	private MyPointChainListLocationEntity chainObject;
	private String mBdStatus;
	private ImageButton mIbSortPoint;
	private ImageButton mIbSorDate;
	private LinearLayout mLlSortDate;
	private LinearLayout mLlSortAmount;
	private String mBundleStatus;
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
		setContentView(R.layout.activity_mypoint_transaction);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.activity_mypoint_transaction_listview);
		mLogo = (ImageView) findViewById(R.id.activity_mypoint_transaction_restaurant_logo);
		mChainName = (TextView) findViewById(R.id.activity_mypoint_transaction_tv_nameRestaurant);
		mAddress = (TextView) findViewById(R.id.activity_mypoint_transaction_tv_addressRestaurant);
		mCityState = (TextView) findViewById(R.id.activity_mypoint_transaction_tv_stateRestaurant);
		mDinnerPoint = (TextView) findViewById(R.id.activity_mypoint_transaction_tv_dinnerPoint);
		mDinnerStatus = (TextView) findViewById(R.id.activity_mypoint_transaction_tv_dinnerStatus);
		mIbSortPoint = (ImageButton) findViewById(R.id.mypoint_ib_sortAmount);
		mIbSorDate = (ImageButton) findViewById(R.id.mypoint_ib_sortdate);
		mLlSortDate = (LinearLayout) findViewById(R.id.activity_mypoint_transaction_layout_date);
		mLlSortAmount = (LinearLayout) findViewById(R.id.activity_mypoint_transaction_layout_amount);
		mLlLogoBackPress = (LinearLayout) findViewById(R.id.activity_mypoint_transaction_layout_icon);
		mLlShape = (LinearLayout) findViewById(R.id.mypoint_transac_shape);
		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mIsFirstTimeToLoad = true;
		// Uncomment to get data from MyPointRestaurantActivity

		Bundle bGetChainỌbject = getIntent().getExtras();
		if (bGetChainỌbject != null) {
			chainObject = getIntent().getExtras().getParcelable(
					StringExtraUtils.KEY_MYPOINT_CHAIN);

		}

		Bundle bStatus = getIntent().getExtras();
		if (bStatus != null) {
			mBdStatus = getIntent().getStringExtra(
					StringExtraUtils.KEY_MYPOINT_CHAIN_STATUS);
		}

		new ListRestaurantPoints().execute();

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
				// if
				// (!Utils.isNetworkConnected(MyPointTransactionActivity.this))
				// {
				// showToastMessage(getString(R.string.mess_error_network));
				// return;
				// }
				/*
				 * Intent intent = new Intent(MyPointTransactionActivity.this,
				 * sdlkfjdklfjkl.class);
				 * intent.putExtra(StringExtraUtils.KEY_MYPOINT_GLOBAL,
				 * mListChains.get(pos)); startActivity(intent);
				 */
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYPOINTTRANSAC) {
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

			Collections.sort(mListTransactionInfos,
					new Comparator<MyPointRestaurantListPointEntity>() {
						@Override
						public int compare(
								MyPointRestaurantListPointEntity lhs,
								MyPointRestaurantListPointEntity rhs) {
							if (lhs.mPoint > rhs.mPoint)
								return -1;
							else if (lhs.mPoint < rhs.mPoint)
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

			Collections.sort(mListTransactionInfos,
					new Comparator<MyPointRestaurantListPointEntity>() {
						@Override
						public int compare(
								MyPointRestaurantListPointEntity lhs,
								MyPointRestaurantListPointEntity rhs) {
							if (lhs.mPoint > rhs.mPoint)
								return 1;
							else if (lhs.mPoint < rhs.mPoint)
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

			Collections.sort(mListTransactionInfos,
					new Comparator<MyPointRestaurantListPointEntity>() {

						@Override
						public int compare(
								MyPointRestaurantListPointEntity lhs,
								MyPointRestaurantListPointEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM/dd/yyyy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getMostRecent());
								d2 = form.parse(rhs.getMostRecent());
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

			Collections.sort(mListTransactionInfos,
					new Comparator<MyPointRestaurantListPointEntity>() {

						@Override
						public int compare(
								MyPointRestaurantListPointEntity lhs,
								MyPointRestaurantListPointEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM/dd/yyyy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getMostRecent());
								d2 = form.parse(rhs.getMostRecent());
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

	public void onClick_sharepoint(View v) {
		Intent intent = new Intent(MyPointTransactionActivity.this,
				MyPointShareActivity.class);
		intent.putExtra(StringExtraUtils.KEY_MYPOINT_CHAIN, chainObject);
		intent.putExtra(StringExtraUtils.KEY_MYPOINT_CHAIN_STATUS,
				mDinnerStatus.getText().toString());
		startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYPOINTTRANSAC);
	}

	public class ListRestaurantPoints extends
			AsyncTask<Void, Void, MyPointRestaurantEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyPointRestaurantEntity doInBackground(Void... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlRestaurantPoint(
								UserObject
										.getInstance(MyPointTransactionActivity.this).mAccess_token,
								chainObject.mId));

				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyPointRestaurantEntity response = gson.fromJson(json,
						MyPointRestaurantEntity.class);

				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyPointRestaurantEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyPointTransactionActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(getString(R.string.mess_error_server));
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
					Toast.makeText(MyPointTransactionActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListTransactions == null) {
						mListTransactions = new ArrayList<MyPointRestaurantEntity>();
					}
					if (mListTransactionInfos == null) {
						mListTransactionInfos = new ArrayList<MyPointRestaurantListPointEntity>();
					}

					mImageLoader.DisplayImage(
							ServerURL.URL + chainObject.getLogo(), mLogo);

					mChainName.setText(chainObject.getName());

					mAddress.setText(chainObject.getAddress());
					mCityState.setText(chainObject.getCity() + ", "
							+ chainObject.getState() + " "
							+ chainObject.getZip());
					mListTransactions.add(result);
					mListTransactionInfos.clear();
					mListTransactionInfos.addAll(result.mListPoints);

					float totalPoint = 0;

					for (int i = 0; i < mListTransactionInfos.size(); i++) {
						if (mListTransactionInfos.get(i).mStatus == 1) {
							totalPoint += mListTransactionInfos.get(i).mPoint;
						} else if (mListTransactionInfos.get(i).mStatus != 1) {
							totalPoint -= mListTransactionInfos.get(i).mPoint;
						}
					}

					mLlShape.setVisibility(View.VISIBLE);
					if (mBdStatus == null && totalPoint == 0) {
						mDinnerStatus.setText("Status:");
						mDinnerPoint.setText("0 Point");
					} else {
						mDinnerStatus.setText("Status: " + mBdStatus);
						if (totalPoint > 1) {
							mDinnerPoint
									.setText(Utils
											.formatPointNumbers(totalPoint)
											+ " Points");
						} else {
							mDinnerPoint.setText(Utils
									.formatPointNumbers(totalPoint) + " Point");
						}
					}

					mAdapter = new MyPointTransactionAdapter(
							MyPointTransactionActivity.this,
							R.layout.mypoint_transaction_item,
							mListTransactionInfos);
					mListView.setAdapter(mAdapter);
					onClick_sortDateRecent();
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
		if (!mIsFirstTimeToLoad) {
			new ListRestaurantPoints().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTTRANSAC);
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
		startActivityForResult(new Intent(MyPointTransactionActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTTRANSAC);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyPointTransactionActivity.this, null, "", 0, true);
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
					MyPointTransactionActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyPointTransactionActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyPointTransactionActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyPointTransactionActivity.this)) {
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
										MyPointTransactionActivity.this)
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
							.isNetworkConnected(MyPointTransactionActivity.this)) {
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
					Toast.makeText(MyPointTransactionActivity.this,
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

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyNotificationRestaurantAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener.OnSwipeCallback;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.DeleteNotificationByRestaurantEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationChainListEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationRestaurantEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationRestaurantListEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * @Description This class use to load all notification of restaurant
 */
public class MyNotificationRestaurantActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListview;
	private ImageView mIvLbSortType;
	private ImageView mIvLbSortDate;
	private TextView mTvSortDate;
	private MyNotificationRestaurantAdapter mRestaurantAdapter;
	List<MyNotificationRestaurantListEntity> mListRestaurantObject;
	private ImageView mLogo;
	private TextView mResName;
	private TextView mStreet;
	private TextView mState;
	private TextView mTotalMessage;
	private RelativeLayout mSortType;
	private ImageLoader mImageLoader;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mIsFirstTimeToLoad = false;
	private TextView mTvLableTotalMsg;
	private int[] mReverseSortedPositions;
	private String mGlobalLogo;
	private List<MyNotificationRestaurantListEntity> mListToKeepAllMessages;

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	private MyNotificationChainListEntity chainObject;
	private String mChainGlobalName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mynotification_restaurant);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListview = (ListView) findViewById(R.id.mynotification_restaurant_list_lv);
		mIvLbSortType = (ImageView) findViewById(R.id.mynotification_restaurant_iv_status_sortType);
		mIvLbSortDate = (ImageView) findViewById(R.id.mynotification_restaurant_iv_status_sortDate);
		mTvSortDate = (TextView) findViewById(R.id.mynotification_restaurant_sortDate);
		mLogo = (ImageView) findViewById(R.id.mynotification_restaurant_list_ic_logo);
		mResName = (TextView) findViewById(R.id.mynotification_restaurant_store_name);
		mStreet = (TextView) findViewById(R.id.mynotification_restaurant_store_street);
		mState = (TextView) findViewById(R.id.mynotification_restaurant_store_state);
		mTotalMessage = (TextView) findViewById(R.id.mynotification_restaurant_total_new_msg);
		mSortType = (RelativeLayout) findViewById(R.id.mynotification_message_sort_type);
		mTvLableTotalMsg = (TextView) findViewById(R.id.mynotification_restaurant_lb_new_msg);
		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		mIsFirstTimeToLoad = true;

		Bundle bGetGlobalỌbject = getIntent().getExtras();
		if (bGetGlobalỌbject != null) {
			chainObject = getIntent().getExtras().getParcelable(
					StringExtraUtils.KEY_MYNOTIFICATION_CHAIN);
			if (chainObject != null) {
				try {
					mImageLoader.DisplayImage(
							ServerURL.URL + chainObject.getLogo(), mLogo);
					mResName.setText(chainObject.getName());
					mStreet.setText(chainObject.getAddress());
					mState.setText(chainObject.getCity() + ", "
							+ chainObject.getState() + " "
							+ chainObject.getZip());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new GetListNotificationRestaurantTask().execute();
			} else {
				mChainGlobalName = getIntent().getExtras().getString(
						StringExtraUtils.KEY_MYNOTIFICATION_CHAIN_ALL_MSG);
				mGlobalLogo = getIntent().getExtras().getString(
						StringExtraUtils.KEY_MYNOTIFICATION_CHAIN_ALL_MSG_LOGO);
				try {
					mImageLoader.DisplayImage(ServerURL.URL + mGlobalLogo,
							mLogo);
					mResName.setText(mChainGlobalName);
					mStreet.setVisibility(View.GONE);
					mState.setVisibility(View.GONE);

				} catch (Exception e) {
					e.printStackTrace();
				}

				new GetListNotificationRestaurantTask().execute();
			}

		}

		mTvSortDate.setOnClickListener(new OnClickListener() {
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

		mSortType.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortByAnphabelAZ();
				} else if (count % 2 == 0) {
					onClick_sortByAnphabelZA();
				}
			}
		});

		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				Intent intent = new Intent(
						MyNotificationRestaurantActivity.this,
						MyNotificationMessageActivity.class);

				if (!Utils
						.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
					showToastMessage(getString(R.string.mess_error_network));
					return;
				} else {
					intent.putExtra(
							StringExtraUtils.KEY_MYNOTIFICATION_RESTAURANT,
							mListRestaurantObject.get(position));
					if (chainObject != null) {
						intent.putExtra(
								StringExtraUtils.KEY_MYNOTIFICATION_RESTAURANT_LOGO,
								chainObject.getLogo());
					} else {
						intent.putExtra(
								StringExtraUtils.KEY_MYNOTIFICATION_RESTAURANT_LOGO,
								mGlobalLogo);
					}
					if (intent != null) {
						startActivityForResult(intent,
								CodeRequest.CODE_REQUEST_MYNOTIRESTAURANT);
						mListToKeepAllMessages.get(position).setStatus(1);
					}
				}
			}
		});

		SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(
				mListview, new OnSwipeCallback() {

					@Override
					public void onSwipeRight(ListView listView,
							int[] reverseSortedPositions) {
					}

					@Override
					public void onSwipeLeft(ListView listView,
							int[] reverseSortedPositions) {
						mReverseSortedPositions = reverseSortedPositions;
						try {
							DialogConfirm d = new DialogConfirm(
									MyNotificationRestaurantActivity.this, -1,
									"Delete Message",
									getString(R.string.delete_message), true,
									new ProcessDialogConfirm() {

										@Override
										public void click_Ok() {
											try {
												for (int position : mReverseSortedPositions) {
													int mId = mRestaurantAdapter
															.getItem(position).mId;
													mRestaurantAdapter
															.remove(mRestaurantAdapter
																	.getItem(position));

													// apply service delete !!!
													new DeleteNotificationByRestaurantAsyncTask()
															.execute(mId);
												}
												mRestaurantAdapter
														.notifyDataSetChanged();
											} catch (Exception e) {

											}
										}

										@Override
										public void click_Cancel() {

										}
									});

							d.show();
						} catch (Exception e) {
						}

					}
				}, false, false);
		mListview.setOnTouchListener(touchListener);
		mListview.setOnScrollListener(touchListener.makeScrollListener());
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

	public void onClick_sortByAnphabelAZ() {
		try {
			mIvLbSortType.setVisibility(View.VISIBLE);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortType.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListToKeepAllMessages,
					new Comparator<MyNotificationRestaurantListEntity>() {
						@Override
						public int compare(
								MyNotificationRestaurantListEntity lhs,
								MyNotificationRestaurantListEntity rhs) {

							if (lhs.mMsgSubject
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.mMsgSubject.toUpperCase(
													Locale.US).trim()) > 0)
								return 1;
							else if (lhs.mMsgSubject.toUpperCase(Locale.US)
									.trim()
									.compareTo(rhs.mMsgSubject.toUpperCase(

									Locale.US).trim()) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRestaurantAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAnphabelZA() {
		try {
			mIvLbSortType.setVisibility(View.VISIBLE);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortType.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListToKeepAllMessages,
					new Comparator<MyNotificationRestaurantListEntity>() {
						@Override
						public int compare(
								MyNotificationRestaurantListEntity lhs,
								MyNotificationRestaurantListEntity rhs) {

							if (lhs.mMsgSubject
									.toUpperCase(Locale.US)
									.trim()
									.compareTo(
											rhs.mMsgSubject.toUpperCase(
													Locale.US).trim()) > 0)
								return -1;
							else if (lhs.mMsgSubject.toUpperCase(Locale.US)
									.trim()
									.compareTo(rhs.mMsgSubject.toUpperCase(

									Locale.US).trim()) < 0)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRestaurantAdapter.notifyDataSetChanged();
	}

	public void onClick_sortDateRecent() {
		try {
			mIvLbSortType.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.VISIBLE);
			mIvLbSortDate.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListToKeepAllMessages,
					new Comparator<MyNotificationRestaurantListEntity>() {

						@Override
						public int compare(
								MyNotificationRestaurantListEntity lhs,
								MyNotificationRestaurantListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getmUpdateDate());
								d2 = form.parse(rhs.getmUpdateDate());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (d1.compareTo(d2) > 0) {
								return -1;
							} else if (d1.compareTo(d2) < 0) {
								return 1;
							} else {
								if (lhs.getmUpdateTimes()
										.trim()
										.compareTo(rhs.getmUpdateTimes().trim()) > 0) {
									return -1;
								} else if (lhs
										.getmUpdateTimes()
										.trim()
										.compareTo(rhs.getmUpdateTimes().trim()) < 0) {
									return 1;
								} else
									return 0;
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRestaurantAdapter.notifyDataSetChanged();
	}

	public void onClick_sortDateFar() {
		try {
			mIvLbSortType.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.VISIBLE);
			mIvLbSortDate.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListToKeepAllMessages,
					new Comparator<MyNotificationRestaurantListEntity>() {

						@Override
						public int compare(
								MyNotificationRestaurantListEntity lhs,
								MyNotificationRestaurantListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getmUpdateDate());
								d2 = form.parse(rhs.getmUpdateDate());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (d1.compareTo(d2) > 0) {
								return 1;
							} else if (d1.compareTo(d2) < 0) {
								return -1;
							} else {
								if (lhs.getmUpdateTimes()
										.trim()
										.compareTo(rhs.getmUpdateTimes().trim()) > 0) {
									return 1;
								} else if (lhs
										.getmUpdateTimes()
										.trim()
										.compareTo(rhs.getmUpdateTimes().trim()) < 0) {
									return -1;
								} else
									return 0;
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRestaurantAdapter.notifyDataSetChanged();
	}

	public void sortDateRecent() {
		try {

			mIvLbSortType.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.VISIBLE);
			mIvLbSortDate.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListToKeepAllMessages,
					new Comparator<MyNotificationRestaurantListEntity>() {

						@Override
						public int compare(
								MyNotificationRestaurantListEntity lhs,
								MyNotificationRestaurantListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yyyy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getmUpdateDate());
								d2 = form.parse(rhs.getmUpdateDate());
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
		mRestaurantAdapter.notifyDataSetChanged();
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

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			if (MyNotificationChainActivity.mCheckStatus) {
				if (MyNotificationMessageActivity.mIntentStatus) {
					for (int i = 0; i < mListToKeepAllMessages.size(); i++) {
						if (mListToKeepAllMessages.get(i).mId == MyNotificationMessageActivity.mIntentMessageId) {
							mListToKeepAllMessages.remove(i);
							break;
						}
					}
				}

				mRestaurantAdapter = new MyNotificationRestaurantAdapter(
						MyNotificationRestaurantActivity.this,
						R.id.mynotification_restaurant_list_lv,
						mListToKeepAllMessages);
				mListview.setAdapter(mRestaurantAdapter);

				int mTotalMsg = 0;

				for (int i = 0; i < mListToKeepAllMessages.size(); i++) {
					if (mListToKeepAllMessages.get(i).mStatus == 0) {
						mTotalMsg++;
					}
				}

				mTotalMessage.setText(mTotalMsg + "");
				if (mTotalMsg <= 1) {
					mTvLableTotalMsg.setText("Notification");
					if (mTotalMsg == 0) {
						mTotalMessage.setTextColor(getResources().getColor(
								R.color.White));
					} else {
						mTotalMessage.setTextColor(getResources().getColor(
								R.color.mynotification_unread));
					}
				} else {
					mTvLableTotalMsg.setText("Notifications");
					mTotalMessage.setTextColor(getResources().getColor(
							R.color.mynotification_unread));
				}
				sortDateRecent();
			} else {
				new GetListNotificationRestaurantTask().execute();
				sortDateRecent();
			}
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyNotificationRestaurantActivity.this, null, "", 0, true);
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
					MyNotificationRestaurantActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyNotificationRestaurantActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
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

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYNOTIRESTAURANT);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(
				new Intent(MyNotificationRestaurantActivity.this,
						MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYNOTIRESTAURANT);
	}

	public void onClick_back(View v) {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CodeRequest.CODE_REQUEST_MYNOTIRESTAURANT) {
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

	public class GetListNotificationRestaurantTask extends
			AsyncTask<String, Void, MyNotificationRestaurantEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyNotificationRestaurantEntity doInBackground(
				String... params) {
			InputStream source = null;
			try {

				if (chainObject != null) {
					source = Server
							.requestGet(ServerURL.getUrlMyNotificationRestaurant(
									UserObject
											.getInstance(MyNotificationRestaurantActivity.this).mAccess_token,
									chainObject.mLocationId));
				} else {
					source = Server
							.requestGet(ServerURL.getUrlUnreadMsg(
									UserObject
											.getInstance(MyNotificationRestaurantActivity.this).mAccess_token,
									mChainGlobalName));
				}

				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyNotificationRestaurantEntity response = gson.fromJson(json,
						MyNotificationRestaurantEntity.class);
				return response;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyNotificationRestaurantEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
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
					Toast.makeText(MyNotificationRestaurantActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListRestaurantObject == null) {
						mListRestaurantObject = new ArrayList<MyNotificationRestaurantListEntity>();
						mListToKeepAllMessages = new ArrayList<MyNotificationRestaurantListEntity>();
					}
					mListRestaurantObject.clear();
					mListToKeepAllMessages.clear();
					mListRestaurantObject.addAll(result.mMessages);
					mListToKeepAllMessages.addAll(result.mMessages);

					mRestaurantAdapter = new MyNotificationRestaurantAdapter(
							MyNotificationRestaurantActivity.this,
							R.id.mynotification_restaurant_list_lv,
							mListRestaurantObject);
					mListview.setAdapter(mRestaurantAdapter);

					int mTotalMsg = 0;

					for (int i = 0; i < mListRestaurantObject.size(); i++) {
						if (mListRestaurantObject.get(i).mStatus == 0) {
							mTotalMsg++;
						}
					}

					mTotalMessage.setText(mTotalMsg + "");
					if (mTotalMsg <= 1) {
						mTvLableTotalMsg.setText("Notification");
						if (mTotalMsg == 0) {
							mTotalMessage.setTextColor(getResources().getColor(
									R.color.White));
						} else {
							mTotalMessage.setTextColor(getResources().getColor(
									R.color.mynotification_unread));
						}
					} else {
						mTvLableTotalMsg.setText("Notifications");
						mTotalMessage.setTextColor(getResources().getColor(
								R.color.mynotification_unread));
					}

					sortDateRecent();
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class DeleteNotificationByRestaurantAsyncTask extends
			AsyncTask<Integer, Void, DeleteNotificationByRestaurantEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected DeleteNotificationByRestaurantEntity doInBackground(
				Integer... params) {
			try {
				InputStream source = null;
				source = Server.requestPost(ServerURL
						.getUrlDeleteNotificationByRestaurant(), ServerURL
						.getEntityDeleteNotificationByRestaurant(
								UserObject.getInstance(
										MyNotificationRestaurantActivity.this)
										.getAccess_token(), params[0]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				DeleteNotificationByRestaurantEntity response = gson.fromJson(
						json, DeleteNotificationByRestaurantEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(DeleteNotificationByRestaurantEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
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
					Toast.makeText(MyNotificationRestaurantActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				}
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					showToastMessage("Message has been deleted successfully !");
					new GetListNotificationRestaurantTask().execute();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

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
										MyNotificationRestaurantActivity.this)
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
							.isNetworkConnected(MyNotificationRestaurantActivity.this)) {
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
					Toast.makeText(MyNotificationRestaurantActivity.this,
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

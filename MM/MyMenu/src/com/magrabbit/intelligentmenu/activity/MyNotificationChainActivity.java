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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyNotificationChainAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyNoficationGlobalListEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationChainEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationChainListEntity;
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
 * @Description This class use to load all notification of chain
 */
public class MyNotificationChainActivity extends BaseActivity implements
		OptionMenuClickListener, OnClickListener {

	private ListView mListview;
	private ImageView mIvLbSortAlphabet;
	private ImageView mIvLbSortAmount;
	private TextView mTvAmount;
	private MyNotificationChainAdapter mChainAdapter;
	List<MyNotificationChainListEntity> mListChainObject;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mIsFirstTimeToLoad = false;

	// bundle
	private MyNoficationGlobalListEntity globalObject;
	private ImageView mLogo;
	private TextView mTotalMessage;
	private ImageLoader mImageLoader;
	private RelativeLayout mSortAlpha;
	private TextView mTvLableTotalMsg;
	private LinearLayout mLlShowAllUnreadMessage;
	public static boolean mCheckStatus = false;

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
		setContentView(R.layout.activity_mynotification_chain);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListview = (ListView) findViewById(R.id.mynotification_chain_list_lv);
		mIvLbSortAlphabet = (ImageView) findViewById(R.id.mynotification_chain_iv_status_sortAphabel);
		mIvLbSortAmount = (ImageView) findViewById(R.id.mynotification_chain_iv_status_sortAmount);
		mTvAmount = (TextView) findViewById(R.id.mynotification_chain_sortAmount);
		mLogo = (ImageView) findViewById(R.id.mynotification_chain_list_ic);
		mTotalMessage = (TextView) findViewById(R.id.mynotification_chain_total_new_msg);
		mSortAlpha = (RelativeLayout) findViewById(R.id.mynotification_rl_sort_alphabet);
		mTvLableTotalMsg = (TextView) findViewById(R.id.mynotification_chain_lb_new_msg);
		mLlShowAllUnreadMessage = (LinearLayout) findViewById(R.id.mynotification_chain_show_all_message);
		mImageLoader = new ImageLoader(MyNotificationChainActivity.this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		mIsFirstTimeToLoad = true;

		Bundle bGetGlobalỌbject = getIntent().getExtras();
		if (bGetGlobalỌbject != null) {
			globalObject = getIntent().getExtras().getParcelable(
					StringExtraUtils.KEY_MYNOTIFICATION_GLOBAL);

		}

		mImageLoader
				.DisplayImage(ServerURL.URL + globalObject.getLogo(), mLogo);

		// TODO

		new GetListNotificationChainTask().execute();

		mSortAlpha.setOnClickListener(new OnClickListener() {
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

		mTvAmount.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortByAmountHighToLow();
				} else if (count % 2 == 0) {
					onClick_sortByAmountLowToHigh();
				}
			}
		});

		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				Intent intent = new Intent(MyNotificationChainActivity.this,
						MyNotificationRestaurantActivity.class);

				if (!Utils.isNetworkConnected(MyNotificationChainActivity.this)) {
					showToastMessage(getString(R.string.mess_error_network));
					return;
				} else {
					intent.putExtra(StringExtraUtils.KEY_MYNOTIFICATION_CHAIN,
							mListChainObject.get(position));
					if (intent != null) {
						startActivityForResult(intent,
								CodeRequest.CODE_REQUEST_MYNOTICHAIN);
						mCheckStatus = false;
					}
				}
			}

		});

		mLlShowAllUnreadMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Integer.parseInt(mTotalMessage.getText().toString().trim()) != 0) {
					onClick_Show_All_Msg();
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYNOTICHAIN) {
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

	public void onClick_sortByAnphabelAZ() {
		try {
			mIvLbSortAlphabet.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortAlphabet.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListChainObject,
					new Comparator<MyNotificationChainListEntity>() {
						@Override
						public int compare(MyNotificationChainListEntity lhs,
								MyNotificationChainListEntity rhs) {
							if (lhs.getName()
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.getName().trim()
													.toUpperCase(Locale.US)) > 0)
								return 1;
							else if (lhs
									.getName()
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.getName().trim()
													.toUpperCase(Locale.US)) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mChainAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAnphabelZA() {
		try {
			mIvLbSortAlphabet.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortAlphabet.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListChainObject,
					new Comparator<MyNotificationChainListEntity>() {
						@Override
						public int compare(MyNotificationChainListEntity lhs,
								MyNotificationChainListEntity rhs) {
							if (lhs.getName()
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.getName().trim()
													.toUpperCase(Locale.US)) > 0)
								return -1;
							else if (lhs
									.getName()
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.getName().trim()
													.toUpperCase(Locale.US)) < 0)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mChainAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAmountHighToLow() {
		try {
			mIvLbSortAlphabet.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mListChainObject,
					new Comparator<MyNotificationChainListEntity>() {
						@Override
						public int compare(MyNotificationChainListEntity lhs,
								MyNotificationChainListEntity rhs) {
							if (lhs.mUnread > rhs.mUnread)
								return -1;
							else if (lhs.mUnread < rhs.mUnread)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mChainAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAmountLowToHigh() {
		try {
			mIvLbSortAlphabet.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListChainObject,
					new Comparator<MyNotificationChainListEntity>() {
						@Override
						public int compare(MyNotificationChainListEntity lhs,
								MyNotificationChainListEntity rhs) {
							if (lhs.mUnread > rhs.mUnread)
								return 1;
							else if (lhs.mUnread < rhs.mUnread)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mChainAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new GetListNotificationChainTask().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyNotificationChainActivity.this, null, "", 0, true);
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

	public void onClick_back(View v) {
		super.onBackPressed();
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
					MyNotificationChainActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyNotificationChainActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyNotificationChainActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyNotificationChainActivity.this)) {
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
				CodeRequest.CODE_REQUEST_MYNOTICHAIN);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyNotificationChainActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYNOTICHAIN);
	}

	@Override
	public void onClick(View v) {

	}

	public class GetListNotificationChainTask extends
			AsyncTask<String, Void, MyNotificationChainEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyNotificationChainEntity doInBackground(String... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlMyNotificationChain(
								UserObject
										.getInstance(MyNotificationChainActivity.this).mAccess_token,
								globalObject.getChainName()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyNotificationChainEntity response = gson.fromJson(json,
						MyNotificationChainEntity.class);
				return response;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyNotificationChainEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationChainActivity.this)) {
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
					Toast.makeText(MyNotificationChainActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListChainObject == null) {
						mListChainObject = new ArrayList<MyNotificationChainListEntity>();
					}
					mListChainObject.clear();
					mListChainObject.addAll(result.mRestaurants);
				}
				mChainAdapter = new MyNotificationChainAdapter(
						MyNotificationChainActivity.this,
						R.id.mynotification_chain_list_lv, mListChainObject);
				mListview.setAdapter(mChainAdapter);

				int mTotalMsg = 0;

				for (int i = 0; i < mListChainObject.size(); i++) {
					mTotalMsg += mListChainObject.get(i).mUnread;
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

				onClick_sortByAmountHighToLow();
				dismissProgress();

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
										MyNotificationChainActivity.this)
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
							.isNetworkConnected(MyNotificationChainActivity.this)) {
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
					Toast.makeText(MyNotificationChainActivity.this,
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

	public void onClick_Show_All_Msg() {
		Intent intent = new Intent(MyNotificationChainActivity.this,
				MyNotificationRestaurantActivity.class);

		if (!Utils.isNetworkConnected(MyNotificationChainActivity.this)) {
			showToastMessage(getString(R.string.mess_error_network));
			return;
		} else {
			intent.putExtra(StringExtraUtils.KEY_MYNOTIFICATION_CHAIN_ALL_MSG,
					globalObject.getChainName());
			intent.putExtra(
					StringExtraUtils.KEY_MYNOTIFICATION_CHAIN_ALL_MSG_LOGO,
					globalObject.getLogo());
			if (intent != null) {
				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MYNOTICHAIN);
				mCheckStatus = true;
			}
		}
	}

}

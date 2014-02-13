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
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyNotificationGlobalAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyNoficationGlobalListEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Vule
 * @Description It will store all notification of user, show new message when
 *              they have
 * 
 */
public class MyNotificationGlobalActivity extends BaseActivity implements
		OnItemClickListener, OptionMenuClickListener {

	private List<MyNoficationGlobalListEntity> mLstMyNotificationGlobal;
	private ListView mListview;
	private ImageView mIvLbSortAlphabet;
	private ImageView mIvLbSortAmount;
	private TextView mTvAmount;
	private TextView mSortAnphabet;
	private MyNotificationGlobalAdapter mMyNotificationAdapter;
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
		setContentView(R.layout.activity_mynotification_global);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListview = (ListView) findViewById(R.id.mynotification_list_lv);
		mIvLbSortAlphabet = (ImageView) findViewById(R.id.mynotification_iv_status_sortAphabel);
		mIvLbSortAmount = (ImageView) findViewById(R.id.mynotification_iv_status_sortAmount);
		mTvAmount = (TextView) findViewById(R.id.mynotification_sortAmount);
		mSortAnphabet = (TextView) findViewById(R.id.mynotification_sortAphabel);
		mIsFirstTimeToLoad = true;
		mSortAnphabet.setOnClickListener(new OnClickListener() {
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
					onClick_sortByAmountHightToLow();
				} else if (count % 2 == 0) {
					onClick_sortByAmountLowToHight();
				}

			}
		});

		new GetListNotificationGlbalTask().execute();
		mListview.setOnItemClickListener(this);
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

	@Override
	protected void onDestroy() {
		if (mLstMyNotificationGlobal != null) {
			mLstMyNotificationGlobal.clear();
			mLstMyNotificationGlobal = null;
		}

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int postion,
			long id) {
		Intent intent = new Intent(MyNotificationGlobalActivity.this,
				MyNotificationChainActivity.class);

		if (!Utils.isNetworkConnected(this)) {
			showToastMessage(this.getResources().getString(
					R.string.mess_error_network));
			return;
		} else {
			intent.putExtra(StringExtraUtils.KEY_MYNOTIFICATION_GLOBAL,
					mLstMyNotificationGlobal.get(postion));
			if (intent != null) {
				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MYNOTIGLOBAL);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYNOTIGLOBAL) {
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

	public void onClick_sortByAnphabelAZ() {
		try {
			mIvLbSortAlphabet.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortAlphabet.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mLstMyNotificationGlobal,
					new Comparator<MyNoficationGlobalListEntity>() {
						@Override
						public int compare(MyNoficationGlobalListEntity lhs,
								MyNoficationGlobalListEntity rhs) {
							if (lhs.getChainName()
									.trim()
									.toUpperCase(Locale.US)
									.compareTo(
											rhs.getChainName()
													.toUpperCase(Locale.US)
													.trim()) > 0)
								return 1;
							else if (lhs
									.getChainName()
									.toUpperCase(Locale.US)
									.trim()
									.compareTo(
											rhs.getChainName()
													.toUpperCase(Locale.US)
													.trim()) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAnphabelZA() {
		try {
			mIvLbSortAlphabet.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortAlphabet.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mLstMyNotificationGlobal,
					new Comparator<MyNoficationGlobalListEntity>() {
						@Override
						public int compare(MyNoficationGlobalListEntity lhs,
								MyNoficationGlobalListEntity rhs) {
							if (lhs.getChainName()
									.toUpperCase(Locale.US)
									.trim()
									.compareTo(
											rhs.getChainName()
													.toUpperCase(Locale.US)
													.trim()) > 0)
								return -1;
							else if (lhs
									.getChainName()
									.toUpperCase(Locale.US)
									.trim()
									.compareTo(
											rhs.getChainName()
													.toUpperCase(Locale.US)
													.trim()) < 0)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAmountHightToLow() {
		try {
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortAlphabet.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mLstMyNotificationGlobal,
					new Comparator<MyNoficationGlobalListEntity>() {
						@Override
						public int compare(MyNoficationGlobalListEntity lhs,
								MyNoficationGlobalListEntity rhs) {
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
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAmountLowToHight() {
		try {
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortAlphabet.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mLstMyNotificationGlobal,
					new Comparator<MyNoficationGlobalListEntity>() {
						@Override
						public int compare(MyNoficationGlobalListEntity lhs,
								MyNoficationGlobalListEntity rhs) {
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
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new GetListNotificationGlbalTask().execute();
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
				MyNotificationGlobalActivity.this, null, "", 0, true);
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
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYNOTIGLOBAL);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyNotificationGlobalActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYNOTIGLOBAL);
	}

	public void onClick_back(View v) {
		super.onBackPressed();
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(
					MyNotificationGlobalActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyNotificationGlobalActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyNotificationGlobalActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyNotificationGlobalActivity.this)) {
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

	public void sortDateRecent() {
		try {
			Collections.sort(mLstMyNotificationGlobal,
					new Comparator<MyNoficationGlobalListEntity>() {

						@Override
						public int compare(MyNoficationGlobalListEntity lhs,
								MyNoficationGlobalListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"MM.dd.yyyy");

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
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	public class GetListNotificationGlbalTask extends
			AsyncTask<String, Void, MyNotificationGlobalEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyNotificationGlobalEntity doInBackground(String... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlMyNotificationGlobal(UserObject
								.getInstance(MyNotificationGlobalActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyNotificationGlobalEntity response = gson.fromJson(json,
						MyNotificationGlobalEntity.class);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyNotificationGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationGlobalActivity.this)) {
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
					Toast.makeText(MyNotificationGlobalActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mLstMyNotificationGlobal == null) {
						mLstMyNotificationGlobal = new ArrayList<MyNoficationGlobalListEntity>();
					}
					mLstMyNotificationGlobal.clear();
					mLstMyNotificationGlobal.addAll(result.mRestaurant);
					mMyNotificationAdapter = new MyNotificationGlobalAdapter(
							MyNotificationGlobalActivity.this,
							R.id.mynotification_list_lv,
							mLstMyNotificationGlobal);
					mListview.setAdapter(mMyNotificationAdapter);
					onClick_sortByAmountHightToLow();
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
										MyNotificationGlobalActivity.this)
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
							.isNetworkConnected(MyNotificationGlobalActivity.this)) {
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
					Toast.makeText(MyNotificationGlobalActivity.this,
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

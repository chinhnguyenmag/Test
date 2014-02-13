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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyPointGlobalAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyPointGlobalEntity;
import com.magrabbit.intelligentmenu.model.MyPointListLocationEntity;
import com.magrabbit.intelligentmenu.model.UserGlobalEntity;
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
 * @Desctiption: display global point for all restaurants to interact and share
 *               points
 */
public class MyPointGlobalActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListView;
	private MyPointGlobalAdapter mAdapter;
	private UserGlobalEntity mUserGlobal;
	private List<MyPointListLocationEntity> mListRestaurantGlobals;

	private ImageButton mImbtSortPoint;
	private ImageButton mImbtSortAZ;
	private TextView mPoint;
	private TextView mStatus;
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
		setContentView(R.layout.activity_mypoint_global);
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mImbtSortPoint = (ImageButton) findViewById(R.id.activity_mypoint_global_imbt_sortPoint);
		mImbtSortAZ = (ImageButton) findViewById(R.id.activity_mypoint_global_imbt_sortAZ);
		mListView = (ListView) findViewById(R.id.activity_mypoint_global_listview);
		mPoint = (TextView) findViewById(R.id.activity_mypoint_global_point);
		mStatus = (TextView) findViewById(R.id.activity_mypoint_global_status);
		mLlSortAz = (LinearLayout) findViewById(R.id.activity_mypoint_global_layout_az);
		mLlSortAmount = (LinearLayout) findViewById(R.id.activity_mypoint_global_layout_amount);
		mLlLogoBackPress = (LinearLayout) findViewById(R.id.activity_mypoint_global_layout_icon);
		mLlShape = (LinearLayout) findViewById(R.id.activity_mypoint_global_shape);
		mIsFirstTimeToLoad = true;
		new ListGlobalPoints().execute();

		mLlSortAz.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortAnphabetAz();
				} else if (count % 2 == 0) {
					onClick_sortAnphabetZa();
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
					onClick_sortAmountHighToLow();
				} else if (count % 2 == 0) {
					onClick_sortAmountLowToHigh();
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapt, View view, int pos,
					long id) {

				try {
					if (!Utils.isNetworkConnected(MyPointGlobalActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
						return;
					}

					Intent intent = new Intent(MyPointGlobalActivity.this,
							MyPointChainActivity.class);
					intent.putExtra(StringExtraUtils.KEY_MYPOINT_GLOBAL,
							mListRestaurantGlobals.get(pos));
					if (intent != null) {
						startActivityForResult(intent,
								CodeRequest.CODE_REQUEST_MYPOINTGLOBAL);
					}

				} catch (Exception e) {
					e.printStackTrace();
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYPOINTGLOBAL) {
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

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new ListGlobalPoints().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	public void onClick_sortAnphabetAz() {
		try {

			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortPoint.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyPointListLocationEntity>() {
						@Override
						public int compare(MyPointListLocationEntity lhs,
								MyPointListLocationEntity rhs) {
							if (lhs.getChainName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getChainName().toString()
													.trim().toUpperCase(Locale.ENGLISH)) > 0)
								return 1;
							else if (lhs
									.getChainName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getChainName()
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

	public void onClick_sortAnphabetZa() {
		try {

			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortPoint.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyPointListLocationEntity>() {
						@Override
						public int compare(MyPointListLocationEntity lhs,
								MyPointListLocationEntity rhs) {
							if (lhs.getChainName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getChainName()
													.toString()
													.trim()
													.toUpperCase(Locale.ENGLISH)) > 0)
								return -1;
							else if (lhs
									.getChainName()
									.toString()
									.trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(
											rhs.getChainName()
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

	public void onClick_sortAmountHighToLow() {
		try {
			mImbtSortPoint.setVisibility(View.VISIBLE);
			mImbtSortPoint.setBackgroundResource(R.drawable.ic_arrow_up);
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyPointListLocationEntity>() {
						@Override
						public int compare(MyPointListLocationEntity lhs,
								MyPointListLocationEntity rhs) {
							if (lhs.mTotalPoint > rhs.mTotalPoint)
								return -1;
							else if (lhs.mTotalPoint < rhs.mTotalPoint)
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
			mImbtSortPoint.setVisibility(View.VISIBLE);
			mImbtSortPoint.setBackgroundResource(R.drawable.ic_arrow_down);
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyPointListLocationEntity>() {
						@Override
						public int compare(MyPointListLocationEntity lhs,
								MyPointListLocationEntity rhs) {
							if (lhs.mTotalPoint > rhs.mTotalPoint)
								return 1;
							else if (lhs.mTotalPoint < rhs.mTotalPoint)
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

	/**
	 * @Description: Open Share option
	 * @param v
	 */
	public void onClick_Share(View v) {
		// Add code to Share
		return;
	}

	public class ListGlobalPoints extends
			AsyncTask<String, Void, MyPointGlobalEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyPointGlobalEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGlobalPoint(UserObject
								.getInstance(MyPointGlobalActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyPointGlobalEntity response = gson.fromJson(json,
						MyPointGlobalEntity.class);

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyPointGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MyPointGlobalActivity.this)) {
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
					Toast.makeText(MyPointGlobalActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListRestaurantGlobals == null) {
						mListRestaurantGlobals = new ArrayList<MyPointListLocationEntity>();
					}
					mUserGlobal = new UserGlobalEntity();
					mUserGlobal = result.mUserGlobal;
					mListRestaurantGlobals.clear();
					mListRestaurantGlobals.addAll(result.mListLocation);

					float totalPoint = 0;

					for (int i = 0; i < mListRestaurantGlobals.size(); i++) {
						totalPoint += mListRestaurantGlobals.get(i).mTotalPoint;
					}

					mLlShape.setVisibility(View.VISIBLE);
					if (result.mUserGlobal.getStatus().equals("")
							&& result.mUserGlobal.getPointNumber() == 0) {
						mStatus.setText("Status:");
						mPoint.setText("0 Point");
					} else {
						mStatus.setText("Status: " + mUserGlobal.getStatus());
						if (totalPoint != 0) {
							if (totalPoint > 1) {
								mPoint.setText(Utils
										.formatPointNumbers(totalPoint)
										+ " Points");
							} else {
								mPoint.setText(Utils
										.formatPointNumbers(totalPoint)
										+ " Point");
							}
						} else {
							if (result.mUserGlobal.getPointNumber() > 1) {
								mPoint.setText(Utils
										.formatPointNumbers(result.mUserGlobal
												.getPointNumber())
										+ " Points");
							} else {
								mPoint.setText(Utils
										.formatPointNumbers(result.mUserGlobal
												.getPointNumber())
										+ " Point");
							}
						}
					}

					mAdapter = new MyPointGlobalAdapter(
							MyPointGlobalActivity.this,
							R.layout.mypoint_global_item,
							mListRestaurantGlobals);
					mListView.setAdapter(mAdapter);
					onClick_sortAmountHighToLow();
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTGLOBAL);
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
		startActivityForResult(new Intent(MyPointGlobalActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTGLOBAL);

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
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyPointGlobalActivity.this,
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
			mDialogMyFeedback = new DialogMyFeedback(
					MyPointGlobalActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyPointGlobalActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyPointGlobalActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyPointGlobalActivity.this)) {
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
										MyPointGlobalActivity.this)
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
					if (!Utils.isNetworkConnected(MyPointGlobalActivity.this)) {
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
					Toast.makeText(MyPointGlobalActivity.this,
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

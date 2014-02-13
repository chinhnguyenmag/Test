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
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyOrderGlobalAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyOrderGlobalEntity;
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
 * @Desctiption: display global orders for all restaurants to interact and share
 *               order details
 */
public class MyOrderGlobalActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListView;
	private MyOrderGlobalAdapter mAdapter;
	private List<MyOrderGlobalObject> mListRestaurantGlobals;

	private ImageButton mImbtSortNumberOrder;
	private ImageButton mImbtSortAZ;
	private LinearLayout mLayoutSortAZ;
	private LinearLayout mLayoutSortAmount;
	private LinearLayout mLayoutLogoBackPress;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_global);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mImbtSortNumberOrder = (ImageButton) findViewById(R.id.activity_myorder_global_imbt_sortPoint);
		mImbtSortAZ = (ImageButton) findViewById(R.id.activity_myorder_global_imbt_sortAZ);
		mListView = (ListView) findViewById(R.id.activity_myorder_global_listview);
		mLayoutSortAZ = (LinearLayout) findViewById(R.id.activity_myorder_global_layout_az);
		mLayoutSortAmount = (LinearLayout) findViewById(R.id.activity_myorder_global_layout_amount);
		mLayoutLogoBackPress = (LinearLayout) findViewById(R.id.activity_myorder_global_layout_icon);

		// Call service to load all global restaurant orders
		 new ListGlobalOrders().execute();

		mLayoutSortAZ.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortAlphabetAZ();
				} else if (count % 2 == 0) {
					onClick_sortAlphabetZA();
				}
			}
		});

		mLayoutLogoBackPress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mLayoutSortAmount.setOnClickListener(new OnClickListener() {
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
					if (!Utils.isNetworkConnected(MyOrderGlobalActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
						return;
					}

					Intent intent = new Intent(MyOrderGlobalActivity.this,
							MyOrderChainActivity.class);
					intent.putExtra(StringExtraUtils.KEY_MYORDER_GLOBAL,
							mListRestaurantGlobals.get(pos));
					startActivityForResult(intent,
							CodeRequest.CODE_REQUEST_MYORDER_GLOBAL);

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
		if (requestCode == CodeRequest.CODE_REQUEST_MYORDER_GLOBAL) {
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

	public void onClick_sortAlphabetAZ() {
		try {

			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortNumberOrder.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyOrderGlobalObject>() {
						@Override
						public int compare(MyOrderGlobalObject lhs,
								MyOrderGlobalObject rhs) {
							if (lhs.getName().toString().trim()
									.toUpperCase(Locale.ENGLISH)
									.compareTo(rhs.getName().toString().trim()) > 0)
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

	public void onClick_sortAlphabetZA() {
		try {

			mImbtSortAZ.setVisibility(View.VISIBLE);
			mImbtSortNumberOrder.setVisibility(View.INVISIBLE);
			mImbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyOrderGlobalObject>() {
						@Override
						public int compare(MyOrderGlobalObject lhs,
								MyOrderGlobalObject rhs) {
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

	public void onClick_sortAmountHighToLow() {
		try {
			mImbtSortNumberOrder.setVisibility(View.VISIBLE);
			mImbtSortNumberOrder.setBackgroundResource(R.drawable.ic_arrow_up);
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyOrderGlobalObject>() {
						@Override
						public int compare(MyOrderGlobalObject lhs,
								MyOrderGlobalObject rhs) {
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
			mImbtSortNumberOrder.setVisibility(View.VISIBLE);
			mImbtSortNumberOrder
					.setBackgroundResource(R.drawable.ic_arrow_down);
			mImbtSortAZ.setVisibility(View.INVISIBLE);
			Collections.sort(mListRestaurantGlobals,
					new Comparator<MyOrderGlobalObject>() {
						@Override
						public int compare(MyOrderGlobalObject lhs,
								MyOrderGlobalObject rhs) {
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

	/**
	 * @Description: Open Share option
	 * @param v
	 */
	public void onClick_Share(View v) {
		// Add code to Share
		return;
	}

	public class ListGlobalOrders extends
			AsyncTask<String, Void, MyOrderGlobalEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MyOrderGlobalEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGetMyOrderGlobal(UserObject
								.getInstance(MyOrderGlobalActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyOrderGlobalEntity response = gson.fromJson(json,
						MyOrderGlobalEntity.class);

				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyOrderGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					Toast.makeText(MyOrderGlobalActivity.this,
							R.string.mess_error_server, Toast.LENGTH_LONG)
							.show();
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
				if (result.mListGlobalOrders != null) {
					if (mListRestaurantGlobals == null) {
						mListRestaurantGlobals = new ArrayList<MyOrderGlobalObject>();
					}
					mListRestaurantGlobals.clear();
					mListRestaurantGlobals.addAll(result.mListGlobalOrders);

					mAdapter = new MyOrderGlobalAdapter(
							MyOrderGlobalActivity.this,
							R.layout.myorder_global_item,
							mListRestaurantGlobals);
					mListView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
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
				CodeRequest.CODE_REQUEST_MYORDER_GLOBAL);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyOrderGlobalActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_GLOBAL);

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
		DialogStatInfo dialog = new DialogStatInfo(MyOrderGlobalActivity.this,
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
					MyOrderGlobalActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyOrderGlobalActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyOrderGlobalActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyOrderGlobalActivity.this)) {
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
										MyOrderGlobalActivity.this)
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
					if (!Utils.isNetworkConnected(MyOrderGlobalActivity.this)) {
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

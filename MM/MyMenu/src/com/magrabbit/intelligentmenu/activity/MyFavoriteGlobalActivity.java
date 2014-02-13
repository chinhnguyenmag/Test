package com.magrabbit.intelligentmenu.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyFavoriteGlobalAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.MyFavoriteGlobalEntity;
import com.magrabbit.intelligentmenu.model.MyFavoriteGlobalListEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
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
 * @Description This class use to load all favorites global
 */
public class MyFavoriteGlobalActivity extends BaseActivity implements
		OnItemClickListener, OptionMenuClickListener {

	private List<MyFavoriteGlobalListEntity> mListFavoriteGlobals;
	private ListView mListView;
	private ImageView mIvLbSortRestaurant;
	private ImageView mIvLbSortAmount;
	private TextView mTvAmount;
	private TextView mTvSortRestaurant;
	private MyFavoriteGlobalAdapter mGlobalAdapter;
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
		setContentView(R.layout.activity_myfavorite_global);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.myfavorite_global_list_lv);
		mIvLbSortRestaurant = (ImageView) findViewById(R.id.myfavorite_global_iv_status_sortAphabel);
		mIvLbSortAmount = (ImageView) findViewById(R.id.myfavorite_global_iv_status_sortAmount);
		mTvAmount = (TextView) findViewById(R.id.myfavorite_global_sortAmount);
		mTvSortRestaurant = (TextView) findViewById(R.id.myfavorite_global_sortAphabel);
		mIsFirstTimeToLoad = true;

		new GetFavoriteGlobalTask().execute();
		mListView.setOnItemClickListener(this);

		mTvSortRestaurant.setOnClickListener(new OnClickListener() {
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
	 * @Description This method use to sort name of global favorite by alphabet
	 *              from A to Z
	 */
	public void onClick_sortByAnphabelAZ() {
		try {
			mIvLbSortRestaurant.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortRestaurant.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListFavoriteGlobals,
					new Comparator<MyFavoriteGlobalListEntity>() {
						@Override
						public int compare(MyFavoriteGlobalListEntity lhs,
								MyFavoriteGlobalListEntity rhs) {
							if (lhs.getChainName()
									.trim()
									.toUpperCase()
									.compareTo(
											rhs.getChainName().toUpperCase()
													.trim()) > 0)
								return 1;
							else if (lhs
									.getChainName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getChainName().toUpperCase()
													.trim()) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGlobalAdapter.notifyDataSetChanged();
	}

	/**
	 * @Description This method use to sort name of global favorite by alphabet
	 *              from Z to A
	 */
	public void onClick_sortByAnphabelZA() {
		try {
			mIvLbSortRestaurant.setVisibility(View.VISIBLE);
			mIvLbSortAmount.setVisibility(View.INVISIBLE);
			mIvLbSortRestaurant.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListFavoriteGlobals,
					new Comparator<MyFavoriteGlobalListEntity>() {
						@Override
						public int compare(MyFavoriteGlobalListEntity lhs,
								MyFavoriteGlobalListEntity rhs) {
							if (lhs.getChainName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getChainName().toUpperCase()
													.trim()) > 0)
								return -1;
							else if (lhs
									.getChainName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getChainName().toUpperCase()
													.trim()) < 0)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGlobalAdapter.notifyDataSetChanged();
	}

	/**
	 * @Description This method use to sort name of global favorite by amount
	 *              from high to low
	 */
	public void onClick_sortByAmountHighToLow() {
		try {
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortRestaurant.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListFavoriteGlobals,
					new Comparator<MyFavoriteGlobalListEntity>() {
						@Override
						public int compare(MyFavoriteGlobalListEntity lhs,
								MyFavoriteGlobalListEntity rhs) {
							if (lhs.mTotalFavorites > rhs.mTotalFavorites)
								return -1;
							else if (lhs.mTotalFavorites < rhs.mTotalFavorites)
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGlobalAdapter.notifyDataSetChanged();
	}

	/**
	 * @Description This method use to sort name of global favorite by amount
	 *              from low to high
	 */
	public void onClick_sortByAmountLowToHigh() {
		try {
			mIvLbSortAmount.setVisibility(View.VISIBLE);
			mIvLbSortRestaurant.setVisibility(View.INVISIBLE);
			mIvLbSortAmount.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mListFavoriteGlobals,
					new Comparator<MyFavoriteGlobalListEntity>() {
						@Override
						public int compare(MyFavoriteGlobalListEntity lhs,
								MyFavoriteGlobalListEntity rhs) {
							if (lhs.mTotalFavorites > rhs.mTotalFavorites)
								return 1;
							else if (lhs.mTotalFavorites < rhs.mTotalFavorites)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGlobalAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			setResult(CodeRequest.CODE_RESULT_GO_HOME);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == CodeRequest.CODE_REQUEST_MYFAVORITE_GLOBAL) {
				// Update Avatar after changing it from EditInfo
				if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					finish();
				} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
					setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
					finish();
				} else if (resultCode == CodeRequest.CODE_RESULT_START_OVER) {
					setResult(CodeRequest.CODE_RESULT_START_OVER);
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new GetFavoriteGlobalTask().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYFAVORITE_GLOBAL);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyFavoriteGlobalActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYFAVORITE_GLOBAL);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyFavoriteGlobalActivity.this, null, "", 0, true);
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

	public void onClick_backpress(View v) {
		super.onBackPressed();
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(
					MyFavoriteGlobalActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyFavoriteGlobalActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyFavoriteGlobalActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyFavoriteGlobalActivity.this)) {
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
	public void onItemClick(AdapterView<?> adapterView, View view, int postion,
			long id) {
		try {
			Intent intent = new Intent(MyFavoriteGlobalActivity.this,
					MyFavoriteChainActivity.class);

			if (!Utils.isNetworkConnected(this)) {
				showToastMessage(this.getResources().getString(
						R.string.mess_error_network));
				return;
			} else {
				intent.putExtra(StringExtraUtils.KEY_MYFAVORITE_GLOBAL,
						mListFavoriteGlobals.get(postion));
				if (intent != null) {
					startActivityForResult(intent,
							CodeRequest.CODE_REQUEST_MYFAVORITE_GLOBAL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author vule
	 * @description This asynctask will load all favorites of global
	 * @return list favorites of global
	 */
	public class GetFavoriteGlobalTask extends
			AsyncTask<String, Void, MyFavoriteGlobalEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyFavoriteGlobalEntity doInBackground(String... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlMyFavoriteGlobal(UserObject
								.getInstance(MyFavoriteGlobalActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFavoriteGlobalEntity response = gson.fromJson(json,
						MyFavoriteGlobalEntity.class);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFavoriteGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyFavoriteGlobalActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(MyFavoriteGlobalActivity.this
								.getResources().getString(
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
					Toast.makeText(MyFavoriteGlobalActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListFavoriteGlobals == null) {
						mListFavoriteGlobals = new ArrayList<MyFavoriteGlobalListEntity>();
					}
					mListFavoriteGlobals.clear();
					int size = result.mListFavorites.size();
					if (size == 1) {
						MyFavoriteGlobalListEntity obj1 = result.mListFavorites
								.get(0);
						mListFavoriteGlobals
								.add(new MyFavoriteGlobalListEntity(
										obj1.mChainName, obj1.mLogo,
										obj1.mTotalFavorites));
					} else {
						for (int i = 0; i < size; i++) {
							MyFavoriteGlobalListEntity obj1 = result.mListFavorites
									.get(i);
							if (i == size - 1) {
								if (obj1.mTotalFavorites != -1) {
									mListFavoriteGlobals
											.add(new MyFavoriteGlobalListEntity(
													obj1.mChainName,
													obj1.mLogo,
													obj1.mTotalFavorites));
								}
							} else {
								boolean isExist = false;
								for (int j = i + 1; j < size; j++) {
									MyFavoriteGlobalListEntity obj2 = result.mListFavorites
											.get(j);
									if ((obj1.mChainName
											.equalsIgnoreCase(obj2.mChainName))
											&& (obj1.mTotalFavorites != -1)) {
										obj1.mTotalFavorites += obj2.mTotalFavorites;
										mListFavoriteGlobals
												.add(new MyFavoriteGlobalListEntity(
														obj1.mChainName,
														obj1.mLogo,
														obj1.mTotalFavorites));
										result.mListFavorites.get(j).mTotalFavorites = -1;
										obj2 = null;
										isExist = true;
										break;
									}
									obj2 = null;
								}
								if (!isExist) {
									mListFavoriteGlobals
											.add(new MyFavoriteGlobalListEntity(
													obj1.mChainName,
													obj1.mLogo,
													obj1.mTotalFavorites));
								}
							}
							obj1 = null;
						}
					}
					// mListFavoriteGlobals.addAll(result.mListFavorites);
					mGlobalAdapter = new MyFavoriteGlobalAdapter(
							MyFavoriteGlobalActivity.this,
							R.id.myfavorite_global_list_lv,
							mListFavoriteGlobals);
					mListView.setAdapter(mGlobalAdapter);
					onClick_sortByAnphabelAZ();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author vule
	 * @description: This asynctask will post feedback from customer to
	 *               application managers.
	 * @return: message successfully if it's success
	 */
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
										MyFavoriteGlobalActivity.this)
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
							.isNetworkConnected(MyFavoriteGlobalActivity.this)) {
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
					Toast.makeText(MyFavoriteGlobalActivity.this,
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

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
import com.magrabbit.intelligentmenu.adapter.MyFavoriteRestaurantAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFavoriteChainListEntity;
import com.magrabbit.intelligentmenu.model.MyFavoriteListItemImgsEntity;
import com.magrabbit.intelligentmenu.model.MyFavoriteRestaurantEntity;
import com.magrabbit.intelligentmenu.model.MyFavoriteRestaurantListEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.ServerDetailsEntity;
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
 * @Description This class use to load all favorite restaurant
 */
public class MyFavoriteRestaurantActivity extends BaseActivity implements
		OnItemClickListener, OptionMenuClickListener {

	private List<MyFavoriteRestaurantListEntity> mListFavoriteRestaurant;
	private ListView mListView;
	private ImageView mIvLbSortName;
	private ImageView mIvLbSortRating;
	private TextView mTvRating;
	private TextView mTvSortName;
	private MyFavoriteRestaurantAdapter mRestaurantAdapter;
	private ImageView mIvResLogo;
	private TextView mTvResName;
	private TextView mTvStreet;
	private TextView mTvState;
	private MyFavoriteChainListEntity chaintObject;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private ImageLoader mImageLoader;
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
		setContentView(R.layout.activity_myfavorite_restaurant);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListView = (ListView) findViewById(R.id.myfavorite_restaurant_lv_listview);
		mIvLbSortName = (ImageView) findViewById(R.id.myfavorite_restaurant_iv_status_sortAnphabet);
		mIvLbSortRating = (ImageView) findViewById(R.id.myfavorite_restaurant_iv_status_sortRating);
		mTvRating = (TextView) findViewById(R.id.myfavorite_restaurant_sortRating);
		mTvSortName = (TextView) findViewById(R.id.myfavorite_restaurant_sortAnphabet);
		mIvResLogo = (ImageView) findViewById(R.id.myfavorite_restaurant_iv_restaurant_logo);
		mTvResName = (TextView) findViewById(R.id.myfavorite_restaurant_tv_restaurant_name);
		mTvStreet = (TextView) findViewById(R.id.myfavorite_restaurant_tv_restaurant_street);
		mTvState = (TextView) findViewById(R.id.myfavorite_restaurant_tv_restaurant_state);
		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		mIsFirstTimeToLoad = true;

		// It's bundle will get data from object pass data
		Bundle bGetGlobalỌbject = getIntent().getExtras();
		if (bGetGlobalỌbject != null) {
			chaintObject = getIntent().getExtras().getParcelable(
					StringExtraUtils.KEY_MYFAVORITE_CHAIN);
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL + chaintObject.getLogo(),
					mIvResLogo);
			mTvResName.setText(chaintObject.getName());
			mTvStreet.setText(chaintObject.getAddress());
			mTvState.setText(chaintObject.getCity() + ", "
					+ chaintObject.getState() + " " + chaintObject.getZipcode());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new GetFavoriteRestaurantTask().execute();
		mListView.setOnItemClickListener(this);

		mTvSortName.setOnClickListener(new OnClickListener() {
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

		mTvRating.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortByRatingHighToLow();
				} else if (count % 2 == 0) {
					onClick_sortByRatingLowToHigh();
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

	public void onClick_sortByAnphabelAZ() {
		try {
			mIvLbSortName.setVisibility(View.VISIBLE);
			mIvLbSortRating.setVisibility(View.INVISIBLE);
			mIvLbSortName.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListFavoriteRestaurant,
					new Comparator<MyFavoriteRestaurantListEntity>() {
						@Override
						public int compare(MyFavoriteRestaurantListEntity lhs,
								MyFavoriteRestaurantListEntity rhs) {
							if (lhs.getName()
									.trim()
									.toUpperCase()
									.compareTo(
											rhs.getName().toUpperCase().trim()) > 0)
								return 1;
							else if (lhs
									.getName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getName().toUpperCase().trim()) < 0)
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
			mIvLbSortName.setVisibility(View.VISIBLE);
			mIvLbSortRating.setVisibility(View.INVISIBLE);
			mIvLbSortName.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListFavoriteRestaurant,
					new Comparator<MyFavoriteRestaurantListEntity>() {
						@Override
						public int compare(MyFavoriteRestaurantListEntity lhs,
								MyFavoriteRestaurantListEntity rhs) {
							if (lhs.getName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getName().toUpperCase().trim()) > 0)
								return -1;
							else if (lhs
									.getName()
									.toUpperCase()
									.trim()
									.compareTo(
											rhs.getName().toUpperCase().trim()) < 0)
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

	public void onClick_sortByRatingHighToLow() {
		try {
			mIvLbSortRating.setVisibility(View.VISIBLE);
			mIvLbSortName.setVisibility(View.INVISIBLE);
			mIvLbSortRating.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mListFavoriteRestaurant,
					new Comparator<MyFavoriteRestaurantListEntity>() {
						@Override
						public int compare(MyFavoriteRestaurantListEntity lhs,
								MyFavoriteRestaurantListEntity rhs) {
							if (Double.parseDouble(lhs.mRating) > Double
									.parseDouble(rhs.mRating))
								return 1;
							else if (Double.parseDouble(lhs.mRating) < Double
									.parseDouble(rhs.mRating))
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

	public void onClick_sortByRatingLowToHigh() {
		try {
			mIvLbSortRating.setVisibility(View.VISIBLE);
			mIvLbSortName.setVisibility(View.INVISIBLE);
			mIvLbSortRating.setBackgroundResource(R.drawable.ic_arrow_down);
			Collections.sort(mListFavoriteRestaurant,
					new Comparator<MyFavoriteRestaurantListEntity>() {
						@Override
						public int compare(MyFavoriteRestaurantListEntity lhs,
								MyFavoriteRestaurantListEntity rhs) {
							if (Double.parseDouble(lhs.mRating) > Double
									.parseDouble(rhs.mRating))
								return -1;
							else if (Double.parseDouble(lhs.mRating) < Double
									.parseDouble(rhs.mRating))
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYFAVORITE_RESTAURANT) {
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new GetFavoriteRestaurantTask().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYFAVORITE_RESTAURANT);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyFavoriteRestaurantActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYFAVORITE_RESTAURANT);

	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyFavoriteRestaurantActivity.this, null, "", 0, true);
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
					MyFavoriteRestaurantActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyFavoriteRestaurantActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyFavoriteRestaurantActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyFavoriteRestaurantActivity.this)) {
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
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		try {
			MyFavoriteRestaurantListEntity obj = mListFavoriteRestaurant
					.get(position);
			if (obj.getType() == 1) {
				obj = null;
				return;
			}
			Intent mIntent = new Intent(MyFavoriteRestaurantActivity.this,
					MenuItemActivity.class);

			if (Utils.isNetworkConnected(this)) {
				mIntent.putExtra(StringExtraUtils.KEY_CATEGORY_FROM,
						Constants.CATEGORY_FROM_FAVORITE);
				mIntent.putExtra(StringExtraUtils.KEY_ID_ITEM,
						mListFavoriteRestaurant.get(position).mId);
				mIntent.putExtra(StringExtraUtils.KEY_ID_LOCATION,
						chaintObject.mLocationId);
				mIntent.putExtra(StringExtraUtils.KEY_ID_CATEGORY,
						mListFavoriteRestaurant.get(position).mCategoryId);
				mIntent.putExtra(StringExtraUtils.KEY_NAME_CATEGORY,
						mListFavoriteRestaurant.get(position).mCategoryName);
				startActivityForResult(mIntent,
						CodeRequest.CODE_REQUEST_MYFAVORITE_RESTAURANT);
			} else {
				showToastMessage(getString(R.string.mess_error_network));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class GetFavoriteRestaurantTask extends
			AsyncTask<String, Void, MyFavoriteRestaurantEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyFavoriteRestaurantEntity doInBackground(String... params) {
			InputStream source = null;
			try {
				source = Server
						.requestGet(ServerURL.getUrlMyFavoriteRestaurant(
								UserObject
										.getInstance(MyFavoriteRestaurantActivity.this).mAccess_token,
								chaintObject.mLocationId));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFavoriteRestaurantEntity response = gson.fromJson(json,
						MyFavoriteRestaurantEntity.class);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFavoriteRestaurantEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyFavoriteRestaurantActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(MyFavoriteRestaurantActivity.this
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
					Toast.makeText(MyFavoriteRestaurantActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListFavoriteRestaurant == null) {
						mListFavoriteRestaurant = new ArrayList<MyFavoriteRestaurantListEntity>();
					}
					mListFavoriteRestaurant.clear();
					mListFavoriteRestaurant.addAll(result.mListItem);

					if (result.mListServer != null
							&& result.mListServer.size() > 0) {
						for (ServerDetailsEntity server : result.mListServer) {
							List<MyFavoriteListItemImgsEntity> mList = new ArrayList<MyFavoriteListItemImgsEntity>();
							mList.add(new MyFavoriteListItemImgsEntity(server
									.getAvatar()));
							mListFavoriteRestaurant
									.add(new MyFavoriteRestaurantListEntity(
											server.getId(), server.getName(),
											server.getRating(), 1, mList));
							mList = null;
						}
					}

					mRestaurantAdapter = new MyFavoriteRestaurantAdapter(
							MyFavoriteRestaurantActivity.this,
							R.id.myfavorite_restaurant_lv_listview,
							mListFavoriteRestaurant);
					mListView.setAdapter(mRestaurantAdapter);
					onClick_sortByAnphabelAZ();
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
										MyFavoriteRestaurantActivity.this)
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
							.isNetworkConnected(MyFavoriteRestaurantActivity.this)) {
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
					Toast.makeText(MyFavoriteRestaurantActivity.this,
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

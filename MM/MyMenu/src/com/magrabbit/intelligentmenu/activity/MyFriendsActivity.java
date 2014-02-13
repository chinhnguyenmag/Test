package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyFriendAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFriendProfile;
import com.magrabbit.intelligentmenu.customview.DialogMyFriendProfile.ProcessDialogConfirm_MyFriend;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyFriendDeleteEntity;
import com.magrabbit.intelligentmenu.model.MyFriendListEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Vu Le
 * @Description It will show list my friend, user can search by voice, sort list
 *              friend and view detail of user's friend.
 * 
 */
public class MyFriendsActivity extends BaseActivity implements
		OnItemClickListener, OptionMenuClickListener {

	private List<MyFriendListEntity> mLstMyFriend;
	private MyFriendAdapter mMyFriendAdapter;
	private ListView mListview;
	private DialogMyFriendProfile mDialogProfile;
	private EditText mEdtSearch;
	private ImageButton mBtnSpeak;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private TextView mTvSortAnphabet;
	private TextView mTvSortDate;
	private TextView mTvSortStatus;
	private ImageView mIvLbSortAnphabet;
	private ImageView mIvLbSortDate;
	private ImageView mIvLbSortStatus;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mCheckFilter = false;

	// check first time to load dialog process
	private boolean mIsFirstTimeToLoad = false;

	private List<ResolveInfo> mActivities;
	private ArrayList<String> mTextMatchList;
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
		setContentView(R.layout.activity_my_friends);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		// get id widget UI
		mListview = (ListView) findViewById(R.id.my_friend_list_listview);
		mEdtSearch = (EditText) findViewById(R.id.my_friend_list_et_search);
		mTvSortAnphabet = (TextView) findViewById(R.id.my_friend_list_tv_sortaz);
		mTvSortDate = (TextView) findViewById(R.id.my_friend_list_tv_sort_date);
		mTvSortStatus = (TextView) findViewById(R.id.my_friend_list_tv_sort_status);
		mIvLbSortAnphabet = (ImageView) findViewById(R.id.my_friend_list_iv_lb_sortaz);
		mIvLbSortDate = (ImageView) findViewById(R.id.my_friend_list_iv_lb_sort_date);
		mIvLbSortStatus = (ImageView) findViewById(R.id.my_friend_list_iv_lb_sort_status);
		mIsFirstTimeToLoad = true;

		new GetListFriendsTask().execute();
		mListview.setOnItemClickListener(this);

		searchWithFilter();

		mTvSortAnphabet.setOnClickListener(new OnClickListener() {
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
		mTvSortDate.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortByDateHightToLow();
				} else if (count % 2 == 0) {
					onClick_sortByDateLowToHigh();
				}
			}
		});

		mTvSortStatus.setOnClickListener(new OnClickListener() {
			int count = 0;

			@Override
			public void onClick(View v) {
				count++;
				if (count % 2 != 0) {
					onClick_sortStatusRegistered();
				} else if (count % 2 == 0) {
					onClick_sortStatusPending();
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
	protected void onDestroy() {
		super.onDestroy();

		if (mLstMyFriend != null) {
			mLstMyFriend.clear();
			mLstMyFriend = null;
		}

		if (mActivities != null) {
			mActivities.clear();
			mActivities = null;
		}

		if (mTextMatchList != null) {
			mTextMatchList.clear();
			mTextMatchList = null;
		}
	}

	public void onClick_Invite(View v) {
		startActivity(new Intent(this, InviteFriendActivity.class));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		mDialogProfile = new DialogMyFriendProfile(this,
				mLstMyFriend.get(position),
				new ProcessDialogConfirm_MyFriend() {

					@Override
					public void click_btn_remove(
							MyFriendListEntity myFriendObject, boolean isRemove) {
						if (isRemove) {
							new DeleteFriendTask().execute(mLstMyFriend.get(
									position).getId());
						}
					}

					@Override
					public void click_btn_cancel() {
						mDialogProfile.dismiss();
					}

				});

		mDialogProfile.show();

	}

	/**
	 * Show speak interface for users to speak and recognize
	 * 
	 * @param v
	 */
	public void onClick_SpeakSearch(View v) {
		checkVoiceRecognition();
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());
		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * This method use to call ACTION_RECOGNIZE_SPEECH from system
	 */
	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		mActivities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (mActivities.size() == 0) {
			mBtnSpeak.setEnabled(false);
			showToastMessage("Voice recognizer not present");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CodeRequest.CODE_REQUEST_MYFRIEND) {
			// Update Avatar after changing it from EditInfo
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
		}

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				mTextMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!mTextMatchList.isEmpty()) {
					// If first Match contains the 'open' word
					// Then start web search.
					if (mTextMatchList.get(0).contains("open")) {

						String searchQuery = mTextMatchList.get(0).replace(
								"open", " ");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);
					} else {
						mEdtSearch.setText(mTextMatchList.get(0));
						searchWithFilter();
					}
				}
				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage(getResources().getString(R.string.audioError));
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage(getResources().getString(R.string.clientError));
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage(getResources()
						.getString(R.string.networkError));
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage(getResources().getString(R.string.noMatch));
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage(getResources().getString(R.string.serverError));
			}
		} else if (requestCode == CodeRequest.CODE_REQUEST_MAPVIEW) {// using
																		// logout
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				finish();
			}
			if (resultCode == CodeRequest.CODE_RESULT_ADDRESS) {
				if (null != data) {
					mEdtSearch.setText(data
							.getStringExtra(StringExtraUtils.KEY_ADDRESS));
				}
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

	/**
	 * This method use to filter when user change text from edit text
	 */
	public void searchWithFilter() {
		mEdtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				String text = mEdtSearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				if (text.equals("")) {
					new GetListFriendsTask().execute();
				} else {
					mMyFriendAdapter.filterName(text);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFirstTimeToLoad) {
			new GetListFriendsTask().execute();
		}
		mIsFirstTimeToLoad = false;
	}

	/**
	 * This method use to sort alphabet for list view
	 * 
	 * @param v
	 */
	public void onClick_sortAnphabetAz() {
		try {
			mIvLbSortAnphabet.setVisibility(View.VISIBLE);
			mIvLbSortAnphabet.setBackgroundResource(R.drawable.ic_arrow_up);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortStatus.setVisibility(View.INVISIBLE);

			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {
						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {
							if (lhs.getFirstName()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getFirstName()
													.trim()
													.toUpperCase(
															Locale.getDefault())) > 0)
								return 1;
							else if (lhs
									.getFirstName()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getFirstName()
													.trim()
													.toUpperCase(
															Locale.getDefault())) < 0)
								return -1;
							else
								return 0;
						}
					});
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use to sort alphabet for list view
	 * 
	 * @param v
	 */
	public void onClick_sortAnphabetZa() {
		try {
			mIvLbSortAnphabet.setVisibility(View.VISIBLE);
			mIvLbSortAnphabet.setBackgroundResource(R.drawable.ic_arrow_down);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortStatus.setVisibility(View.INVISIBLE);

			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {
						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {
							if (lhs.getFirstName()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getFirstName()
													.trim()
													.toUpperCase(
															Locale.getDefault())) > 0)
								return -1;
							else if (lhs
									.getFirstName()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getFirstName()
													.trim()
													.toUpperCase(
															Locale.getDefault())) < 0)
								return 1;
							else
								return 0;
						}
					});
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method use to sort pending for list view
	 * 
	 * @param v
	 */
	public void onClick_sortStatusRegistered() {
		try {
			mIvLbSortAnphabet.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortStatus.setVisibility(View.VISIBLE);
			mIvLbSortStatus.setBackgroundResource(R.drawable.ic_arrow_up);

			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {
						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {
							if (lhs.getStatus()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getStatus()
													.trim()
													.toUpperCase(
															Locale.getDefault())) > 0)
								return 1;
							else if (lhs
									.getStatus()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getStatus()
													.trim()
													.toUpperCase(
															Locale.getDefault())) < 0)
								return -1;
							else
								return 0;
						}
					});
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method use to sort pending for list view
	 * 
	 * @param v
	 */
	public void onClick_sortStatusPending() {
		try {
			mIvLbSortAnphabet.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.INVISIBLE);
			mIvLbSortStatus.setVisibility(View.VISIBLE);
			mIvLbSortStatus.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {
						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {
							if (lhs.getStatus()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getStatus()
													.trim()
													.toUpperCase(
															Locale.getDefault())) > 0)
								return -1;
							else if (lhs
									.getStatus()
									.trim()
									.toUpperCase(Locale.getDefault())
									.compareTo(
											rhs.getStatus()
													.trim()
													.toUpperCase(
															Locale.getDefault())) < 0)
								return 1;
							else
								return 0;
						}
					});
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_sortByDateHightToLow() {
		try {
			mIvLbSortAnphabet.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.VISIBLE);
			mIvLbSortDate.setBackgroundResource(R.drawable.ic_arrow_up);
			mIvLbSortStatus.setVisibility(View.INVISIBLE);
			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {

						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"dd/MM/yy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getDateInvited());
								d2 = form.parse(rhs.getDateInvited());
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
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_sortByDateLowToHigh() {
		try {
			mIvLbSortAnphabet.setVisibility(View.INVISIBLE);
			mIvLbSortDate.setVisibility(View.VISIBLE);
			mIvLbSortDate.setBackgroundResource(R.drawable.ic_arrow_down);
			mIvLbSortStatus.setVisibility(View.INVISIBLE);
			Collections.sort(mLstMyFriend,
					new Comparator<MyFriendListEntity>() {

						@Override
						public int compare(MyFriendListEntity lhs,
								MyFriendListEntity rhs) {

							SimpleDateFormat form = new SimpleDateFormat(
									"dd/MM/yy");

							Date d1 = null;
							Date d2 = null;
							try {
								d1 = form.parse(lhs.getDateInvited());
								d2 = form.parse(rhs.getDateInvited());
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
			mMyFriendAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class GetListFriendsTask extends
			AsyncTask<Void, Void, MyFriendListEntity[]> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyFriendListEntity[] doInBackground(Void... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlListMyFriends(UserObject
								.getInstance(MyFriendsActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFriendListEntity[] response = gson.fromJson(json,
						MyFriendListEntity[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyFriendListEntity[] result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MyFriendsActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(MyFriendsActivity.this.getResources()
								.getString(R.string.mess_error_server));
					}
					dismissProgress();
					return;
				}

				if (Server.mStatus != 200) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					showToastMessage(getResources().getString(
							R.string.mess_invalid_accesstoken));
					finish();
					return;
				}

				if (mLstMyFriend == null) {
					mLstMyFriend = new ArrayList<MyFriendListEntity>();
				}
				mLstMyFriend.clear();
				for (int i = 0; i < result.length; i++) {
					mLstMyFriend.add(result[i]);
				}
				mMyFriendAdapter = new MyFriendAdapter(MyFriendsActivity.this,
						R.id.my_friend_list_listview, mLstMyFriend);
				mMyFriendAdapter.notifyDataSetChanged();
				mListview.setAdapter(mMyFriendAdapter);
				onClick_sortByDateHightToLow();
				dismissProgress();

			} catch (Exception e) {
				e.printStackTrace();
			}

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
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyFriendsActivity.this,
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
			mDialogMyFeedback = new DialogMyFeedback(MyFriendsActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyFriendsActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyFriendsActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyFriendsActivity.this)) {
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
				CodeRequest.CODE_REQUEST_MYFRIEND);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyFriendsActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYFRIEND);
	}

	/**
	 * @author vule
	 * 
	 *         This method use to delete friend from list friend
	 * 
	 */
	public class DeleteFriendTask extends
			AsyncTask<Integer, Void, MyFriendDeleteEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MyFriendDeleteEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestPost(ServerURL
						.getUrlDeleteFriend(), ServerURL.getEntityDeleteFriend(
						UserObject.getInstance(MyFriendsActivity.this)
								.getAccess_token(), params[0]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFriendDeleteEntity response = gson.fromJson(json,
						MyFriendDeleteEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFriendDeleteEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MyFriendsActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(MyFriendsActivity.this.getResources()
								.getString(R.string.mess_error_server));
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
					showToastMessage(getResources().getString(
							R.string.mess_invalid_accesstoken));
					finish();
					return;
				}

				if (result.mError != null) {
					if (result.mError.toUpperCase().trim()
							.equals("NOT EXIST FRIENDSHIP".trim())) {
						showToastMessage("Your friend has been deleted successfully !");
						new GetListFriendsTask().execute();
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
					}
					dismissProgress();
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					new GetListFriendsTask().execute();
					dismissProgress();
				}
			} catch (Exception e) {
				e.toString();
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
								UserObject.getInstance(MyFriendsActivity.this)
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
					if (!Utils.isNetworkConnected(MyFriendsActivity.this)) {
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
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
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

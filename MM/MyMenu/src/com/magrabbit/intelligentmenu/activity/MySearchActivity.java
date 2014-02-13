package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MySearchProfilesAdapter;
import com.magrabbit.intelligentmenu.adapter.MySearchProfilesAdapter.ProcessDialogConfirm_MySearchAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogCreateNewProfile;
import com.magrabbit.intelligentmenu.customview.DialogCreateNewProfile.ProcessDialogConfirm_CreateNewProfile;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.SearchProfile;
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
 * @Description This is the place where users can add a new search profile, edit
 *              profile, delete profile and search favorite restaurants advanced
 */
public class MySearchActivity extends BaseActivity implements
		OptionMenuClickListener {

	public ListView mLvProfiles;
	public MySearchProfilesAdapter mAdapter;
	public int mIndexOfDefault = 0;
	private boolean mIsFirstProfile;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;

	private boolean mIsEditMode = false;
	private int mIndexAddedProfile = 0;
	// Layout elements
	private Button mBtnSavedProfile;
	private Button mBtnCreateNewProfile;
	/**
	 * Collection of search profiles
	 */
	public List<SearchProfile> mListSearchProfiles;
	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;
	AppPreferences mAppPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mysearch);
		// Inflate elements from layout
		mBtnCreateNewProfile = (Button) findViewById(R.id.activity_advanced_search_btn_createNewProfile);
		mBtnSavedProfile = (Button) findViewById(R.id.activity_advanced_search_btn_SavedProfile);
		// Add Optional Menu to layout
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);
		// Inflate elements from layout
		mLvProfiles = (ListView) findViewById(R.id.activity_mysearch_listview);
		mLvProfiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				if (Utils.isNetworkConnected(MySearchActivity.this)) {
					// Set variables for editing mode
					mIsEditMode = true;
					mIsFirstProfile = false;
					// Open Dialog for users to change settings
					DialogCreateNewProfile dialog = new DialogCreateNewProfile(
							MySearchActivity.this, mIsEditMode, pos,
							mIsFirstProfile, mListSearchProfiles,
							new ProcessDialogConfirm_CreateNewProfile() {

								@Override
								public void click_Search(
										SearchProfile searchprofile) {
									try {
										// Search Advanced when editing profile
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putParcelable(
												StringExtraUtils.KEY_SEARCHPROFILE_MYSEARCH,
												searchprofile);
										intent.putExtras(bundle);
										setResult(
												CodeResult.CODE_RESULT_MYSEARCH_PROFILE,
												intent);
										finish();
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void click_Save(
										List<SearchProfile> listSearchProfiles,
										int indexOfAddedProfile) {
									try {
										if (Utils
												.isNetworkConnected(MySearchActivity.this)) {
											// Update Adapter to
											// refresh
											// list view
											mListSearchProfiles.clear();
											mListSearchProfiles
													.addAll(listSearchProfiles);
											mLvProfiles.setAdapter(mAdapter);
											mAdapter.notifyDataSetChanged();
											mLvProfiles
													.smoothScrollToPosition(indexOfAddedProfile);
										} else {
											showToastMessage(MySearchActivity.this
													.getResources()
													.getString(
															R.string.mess_error_network));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void click_ChangeButtonBackground() {
									// TODO Auto-generated
									// method stub

								}
							});
					dialog.show();
				} else {
					showToastMessage(MySearchActivity.this.getResources()
							.getString(R.string.mess_error_network));
				}
			}
		});
		mListSearchProfiles = new ArrayList<SearchProfile>();
		mAppPreferences = new AppPreferences(MySearchActivity.this);
		if (Utils.isNetworkConnected(this)) {
			new getAllSearchProfilesTask().execute();
		} else {
			showToastMessage(this.getResources().getString(
					R.string.mess_error_network));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
		try {
			FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			FlurryAgent.onEndSession(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			setResult(CodeResult.CODE_RESULT_MYSEARCH_BACK);
			finish();
		}

	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Get all search profiles when going to MySearch screen
	 */
	public class getAllSearchProfilesTask extends
			AsyncTask<Void, Void, SearchProfile[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SearchProfile[] doInBackground(Void... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlGetAllSearchProfile(MySearchActivity.this));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SearchProfile[] response = gson.fromJson(json,
						SearchProfile[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchProfile[] result) {
			try {
				if (result == null) {
					dismissProgress();
					if (!Utils.isNetworkConnected(MySearchActivity.this)) {
						showToastMessage(MySearchActivity.this.getResources()
								.getString(R.string.mess_error_network));
					} else {
						if (Server.mStatus != 200) {
							setResult(CodeRequest.CODE_RESULT_LOGOUT);
							dismissProgress();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.mess_invalid_accesstoken),
									Toast.LENGTH_LONG).show();
							finish();
						} else {
							showToastMessage(MySearchActivity.this
									.getResources().getString(
											R.string.mess_error_server));
						}
					}
					return;
				} else if (result.length == 0) {
					showToastMessage(MySearchActivity.this.getResources()
							.getString(R.string.search_profile_no_result));
					dismissProgress();
				} else {
					mListSearchProfiles.clear();
					for (int i = 0; i < result.length; i++) {
						mListSearchProfiles.add(result[i]);
					}
					for (SearchProfile s : mListSearchProfiles) {
						if (s.getIsDefault() == 1) {
							mIndexOfDefault = mListSearchProfiles.indexOf(s);
							break;
						}
					}
					mAdapter = new MySearchProfilesAdapter(
							MySearchActivity.this,
							R.layout.mysearch_profile_item,
							mListSearchProfiles,
							new ProcessDialogConfirm_MySearchAdapter() {

								@Override
								public void click_Search(
										SearchProfile searchprofile) {
									// Search Advanced when editing profile
									Intent intent = new Intent();
									Bundle bundle = new Bundle();
									bundle.putParcelable(
											StringExtraUtils.KEY_SEARCHPROFILE_MYSEARCH,
											searchprofile);
									intent.putExtras(bundle);
									setResult(
											CodeResult.CODE_RESULT_MYSEARCH_PROFILE,
											intent);
									finish();

								}

								@Override
								public void click_UpdateListView(
										List<SearchProfile> listSearchProfiles,
										int indexOfDefaultProfile) {
									// Notify adapter to refresh list view
									mListSearchProfiles.clear();
									mListSearchProfiles
											.addAll(listSearchProfiles);
									// Set default profile again
									// if (mListSearchProfiles.size() != 0) {
									// mListSearchProfiles.get(indexOfDefault)
									// .setIsDefault(1);
									// }
									mLvProfiles.setAdapter(mAdapter);
									mAdapter.notifyDataSetChanged();
									mLvProfiles
											.smoothScrollToPosition(indexOfDefaultProfile);
								}

								@Override
								public void process_lostAccessToken() {
									setResult(CodeRequest.CODE_RESULT_LOGOUT);
									finish();
								}
							});
					mLvProfiles.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					mLvProfiles.smoothScrollToPosition(mIndexAddedProfile);

					// Set default profile id for the first profile
					if (mIsFirstProfile) {
						UserObject.getInstance(MySearchActivity.this)
								.setDefaultProfileId(
										mListSearchProfiles.get(0).getId());
						mAppPreferences
								.setDefaultsearchprofile(mListSearchProfiles
										.get(0).getId());

					}
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

		}
	}

	/**
	 * 
	 * @param v
	 * @Description: Show a dialog to create a new profile
	 */
	public void onClick_createProfile(View v) {
		try {
			if (Utils.isNetworkConnected(MySearchActivity.this)) {
				mBtnSavedProfile
						.setBackgroundResource(R.drawable.ic_tab_unselected);
				mBtnCreateNewProfile
						.setBackgroundResource(R.drawable.ic_tab_selected);
				if (mListSearchProfiles.size() == 0) {
					mIsFirstProfile = true;
				} else {
					mIsFirstProfile = false;
				}
				DialogCreateNewProfile dialog = new DialogCreateNewProfile(
						MySearchActivity.this, false, -1, mIsFirstProfile,
						mListSearchProfiles,
						new ProcessDialogConfirm_CreateNewProfile() {

							@Override
							public void click_Search(SearchProfile searchprofile) {
								Intent intent = new Intent(
										MySearchActivity.this,
										MapViewActivity.class);
								intent.putExtra(
										StringExtraUtils.KEY_SEARCHPROFILE_MYSEARCH,
										searchprofile);
								setResult(
										CodeResult.CODE_RESULT_MYSEARCH_PROFILE,
										intent);
								finish();
							}

							@Override
							public void click_Save(
									List<SearchProfile> listSearchProfiles,
									int indexOfAddedProfile) {
								mIndexAddedProfile = indexOfAddedProfile;
								if (Utils
										.isNetworkConnected(MySearchActivity.this)) {
									new getAllSearchProfilesTask().execute();
								} else {
									showToastMessage(MySearchActivity.this
											.getResources()
											.getString(
													R.string.mess_error_network));
								}
							}

							@Override
							public void click_ChangeButtonBackground() {
								mBtnSavedProfile
										.setBackgroundResource(R.drawable.ic_tab_selected);
								mBtnCreateNewProfile
										.setBackgroundResource(R.drawable.ic_tab_unselected);

							}
						});
				dialog.show();
			} else {
				showToastMessage(MySearchActivity.this.getResources()
						.getString(R.string.mess_error_network));
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYSEARCH) {
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			}
		}
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYSEARCH);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MySearchActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYSEARCH);
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MySearchActivity.this, null,
				"", 0, true);
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
			mDialogMyFeedback = new DialogMyFeedback(MySearchActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils.isNetworkConnected(MySearchActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MySearchActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MySearchActivity.this)) {
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
								UserObject.getInstance(MySearchActivity.this)
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
					if (!Utils.isNetworkConnected(MySearchActivity.this)) {
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
				dismissProgress();
			}
		}
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Get all search profiles when going to MySearch screen
	 */
	public class LoadAllSearchProfilesAgainTask extends
			AsyncTask<Void, Void, SearchProfile[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SearchProfile[] doInBackground(Void... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlGetAllSearchProfile(MySearchActivity.this));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SearchProfile[] response = gson.fromJson(json,
						SearchProfile[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchProfile[] result) {
			try {
				if (result == null) {
					dismissProgress();
					if (!Utils.isNetworkConnected(MySearchActivity.this)) {
						showToastMessage(MySearchActivity.this.getResources()
								.getString(R.string.mess_error_network));
					} else {
						if (Server.mStatus != 200) {
							setResult(CodeRequest.CODE_RESULT_LOGOUT);
							dismissProgress();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.mess_invalid_accesstoken),
									Toast.LENGTH_LONG).show();
							finish();
						} else {
							showToastMessage(MySearchActivity.this
									.getResources().getString(
											R.string.mess_error_server));
						}
					}

					return;
				} else if (result.length == 0) {
					showToastMessage(MySearchActivity.this.getResources()
							.getString(R.string.search_profile_no_result));
					dismissProgress();
				} else {
					mListSearchProfiles.clear();
					for (int i = 0; i < result.length; i++) {
						mListSearchProfiles.add(result[i]);
					}
					for (SearchProfile s : mListSearchProfiles) {
						if (s.getIsDefault() == 1) {
							mIndexOfDefault = mListSearchProfiles.indexOf(s);
							break;
						}
					}
					mLvProfiles.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

		}
	}
}

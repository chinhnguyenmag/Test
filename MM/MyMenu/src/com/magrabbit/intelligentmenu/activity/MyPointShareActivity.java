package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.ExpandableListAdapter;
import com.magrabbit.intelligentmenu.adapter.ExpandableListAdapter.Process_ExpandableListAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirmSharePoint;
import com.magrabbit.intelligentmenu.customview.DialogConfirmSharePoint.ProcessDialogConfirm_SaveProfile;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogPickSingleEmailContact;
import com.magrabbit.intelligentmenu.customview.DialogPickSinglePhoneContact;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.listener.PickContactListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyFriendListEntity;
import com.magrabbit.intelligentmenu.model.MyPointChainListLocationEntity;
import com.magrabbit.intelligentmenu.model.SharePointViaEmailEntity;
import com.magrabbit.intelligentmenu.model.SharePointViaFriendListEntity;
import com.magrabbit.intelligentmenu.model.SharePointViaSMSEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.task.LoadContactPhoneTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @Description Sharing point in a specific restaurant to another dinner through
 *              SMS, Email, or FriendList in MyMenu
 */
public class MyPointShareActivity extends BaseActivity implements
		OptionMenuClickListener, PickContactListener {

	private ExpandableListAdapter mAdapter;
	private ExpandableListView mExpandableListView;
	private List<String> mListDataHeaders;
	// List of Friends
	private List<MyFriendListEntity> mListMyFriends;
	// For SMS share point
	private List<ContactObject> mListContactResults;
	// List Id contacts for SMS
	private List<String> mListIdContacts1;
	// List Id contacts for Email
	private List<String> mListIdContacts2;
	private int mRestaurantId;
	private int mPointShared;
	// For Email share point
	private String mToEmail;
	// For SMS share point
	private String mToSMS;
	// For FriendList share point
	private int mIndexOfFriend = -1;
	private String mToUserName;

	private MyPointChainListLocationEntity mChainObject;
	// Radio Button Processing
	private int mSentType = 0;// 0:SMS, 1:Email, 2: Friend
	private RadioButton mRdbSMS;
	private RadioButton mRdbEmail;
	// Layout elements
	private TextView mEdtSMS;
	private TextView mEdtEmail;
	private EditText mEdtPointShared;
	private ImageView mIvLogo;
	private TextView mTvChainName;
	private TextView mTvAddress;
	private TextView mTvCityState;
	private TextView mTvDinnerPoint;
	private TextView mTvDinnerStatus;
	// Loader for images
	private ImageLoader mImageLoader;
	// Dinner Status for this restaurant
	private String mStatus;
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
	private String mFullName;
	private boolean isEditContact;
	DialogPickSinglePhoneContact mDialogPhoneContact;
	DialogPickSingleEmailContact mDialogEmailContact;

	boolean mIsTypeEmail = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypoint_share);
		// Add Optional Menu to layout
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mExpandableListView = (ExpandableListView) findViewById(R.id.activity_mypoint_share_lv_Expandable);
		mRdbSMS = (RadioButton) findViewById(R.id.activity_mypoint_share_cbx_SMS);
		// Set default sending type for SMS
		mRdbSMS.setChecked(true);

		mRdbEmail = (RadioButton) findViewById(R.id.activity_mypoint_share_cbx_Email);
		mEdtSMS = (TextView) findViewById(R.id.activity_mypoint_share_edt_phone);
		mEdtEmail = (TextView) findViewById(R.id.activity_mypoint_share_email);
		// Set default to disable Email text box
		mEdtEmail.setEnabled(false);
		mEdtPointShared = (EditText) findViewById(R.id.activity_mypoint_share_edt_pointShared);
		mIvLogo = (ImageView) findViewById(R.id.activity_mypoint_share_restaurant_logo);
		mTvChainName = (TextView) findViewById(R.id.activity_mypoint_share_tv_nameRestaurant);
		mTvAddress = (TextView) findViewById(R.id.activity_mypoint_share_tv_streetRestaurant);
		mTvCityState = (TextView) findViewById(R.id.activity_mypoint_share_tv_stateRestaurant);
		mTvDinnerPoint = (TextView) findViewById(R.id.activity_mypoint_share_tv_point);
		mTvDinnerStatus = (TextView) findViewById(R.id.activity_mypoint_share_tv_status);
		mLayoutLogoBackPress = (LinearLayout) findViewById(R.id.activity_mypoint_share_layout_icon);

		// Process onClick events on Phone and Email text box
		mEdtSMS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFullName = "";
				isEditContact = false;
				mIsTypeEmail = false;
				switch (ContactObject.mLoadStatus) {
				case ContactObject.LOADING:
					showProgress();
					break;
				case ContactObject.LOAD_FINISHED:
					new ListViewContactsPhoneLoader().execute();
					break;
				case ContactObject.LOAD_ERROR:
					LoadContactPhoneTask.execute(getApplicationContext());
					break;
				}

			}
		});
		mEdtEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFullName = "";
				isEditContact = false;
				mIsTypeEmail = true;
				switch (ContactObject.mLoadStatus) {
				case ContactObject.LOADING:
					showProgress();
					break;
				case ContactObject.LOAD_FINISHED:
					new ListViewContactsEmailLoader().execute();
					break;
				case ContactObject.LOAD_ERROR:
					LoadContactPhoneTask.execute(getApplicationContext());
					break;
				}

			}
		});

		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);

		mListContactResults = new ArrayList<ContactObject>();
		mListIdContacts1 = new ArrayList<String>();
		mListIdContacts2 = new ArrayList<String>();
		mListMyFriends = new ArrayList<MyFriendListEntity>();
		// Get bundle from my point chain activity
		try {
			Bundle bGetChainỌbject = getIntent().getExtras();
			if (bGetChainỌbject != null) {
				mChainObject = getIntent().getExtras().getParcelable(
						StringExtraUtils.KEY_MYPOINT_CHAIN);
			}

			Bundle bStatus = getIntent().getExtras();
			if (bStatus != null) {
				mStatus = getIntent().getStringExtra(
						StringExtraUtils.KEY_MYPOINT_CHAIN_STATUS);
			}
			// Set data from Bundle for my point share if they exists
			mImageLoader.DisplayImage(ServerURL.URL + mChainObject.getLogo(),
					mIvLogo);
			if (mChainObject.mTotal > 1) {
				mTvDinnerPoint.setText(Utils
						.formatPointNumbers(mChainObject.mTotal) + " Points");
			} else {
				mTvDinnerPoint.setText(Utils
						.formatPointNumbers(mChainObject.mTotal) + " Point");
			}
			mTvChainName.setText(mChainObject.getName());
			// Get restaurant ID
			mRestaurantId = mChainObject.mId;

			if (mStatus != null) {
				mTvDinnerStatus.setText(" " + mStatus);
			} else {
				mTvDinnerStatus.setText("");
			}
			mTvAddress.setText(mChainObject.getAddress());
			mTvCityState.setText(mChainObject.getCity() + ", "
					+ mChainObject.getState() + " " + mChainObject.getZip());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Set real data for expandableListview
		// Set Group header
		mListDataHeaders = new ArrayList<String>();
		mListDataHeaders.add("MyFriends");
		// Get data for
		new GetListFriendsTask().execute();
		mExpandableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int pos,
					long id) {
				mSentType = 2;
				mRdbSMS.setChecked(false);
				mRdbEmail.setChecked(false);
				// Load ExpandableList again
				mIndexOfFriend = pos;
				mAdapter.notifyDataSetChanged();

			}
		});
		// Process to change activity when click on LOGO
		mLayoutLogoBackPress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mRdbSMS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSentType = 0;
				loadSentType(mSentType);
				mEdtSMS.setEnabled(true);
				mEdtEmail.setText("");
				mEdtEmail.setEnabled(false);

			}
		});
		mRdbEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSentType = 1;
				loadSentType(mSentType);
				mEdtEmail.setEnabled(true);
				mEdtSMS.setText("");
				mEdtSMS.setEnabled(false);
			}
		});
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Constants.DISPLAY_MESSAGE_ACTION));
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
		if (mAdapter != null) {
			mAdapter.clear();
		}
	}

	/**
	 * 
	 * @param sentType
	 * @Description load the selected way to send point including SMS, Email,
	 *              and FriendList
	 */
	public void loadSentType(int sentType) {
		switch (sentType) {
		case 0:
			mRdbSMS.setChecked(true);
			mRdbEmail.setChecked(false);
			// Load ExpandableList again
			mIndexOfFriend = -1;
			mAdapter.setIndexFriend(mIndexOfFriend);
			mAdapter.notifyDataSetChanged();
			break;
		case 1:
			mRdbSMS.setChecked(false);
			mRdbEmail.setChecked(true);
			// Load ExpandableList again
			mIndexOfFriend = -1;
			mAdapter.setIndexFriend(mIndexOfFriend);
			mAdapter.notifyDataSetChanged();
			break;
		default:
			break;
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
	 * 
	 * @param v
	 * @Description Processing the Share event
	 */
	public void onClick_Share(View v) {

		switch (mSentType) {
		case 0:// SMS
			try {
				// Compare the input point with the total point of this specific
				// restaurant
				String sms = mEdtPointShared.getText().toString().trim();
				if (sms.equals("")) {
					showToastMessage(getResources().getString(
							R.string.activity_mypoint_share_mess_input_point));
				} else {
					mPointShared = Integer.parseInt(mEdtPointShared.getText()
							.toString().trim());
					if ((mPointShared > 0)
							&& (mPointShared <= mChainObject.mTotal)) {

						mToSMS = mEdtSMS.getText().toString().trim();
						if (!mToSMS.equals("")) {
							// Open Dialog to confirm
							DialogConfirmSharePoint dialog = new DialogConfirmSharePoint(
									MyPointShareActivity.this,
									mListContactResults.get(0).getPhoneNumber(),
									mPointShared, mSentType,
									new ProcessDialogConfirm_SaveProfile() {

										@Override
										public void click_Save() {
											// Call AsyncTask to share point and
											// send url to
											// users
											TelephonyManager telMgr = (TelephonyManager) getSystemService(MyPointShareActivity.this.TELEPHONY_SERVICE);
											int simState = telMgr.getSimState();
											switch (simState) {
											case TelephonyManager.SIM_STATE_ABSENT:
												Toast.makeText(
														getApplicationContext(),
														MyPointShareActivity.this
																.getString(R.string.activity_mypoint_share_mess_sms_no_sim),
														Toast.LENGTH_SHORT)
														.show();
												break;
											case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
												Toast.makeText(
														getApplicationContext(),
														MyPointShareActivity.this
																.getString(R.string.activity_mypoint_share_mess_sms_failed),
														Toast.LENGTH_SHORT)
														.show();
												break;
											case TelephonyManager.SIM_STATE_PIN_REQUIRED:
												Toast.makeText(
														getApplicationContext(),
														MyPointShareActivity.this
																.getString(R.string.activity_mypoint_share_mess_sms_failed),
														Toast.LENGTH_SHORT)
														.show();
												break;
											case TelephonyManager.SIM_STATE_PUK_REQUIRED:
												Toast.makeText(
														getApplicationContext(),
														MyPointShareActivity.this
																.getString(R.string.activity_mypoint_share_mess_sms_failed),
														Toast.LENGTH_SHORT)
														.show();
												break;
											case TelephonyManager.SIM_STATE_READY:
												String content[] = new String[3];
												content[0] = Utils
														.formatPhoneNumber(mListContactResults
																.get(0)
																.getPhoneNumber());
												content[1] = Utils
														.removeSpecial(mListContactResults
																.get(0)
																.getFristName());
												content[2] = Utils
														.removeSpecial(mListContactResults
																.get(0)
																.getLastName());
												new SharePointViaSMSTask()
														.execute(content);
												break;
											case TelephonyManager.SIM_STATE_UNKNOWN:
												Toast.makeText(
														getApplicationContext(),
														MyPointShareActivity.this
																.getString(R.string.activity_mypoint_share_mess_sms_failed),
														Toast.LENGTH_SHORT)
														.show();
												break;
											}

										}

										@Override
										public void click_Cancel() {
											// TODO Auto-generated method stub
										}
									});
							dialog.show();
						} else {
							showToastMessage(getResources()
									.getString(
											R.string.activity_mypoint_share_mess_input_phone));
						}
					} else {
						showToastMessage(getResources()
								.getString(
										R.string.activity_mypoint_share_mess_point_over));
					}
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1:// Email
				// Compare the input point with the total point of this specific
				// restaurant
			String message = mEdtPointShared.getText().toString().trim();
			if (!message.equals("")) {
				mPointShared = Integer.parseInt(mEdtPointShared.getText()
						.toString());
				if ((mPointShared > 0) && (mPointShared <= mChainObject.mTotal)) {
					mToEmail = mEdtEmail.getText().toString();
					if (!mToEmail.equals("")) {
						// Open Dialog to confirm
						DialogConfirmSharePoint dialog = new DialogConfirmSharePoint(
								MyPointShareActivity.this, mToEmail,
								mPointShared, mSentType,
								new ProcessDialogConfirm_SaveProfile() {

									@Override
									public void click_Save() {
										String content[] = new String[3];
										content[0] = mListContactResults.get(0)
												.getEmail();
										content[1] = Utils
												.removeSpecial(mListContactResults
														.get(0).getFristName());
										content[2] = Utils
												.removeSpecial(mListContactResults
														.get(0).getLastName());
										// Call AsyncTask to share point
										new SharePointViaEmailTask()
												.execute(content);
									}

									@Override
									public void click_Cancel() {
										// TODO Auto-generated method stub
									}
								});
						dialog.show();

					} else {
						showToastMessage(getResources()
								.getString(
										R.string.activity_mypoint_share_mess_input_email));
					}
				} else {
					showToastMessage(getResources().getString(
							R.string.activity_mypoint_share_mess_point_over));
				}
			} else {
				showToastMessage(getResources().getString(
						R.string.activity_mypoint_share_mess_input_point));
			}
			break;
		case 2:// FriendList
				// Compare the input point with the total point of this specific
				// restaurant
			String mess = mEdtPointShared.getText().toString().trim();
			if (!mess.equals("")) {
				mPointShared = Integer.parseInt(mEdtPointShared.getText()
						.toString());
				mToUserName = mListMyFriends.get(mIndexOfFriend).getFirstName()
						+ " "
						+ mListMyFriends.get(mIndexOfFriend).getLastName();
				if ((mPointShared > 0) && (mPointShared <= mChainObject.mTotal)) {
					// Open Dialog to confirm
					DialogConfirmSharePoint dialog = new DialogConfirmSharePoint(
							MyPointShareActivity.this, mToUserName,
							mPointShared, mSentType,
							new ProcessDialogConfirm_SaveProfile() {

								@Override
								public void click_Save() {
									// Call AsyncTask to share point
									new SharePointViaFriendListTask().execute();
								}

								@Override
								public void click_Cancel() {
									// TODO Auto-generated method stub
								}
							});
					dialog.show();

				} else {
					showToastMessage(getResources().getString(
							R.string.activity_mypoint_share_mess_point_over));
				}
			} else {
				showToastMessage(getResources().getString(
						R.string.activity_mypoint_share_mess_input_point));
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @param v
	 * @Description Processing the Cancel event
	 */
	public void onClick_Cancel(View v) {
		// SmsManager smsManager = SmsManager.getDefault();
		// ArrayList<String> parts = smsManager
		// .divideMessage("http://54.245.230.7:9000/invite-friend-share/7d4519626dde0866fd02c5d58193118d37f03ee3");
		// smsManager.sendMultipartTextMessage("01689906671", null, parts, null,
		// null);
		finish();
	}

	/** An AsyncTask class to retrieve and load listview with contacts */
	private class ListViewContactsPhoneLoader extends
			AsyncTask<Void, Void, List<ContactObject>> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected List<ContactObject> doInBackground(Void... params) {
			try {
				if (mListContactResults != null) {
					mListContactResults.clear();
				}
				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getPhoneNumber() != null
							&& contact.getPhoneNumber().length() > 0) {
						mListContactResults.add(contact);
					}
				}

				if (mListIdContacts1 == null || mListIdContacts1.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdContacts1.size() > 0) {
					for (int i = 0; i < mListIdContacts1.size(); i++) {
						for (int j = 0; j < mListContactResults.size(); j++) {
							if (mListIdContacts1.get(i).equals(
									mListContactResults.get(j).getIdContact())) {
								mListContactResults.get(j).setSelected(true);
							} else {
								mListContactResults.get(j).setSelected(false);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<ContactObject> result) {
			// Setting the cursor containing contacts to listview

			mDialogPhoneContact = new DialogPickSinglePhoneContact(
					MyPointShareActivity.this, MyPointShareActivity.this,
					mListContactResults, mFullName, isEditContact);
			mDialogPhoneContact.show();
			dismissProgress();
		}
	}

	/** An AsyncTask class to retrieve and load listview with contacts */
	private class ListViewContactsEmailLoader extends
			AsyncTask<Void, Void, List<ContactObject>> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected List<ContactObject> doInBackground(Void... params) {
			try {
				if (mListContactResults != null) {
					mListContactResults.clear();
				}
				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getEmail() != null
							&& contact.getEmail().length() > 0) {
						mListContactResults.add(contact);
					}
				}

				if (mListIdContacts2 == null || mListIdContacts2.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdContacts2.size() != 0) {
					for (int i = 0; i < mListIdContacts2.size(); i++) {
						for (int j = 0; j < mListContactResults.size(); j++) {
							if (mListIdContacts2.get(i).equals(
									mListContactResults.get(j).getIdContact())) {
								mListContactResults.get(j).setSelected(true);
							} else {
								mListContactResults.get(j).setSelected(false);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
			return null;

		}

		@Override
		protected void onPostExecute(List<ContactObject> result) {
			// Setting the cursor containing contacts to listview
			mDialogEmailContact = new DialogPickSingleEmailContact(
					MyPointShareActivity.this, MyPointShareActivity.this,
					mListContactResults, mFullName, isEditContact);
			mDialogEmailContact.show();
			dismissProgress();
		}
	}

	public class GetListFriendsTask extends
			AsyncTask<Void, Void, MyFriendListEntity[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MyFriendListEntity[] doInBackground(Void... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlListMyFriends(UserObject
								.getInstance(MyPointShareActivity.this).mAccess_token));
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

			if (result == null) {
				dismissProgress();
				if (Utils.isNetworkConnected(MyPointShareActivity.this)) {
					showToastMessage(MyPointShareActivity.this.getResources()
							.getString(R.string.mess_error_server));
				} else {
					showToastMessage(MyPointShareActivity.this.getResources()
							.getString(R.string.mess_error_network));
				}
				if (Server.mStatus != 200) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
				}
				return;
			} else {
				if (mListMyFriends == null) {
					mListMyFriends = new ArrayList<MyFriendListEntity>();
				}
				mListMyFriends.clear();
				for (int i = 0; i < result.length; i++) {
					if (result[i].getStatus().toUpperCase()
							.equals("REGISTERED")) {
						mListMyFriends.add(result[i]);
					}
				}
				// set data for adapter
				mAdapter = new ExpandableListAdapter(MyPointShareActivity.this,
						mListDataHeaders, mListMyFriends, mIndexOfFriend,
						new Process_ExpandableListAdapter() {

							@Override
							public void click_radioButton(int indexOfFriend) {
								mSentType = 2;
								mIndexOfFriend = indexOfFriend;
								mRdbSMS.setChecked(false);
								// Clear data at Email, SMS text box and
								// disabled them
								mRdbEmail.setChecked(false);
								mEdtEmail.setText("");
								mEdtSMS.setText("");
								mEdtEmail.setEnabled(false);
								mEdtSMS.setEnabled(false);
							}
						});
				mExpandableListView.setAdapter(mAdapter);
				// Always set expanded for ExpandableListView
				mExpandableListView.expandGroup(0);
				dismissProgress();
			}

		}

	}

	public class SharePointViaSMSTask extends
			AsyncTask<String, Void, SharePointViaSMSEntity> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SharePointViaSMSEntity doInBackground(String... params) {
			try {
				InputStream inputstream = Server.requestPost(ServerURL
						.getUrlSharePointSMS(), ServerURL
						.getEntitySharePointSMS(MyPointShareActivity.this,
								params[0], mRestaurantId, mPointShared,
								params[1], params[2]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(inputstream)
						.toString();
				SharePointViaSMSEntity response = gson.fromJson(json,
						SharePointViaSMSEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SharePointViaSMSEntity result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyPointShareActivity.this)) {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_network));
					}
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
				if (!result.getUrl().equals("")) {
					// For Unregistered User
					// Add code to send Url to Users via SMS Manager
					try {
						String message = "Congratulations, "
								+ UserObject
										.getInstance(MyPointShareActivity.this).mUserName
								+ " has shared Restaurant Points with you. Just click the link to signup and add the points to your new account! Feel free to contact us with any questions, Thanks MyMenu Support www.mymenu.us email: support@mymenu.us!\n"
								+ result.getUrl();
						SmsManager smsManager = SmsManager.getDefault();
						ArrayList<String> parts = smsManager
								.divideMessage(message);
						smsManager.sendMultipartTextMessage(mListContactResults
								.get(0).getPhoneNumber(), null, parts, null,
								null);
						showToastMessage(getString(R.string.activity_mypoint_share_sharepoint));
						// showToastMessage(getResources()
						// .getString(
						// R.string.activity_mypoint_share_mess_sms_success));
					} catch (Exception e) {
						showToastMessage(getResources()
								.getString(
										R.string.activity_mypoint_share_mess_sms_failed));
						e.printStackTrace();
					}

				} else {
					// For Registered User
					try {

						String message = "Congratulations, "
								+ UserObject
										.getInstance(MyPointShareActivity.this).mUserName
								+ "  has just shared "
								+ mPointShared
								+ " points with you. These points are gifted to your account for you to redeem as you please! Enjoy! Feel free to contact us with any questions, Thanks MyMenu Support www.mymenu.us email: support@mymenu.us!\n"
								+ result.getUrl();
						SmsManager smsManager = SmsManager.getDefault();
						ArrayList<String> parts = smsManager
								.divideMessage(message);
						smsManager.sendMultipartTextMessage(mListContactResults
								.get(0).getPhoneNumber(), null, parts, null,
								null);
						showToastMessage(getString(R.string.activity_mypoint_share_sharepoint));
						// showToastMessage(getResources()
						// .getString(
						// R.string.activity_mypoint_share_mess_sms_success));
					} catch (Exception e) {
						showToastMessage(getResources()
								.getString(
										R.string.activity_mypoint_share_mess_sms_failed));
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();

		}
	}

	public class SharePointViaEmailTask extends
			AsyncTask<String, Void, SharePointViaEmailEntity> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SharePointViaEmailEntity doInBackground(String... params) {
			try {
				InputStream inputstream = Server.requestPost(ServerURL
						.getUrlSharePointEmail(), ServerURL
						.getEntitySharePointEmail(MyPointShareActivity.this,
								params[0], mRestaurantId, mPointShared,
								params[1], params[2]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(inputstream)
						.toString();
				SharePointViaEmailEntity response = gson.fromJson(json,
						SharePointViaEmailEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SharePointViaEmailEntity result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyPointShareActivity.this)) {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_network));
					}
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
				if (result.getStatus().equals("success")) {
					showToastMessage(getString(R.string.activity_mypoint_share_sharepoint));
				} else if (!result.getStatus().equals("")) {
					showToastMessage(result.getStatus());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}

	}

	public class SharePointViaFriendListTask extends
			AsyncTask<Void, Void, SharePointViaFriendListEntity> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SharePointViaFriendListEntity doInBackground(Void... params) {
			try {
				InputStream inputstream = Server.requestPost(ServerURL
						.getUrlSharePointFriendList(), ServerURL
						.getEntitySharePointFriendList(
								MyPointShareActivity.this,
								mListMyFriends.get(mIndexOfFriend).getId(),
								mRestaurantId, mPointShared));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(inputstream)
						.toString();
				SharePointViaFriendListEntity response = gson.fromJson(json,
						SharePointViaFriendListEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SharePointViaFriendListEntity result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyPointShareActivity.this)) {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyPointShareActivity.this
								.getResources().getString(
										R.string.mess_error_network));
					}
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
				if (result.getStatus().equals("success")) {
					showToastMessage(getString(R.string.activity_mypoint_share_sharepoint));
				} else if (!result.getStatus().equals("")) {
					showToastMessage(result.getStatus());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
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
		if (requestCode == CodeRequest.CODE_REQUEST_MYPOINTSHARE) {
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
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYPOINTSHARE);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyPointShareActivity.this,
				MyAccountActivity.class), CodeRequest.CODE_REQUEST_MYPOINTSHARE);
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyPointShareActivity.this,
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
			mDialogMyFeedback = new DialogMyFeedback(MyPointShareActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyPointShareActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyPointShareActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyPointShareActivity.this)) {
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
	public void getListChooseContact(List<ContactObject> mListContact,
			boolean typeContact) {
		if (typeContact) {
			// FOR SMS
			this.mListContactResults.clear();
			mListIdContacts1.clear();
			String disPlayNameContact = "";
			mEdtSMS.setText("");
			this.mListContactResults.addAll(mListContact);
			if (this.mListContactResults.size() != 0) {
				isEditContact = false;
				mListIdContacts1.add(this.mListContactResults.get(0)
						.getIdContact());
				disPlayNameContact += this.mListContactResults.get(0)
						.getFullName();

				if (disPlayNameContact != null) {
					mEdtSMS.setText(disPlayNameContact);
				}
			}
		} else {
			// FOR EMAIL
			this.mListContactResults.clear();
			mListIdContacts2.clear();
			String disPlayEmailContact = "";
			mEdtEmail.setText("");
			this.mListContactResults.addAll(mListContact);
			if (this.mListContactResults.size() != 0) {
				isEditContact = false;
				mListIdContacts2.add(this.mListContactResults.get(0)
						.getIdContact());
				disPlayEmailContact += this.mListContactResults.get(0)
						.getEmail();
			}
			if (disPlayEmailContact != null) {
				mEdtEmail.setText(disPlayEmailContact);
			}
		}
		mListContact.clear();
		mListContact = null;

	}

	@Override
	public void isSaveAddContact(boolean save, boolean typeContact,
			ContactObject co) {
		mFullName = "";
		if (co != null) {
			isEditContact = true;
			mFullName = co.getFullName();
		}
		showProgress();
		LoadContactPhoneTask.execute(getApplicationContext());
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of textBox
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) MyPointShareActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
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
										MyPointShareActivity.this)
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
					if (!Utils.isNetworkConnected(MyPointShareActivity.this)) {
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

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String message = intent.getExtras().getString(
						Constants.KEY_MESSAGE_ACTION);
				if (message.equalsIgnoreCase(Constants.CONNECT_SUCCESS)) {
					if (mIsPause) {
						return;
					}
					if (mIsTypeEmail) {
						if (mDialogEmailContact == null
								|| !mDialogEmailContact.isShowing()) {
							new ListViewContactsEmailLoader().execute();
						}
					} else {
						if (mDialogPhoneContact == null
								|| !mDialogPhoneContact.isShowing()) {
							new ListViewContactsPhoneLoader().execute();
						}
					}
				}
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
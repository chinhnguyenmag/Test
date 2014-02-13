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
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogPickEmailContact;
import com.magrabbit.intelligentmenu.customview.DialogPickPhoneContact;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.listener.PickContactListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
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
 * @Description Share order details by SMS or Email
 */
public class MyOrderShare extends BaseActivity implements
		OptionMenuClickListener, PickContactListener {

	private TextView mTvTitle;
	private TextView mTvTo;
	private EditText mEdtOrderInfo;
	private Button mBtSms;
	private Button mBtEmail;
	private String mToEmail;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;

	// For distinguish sending by SMS or Email
	private int mSentType = -1;

	// Order Message from screen MyOrderDetailsActivity
	private String mOrderMessageSMS;
	private String mOrderMessageEMAIL;
	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;
	private String mFullName;
	private List<ContactObject> mListContactResult;
	private List<String> mListIdContact;
	DialogPickPhoneContact mDialogPhoneContact;
	DialogPickEmailContact mDialogEmailContact;

	boolean mIsTypeEmail = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_test);

		mListIdContact = new ArrayList<String>();
		mListContactResult = new ArrayList<ContactObject>();
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		// Inflate layout elements
		mTvTitle = (TextView) findViewById(R.id.header_title_tv);
		mTvTitle.setText("MyOrder Share");
		mTvTo = (TextView) findViewById(R.id.share_location_friend_edt_to);
		mTvTo.setEnabled(false);
		mEdtOrderInfo = (EditText) findViewById(R.id.share_location_edt_comment);
		mBtSms = (Button) findViewById(R.id.share_location_bt_sms);
		mBtEmail = (Button) findViewById(R.id.share_location_bt_email);

		// Get data - order message from MyOrderDetails
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mOrderMessageSMS = bundle
					.getString(StringExtraUtils.KEY_MYORDER_ORDER_MESSAGE_SMS);
			mOrderMessageEMAIL = bundle
					.getString(StringExtraUtils.KEY_MYORDER_ORDER_MESSAGE_EMAIL);
		}

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Constants.DISPLAY_MESSAGE_ACTION));
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
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	public void onClick_Sms(View v) {
		try {
			// Enable text box TO
			mTvTo.setEnabled(true);
			if (mSentType != 0) {
				mListContactResult.clear();
				mListIdContact.clear();
				mTvTo.setText("");
			}
			mSentType = 0;
			mTvTo.setVisibility(View.VISIBLE);
			showLine();
			// Add Order info
			mEdtOrderInfo.setText(mOrderMessageSMS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Email(View v) {
		try {
			// Enable text box TO
			mTvTo.setEnabled(true);
			if (mSentType != 1) {
				mListContactResult.clear();
				mListIdContact.clear();
				mTvTo.setText("");
			}
			mSentType = 1;
			mTvTo.setVisibility(View.VISIBLE);
			showLine();
			// Add Order info
			mEdtOrderInfo.setText(Html.fromHtml(mOrderMessageEMAIL));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Choose(View v) {
		if (mSentType == 0) {
			// Send SMS
			onClick_LoadPhone(v);
		} else if (mSentType == 1) {
			// Send Email
			onClick_LoadEmail(v);
		}
	}

	public void onClick_Send(View v) {
		switch (mSentType) {
		case 0:// SMS
			try {
				if (mTvTo.getText().toString().equals("")) {
					showToastMessage(getResources().getString(
							R.string.activity_mypoint_share_mess_input_phone));
				} else {

					// Call AsyncTask to share point and
					// send url to
					// users
					TelephonyManager telMgr = (TelephonyManager) getSystemService(MyOrderShare.this.TELEPHONY_SERVICE);
					int simState = telMgr.getSimState();
					switch (simState) {
					case TelephonyManager.SIM_STATE_ABSENT:
						Toast.makeText(
								getApplicationContext(),
								MyOrderShare.this
										.getString(R.string.activity_mypoint_share_mess_sms_no_sim),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
						Toast.makeText(
								getApplicationContext(),
								MyOrderShare.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PIN_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								MyOrderShare.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PUK_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								MyOrderShare.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_READY:
						try {
							// Call SMS manager to send message
							SmsManager smsManager = SmsManager.getDefault();
							ArrayList<String> parts = smsManager
									.divideMessage(mOrderMessageSMS);
							for (int i = 0; i < mListContactResult.size(); i++) {
								smsManager.sendMultipartTextMessage(
										mListContactResult.get(i)
												.getPhoneNumber(), null, parts,
										null, null);
							}
							showToastMessage(getResources()
									.getString(
											R.string.activity_myorder_share_mess_sms_success));
						} catch (Exception e) {
							showToastMessage(getResources()
									.getString(
											R.string.activity_myorder_share_mess_sms_failed));
							e.printStackTrace();
						}
						break;
					case TelephonyManager.SIM_STATE_UNKNOWN:
						Toast.makeText(
								getApplicationContext(),
								MyOrderShare.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1:// Email
				// Compare the input point with the total point of this specific
				// restaurant
			mToEmail = mTvTo.getText().toString();
			if (mToEmail.equals("")) {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.message_error_email_blank),
						Toast.LENGTH_SHORT).show();
			} else {

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL,
						getListEmailContact(mToEmail));
				email.putExtra(Intent.EXTRA_TEXT,
						Html.fromHtml(mOrderMessageEMAIL));
				email.putExtra(
						Intent.EXTRA_SUBJECT,
						MyOrderShare.this
								.getString(R.string.activity_myorder_share_email_subject));
				// need this to prompts email client only
				email.setType("message/rfc822");
				startActivity(Intent
						.createChooser(
								email,
								MyOrderShare.this
										.getString(R.string.activity_myorder_share_email_choose)));
			}

			break;
		default:
			showToastMessage(getString(R.string.invite_choose_social));
			break;
		}

	}

	public void onClick_Cancel(View v) {
		finish();
	}

	public void onClick_LoadPhone(View v) {
		mFullName = "";
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

	public void onClick_LoadEmail(View v) {
		mFullName = "";
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

	/** An AsyncTask class to retrieve and load list view with contacts */
	private class ListViewContactsEmailLoader extends
			AsyncTask<Void, Void, List<ContactObject>> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected List<ContactObject> doInBackground(Void... params) {
			try {
				if (mListContactResult != null) {
					mListContactResult.clear();
				}
				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getEmail() != null
							&& contact.getEmail().length() > 0) {
						mListContactResult.add(contact);
					}
				}

				if (mListIdContact == null || mListIdContact.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdContact.size() != 0) {
					for (int i = 0; i < mListIdContact.size(); i++) {
						for (int j = 0; j < mListContactResult.size(); j++) {
							if (mListIdContact.get(i).equals(
									mListContactResult.get(j).getIdContact())) {
								mListContactResult.get(j).setSelected(true);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

			return mListContactResult;
		}

		@Override
		protected void onPostExecute(List<ContactObject> result) {

			mDialogEmailContact = new DialogPickEmailContact(MyOrderShare.this,
					MyOrderShare.this, mListIdContact, mListContactResult,
					mFullName);
			mDialogEmailContact.show();
			dismissProgress();
		}
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

				if (mListContactResult != null) {
					mListContactResult.clear();
				}
				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getPhoneNumber() != null
							&& contact.getPhoneNumber().length() > 0) {
						mListContactResult.add(contact);
					}
				}

				if (mListIdContact == null || mListIdContact.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdContact.size() != 0) {
					for (int i = 0; i < mListIdContact.size(); i++) {
						for (int j = 0; j < mListContactResult.size(); j++) {
							if (mListIdContact.get(i).equals(
									mListContactResult.get(j).getIdContact())) {
								mListContactResult.get(j).setSelected(true);
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
			// Setting the cursor containing contacts to list view
			try {
				mDialogPhoneContact = new DialogPickPhoneContact(
						MyOrderShare.this, MyOrderShare.this, mListIdContact,
						mListContactResult, mFullName);
				mDialogPhoneContact.show();
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void getListChooseContact(List<ContactObject> mListContact,
			boolean typeContact) {
		if (typeContact) {
			this.mListContactResult.clear();
			mListIdContact.clear();
			String disPlayNameContact = "";
			mTvTo.setText("");
			this.mListContactResult.addAll(mListContact);
			if (this.mListContactResult.size() != 0) {

				for (int i = 0; i < this.mListContactResult.size(); i++) {
					mListIdContact.add(this.mListContactResult.get(i)
							.getIdContact());

					if (this.mListContactResult.get(i).getFullName() != null
							&& !this.mListContactResult.get(i).getFullName()
									.equals("")) {
						disPlayNameContact += this.mListContactResult.get(i)
								.getFullName() + "; ";
					} else {
						disPlayNameContact += this.mListContactResult.get(i)
								.getPhoneNumber() + "; ";
					}

				}
				if (disPlayNameContact != null) {
					mTvTo.setText(disPlayNameContact);
				}
			}
		} else {
			this.mListContactResult.clear();
			mListIdContact.clear();
			String disPlayEmailContact = "";
			mTvTo.setText("");
			this.mListContactResult.addAll(mListContact);
			if (this.mListContactResult.size() != 0) {
				for (int i = 0; i < this.mListContactResult.size(); i++) {
					mListIdContact.add(this.mListContactResult.get(i)
							.getIdContact());
					if (this.mListContactResult.get(i).getFristName() != null
							&& !this.mListContactResult.get(i).getFristName()
									.equals("")
							|| this.mListContactResult.get(i).getLastName() != null
							&& !this.mListContactResult.get(i).getLastName()
									.equals("")) {
						disPlayEmailContact += this.mListContactResult.get(i)
								.getFullName()
								+ " <"
								+ this.mListContactResult.get(i).getEmail()
								+ ">; ";
					} else {
						disPlayEmailContact += this.mListContactResult.get(i)
								.getEmail();
					}

				}
				if (disPlayEmailContact != null) {
					mTvTo.setText(disPlayEmailContact);
				}
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
			mFullName = co.getFullName();
		}
		showProgress();
		LoadContactPhoneTask.execute(getApplicationContext());
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_SHARE);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyOrderShare.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_SHARE);

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
		DialogStatInfo dialog = new DialogStatInfo(MyOrderShare.this, null, "",
				0, true);
		dialog.show();

	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent,
					CodeRequest.CODE_REQUEST_MYORDER_SHARE);
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
			mDialogMyFeedback = new DialogMyFeedback(MyOrderShare.this, true,
					true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils.isNetworkConnected(MyOrderShare.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyOrderShare.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyOrderShare.this)) {
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
								UserObject.getInstance(MyOrderShare.this)
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
					if (!Utils.isNetworkConnected(MyOrderShare.this)) {
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

	private String[] getListEmailContact(String s) {
		int start;
		int end;
		try {
			String[] StrArr = s.split(";");
			for (int i = 0; i < StrArr.length; i++) {
				start = -1;
				end = -1;
				start = StrArr[i].indexOf("<");
				end = StrArr[i].indexOf(">");
				if (start != -1 && end != -1) {
					StrArr[i] = StrArr[i].substring(start - 1, end);
				}
			}
			return StrArr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showLine() {
		switch (mSentType) {
		case 0:
			mBtSms.setBackgroundResource(R.drawable.sms);
			mBtEmail.setBackgroundResource(R.drawable.ic_share_email);
			break;
		case 1:
			mBtSms.setBackgroundResource(R.drawable.ic_share_sms);
			mBtEmail.setBackgroundResource(R.drawable.email);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYORDER_SHARE) {
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
		}
	}

}

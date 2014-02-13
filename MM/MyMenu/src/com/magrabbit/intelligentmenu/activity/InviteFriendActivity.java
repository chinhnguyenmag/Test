package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.plus.PlusShare;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogPickSingleEmailContact;
import com.magrabbit.intelligentmenu.customview.DialogPickSinglePhoneContact;
import com.magrabbit.intelligentmenu.listener.ContactMailListener;
import com.magrabbit.intelligentmenu.listener.ContactPhoneListener;
import com.magrabbit.intelligentmenu.listener.PickContactListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
import com.magrabbit.intelligentmenu.model.InviteFriendViaEmailEntity;
import com.magrabbit.intelligentmenu.model.InviteFriendViaSMSEntity;
import com.magrabbit.intelligentmenu.model.UserEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.social.facebook.AsyncFacebookRunner;
import com.magrabbit.intelligentmenu.social.facebook.BaseRequestListener;
import com.magrabbit.intelligentmenu.social.facebook.DialogError;
import com.magrabbit.intelligentmenu.social.facebook.Facebook;
import com.magrabbit.intelligentmenu.social.facebook.Facebook.DialogListener;
import com.magrabbit.intelligentmenu.social.facebook.FacebookError;
import com.magrabbit.intelligentmenu.social.facebook.FacebookInfoTask;
import com.magrabbit.intelligentmenu.social.facebook.FacebookListener;
import com.magrabbit.intelligentmenu.social.facebook.SessionStore;
import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.social.twitter.TwitterDialogListener;
import com.magrabbit.intelligentmenu.task.LoadContactPhoneTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Hung Hoang and Chinh Duc. Invite Friend use app from facebook, google
 *         plus, sms, email.
 */
public class InviteFriendActivity extends BaseActivity implements
		FacebookListener, TwitterDialogListener, PickContactListener,
		ContactPhoneListener, ContactMailListener {
	private TextView mTvTo;
	private EditText mEdtComment;
	private TextView mTvTitle;
	private int mTypeShare = -1;
	private Facebook mFacebook = null;
	private TwittSharing mTwitter;
	private Button mBtGoogle, mBtFacebook, mBtTwitter, mBtSms, mBtEmail;
	private List<String> mListIdPhoneCotact;
	private List<String> mListIdEmailCotact;
	private List<ContactObject> mListContactPhone;
	private List<ContactObject> mListContactEmail;
	private Activity mActivity;
	private String urlInviteFriend;
	private String urlInvite;
	private String mFullName;
	private boolean isEditcontact;
	DialogPickSinglePhoneContact mDialogPhoneContact;
	DialogPickSingleEmailContact mDialogEmailContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_friend);

		this.mActivity = this;
		mListIdPhoneCotact = new ArrayList<String>();
		mListIdEmailCotact = new ArrayList<String>();
		mListContactPhone = new ArrayList<ContactObject>();
		mListContactEmail = new ArrayList<ContactObject>();
		mTvTitle = (TextView) findViewById(R.id.header_title_tv);
		mTvTitle.setText(getString(R.string.invite_friend_title));
		mTvTo = (TextView) findViewById(R.id.activity_invite_friend_edt_to);
		mTvTo.setVisibility(View.GONE);
		mEdtComment = (EditText) findViewById(R.id.activity_invite_friend_edt_comment);
		urlInvite = ServerURL.URL + "/invite-social/"
				+ UserObject.getInstance(this).getUserID() + "/"
				+ UserObject.getInstance(this).getUserName();
		mEdtComment.setText("Please join in MyMenu " + urlInvite);

		mBtGoogle = (Button) findViewById(R.id.activity_invite_bt_google);
		mBtFacebook = (Button) findViewById(R.id.activity_invite_bt_facebook);
		mBtTwitter = (Button) findViewById(R.id.activity_invite_bt_twitter);
		mBtSms = (Button) findViewById(R.id.activity_invite_bt_sms);
		mBtEmail = (Button) findViewById(R.id.activity_invite_bt_email);
		mTvTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_Choose();

			}
		});

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Constants.DISPLAY_MESSAGE_ACTION));
	}

	@Override
	public void onBackPressed() {
		setResult(CodeResult.CODE_RESULT_MYINVITE_FRIEND);
		super.onBackPressed();
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
		try {
			if (mListContactEmail != null) {
				mListContactEmail.clear();
				mListContactEmail = null;
			}
			if (mListContactPhone != null) {
				mListContactPhone.clear();
				mListContactPhone = null;
			}
			if (mListIdPhoneCotact != null) {
				mListIdPhoneCotact.clear();
				mListIdPhoneCotact = null;
			}
			if (mListIdEmailCotact != null) {
				mListIdEmailCotact.clear();
				mListIdEmailCotact = null;
			}

			for (ContactObject contact : ContactObject.getListContact()) {
				contact.setSelected(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Cancel(View v) {
		try {
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Send info for friend from social, sms, email.
	 */
	public void onClick_Send(View v) {
		try {
			switch (mTypeShare) {
			case 1:// google
				Intent shareIntent = new PlusShare.Builder(this)
						.setType("text/plain")
						.setText(mEdtComment.getText().toString().trim())
						.getIntent();

				startActivityForResult(shareIntent, 0);
				break;
			case 2:// facebook
				try {
					if (Utils.isNetworkConnected(this)) {
						if (FaceBookAccount.getInstance(this).checkHasToken(
								this)) {
							if (FaceBookAccount.getInstance(this).getFacebook() == null) {
								mFacebook = new Facebook(
										SocialUtil.FACEBOOK_APPID);
							}
							postFacebook();
						} else {
							mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
							SocialUtil.loginFacebook(this, mFacebook,
									new FbLoginDialogListener(), this);
						}

					} else {
						showToastMessage(getString(R.string.mess_error_network));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:// twitter
				if (Utils.isNetworkConnected(this)) {
					mTwitter = new TwittSharing(InviteFriendActivity.this,
							InviteFriendActivity.this,
							SocialUtil.TWITTER_CONSUMER_KEY,
							SocialUtil.TWITTER_CONSUMER_SECRET);
					if (mTwitter.mTwitter.hasAccessToken()) {
						mTwitter.shareToTwitterFile(mEdtComment.getText()
								.toString().trim(), null);
					}
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
				break;
			case 4:// sms
				if (mTvTo.getText().toString().equalsIgnoreCase("")) {

					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_valid_phone_number_blank),
							Toast.LENGTH_SHORT).show();
				} else if (mListContactPhone.get(0).getPhoneNumber().length() < 10) {
					Toast.makeText(mActivity,
							getString(R.string.mess_valid_phone_number),
							Toast.LENGTH_SHORT).show();
				} else {
					String content[] = new String[3];
					content[0] = Utils.formatPhoneNumber(mListContactPhone.get(
							0).getPhoneNumber());
					content[1] = Utils.removeSpecial(mListContactPhone.get(0)
							.getFristName());
					content[2] = Utils.removeSpecial(mListContactPhone.get(0)
							.getLastName());

					@SuppressWarnings("static-access")
					TelephonyManager telMgr = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
					int simState = telMgr.getSimState();
					switch (simState) {
					case TelephonyManager.SIM_STATE_ABSENT:
						Toast.makeText(
								getApplicationContext(),
								InviteFriendActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_no_sim),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
						Toast.makeText(
								getApplicationContext(),
								getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PIN_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								InviteFriendActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PUK_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								InviteFriendActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_READY:
						new InviteFriendSMSTask().execute(content);
						break;
					case TelephonyManager.SIM_STATE_UNKNOWN:
						Toast.makeText(
								getApplicationContext(),
								InviteFriendActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					}

				}
				break;
			case 5:// email
				String to = mTvTo.getText().toString();
				if (to.equals("")) {

					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.message_error_email_blank),
							Toast.LENGTH_SHORT).show();
				} else if (!Utils.isValidEmail(mListContactEmail.get(0)
						.getEmail())) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.message_error_email),
							Toast.LENGTH_SHORT).show();
				} else {
					String content[] = new String[3];
					content[0] = mListContactEmail.get(0).getEmail();
					content[1] = Utils.removeSpecial(mListContactEmail.get(0)
							.getFristName());
					content[2] = Utils.removeSpecial(mListContactEmail.get(0)
							.getLastName());
					new InviteFriendEmailTask().execute(content);
				}
				break;
			default:
				showToastMessage(getString(R.string.invite_choose_social));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Google(View v) {
		mEdtComment.setText("Please join in MyMenu " + urlInvite);
		try {
			mTypeShare = 1;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Facebook(View v) {
		mEdtComment.setText("Please join in MyMenu " + urlInvite);
		try {
			mTypeShare = 2;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Twitter(View v) {
		mEdtComment.setText("Please join in MyMenu " + urlInvite);
		try {
			mTypeShare = 3;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Sms(View v) {
		mEdtComment
				.setText(UserObject.getInstance(InviteFriendActivity.this)
						.getUserName()
						+ " would like you to join MyMenu! Sign up today to earn points and simplify your dining experience!. Feel free to contact us with any questions, Thanks MyMenu Support www.mymenu.us email: support@mymenu.us");
		try {
			if (mTypeShare != 4) {
				mListContactPhone.clear();
				mListIdPhoneCotact.clear();
				mTvTo.setText("");
			}
			mTypeShare = 4;
			mTvTo.setVisibility(View.VISIBLE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Email(View v) {
		mEdtComment
				.setText(UserObject.getInstance(InviteFriendActivity.this)
						.getUserName()
						+ " would like you to join MyMenu! Sign up today to earn points and simplify your dining experience!. Feel free to contact us with any questions, Thanks MyMenu Support www.mymenu.us email: support@mymenu.us");
		try {
			if (mTypeShare != 5) {
				mListContactEmail.clear();
				mListIdEmailCotact.clear();
				mTvTo.setText("");
			}
			mTypeShare = 5;
			mTvTo.setVisibility(View.VISIBLE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Choose() {
		mFullName = "";
		if (mTypeShare == 4) {
			isEditcontact = false;
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

		} else if (mTypeShare == 5) {
			isEditcontact = false;
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
	}

	public void postFacebook() {
		try {
			showProgress();
			FaceBookAccount.getInstance(getApplicationContext()).setFacebook(
					mFacebook);

			Bundle params = new Bundle();
			params.putString("message", mEdtComment.getText().toString());
			AsyncFacebookRunner asy = new AsyncFacebookRunner(mFacebook);
			asy.request("me/feed", params, "POST", new FacebookPostListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class LoginTask extends AsyncTask<String, Void, UserEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected UserEntity doInBackground(String... params) {
			try {
				//
				// InputStream source = Server.requestPost(
				// ServerURL.getUrlLogin(),
				// ServerURL.getEntityLogin(mEdtUserName.getText()
				// .toString().trim(), mEdtPassword.getText()
				// .toString().trim(), mIsEmail));
				// Gson gson = new Gson();
				//
				// String json = Server.inputStreamToString(source).toString();
				// UserEntity response = gson.fromJson(json, UserEntity.class);

				// return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(UserEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
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
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(result.mError);
					dismissProgress();
				} else {
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}
	}

	public class FacebookPostListener extends BaseRequestListener {

		@Override
		public void onComplete(String response) {
			dismissProgress();
			showToast(InviteFriendActivity.this
					.getString(R.string.invite_social));
		}

		public void onFacebookError(FacebookError error) {
			showToast(InviteFriendActivity.this
					.getString(R.string.invite_error));
			dismissProgress();
		}

	}

	@Override
	public void facebookLoginSuccess(FaceBookAccount facebookAccount) {
		postFacebook();
	}

	@Override
	public void facebookLoginError() {
		showToastMessage(getString(R.string.login_error));
	}

	@Override
	public void facebookLoginFail() {
		postFacebook();
	}

	private class FbLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			try {
				showProgress(R.string.mess_waitting);
				SessionStore.save(mFacebook, InviteFriendActivity.this);
				FacebookInfoTask.execute(InviteFriendActivity.this, mFacebook,
						InviteFriendActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			showToastMessage(getString(R.string.login_error));
		}

		public void onError(DialogError error) {
			dismissProgress();
			showToastMessage(getString(R.string.login_error));
		}

		public void onCancel() {
			// showToastMessage("Cancel");
			dismissProgress();
		}
	}

	// Twitter
	@Override
	public void onComplete(String value) {
		mTwitter.shareToTwitterFile(mEdtComment.getText().toString().trim(),
				null);

	}

	void showToast(final String msg) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

			}
		});

	}

	@Override
	public void onError(String value) {

	}

	public void showLine() {
		switch (mTypeShare) {
		case 1:
			mBtGoogle.setBackgroundResource(R.drawable.gplus);
			mBtFacebook.setBackgroundResource(R.drawable.ic_share_fb);
			mBtTwitter.setBackgroundResource(R.drawable.ic_share_twitter);
			mBtSms.setBackgroundResource(R.drawable.ic_share_sms);
			mBtEmail.setBackgroundResource(R.drawable.ic_share_email);
			break;
		case 2:
			mBtGoogle.setBackgroundResource(R.drawable.ic_share_gplus);
			mBtFacebook.setBackgroundResource(R.drawable.fb);
			mBtTwitter.setBackgroundResource(R.drawable.ic_share_twitter);
			mBtSms.setBackgroundResource(R.drawable.ic_share_sms);
			mBtEmail.setBackgroundResource(R.drawable.ic_share_email);
			break;
		case 3:
			mBtGoogle.setBackgroundResource(R.drawable.ic_share_gplus);
			mBtFacebook.setBackgroundResource(R.drawable.ic_share_fb);
			mBtTwitter.setBackgroundResource(R.drawable.twitter);
			mBtSms.setBackgroundResource(R.drawable.ic_share_sms);
			mBtEmail.setBackgroundResource(R.drawable.ic_share_email);
			break;
		case 4:
			mBtGoogle.setBackgroundResource(R.drawable.ic_share_gplus);
			mBtFacebook.setBackgroundResource(R.drawable.ic_share_fb);
			mBtTwitter.setBackgroundResource(R.drawable.ic_share_twitter);
			mBtSms.setBackgroundResource(R.drawable.sms);
			mBtEmail.setBackgroundResource(R.drawable.ic_share_email);
			break;
		case 5:
			mBtGoogle.setBackgroundResource(R.drawable.ic_share_gplus);
			mBtFacebook.setBackgroundResource(R.drawable.ic_share_fb);
			mBtTwitter.setBackgroundResource(R.drawable.ic_share_twitter);
			mBtSms.setBackgroundResource(R.drawable.ic_share_sms);
			mBtEmail.setBackgroundResource(R.drawable.email);
			break;
		default:
			break;
		}
	}

	@Override
	public void getListChooseContact(List<ContactObject> mListContact,
			boolean typeContact) {
		try {

			for (ContactObject contactObject : mListContact) {
				for (ContactObject contact : ContactObject.getListContact()) {
					if (contact.getIdContact().equalsIgnoreCase(
							contactObject.getIdContact())) {
						contact.setSelected(contactObject.isSelected());
					} else {
						contact.setSelected(false);
					}
				}
			}

			if (typeContact) {
				this.mListContactPhone.clear();
				mListIdPhoneCotact.clear();
				String disPlayNameContact = "";
				mTvTo.setText("");
				this.mListContactPhone.addAll(mListContact);
				if (this.mListContactPhone.size() != 0) {
					isEditcontact = false;
					mListIdPhoneCotact.add(this.mListContactPhone.get(0)
							.getIdContact());
					if (this.mListContactPhone.get(0).getFristName() != null
							&& !this.mListContactPhone.get(0).getFristName()
									.equals("")
							|| this.mListContactPhone.get(0).getLastName() != null
							&& !this.mListContactPhone.get(0).getLastName()
									.equals("")) {
						disPlayNameContact = this.mListContactPhone.get(0)
								.getFullName();
					} else {
						disPlayNameContact = this.mListContactPhone.get(0)
								.getPhoneNumber();
					}
					if (disPlayNameContact != null) {
						mTvTo.setText(disPlayNameContact);
					}
				}
			} else {
				this.mListContactEmail.clear();
				mListIdEmailCotact.clear();
				String disPlayEmailContact = "";
				mTvTo.setText("");
				this.mListContactEmail.addAll(mListContact);
				if (this.mListContactEmail.size() != 0) {
					isEditcontact = false;
					mListIdEmailCotact.add(this.mListContactEmail.get(0)
							.getIdContact());
					if (this.mListContactEmail.get(0).getFristName() != null
							&& !this.mListContactEmail.get(0).getFristName()
									.equals("")
							|| this.mListContactEmail.get(0).getLastName() != null
							&& !this.mListContactEmail.get(0).getLastName()
									.equals("")) {
						disPlayEmailContact = this.mListContactEmail.get(0)
								.getFullName()
								+ " <"
								+ this.mListContactEmail.get(0).getEmail()
								+ ">";
					} else {
						disPlayEmailContact = this.mListContactEmail.get(0)
								.getEmail();
					}
					if (disPlayEmailContact != null) {
						mTvTo.setText(disPlayEmailContact);
					}
				}
			}
			mListContact.clear();
			mListContact = null;
		} catch (Exception e) {
			e.printStackTrace();
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
				if (mListContactEmail != null) {
					mListContactEmail.clear();
				}
				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getEmail() != null
							&& contact.getEmail().length() > 0) {
						mListContactEmail.add(contact);
					}
				}
				// mListContactEmail.addAll(ContactObject.getListContact());
				if (mListIdEmailCotact == null
						|| mListIdEmailCotact.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdEmailCotact.size() != 0) {
					for (int i = 0; i < mListIdEmailCotact.size(); i++) {
						for (int j = 0; j < mListContactPhone.size(); j++) {
							if (mListIdEmailCotact.get(i).equals(
									mListContactEmail.get(j).getIdContact())) {
								mListContactEmail.get(j).setSelected(true);
							} else {
								mListContactEmail.get(j).setSelected(false);
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
					InviteFriendActivity.this, InviteFriendActivity.this,
					mListContactEmail, mFullName, isEditcontact);
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
				if (mListContactPhone != null) {
					mListContactPhone.clear();
				}

				for (int i = 0, size = ContactObject.getListContact().size(); i < size; i++) {
					ContactObject contact = ContactObject.getListContact().get(
							i);
					if (contact.getPhoneNumber() != null
							&& contact.getPhoneNumber().length() > 0) {
						mListContactPhone.add(contact);
					}
				}

				if (mListIdPhoneCotact == null
						|| mListIdPhoneCotact.size() == 0) {
					for (int i = 0, size = ContactObject.getListContact()
							.size(); i < size; i++) {
						ContactObject.getListContact().get(i)
								.setSelected(false);
					}
				} else if (mListIdPhoneCotact.size() > 0) {
					for (int i = 0; i < mListIdPhoneCotact.size(); i++) {
						for (int j = 0; j < mListContactPhone.size(); j++) {
							if (mListIdPhoneCotact.get(i).equals(
									mListContactPhone.get(j).getIdContact())) {
								mListContactPhone.get(j).setSelected(true);
							} else {
								mListContactPhone.get(j).setSelected(false);
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
					InviteFriendActivity.this, InviteFriendActivity.this,
					mListContactPhone, mFullName, isEditcontact);
			mDialogPhoneContact.show();
			dismissProgress();
		}
	}

	@Override
	public void isSaveAddContact(boolean save, boolean typeContact,
			ContactObject co) {
		try {
			mFullName = "";
			if (co != null) {
				isEditcontact = true;
				mFullName = co.getFullName();
			}
			showProgress();
			LoadContactPhoneTask.execute(getApplicationContext());
			// if (save && typeContact == false) {
			// new ListViewContactsEmailLoader().execute();
			// } else if (save && typeContact == true) {
			// new ListViewContactsPhoneLoader().execute();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class InviteFriendSMSTask extends
			AsyncTask<String, Void, InviteFriendViaSMSEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected InviteFriendViaSMSEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestPost(ServerURL
						.getUrlInviteFriendSMS(), ServerURL
						.getEntityInviteFriendSMS(InviteFriendActivity.this,
								params[0], params[1], params[2]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				InviteFriendViaSMSEntity response = gson.fromJson(json,
						InviteFriendViaSMSEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}

			return null;
		}

		@Override
		protected void onPostExecute(InviteFriendViaSMSEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(InviteFriendActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(InviteFriendActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					}
					dismissProgress();
					return;
				} else if ((result.mError != null && result.mError.length() > 0 && result.mError
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
				} else if (result.mError != null) {
					Toast.makeText(InviteFriendActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mStatus != null) {
					Toast.makeText(InviteFriendActivity.this,
							result.mStatus.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mUrl != null) {
					try {
						String sms = mEdtComment.getText().toString() + "\n"
								+ result.mUrl;
						SmsManager smsManager = SmsManager.getDefault();
						ArrayList<String> parts = smsManager.divideMessage(sms);
						if (mListContactPhone.size() != 0) {
							smsManager.sendMultipartTextMessage(
									mListContactPhone.get(0).getPhoneNumber(),
									null, parts, null, null);
							Toast.makeText(getApplicationContext(),
									getString(R.string.invite_friend_sucess),
									Toast.LENGTH_SHORT).show();
							dismissProgress();
						}

					} catch (Exception e) {
						Toast.makeText(
								getApplicationContext(),
								getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						dismissProgress();
					}
				} else {
					Toast.makeText(InviteFriendActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					return;
				}
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}

		}
	}

	public class InviteFriendEmailTask extends
			AsyncTask<String, Void, InviteFriendViaEmailEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected InviteFriendViaEmailEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestPost(ServerURL
						.getUrlInviteFriendEmail(), ServerURL
						.getEntityInviteFrienEmail(InviteFriendActivity.this,
								params[0], params[1], params[2]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				InviteFriendViaEmailEntity response = gson.fromJson(json,
						InviteFriendViaEmailEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}

			return null;
		}

		@Override
		protected void onPostExecute(InviteFriendViaEmailEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(InviteFriendActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {

						showToastMessage(InviteFriendActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					}
					dismissProgress();
					return;
				} else if ((result.mError != null && result.mError.length() > 0 && result.mError
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
				} else if (result.mError != null) {
					Toast.makeText(InviteFriendActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();

				} else if (result.mStatus.equals(Constants.CONNECT_SUCCESS)) {
					Toast.makeText(InviteFriendActivity.this,
							getString(R.string.invite_friend_sucess),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Toast.makeText(InviteFriendActivity.this,
							result.mStatus.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					return;
				}
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}
		}
	}

	@Override
	public void loadContactFinish(List<ContactObject> mListContact) {
		if (mListContact == null) {
			dismissProgress();
		} else {
			if (mListIdPhoneCotact.size() != 0) {
				for (int i = 0; i < mListIdPhoneCotact.size(); i++) {
					for (int j = 0; j < mListContact.size(); j++) {
						if (mListIdPhoneCotact.get(i).equals(
								mListContact.get(j).getIdContact())) {
							mListContact.get(j).setSelected(true);
						}
					}
				}
			}
			DialogPickSinglePhoneContact dialog = new DialogPickSinglePhoneContact(
					InviteFriendActivity.this, InviteFriendActivity.this,
					mListContact, mFullName, isEditcontact);
			dialog.show();
			dismissProgress();
		}
	}

	@Override
	public void loadContactError() {
		dismissProgress();
	}

	@Override
	public void loadContactMailFinish(List<ContactObject> mListContactMail) {
		if (mListIdEmailCotact != null && mListIdEmailCotact.size() != 0) {
			for (int i = 0; i < mListIdEmailCotact.size(); i++) {
				for (int j = 0; j < mListContactMail.size(); j++) {
					if (mListIdEmailCotact.get(i).equals(
							mListContactMail.get(j).getIdContact())) {
						mListContactMail.get(j).setSelected(true);
					} else {
						mListContactMail.get(j).setSelected(false);
					}
				}
			}
		}
		DialogPickSingleEmailContact dialog = new DialogPickSingleEmailContact(
				InviteFriendActivity.this, InviteFriendActivity.this,
				mListContactMail, mFullName, isEditcontact);
		dialog.show();
		dismissProgress();
	}

	@Override
	public void loadContactMailError() {
		dismissProgress();
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
					if (mTypeShare == 4) {
						if (mDialogPhoneContact == null
								|| !mDialogPhoneContact.isShowing()) {
							new ListViewContactsPhoneLoader().execute();
						}
					} else if (mTypeShare == 5) {
						if (mDialogEmailContact == null
								|| !mDialogEmailContact.isShowing()) {
							new ListViewContactsEmailLoader().execute();
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

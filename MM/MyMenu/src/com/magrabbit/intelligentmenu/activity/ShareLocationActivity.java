package com.magrabbit.intelligentmenu.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.plus.PlusShare;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogPickEmailContact;
import com.magrabbit.intelligentmenu.customview.DialogPickPhoneContact;
import com.magrabbit.intelligentmenu.listener.PickContactListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
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
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

public class ShareLocationActivity extends BaseActivity implements
		FacebookListener, TwitterDialogListener, PickContactListener {
	private TextView mTvTo;
	private EditText mEdtComment;
	private TextView mTvTitle;
	private TextView mTvName;
	private TextView mTvWebsite;
	private ImageView mIvLogo;
	private int mTypeShare = -1;
	private Facebook mFacebook = null;
	private TwittSharing mTwitter;
	private Button mBtGoogle, mBtFacebook, mBtTwitter, mBtSms, mBtEmail;
	private List<String> mListIdContact;
	private List<ContactObject> mListContactResult;
	private Activity mActivity;
	private String mSubject = "";
	private String mName = "";
	private String mWebsite = "";
	private String mLogo = "";
	private Bitmap mBm;
	private File mNewFile;
	private int typeShare = 0;
	private String mNameShare = "share.jpg";
	int limitTwitter;
	String commentTwitter;
	private String mFullName;
	private String mRestaurantName;

	// Decide whether to share Server info or not
	private boolean mIsShareServerInfo = false;

	DialogPickPhoneContact mDialogPhoneContact;
	DialogPickEmailContact mDialogEmailContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		if (null != getIntent().getExtras()) {
			typeShare = getIntent().getExtras().getInt(
					StringExtraUtils.KEY_SHARE_TYPE);
		} else {
			typeShare = 0;
		}

		if (typeShare == 1) {
			mNameShare = "restaurant.jpg";
		} else if (typeShare == 2) {
			mNameShare = "item.jpg";
		}

		this.mActivity = this;
		mListIdContact = new ArrayList<String>();
		mListContactResult = new ArrayList<ContactObject>();
		mTvTitle = (TextView) findViewById(R.id.header_title_tv);
		mTvName = (TextView) findViewById(R.id.share_location_tv_name);
		mTvWebsite = (TextView) findViewById(R.id.share_location_tv_website);
		mTvTo = (TextView) findViewById(R.id.share_location_friend_edt_to);
		mTvTo.setVisibility(View.GONE);
		mEdtComment = (EditText) findViewById(R.id.share_location_edt_comment);
		mBtGoogle = (Button) findViewById(R.id.share_location_bt_google);
		mBtFacebook = (Button) findViewById(R.id.share_location_bt_facebook);
		mBtTwitter = (Button) findViewById(R.id.share_location_bt_twitter);
		mBtSms = (Button) findViewById(R.id.share_location_bt_sms);
		mBtEmail = (Button) findViewById(R.id.share_location_bt_email);
		mIvLogo = (ImageView) findViewById(R.id.share_location_iv_logo);

		// ------------------------- BAONGUYEN -------------------------------//
		// Get Data from MyOrderActivity
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		if (bundle != null) {
			mIsShareServerInfo = bundle
					.getBoolean(StringExtraUtils.KEY_MYORDER_SHARE_SERVER);
			if (mIsShareServerInfo) {
				mNameShare = "server.jpg";
			}
			if (mIsShareServerInfo) {
				mSubject = bundle
						.getString(StringExtraUtils.KEY_MYORDER_SERVER_TITLE);
				mTvTitle.setText(bundle
						.getString(StringExtraUtils.KEY_MYORDER_SERVER_TITLE));
				mLogo = ServerURL.URL
						+ bundle.getString(StringExtraUtils.KEY_MYORDER_SERVER_AVATAR);
				mRestaurantName = bundle
						.getString(StringExtraUtils.KEY_RESTAURANT_NAME);

				// mTvName.setText(ShareLocationActivity.this
				// .getString(R.string.sharelocation_server_title));
				mTvName.setText(mRestaurantName + "");
				;
				// mEdtComment.setText(bundle
				// .getString(StringExtraUtils.KEY_MYORDER_SERVER_INFO));
				mTvWebsite.setText(bundle
						.getString(StringExtraUtils.KEY_MYORDER_SERVER_NAME));
			} else {
				mSubject = bundle
						.getString(StringExtraUtils.KEY_SHARELOCATION_SUBJECT);
				String mTitle = bundle
						.getString(StringExtraUtils.KEY_SHARELOCATION_TITLE);
				mTvTitle.setText(mTitle);
				mName = bundle
						.getString(StringExtraUtils.KEY_SHARELOCATION_NAME);
				mWebsite = bundle
						.getString(StringExtraUtils.KEY_SHARELOCATION_WEBSITE);
				mLogo = ServerURL.URL
						+ bundle.getString(StringExtraUtils.KEY_SHARELOCATION_LOGO);

				mTvName.setText(mName);
				mTvWebsite.setText(mWebsite);
				if (!mWebsite.contains("http")) {
					mWebsite = "http://" + mWebsite;
				}
			}
		}
		// ----------------------------------------------------------------------//
		if (mLogo != null && mLogo.length() > 0) {
			try {
				if (Utils.isNetworkConnected(mActivity)) {
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							100, 100);
					mIvLogo.setLayoutParams(params);
					new DownloadImageTask(mIvLogo).execute(mLogo);
				} else {
					Toast.makeText(mActivity, R.string.mess_error_network,
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
			if (mListContactResult != null) {
				mListContactResult.clear();
				mListContactResult = null;
			}
			if (mListIdContact != null) {
				mListIdContact.clear();
				mListIdContact = null;
			}
			if (mBm != null) {
				mBm.recycle();
				mBm = null;
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

	@SuppressWarnings("static-access")
	public void onClick_Send(View v) {
		try {
			if (!Utils.isNetworkConnected(this)) {
				showToastMessage(getString(R.string.mess_error_network));
				return;
			}
			switch (mTypeShare) {
			case 1:// google
					// Intent shareIntent = new PlusShare.Builder(this)
					// .setText(
					// mEdtComment.getText().toString().trim()
					// + "\n\n" + mName + "\n" + mWebsite)
					// .setType("text/plain").getIntent();
					//
					// startActivityForResult(shareIntent, 0);

				new sendViaGooglePlusTask().execute();

				break;
			case 2:// facebook
				try {
					if (FaceBookAccount.getInstance(this).checkHasToken(this)) {
						if (FaceBookAccount.getInstance(this).getFacebook() == null) {
							mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
						}
						postFacebook();
					} else {
						mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
						SocialUtil.loginFacebook(this, mFacebook,
								new FbLoginDialogListener(), this);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:// twitter
				if ((mEdtComment.getText().toString().trim() + mWebsite)
						.length() > 100) {
					if (mIsShareServerInfo) {
						commentTwitter = (mEdtComment.getText().toString()
								.trim().substring(0,
								(100 - mWebsite.length() - 3)))
								+ "..."
								+ mWebsite
								+ "\nRestaurant name: "
								+ mRestaurantName
								+ "\nServer name: "
								+ mTvWebsite.getText().toString().trim() + "\n";
					} else {
						commentTwitter = (mEdtComment.getText().toString()
								.trim().substring(0,
								(100 - mWebsite.length() - 3)))
								+ "..."
								+ "\n\n" + mName + "\n"
								+ mWebsite;
					}
				} else {
					if (mIsShareServerInfo) {
						commentTwitter = mEdtComment.getText().toString()
								.trim()
								+ "..."
								+ mWebsite
								+ "\nRestaurant name: "
								+ mRestaurantName
								+ "\nServer name: "
								+ mTvWebsite.getText().toString().trim() + "\n";
					} else {
						commentTwitter = mEdtComment.getText().toString()
								.trim()
								+ "..."
								+ "\n\n" + mName + "\n"
								+ mWebsite;
					}
				}

				mTwitter = new TwittSharing(ShareLocationActivity.this,
						ShareLocationActivity.this,
						SocialUtil.TWITTER_CONSUMER_KEY,
						SocialUtil.TWITTER_CONSUMER_SECRET);
				if (mTwitter.mTwitter.hasAccessToken()) {
					mTwitter.shareToTwitterUrl(commentTwitter, mLogo);
				}
				break;
			case 4:// sms

				if (mTvTo.getText().toString().equalsIgnoreCase("")) {
					Toast.makeText(
							getApplicationContext(),
							ShareLocationActivity.this
									.getString(R.string.sharelocation_phone_required),
							Toast.LENGTH_LONG).show();
				} else if (mListContactResult.get(0).getPhoneNumber().length() < 10) {
					Toast.makeText(mActivity,
							getString(R.string.mess_valid_phone_number),
							Toast.LENGTH_SHORT).show();
				} else {
					TelephonyManager telMgr = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
					int simState = telMgr.getSimState();
					switch (simState) {
					case TelephonyManager.SIM_STATE_ABSENT:
						Toast.makeText(
								getApplicationContext(),
								ShareLocationActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_no_sim),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
						Toast.makeText(
								getApplicationContext(),
								ShareLocationActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PIN_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								ShareLocationActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_PUK_REQUIRED:
						Toast.makeText(
								getApplicationContext(),
								ShareLocationActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					case TelephonyManager.SIM_STATE_READY:

						try {
							new sendMMSTask().execute();
						} catch (Exception e) {
							Toast.makeText(
									getApplicationContext(),
									ShareLocationActivity.this
											.getString(R.string.activity_mypoint_share_mess_sms_failed),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
						break;
					case TelephonyManager.SIM_STATE_UNKNOWN:
						Toast.makeText(
								getApplicationContext(),
								ShareLocationActivity.this
										.getString(R.string.activity_mypoint_share_mess_sms_failed),
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
				break;
			case 5:// email
				boolean isValiEmail = true;
				if (mListContactResult.size() != 0) {
					for (int i = 0; i < mListContactResult.size(); i++) {
						if (!Utils.isValidEmail(mListContactResult.get(i)
								.getEmail())) {
							isValiEmail = false;
							break;
						}
					}
				}
				String to = mTvTo.getText().toString();
				String message;
				if (mIsShareServerInfo) {
					message = mEdtComment.getText().toString()
							+ "\n\nRestaurant name: " + mRestaurantName
							+ "\nServer name: "
							+ mTvWebsite.getText().toString().trim();
				} else {
					message = mEdtComment.getText().toString()
							+ "\n\n" + mName + "\n"
							+ mTvWebsite.getText().toString().trim();
				}
				// TODO
				String subject = "";

				if (typeShare == 1) {
					subject = "My Menu - Share Restaurant";
				} else if (typeShare == 2) {
					subject = "My Menu - Share Menu Item";
				}
				if (mIsShareServerInfo) {
					subject = "My Menu - Share Server Information";
				}

				if (to.equals("")) {
					Toast.makeText(
							getApplicationContext(),
							ShareLocationActivity.this
									.getString(R.string.sharelocation_email_required),
							Toast.LENGTH_LONG).show();
				} else if (isValiEmail == false) {

					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.message_error_email),
							Toast.LENGTH_LONG).show();
				} else {
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, getListEmailContact(to));
					// email.putExtra(Intent.EXTRA_CC, new String[]{ to});
					// email.putExtra(Intent.EXTRA_BCC, new String[]{to});
					email.putExtra(Intent.EXTRA_SUBJECT, subject);
					email.putExtra(Intent.EXTRA_TEXT, message);
					// email.putExtra(android.content.Intent.EXTRA_TEXT,
					// Html.fromHtml(html));
					if (mBm != null) {
						try {
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							mBm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
							String pngUri = Environment
									.getExternalStorageDirectory()
									+ File.separator
									+ getString(R.string.app_name)
									+ File.separator + mNameShare;

							mNewFile = new File(pngUri);
							mNewFile.createNewFile();
							// write the bytes in file
							FileOutputStream fo = new FileOutputStream(mNewFile);
							fo.write(bytes.toByteArray());
							// remember close de FileOutput
							fo.close();
							email.putExtra(android.content.Intent.EXTRA_STREAM,
									Uri.fromFile(mNewFile));
						} catch (Exception e) {
							Log.d("error in save image", "-->"
									+ e.getMessage().toString());
						}
					}
					// need this to prompts email client only
					email.setType("message/rfc822");
					startActivity(Intent
							.createChooser(
									email,
									ShareLocationActivity.this
											.getString(R.string.sharelocation_email_choose)));
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
		try {
			mTypeShare = 1;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Facebook(View v) {
		try {
			mTypeShare = 2;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Twitter(View v) {
		try {
			mTypeShare = 3;
			mTvTo.setVisibility(View.GONE);
			showLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Sms(View v) {
		try {
			if (mTypeShare != 4) {
				mListContactResult.clear();
				mListIdContact.clear();
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
		try {
			if (mTypeShare != 5) {
				mListContactResult.clear();
				mListIdContact.clear();
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
		showProgress();
		FaceBookAccount.getInstance(getApplicationContext()).setFacebook(
				mFacebook);

		Bundle params = new Bundle();
		if (mIsShareServerInfo) {
			params.putString("name", "Restaurant name: " + mRestaurantName
					+ "\nServer name: "
					+ mTvWebsite.getText().toString().trim());
		} else {
			params.putString("name",mName);
		}
		params.putString("picture", mLogo);
		params.putString("link", mWebsite);
		params.putString("message", mEdtComment.getText().toString().trim());
		// params.putString("caption", "");
		// params.putString("description",
		// "Just had this fruit salad and love it!!");

		AsyncFacebookRunner asy = new AsyncFacebookRunner(mFacebook);
		asy.request("me/feed", params, "POST", new FacebookPostListener());
	}

	public class sendMMSTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Intent intentMMS = null;
			try {
				intentMMS = new Intent(Intent.ACTION_SEND);
				intentMMS.setType("image/*");

				PackageManager pm = getPackageManager();
				List<ResolveInfo> activityList = pm.queryIntentActivities(
						intentMMS, 0);
				for (final ResolveInfo app : activityList) {
					if ((app.activityInfo.name).contains("Messaging")
							|| (app.activityInfo.name)
									.contains("com.android.mms.ui.ComposeMessageMms")
							|| (app.activityInfo.name)
									.contains("com.android.mms.ui.ComposeMessageActivity")
							|| (app.activityInfo.name)
									.contains("com.android.mms.ui.ConversationComposer")) {
						intentMMS.setComponent(new ComponentName(
								app.activityInfo.applicationInfo.packageName,
								app.activityInfo.name));

						break;
					}
				}

				String numbers = "";
				if (mListContactResult.size() != 0) {
					for (int i = 0; i < mListContactResult.size(); i++) {
						numbers = numbers
								+ mListContactResult.get(i).getPhoneNumber()
								+ ";";
					}
				}
				String message;
				if (mIsShareServerInfo) {
					message = mEdtComment.getText().toString()
							+ "\n\nRestaurant name: " + mRestaurantName
							+ "\nServer name: "
							+ mTvWebsite.getText().toString().trim();
				} else {
					message = mEdtComment.getText().toString()
							+ "\n\n" + mName + "\n"
							+ mTvWebsite.getText().toString().trim();
				}

				intentMMS.putExtra("address", numbers);
				intentMMS.putExtra("sms_body", message);
				if (mIsShareServerInfo) {
					mSubject = "My Menu - Share Server Information";
				}
				intentMMS.putExtra("subject", getString(R.string.app_name)
						+ " - " + mSubject);

				if (mBm != null) {

					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					mBm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					String pngUri = Environment.getExternalStorageDirectory()
							+ File.separator + getString(R.string.app_name)
							+ File.separator + mNameShare;

					mNewFile = new File(pngUri);
					mNewFile.createNewFile();
					// write the bytes in file
					FileOutputStream fo = new FileOutputStream(mNewFile);
					fo.write(bytes.toByteArray());
					// remember close de FileOutput
					fo.close();
					intentMMS.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(mNewFile));

				}

				// intentMMS.setClassName("com.android.mms",
				// "com.android.mms.ui.ComposeMessageActivity");
				startActivity(intentMMS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgress();
		}
	}

	public class sendViaGooglePlusTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (mBm != null) {

					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					mBm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					String pngUri = Environment.getExternalStorageDirectory()
							+ File.separator + getString(R.string.app_name)
							+ File.separator + Utils.genUUID() + mNameShare;

					mNewFile = new File(pngUri);
					mNewFile.createNewFile();
					// write the bytes in file
					FileOutputStream fo = new FileOutputStream(mNewFile);
					fo.write(bytes.toByteArray());
					// remember close de FileOutput
					fo.close();

					PlusShare.Builder shareImage = new PlusShare.Builder(
							ShareLocationActivity.this);
					shareImage.setText(mEdtComment.getText().toString().trim()
							+ "\n\n" + mName + "\n" + mWebsite
							+ "\nRestaurant name: " + mRestaurantName
							+ "\nServer name: "
							+ mTvWebsite.getText().toString().trim());
					if (mIsShareServerInfo) {
						shareImage.setText(mEdtComment.getText().toString()
								.trim()
								+ "\n\n"
								+ mName
								+ "\n"
								+ mWebsite
								+ "\nRestaurant name: "
								+ mRestaurantName
								+ "\nServer name: "
								+ mTvWebsite.getText().toString().trim());
					} else {
						shareImage.setText(mEdtComment.getText().toString()
								.trim()
								+ "\n\n" + mName + "\n" + mWebsite);
					}

					shareImage.addStream(Uri.fromFile(mNewFile));
					shareImage.setType("image/*");
					startActivityForResult(shareImage.getIntent(), 0);

					// Intent shareIntent = new PlusShare.Builder(this)
					// .setText(mEdtComment.getText().toString().trim())
					// .setType("text/plain")
					// .setContentDeepLinkId("1", mName, "description",
					// Uri.parse(mLogo)).getIntent();
					// startActivityForResult(shareIntent, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgress();
		}
	}

	public class FacebookPostListener extends BaseRequestListener {

		@Override
		public void onComplete(String response) {
			dismissProgress();
			showToast(ShareLocationActivity.this
					.getString(R.string.sharelocation_post_shared));
		}

		public void onFacebookError(FacebookError error) {
			showToast(ShareLocationActivity.this
					.getString(R.string.sharelocation_post_shared_error));
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
				SessionStore.save(mFacebook, ShareLocationActivity.this);
				FacebookInfoTask.execute(ShareLocationActivity.this, mFacebook,
						ShareLocationActivity.this);
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
		try {
			mTwitter.shareToTwitterUrl(commentTwitter, mLogo);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				// // Setting the cursor containing contacts to listview
				// if (mListIdContact != null && mListIdContact.size() != 0) {
				// for (int i = 0; i < mListIdContact.size(); i++) {
				// for (int j = 0; j < result.size(); j++) {
				// if (mListIdContact.get(i).equals(
				// result.get(j).getIdContact())) {
				// result.get(j).setSelected(true);
				// }
				// }
				// }
				// }
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

			return mListContactResult;
		}

		@Override
		protected void onPostExecute(List<ContactObject> result) {

			mDialogEmailContact = new DialogPickEmailContact(
					ShareLocationActivity.this, ShareLocationActivity.this,
					mListIdContact, mListContactResult, mFullName);
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
			// Setting the cursor containing contacts to listview
			try {
				mDialogPhoneContact = new DialogPickPhoneContact(
						ShareLocationActivity.this, ShareLocationActivity.this,
						mListIdContact, mListContactResult, mFullName);
				mDialogPhoneContact.show();
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void isSaveAddContact(boolean save, boolean typeContact,
			ContactObject co) {
		try {

			mFullName = "";
			if (co != null) {
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

	private class DownloadImageTask extends AsyncTask<String, Void, Void> {
		ImageView bmImage;
		InputStream in;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected void onPreExecute() {
			bmImage.setImageResource(R.drawable.ic_load_img_150);
		}

		protected Void doInBackground(String... urls) {
			String urldisplay = urls[0];
			try {
				in = new java.net.URL(urldisplay).openStream();
				mBm = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());

				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			try {
				if (mBm == null) {
					bmImage.setImageResource(R.drawable.ic_load_img_150);
				} else {
					bmImage.setImageBitmap(mBm);
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Receiving push messages
	 */
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

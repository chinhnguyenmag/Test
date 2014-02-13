package com.magrabbit.intelligentmenu.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
import com.magrabbit.intelligentmenu.model.GoogleAccount;
import com.magrabbit.intelligentmenu.model.ListOrderComplexPref;
import com.magrabbit.intelligentmenu.model.MyOrderObject;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.preferences.ComplexPreferences;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.social.facebook.Facebook;
import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.task.LoadContactPhoneTask;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;
import com.magrabbit.intelligentmenu.utils.SystemConfigs;
import com.magrabbit.intelligentmenu.utils.Utils;
import com.testflightapp.lib.TestFlight;

public class SplashActivity extends BaseActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {
	private final int STOPSPLASH = 0;
	// time in milliseconds
	private final long SPLASHTIME = 1500;
	private boolean isStop = false;
	private AppPreferences mAppPreferences;
	private ComplexPreferences mComplexPrefenrences;
	private ListOrderComplexPref mMyOrderObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Initialize TestFlight with your app token.
		TestFlight.takeOff(getApplication(), Constants.TESTFLIGHT_TOKEN);

		mComplexPrefenrences = ComplexPreferences.getComplexPreferences(
				getBaseContext(), MODE_PRIVATE);
		mMyOrderObject = mComplexPrefenrences
				.getObject(ListOrderComplexPref.class);
		if (mMyOrderObject == null) {
			mMyOrderObject = new ListOrderComplexPref();
			mMyOrderObject.mListOrder = new ArrayList<MyOrderObject>();
		} else if (mMyOrderObject != null) {
			mComplexPrefenrences.clearData();
		}
		mAppPreferences = new AppPreferences(this);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(displayMetrics);
		SystemConfigs.Instance().setDisplayMetrics(displayMetrics);
		displayMetrics = null;

		Message msg = new Message();
		msg.what = STOPSPLASH;
		try {
			// Load contact on phone
			if ((ContactObject.mLoadStatus == ContactObject.LOAD_ERROR)
					&& ContactObject.getListContact().size() == 0) {
				LoadContactPhoneTask.execute(getApplicationContext());
			}
			splashHandler.sendMessageDelayed(msg, SPLASHTIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		this.isStop = true;
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppPreferences = null;
	}

	/**
	 * Handler for splash screen
	 */
	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {

				if (msg.what == STOPSPLASH) {
					if (!Utils.isNetworkConnected(getApplicationContext())) {
						Toast.makeText(SplashActivity.this,
								getString(R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
						finish();
					} else if (!isStop) {
						UserObject mUserObject = mAppPreferences.getUser();
						if (mUserObject.getAccess_token() == null
								|| mUserObject.getAccess_token().length() <= 0) {
							logout();

						} else {
							new UserObject(mUserObject.getUserID(),
									mUserObject.getUserName(),
									mUserObject.getEmail(),
									mUserObject.getFirstName(),
									mUserObject.getLastName(),
									mUserObject.getZip(),
									mUserObject.getAddress(),
									mUserObject.getCity(),
									mUserObject.getState(),
									mUserObject.getPassword(),
									mUserObject.getPoints(),
									mUserObject.getTypeLogin(),
									mUserObject.getUserStatus(),
									mUserObject.getProfileAvatar(),
									mUserObject.getDefaultProfileId(),
									mUserObject.getAccess_token());
						}

						if (mAppPreferences.getServerURL().length() > 0) {
							ServerURL.URL = mAppPreferences.getServerURL();
						} else {
							mAppPreferences.setServerURL(ServerURL.URL);
						}

						switch (mUserObject.getTypeLogin()) {
						case Constants.LOGIN_VIA_DEFAULT:
							// new LoginTask().execute(new String[] {
							// UserObject.getInstance(SplashActivity.this).getUserID(),
							// UserObject.getInstance(SplashActivity.this).getEmail(),
							// "" });

							startActivity(new Intent(SplashActivity.this,
									MapViewActivity.class));
							finish();
							break;
						case Constants.LOGIN_VIA_FACEBOOK:
							// new LoginTask().execute(new String[] {
							// UserObject.getInstance(SplashActivity.this).getUserID(),
							// UserObject.getInstance(SplashActivity.this).getEmail(),
							// Constants.PROVIDER_FACEBOOK });
							FaceBookAccount
									.getInstance(getApplicationContext())
									.setFacebook(
											new Facebook(
													SocialUtil.FACEBOOK_APPID));

							startActivity(new Intent(SplashActivity.this,
									MapViewActivity.class));
							finish();
							break;
						case Constants.LOGIN_VIA_TWITTER:
							// new LoginTask().execute(new String[] {
							// UserObject.getInstance(SplashActivity.this).getUserID(),
							// UserObject.getInstance(SplashActivity.this).getEmail(),
							// Constants.PROVIDER_TWITTER });
							startActivity(new Intent(SplashActivity.this,
									MapViewActivity.class));
							finish();
							break;
						case Constants.LOGIN_VIA_GOOGLE:
							PlusClient mPlusClient;
							if (GoogleAccount.getInstance(SplashActivity.this)
									.getPlusClient() == null) {
								mPlusClient = new PlusClient.Builder(
										SplashActivity.this,
										SplashActivity.this,
										SplashActivity.this)
										.setVisibleActivities(
												"http://schemas.google.com/BuyActivity")
										.build();
								GoogleAccount.getInstance(SplashActivity.this)
										.setPlusClient(mPlusClient);
							} else {
								mPlusClient = GoogleAccount.getInstance(
										SplashActivity.this).getPlusClient();
							}
							if (!mPlusClient.isConnected()) {
								mPlusClient.connect();
							} else {
								startActivity(new Intent(SplashActivity.this,
										MapViewActivity.class));
								finish();

							}

							break;
						default:
							startActivity(new Intent(SplashActivity.this,
									LoginActivity.class));
							finish();
							break;
						}

						mUserObject = null;
					}
				}
			} catch (Exception e) {
				startActivity(new Intent(SplashActivity.this,
						LoginActivity.class));
				finish();
			}
		}
	};

	public void logout() {
		mAppPreferences.resetdata();
		try {

			if (TwittSharing.getInstance().getTwitter() != null) {
				TwittSharing.getInstance().getTwitter().resetAccessToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FaceBookAccount.getInstance(SplashActivity.this).clearFacebookData(
					SplashActivity.this);
			if (FaceBookAccount.getInstance(SplashActivity.this).getFacebook() != null) {
				FaceBookAccount.getInstance(SplashActivity.this).getFacebook()
						.logout(SplashActivity.this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		startActivity(new Intent(SplashActivity.this, LoginActivity.class));
		finish();
	}

	@Override
	public void onConnected(Bundle arg0) {
		startActivity(new Intent(SplashActivity.this, MapViewActivity.class));
		finish();

	}

	@Override
	public void onDisconnected() {

	}
}

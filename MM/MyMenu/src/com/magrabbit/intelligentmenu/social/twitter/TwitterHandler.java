package com.magrabbit.intelligentmenu.social.twitter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;

public class TwitterHandler {
	public static Twitter twitterObj;
	private final TwitterSession mSession;
	private AccessToken mAccessToken;
	private final CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private final OAuthProvider mHttpOauthprovider;
	private final String mConsumerKey;
	private final String mSecretKey;
	private final ProgressDialog mProgressDlg;
	private TwitterDialogListener mListener;
	private final Activity context;

	public TwitterHandler(Activity context, String consumerKey, String secretKey) {
		this.context = context;

		twitterObj = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);

		String request_url = SocialUtil.TWITTER_REQUEST_URL;
		String access_token_url = SocialUtil.TWITTER_ACCESS_TOKEN_URL;
		String authorize_url = SocialUtil.TWITTER_AUTHORZE_URL;

		mHttpOauthprovider = new DefaultOAuthProvider(request_url,
				access_token_url, authorize_url);
		mAccessToken = mSession.getAccessToken();

		configureToken();
	}

	public void setListener(TwitterDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			twitterObj.setOAuthConsumer(mConsumerKey, mSecretKey);
			twitterObj.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			CookieSyncManager.createInstance(context);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeSessionCookie();
			twitterObj.setOAuthAccessToken(null);
			twitterObj.shutdown();
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public void updateStatus(String status) throws Exception {
		try {
			twitterObj.updateStatus(status);
		} catch (TwitterException e) {
			throw e;
		}
	}

	public void authorize() {
		mProgressDlg.setMessage("Processing ...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider
							.retrieveRequestToken(mHttpOauthConsumer,
									SocialUtil.TWITTER_CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							verifier);

					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = twitterObj.verifyCredentials();
					mSession.storeAccessToken(mAccessToken,
							user.getScreenName(), String.valueOf(user.getId()),
							user.getName(), user.getScreenName()
									+ Constants.SUFFIX_EMAIL_TWITTER,
							user.getOriginalProfileImageURL());
					// UserObject.getInstance(context)
					// .setFirstName(user.getName());
					// UserObject.getInstance(context).setUserName(
					// user.getScreenName());
					// UserObject.getInstance(context).setEmail(
					// user.getScreenName()
					// + Constants.SUFFIX_EMAIL_TWITTER);
					// UserObject.getInstance(context).setUserID(
					// String.valueOf(user.getId()));
					// UserObject.getInstance(context).setProfileAvatar(
					// user.getOriginalProfileImageURL());
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwitterDialogListener listener = new TwitterDialogListener() {

			@Override
			public void onComplete(String value) {

				processToken(value);

			}

			@Override
			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};

}
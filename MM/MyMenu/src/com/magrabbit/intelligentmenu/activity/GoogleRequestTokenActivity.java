package com.magrabbit.intelligentmenu.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.GoogleAccount;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;
import com.magrabbit.intelligentmenu.utils.Utils;

public class GoogleRequestTokenActivity extends BaseActivity {

	private OAuthConsumer consumer;
	private OAuthProvider provider;
	GoogleAccount mGoogleAccount;
	AppPreferences mAppPreferences;
	boolean mIsFinish = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Utils.isICSOrHigher()) {
			StrictMode.enableDefaults();
		}
		super.onCreate(savedInstanceState);
		try {

			consumer = new CommonsHttpOAuthConsumer(
					SocialUtil.GOOGLE_CONSUMER_KEY,
					SocialUtil.GOOGLE_CONSUMER_SECRET);
			provider = new CommonsHttpOAuthProvider(
					SocialUtil.GOOGLE_REQUEST_URL
							+ "?scope="
							+ URLEncoder.encode(SocialUtil.GOOGLE_SCOPE,
									SocialUtil.ENCODING)
							+ "&xoauth_displayname="
							+ SocialUtil.GOOGLE_APP_NAME,
					SocialUtil.GOOGLE_ACCESS_URL,
					SocialUtil.GOOGLE_AUTHORIZE_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		getRequestToken();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mIsFinish) {
			mIsFinish = true;
		} else {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGoogleAccount = null;
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mGoogleAccount = new GoogleAccount(this);
		final Uri uri = intent.getData();
		if (uri != null
				&& uri.getScheme().equals(
						SocialUtil.GOOGLE_OAUTH_CALLBACK_SCHEME)) {
			getAccessToken(uri);
		}
	}

	private void getRequestToken() {
		try {
			String url = provider.retrieveRequestToken(consumer,
					SocialUtil.GOOGLE_OAUTH_CALLBACK_URL);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
					.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
							| Intent.FLAG_FROM_BACKGROUND);
			this.startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getAccessToken(Uri uri) {
		final String oauth_verifier = uri
				.getQueryParameter(OAuth.OAUTH_VERIFIER);
		try {
			provider.retrieveAccessToken(consumer, oauth_verifier);

			String token = consumer.getToken();
			String secret = consumer.getTokenSecret();

			mGoogleAccount.setToken(token);
			mGoogleAccount.setSecret(secret);

			consumer.setTokenWithSecret(token, secret);
			new GetProfileTask().execute();

		} catch (Exception e) {
			// Access Token Retrieval Error
			e.printStackTrace();
			finish();
		}
	}

	private class GetProfileTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected Void doInBackground(Void... params) {
			getProfile();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setResult(RESULT_OK);
			finish();
		}

	}

	private void getProfile() {
		String jsonOutput = "";
		try {
			jsonOutput = makeSecuredReq(SocialUtil.GOOGLE_GET_PROFILE,
					mGoogleAccount.getConsumer());
			JSONObject jsonResponse = new JSONObject(jsonOutput);
			String id = jsonResponse.getString("id");
			String email = jsonResponse.getString("email");
			String firtname = jsonResponse.getString("given_name");
			String lastname = jsonResponse.getString("family_name");
			String picture = jsonResponse.getString("picture");

			UserObject.getInstance(GoogleRequestTokenActivity.this).setUserID(
					id);
			UserObject.getInstance(GoogleRequestTokenActivity.this).setEmail(
					email);
			UserObject.getInstance(GoogleRequestTokenActivity.this)
					.setFirstName(firtname);
			UserObject.getInstance(GoogleRequestTokenActivity.this)
					.setLastName(lastname);
			UserObject.getInstance(GoogleRequestTokenActivity.this)
					.setProfileAvatar(picture);
			UserObject.getInstance(GoogleRequestTokenActivity.this)
					.setTypeLogin(Constants.LOGIN_VIA_GOOGLE);

			mAppPreferences = new AppPreferences(this);
			mAppPreferences.setUser(UserObject
					.getInstance(GoogleRequestTokenActivity.this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String makeSecuredReq(String url, OAuthConsumer consumer)
			throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		consumer.sign(request);
		HttpResponse response = httpclient.execute(request);
		InputStream data = response.getEntity().getContent();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(data));
		String responeLine;
		StringBuilder responseBuilder = new StringBuilder();
		while ((responeLine = bufferedReader.readLine()) != null) {
			responseBuilder.append(responeLine);
		}
		return responseBuilder.toString();
	}
}

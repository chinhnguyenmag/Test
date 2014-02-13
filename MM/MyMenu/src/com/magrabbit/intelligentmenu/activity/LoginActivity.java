package com.magrabbit.intelligentmenu.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogChangeServer;
import com.magrabbit.intelligentmenu.customview.DialogChangeServer.ProcessDialogChangeServer;
import com.magrabbit.intelligentmenu.customview.DialogForgotPass;
import com.magrabbit.intelligentmenu.customview.DialogForgotPass.ProcessDialogForgotPassword;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.AvatarEntity;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
import com.magrabbit.intelligentmenu.model.GoogleAccount;
import com.magrabbit.intelligentmenu.model.SignUpEntity;
import com.magrabbit.intelligentmenu.model.UserEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.social.facebook.DialogError;
import com.magrabbit.intelligentmenu.social.facebook.Facebook;
import com.magrabbit.intelligentmenu.social.facebook.Facebook.DialogListener;
import com.magrabbit.intelligentmenu.social.facebook.FacebookError;
import com.magrabbit.intelligentmenu.social.facebook.FacebookInfoTask;
import com.magrabbit.intelligentmenu.social.facebook.FacebookListener;
import com.magrabbit.intelligentmenu.social.facebook.SessionStore;
import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.social.twitter.TwitterDialogListener;
import com.magrabbit.intelligentmenu.social.twitter.TwitterSession;
import com.magrabbit.intelligentmenu.task.LogoutErrorTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.SocialUtil;
import com.magrabbit.intelligentmenu.utils.Utils;

public class LoginActivity extends BaseActivity implements
		TwitterDialogListener, FacebookListener, ConnectionCallbacks,
		OnConnectionFailedListener, OnAccessRevokedListener {
	private EditText mEdtUserName;
	private EditText mEdtPassword;
	private AppPreferences mAppPreferences;
	private Facebook mFacebook = null;
	boolean mIsEmail = false;
	private Bitmap mBm;
	// private InstagramApp mInstagram = null;
	// The core Google+ client.
	public PlusClient mPlusClient;

	// We can store the connection result from a failed connect()
	// attempt in order to make the application feel a bit more
	// responsive for the user.
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mAppPreferences = new AppPreferences(this);
		mEdtUserName = (EditText) findViewById(R.id.login_edt_username);
		mEdtPassword = (EditText) findViewById(R.id.login_edt_password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_change_server:
			DialogChangeServer dialog = new DialogChangeServer(this,
					new ProcessDialogChangeServer() {

						@Override
						public void click_Save() {

						}
					});
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (mAppPreferences.getServerURL().length() > 0) {
				ServerURL.URL = mAppPreferences.getServerURL();
			} else {
				mAppPreferences.setServerURL(ServerURL.URL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
			logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		try {

			FlurryAgent.onEndSession(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mFacebook = null;
			mAppPreferences = null;
			mPlusClient = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Submit(View v) {
		try {
			String username, password;
			username = mEdtUserName.getText().toString().trim();
			password = mEdtPassword.getText().toString().trim();
			if (validate(username, password)) {
				if (Utils.isNetworkConnected(this)) {
					new LoginTask()
							.execute(new String[] { username, password });
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Forgot(View v) {
		try {
			if (Utils.isNetworkConnected(this)) {
				DialogForgotPass dialog = new DialogForgotPass(this,
						new ProcessDialogForgotPassword() {

							@Override
							public void click_Dialog(boolean result) {

							}
						});
				dialog.show();
			} else {
				showToastMessage(getString(R.string.mess_error_network));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Facebook(View v) {
		try {
			if (Utils.isNetworkConnected(this)) {
				mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
				SocialUtil.loginFacebook(this, mFacebook,
						new FbLoginDialogListener(), this);
			} else {
				showToastMessage(getString(R.string.mess_error_network));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Twitter(View v) {
		try {
			if (Utils.isNetworkConnected(this)) {
				new TwittSharing(this, this, SocialUtil.TWITTER_CONSUMER_KEY,
						SocialUtil.TWITTER_CONSUMER_SECRET);
			} else {
				showToastMessage(getString(R.string.mess_error_network));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Google(View v) {
		try {
			if (Utils.isNetworkConnected(this)) {
				showProgress();
				mPlusClient = new PlusClient.Builder(this, this, this)
						.setVisibleActivities(
								"http://schemas.google.com/BuyActivity")
						.build();
				GoogleAccount.getInstance(this).setPlusClient(mPlusClient);
				mPlusClient.connect();
			} else {
				showToastMessage(getString(R.string.mess_error_network));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void onClick_Instagram(View v) {
	// try {
	// if (Utils.isNetworkConnected(this)) {
	// showProgress(R.string.mess_waitting);
	// mInstagram = new InstagramApp(this,
	// SocialUtil.INSTAGRAM_CLIENT_ID,
	// SocialUtil.INSTAGRAM_CLIENT_SECRET,
	// SocialUtil.INSTAGRAM_CALLBACK_URL);
	// mInstagram.setListener(listener);
	// if (mInstagram.hasAccessToken()) {
	// // mTvName.setText("Id: " + mApp.getId() + "\nUserName: "
	// // + mApp.getUserName() + "\nName: " + mApp.getName());
	// // mTvSummary.setText("Connected");
	// // mBtLogin.setText("Disconnect");
	// startActivity(new Intent(LoginActivity.this,
	// MapViewActivity.class));
	// finish();
	// } else {
	// mInstagram.authorize();
	// }
	// } else {
	// showToastMessage(R.string.mess_error_network);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void onClick_CreateAcc(View v) {

		startActivityForResult(new Intent(this, SignUpActivity.class),
				CodeRequest.CODE_REQUEST_LOGIN);
	}

	public class LoginTask extends AsyncTask<String, Void, UserEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting, this);
		}

		@Override
		protected UserEntity doInBackground(String... params) {
			try {

				InputStream source = Server.requestPost(
						ServerURL.getUrlLogin(),
						ServerURL.getEntityLogin(mEdtUserName.getText()
								.toString().trim(), mEdtPassword.getText()
								.toString().trim(), mIsEmail));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();

				UserEntity response = gson.fromJson(json, UserEntity.class);

				return response;
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
					if (!Utils.isNetworkConnected(LoginActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(LoginActivity.this.getResources()
								.getString(R.string.mess_error_server));
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(result.mError);
					dismissProgress();
				} else {
					UserObject.getInstance(LoginActivity.this).setUserObject(
							result.mUser);
					UserObject.getInstance(LoginActivity.this).setTypeLogin(
							Constants.LOGIN_VIA_DEFAULT);
					UserObject.getInstance(LoginActivity.this).setAccess_token(
							result.mAccessToken);

					mAppPreferences.setUser(UserObject
							.getInstance(LoginActivity.this));

					startActivity(new Intent(LoginActivity.this,
							MapViewActivity.class));
					finish();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}
	}

	public class LoginSocialTask extends AsyncTask<String, Void, UserEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected UserEntity doInBackground(String... params) {
			try {

				InputStream source = Server.requestPost(ServerURL
						.getUrlCheckToken(), ServerURL.getEntityCheckToken(
						UserObject.getInstance(LoginActivity.this).getUserID(),
						UserObject.getInstance(LoginActivity.this)
								.getTypeLogin(),
						UserObject.getInstance(LoginActivity.this)
								.getUserName(),
						UserObject.getInstance(LoginActivity.this).getEmail()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				UserEntity response = gson.fromJson(json, UserEntity.class);

				return response;
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
					if (!Utils.isNetworkConnected(LoginActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(LoginActivity.this.getResources()
								.getString(R.string.mess_error_server));
					}
					logout();
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					if (result.mExist == 0) {
						showToastMessage(getString(R.string.mess_username_already));
						logout();
						dismissProgress();
					} else {
						// Intent intentSignUp = new Intent(LoginActivity.this,
						// SignUpActivity.class);
						// intentSignUp.putExtra(StringExtraUtils.KEY_TYPE_LOGIN,
						// UserObject.getInstance(LoginActivity.this)
						// .getTypeLogin());
						// startActivityForResult(intentSignUp,
						// CodeRequest.CODE_REQUEST_LOGIN);
						new SignUpSocialTask().execute();
					}
				} else {
					 UserObject.getInstance(LoginActivity.this).setUserID(
					 result.mUser.mUserID) ;
					UserObject.getInstance(LoginActivity.this).setEmail(
							result.mUser.mEmail);
					UserObject.getInstance(LoginActivity.this).setUserName(
							result.mUser.mUserName);
					UserObject.getInstance(LoginActivity.this).setFirstName(
							result.mUser.mFirstName);
					UserObject.getInstance(LoginActivity.this).setLastName(
							result.mUser.mLastName);
					UserObject.getInstance(LoginActivity.this).setAddress(
							result.mUser.mAddress);
					UserObject.getInstance(LoginActivity.this).setCity(
							result.mUser.mCity);
					UserObject.getInstance(LoginActivity.this).setState(
							result.mUser.mState);
					UserObject.getInstance(LoginActivity.this).setZip(
							result.mUser.mZip);
					UserObject.getInstance(LoginActivity.this).setPoints(
							result.mUser.mPoints);
					UserObject.getInstance(LoginActivity.this).setUserStatus(
							result.mUser.mUserStatus);
					UserObject.getInstance(LoginActivity.this)
							.setProfileAvatar(result.mUser.mProfileAvatar);

					UserObject.getInstance(LoginActivity.this).setAccess_token(
							result.mAccessToken);

					dismissProgress();
					mAppPreferences.setUser(UserObject
							.getInstance(LoginActivity.this));
					startActivity(new Intent(LoginActivity.this,
							MapViewActivity.class));
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class SignUpSocialTask extends AsyncTask<String, Void, SignUpEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected SignUpEntity doInBackground(String... params) {
			try {
				InputStream source = null;

				switch (UserObject.getInstance(LoginActivity.this)
						.getTypeLogin()) {
				case Constants.LOGIN_VIA_FACEBOOK:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL.getEntitySignUpSocial(UserObject
									.getInstance(LoginActivity.this)
									.getUserID(), Constants.PROVIDER_FACEBOOK,
									UserObject.getInstance(LoginActivity.this)
											.getEmail(), Utils.genUUID()
											.substring(0, 6), UserObject
											.getInstance(LoginActivity.this)
											.getEmail(), UserObject
											.getInstance(LoginActivity.this)
											.getFirstName(), UserObject
											.getInstance(LoginActivity.this)
											.getLastName(), "", ""));
					break;
				case Constants.LOGIN_VIA_GOOGLE:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL.getEntitySignUpSocial(UserObject
									.getInstance(LoginActivity.this)
									.getUserID(), Constants.PROVIDER_GOOGLE,
									UserObject.getInstance(LoginActivity.this)
											.getEmail(), Utils.genUUID()
											.substring(0, 6), UserObject
											.getInstance(LoginActivity.this)
											.getEmail(), UserObject
											.getInstance(LoginActivity.this)
											.getFirstName(), UserObject
											.getInstance(LoginActivity.this)
											.getLastName(), "", ""));
					break;
				case Constants.LOGIN_VIA_TWITTER:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL.getEntitySignUpSocial(UserObject
									.getInstance(LoginActivity.this)
									.getUserID(), Constants.PROVIDER_TWITTER,
									UserObject.getInstance(LoginActivity.this)
											.getUserName(), Utils.genUUID()
											.substring(0, 6), UserObject
											.getInstance(LoginActivity.this)
											.getEmail(), UserObject
											.getInstance(LoginActivity.this)
											.getFirstName(), UserObject
											.getInstance(LoginActivity.this)
											.getLastName(), "", ""));
					break;
				}

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SignUpEntity response = gson.fromJson(json, SignUpEntity.class);

				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SignUpEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(LoginActivity.this)) {
						Toast.makeText(LoginActivity.this,
								R.string.mess_error_network, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(LoginActivity.this,
								R.string.mess_error_server, Toast.LENGTH_SHORT)
								.show();
					}
					logout();
					dismissProgress();
					return;

				}
				if (result.mAccessToken != null
						&& result.mAccessToken.length() > 0) {

					UserObject.getInstance(LoginActivity.this).setAccess_token(
							result.mAccessToken);
					UserObject.getInstance(LoginActivity.this).setUserID(
							result.mId);
					UserObject.getInstance(LoginActivity.this).setCity(
							result.mCity);
					UserObject.getInstance(LoginActivity.this).setState(
							result.mState);
					UserObject.getInstance(LoginActivity.this).setUserStatus(
							result.mDinnerStatus);

					mAppPreferences.setUser(UserObject
							.getInstance(LoginActivity.this));

					if (mBm != null) {
						new ImageUploadTask().execute(result.mAccessToken);
					} else {
						startActivity(new Intent(LoginActivity.this,
								MapViewActivity.class));
						finish();
					}
				} else {
					showToastMessage(LoginActivity.this.getResources()
							.getString(R.string.mess_register_error));

				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
			dismissProgress();
		}
	}

	private class DownloadAvatarTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... param) {
			// TODO Auto-generated method stub
			return downloadBitmap(param[0]);
		}

		@Override
		protected void onPreExecute() {
			Log.i("Async-Example", "onPreExecute Called");
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("Async-Example", "onPostExecute Called");
			mBm = result;
		}

		private Bitmap downloadBitmap(String url) {
			// initilize the default HTTP client object
			final DefaultHttpClient client = new DefaultHttpClient();

			// forming a HttoGet request
			final HttpGet getRequest = new HttpGet(url);
			try {

				HttpResponse response = client.execute(getRequest);

				// check 200 OK for success
				final int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					Log.w("ImageDownloader", "Error " + statusCode
							+ " while retrieving bitmap from " + url);
					return null;

				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream
						inputStream = entity.getContent();

						// decoding stream data back into image Bitmap that
						// android understands
						final Bitmap bitmap = BitmapFactory
								.decodeStream(inputStream);

						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				// You Could provide a more explicit error message for
				// IOException
				getRequest.abort();
				Log.e("ImageDownloader", "Something went wrong while"
						+ " retrieving bitmap from " + url + e.toString());
			}

			return null;
		}
	}

	public class ImageUploadTask extends AsyncTask<String, Void, AvatarEntity> {

		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected AvatarEntity doInBackground(String... params) {
			try {
				HttpEntity resEntity;
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(ServerURL.getUrlAvatar());

				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				mBm.compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, "av_"
						+ UserObject.getInstance(LoginActivity.this)
								.getUserName() + ".jpg");

				entity.addPart("avatar", bab);

				entity.addPart("access_token", new StringBody(params[0]));

				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost,
						localContext);

				resEntity = response.getEntity();
				String response_str = EntityUtils.toString(resEntity);
				Gson gson = new Gson();

				AvatarEntity resAvatar = gson.fromJson(response_str,
						AvatarEntity.class);
				return resAvatar;

			} catch (Exception e) {
				// something went wrong. connection with the server error
			}
			return null;
		}

		@Override
		protected void onPostExecute(AvatarEntity result) {
			if (result == null) {
				if (!Utils.isNetworkConnected(LoginActivity.this)) {
					Toast.makeText(LoginActivity.this,
							R.string.mess_error_network, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(LoginActivity.this,
							R.string.mess_error_avatar, Toast.LENGTH_SHORT)
							.show();
					startActivity(new Intent(LoginActivity.this,
							MapViewActivity.class));
					finish();
				}
				dismissProgress();
				return;
			}
			if ((result.mError != null && result.mError.length() > 0 && result.mError
					.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
					|| (result.mMessage != null && result.mMessage.length() > 0)
					&& result.mMessage
							.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
				dismissProgress();
				Toast.makeText(getApplicationContext(),
						getString(R.string.mess_invalid_accesstoken),
						Toast.LENGTH_LONG).show();
				return;
			} else if (result.mError != null) {
				Toast.makeText(LoginActivity.this, R.string.mess_error_avatar,
						Toast.LENGTH_SHORT).show();
				startActivity(new Intent(LoginActivity.this,
						MapViewActivity.class));
				finish();
				dismissProgress();
			} else if (result.mAvatar != null) {
				UserObject.getInstance(LoginActivity.this).setProfileAvatar(
						result.mAvatar.toString());
				mAppPreferences.setUser(UserObject
						.getInstance(LoginActivity.this));
				startActivity(new Intent(LoginActivity.this,
						MapViewActivity.class));
				finish();
			}
		}

	}

	private boolean validate(String username, String password) {
		if (username.length() == 0) {
			showToastMessage(getString(R.string.mess_input_username));
			return false;
		}

		if (!Utils.isValidUsername(username) && !Utils.isValidEmail(username)) {
			showToastMessage(getString(R.string.login_error_username));
			mEdtUserName.requestFocus();
			return false;
		}

		if (Utils.isValidEmail(username)) {
			mIsEmail = true;
		} else {
			mIsEmail = false;
		}

		if (password.length() == 0) {
			showToastMessage(getString(R.string.mess_input_password));
			return false;
		}

		if (!Utils.isValidPassword(password)) {
			showToastMessage(getString(R.string.login_error_password));
			mEdtPassword.requestFocus();
			return false;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CodeRequest.CODE_REQUEST_LOGIN) {
				startActivity(new Intent(LoginActivity.this,
						MapViewActivity.class));
				finish();
			} else if (requestCode == CodeRequest.CODE_REQUEST_LOGIN_GOOGLE) {
				// If we have a successful result, lets call connect() again. If
				// there are any more errors to resolve we'll get our
				// onConnectionFailed, but if not, we'll get onConnected.
				mPlusClient.connect();
			}

		} else if (resultCode == RESULT_CANCELED) {
			if (requestCode == CodeRequest.CODE_REQUEST_LOGIN_GOOGLE) {
				mPlusClient.disconnect();
				mPlusClient = null;
				GoogleAccount.getInstance(this).setPlusClient(mPlusClient);
			}
			// If we've got an error we can't resolve, we're no
			// longer in the midst of signing in, so we can stop
			// the progress spinner.
			dismissProgress();
		}
	}

	// ============ Facebook ============

	@Override
	public void facebookLoginSuccess(FaceBookAccount facebookAccount) {
		try {
			UserObject.getInstance(LoginActivity.this).setUserID(
					facebookAccount.mUserId);
			UserObject.getInstance(LoginActivity.this).setEmail(
					facebookAccount.mEmail);
			UserObject.getInstance(LoginActivity.this).setUserName(
					facebookAccount.mEmail);
			UserObject.getInstance(LoginActivity.this).setFirstName(
					Utils.removeSpecial(facebookAccount.mFirstName));
			UserObject.getInstance(LoginActivity.this).setLastName(
					Utils.removeSpecial(facebookAccount.mLastName));
			UserObject.getInstance(LoginActivity.this).setProfileAvatar(
					facebookAccount.mPicture);
			UserObject.getInstance(LoginActivity.this).setTypeLogin(
					Constants.LOGIN_VIA_FACEBOOK);
			mAppPreferences.setUser(UserObject.getInstance(LoginActivity.this));

			FaceBookAccount.getInstance(getApplicationContext()).setFacebook(
					mFacebook);

			new LoginSocialTask().execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void facebookLoginError() {
		showToastMessage(getString(R.string.login_error));
		dismissProgress();
	}

	@Override
	public void facebookLoginFail() {
		new DownloadAvatarTask().execute(UserObject.getInstance(
				LoginActivity.this).getProfileAvatar());
		new LoginSocialTask().execute();

	}

	private class FbLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			try {
				showProgress(R.string.mess_waitting);
				SessionStore.save(mFacebook, LoginActivity.this);
				// SocialUtil.getFacebookName(LoginActivity.this, mFacebook,
				// LoginActivity.this);
				FacebookInfoTask.execute(LoginActivity.this, mFacebook,
						LoginActivity.this);
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

	// ============ Twitter =============
	@Override
	public void onComplete(String value) {
		UserObject.getInstance(LoginActivity.this).setTypeLogin(
				Constants.LOGIN_VIA_TWITTER);
		TwitterSession mSession = new TwitterSession(this);
		UserObject.getInstance(LoginActivity.this).setUserID(
				mSession.getUserId());
		UserObject.getInstance(LoginActivity.this).setUserName(
				mSession.getUsername());
		UserObject.getInstance(LoginActivity.this).setFirstName(
				Utils.removeSpecial(mSession.getUserFirstName()));
		UserObject.getInstance(LoginActivity.this).setEmail(
				mSession.getUserEmail());
		UserObject.getInstance(LoginActivity.this).setProfileAvatar(
				mSession.getUserAvatar());
		mAppPreferences.setUser(UserObject.getInstance(LoginActivity.this));
		new DownloadAvatarTask().execute(UserObject.getInstance(
				LoginActivity.this).getProfileAvatar());
		new LoginSocialTask().execute();
	}

	@Override
	public void onError(String value) {
		logout();
		showToastMessage(getString(R.string.login_error));
		dismissProgress();
	}

	// // ============ Instagram ============
	//
	// OAuthAuthenticationListener listener = new OAuthAuthenticationListener()
	// {
	//
	// @Override
	// public void onSuccess() {
	// // mTvName.setText("Id: " + mApp.getId() + "\nUserName: "
	// // + mApp.getUserName() + "\nName: " + mApp.getName());
	// // mTvSummary.setText("Connected");
	// // mLoader.DisplayImage(mApp.getAvatar(), mIvAvatar);
	// // mBtLogin.setText("Disconnect");
	// InstagramSession instagramSession = new InstagramSession(
	// LoginActivity.this);
	// UserObject.getInstance(LoginActivity.this).setUserName(instagramSession.getName());
	// UserObject.getInstance(LoginActivity.this).setUserID(instagramSession.getId());
	// UserObject.getInstance(LoginActivity.this).setProfileAvatar(
	// instagramSession.getAvatar());
	// UserObject.getInstance(LoginActivity.this)
	// .setTypeLogin(Constants.LOGIN_VIA_INSTAGRAM);
	// mAppPreferences.setUser(UserObject.getInstance(LoginActivity.this));
	//
	// startActivity(new Intent(LoginActivity.this, MapViewActivity.class));
	// finish();
	// }
	//
	// @Override
	// public void onFail(String error) {
	// Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
	// .show();
	// dismissProgress();
	// }
	// };

	// ============ Google ===============

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		// Most of the time, the connection will fail with a
		// user resolvable result. We can store that in our
		// mConnectionResult property ready for to be used
		// when the user clicks the sign-in button.
		if (result.hasResolution()) {
			mConnectionResult = result;
			// This is a local helper function that starts
			// the resolution of the problem, which may be
			// showing the user an account chooser or similar.
			startResolution();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (mPlusClient != null) {
			new DownloadAvatarTask().execute(UserObject.getInstance(
					LoginActivity.this).getProfileAvatar());
			new InformationGoogleTask().execute();
		}
	}

	public class InformationGoogleTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// String scope = "oauth2:" + Scopes.PLUS_LOGIN;
			try {
				// String token = GoogleAuthUtil.getToken(LoginActivity.this,
				// mPlusClient.getAccountName(), scope);
				UserObject.getInstance(LoginActivity.this).setUserID(
						mPlusClient.getCurrentPerson().getId());
				UserObject.getInstance(LoginActivity.this).setEmail(
						mPlusClient.getAccountName());
				UserObject.getInstance(LoginActivity.this).setUserName(
						mPlusClient.getAccountName());
				UserObject.getInstance(LoginActivity.this).setFirstName(
						Utils.removeSpecial(mPlusClient.getCurrentPerson()
								.getName().getGivenName()));
				UserObject.getInstance(LoginActivity.this).setLastName(
						Utils.removeSpecial(mPlusClient.getCurrentPerson()
								.getName().getFamilyName()));
				UserObject.getInstance(LoginActivity.this).setProfileAvatar(
						mPlusClient.getCurrentPerson().getImage().getUrl());
				UserObject.getInstance(LoginActivity.this).setTypeLogin(
						Constants.LOGIN_VIA_GOOGLE);
				mAppPreferences.setUser(UserObject
						.getInstance(LoginActivity.this));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgress();
			new DownloadAvatarTask().execute(UserObject.getInstance(
					LoginActivity.this).getProfileAvatar());
			new LoginSocialTask().execute();
		}
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onAccessRevoked(ConnectionResult status) {
		// mPlusClient is now disconnected and access has been revoked.
		// We should now delete any data we need to comply with the
		// developer properties. To reset ourselves to the original state,
		// we should now connect again. We don't have to disconnect as that
		// happens as part of the call.
		mPlusClient.connect();

		// Hide the sign out buttons, show the sign in button.
		// findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
		// findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
		// findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);
	}

	/**
	 * A helper method to flip the mResolveOnFail flag and start the resolution
	 * of the ConnenctionResult from the failed connect() call.
	 */
	private void startResolution() {
		try {
			// If we can resolve the error, then call start resolution
			// and pass it an integer tag we can use to track. This means
			// that when we get the onActivityResult callback we'll know
			// its from being started here.
			mConnectionResult.startResolutionForResult(this,
					CodeRequest.CODE_REQUEST_LOGIN_GOOGLE);
		} catch (SendIntentException e) {
			// Any problems, just try to connect() again so we get a new
			// ConnectionResult.
			mPlusClient.connect();
		}
	}

	public void logout() {
		try {
			LogoutErrorTask.execute(LoginActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

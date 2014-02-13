package com.magrabbit.intelligentmenu.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.CropOptionAdapter;
import com.magrabbit.intelligentmenu.listener.LogoutListener;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.AvatarEntity;
import com.magrabbit.intelligentmenu.model.CropOption;
import com.magrabbit.intelligentmenu.model.SignUpEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.task.LogoutSocialTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Hung Hoang
 * 
 */
public class SignUpActivity extends BaseActivity implements LogoutListener,
		OnAccessRevokedListener {
	private static final int SELECT_FILE = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private ImageView mIvAvatar;
	private EditText mEtFirstName;
	private EditText mEtLastName;
	private EditText mEtEmailAddress;
	private EditText mEtUserName;
	private EditText mEtZip;
	private EditText mEtPassword;
	private EditText mEtConfirmPassword;
	private Dialog mDlPhoto;
	private AppPreferences mAppPreferences;
	/**
	 * Save path of image when chose profile avatar.
	 */
	// Layout Dialog Photo
	private Button mTvTakePhoto;
	private Button mTvChoosePhoto;
	private TextView mTvHeader;
	private Button mBtCancel;
	private int mTypeSocial;
	private Bitmap mBm;
	private Uri selectedImageUri;
	private ActivityInfo mActivityInfo;
	private ComponentName mCn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		mTypeSocial = getIntent().getIntExtra(StringExtraUtils.KEY_TYPE_LOGIN,
				-1);
		mAppPreferences = new AppPreferences(this);
		mIvAvatar = (ImageView) findViewById(R.id.profile_iv_avatar);
		mEtConfirmPassword = (EditText) findViewById(R.id.profile_et_confirm_password);
		mEtEmailAddress = (EditText) findViewById(R.id.profile_et_email);
		mEtFirstName = (EditText) findViewById(R.id.profile_et_first_name);
		mEtLastName = (EditText) findViewById(R.id.profile_et_last_name);
		mEtPassword = (EditText) findViewById(R.id.profile_et_password);
		mEtUserName = (EditText) findViewById(R.id.profile_et_user_name);
		mTvHeader = (TextView) findViewById(R.id.dialog_advanced_search_header_title);
		mTvHeader.setText("Create a New Account");
		mEtZip = (EditText) findViewById(R.id.profile_et_zip);

		InputFilter filterFirstName = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (Character.isSpaceChar(source.charAt(i))) {
						return "";
					}
				}
				return null;
			}
		};
		InputFilter filterLastName = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {

					if (Character.isSpaceChar(source.charAt(i))) {
						return "";
					}
				}

				return null;
			}
		};
		InputFilter filterUserName = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {

					if (Character.isSpaceChar(source.charAt(i))) {
						return "";
					}
				}

				return null;
			}
		};
		mEtFirstName.setFilters(new InputFilter[] { filterFirstName,
				new InputFilter.LengthFilter(30) });
		mEtLastName.setFilters(new InputFilter[] { filterLastName,
				new InputFilter.LengthFilter(30) });
		mEtUserName.setFilters(new InputFilter[] { filterUserName,
				new InputFilter.LengthFilter(30) });
		mEtPassword
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
		mEtConfirmPassword
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });

		if (mTypeSocial != -1) {
			if (mTypeSocial == Constants.LOGIN_VIA_FACEBOOK) {
				mEtEmailAddress.setEnabled(false);
			} else if (mTypeSocial == Constants.LOGIN_VIA_GOOGLE) {
				mEtEmailAddress.setEnabled(false);
			}
			mEtEmailAddress.setText(UserObject.getInstance(SignUpActivity.this)
					.getEmail());
			mEtFirstName.setText(UserObject.getInstance(SignUpActivity.this)
					.getFirstName());
			mEtLastName.setText(UserObject.getInstance(SignUpActivity.this)
					.getLastName());
			mEtZip.setText(String.valueOf(UserObject.getInstance(
					SignUpActivity.this).getZip()));
			try {
				if (Utils.isNetworkConnected(this)) {
					new DownloadAvatarTask().execute(UserObject.getInstance(
							SignUpActivity.this).getProfileAvatar());
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mTypeSocial != -1) {
			logout();
		}
	}

	public void onClick_Back(View v) {
		if (mTypeSocial != -1) {
			logout();
		} else {
			finish();
		}
	}

	/**
	 * @param v
	 *            Pressed by consumer once username & password are entered to
	 *            enter into MyMenu.
	 */
	public void onClick_Submit(View v) {
		if (validate() == true) {
			try {
				if (Utils.isNetworkConnected(this)) {
					new SignUpTask().execute();
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param v
	 *            Will close the ‘Create a New Account’ screen and return to the
	 *            login screen.
	 */
	public void onClick_Cancel(View v) {
		try {
			AlertDialog dialog =

			new AlertDialog.Builder(this)
					.setMessage(
							getResources().getString(
									R.string.profile_dialog_confirm))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							})

					.create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Avatar(View v) {
		selectImage();
	}

	/**
	 * Select photo from camera or library.
	 */
	private void selectImage() {
		try {
			if (mDlPhoto == null) {
				mDlPhoto = new Dialog(this);
				this.getWindow().setBackgroundDrawableResource(
						android.R.color.transparent);
				mDlPhoto.requestWindowFeature(Window.FEATURE_NO_TITLE);
				/** Design the dialog in xml file */
				mDlPhoto.setContentView(R.layout.dialog_custom_photo);
				mTvTakePhoto = (Button) mDlPhoto
						.findViewById(R.id.custom_photo_bt_take_photo);
				mTvChoosePhoto = (Button) mDlPhoto
						.findViewById(R.id.custom_photo_bt_choose);
				mBtCancel = (Button) mDlPhoto
						.findViewById(R.id.custom_photo_bt_cancel);

				mTvTakePhoto.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);

							selectedImageUri = Uri.fromFile(new File(
									Environment.getExternalStorageDirectory(),
									"tmp_avatar_"
											+ String.valueOf(System
													.currentTimeMillis())
											+ ".jpg"));

							intent.putExtra(
									android.provider.MediaStore.EXTRA_OUTPUT,
									selectedImageUri);

							try {
								intent.putExtra("return-data", true);

								startActivityForResult(intent,
										CodeRequest.CODE_REQUEST_CAMERA);
							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
							}
						} catch (IllegalArgumentException e) {
							Log.d("", e.getMessage());
						}
						mDlPhoto.dismiss();
					}
				});

				mTvChoosePhoto.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						boolean isGallery = false;
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);

						PackageManager pm = getPackageManager();
						List<ResolveInfo> activityList = pm
								.queryIntentActivities(intent, 0);

						for (final ResolveInfo app : activityList) {
							if (!isGallery) {
								if ((app.activityInfo.name).contains("Gallery")) {
									isGallery = true;
									mActivityInfo = app.activityInfo;
									break;
								}
							}
						}
						if (isGallery) {
							mCn = new ComponentName(
									mActivityInfo.applicationInfo.packageName,
									mActivityInfo.name);
							intent.setComponent(mCn);
							startActivityForResult(intent, SELECT_FILE);
						} else {
							Intent intent1 = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							intent1.setType("image/*");
							startActivityForResult(Intent.createChooser(
									intent1, "Select File"), SELECT_FILE);
						}
						mDlPhoto.dismiss();

					}
				});
				mBtCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mDlPhoto.dismiss();

					}
				});
			} else if (mDlPhoto != null && mDlPhoto.isShowing()) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDlPhoto.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == CodeRequest.CODE_REQUEST_CAMERA) {
					doCrop();
				} else if (requestCode == SELECT_FILE) {
					selectedImageUri = data.getData();
					doCrop();
				} else if (requestCode == CROP_FROM_CAMERA) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						mIvAvatar.setScaleType(ScaleType.FIT_XY);
						mBm = extras.getParcelable("data");
						mIvAvatar.setImageBitmap(mBm);
					}
					File f = new File(selectedImageUri.getPath());

					if (f.exists())
						f.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate all field for signUp.
	 */
	public boolean validate() {
		boolean mResult = true;
		if (mEtFirstName.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_first_name_blank));
			mEtFirstName.requestFocus();
			mResult = false;
		} else if (!Utils.isValidName(mEtFirstName.getText().toString())) {
			showToastMessage(getString(R.string.message_error_first_name));
			mEtFirstName.requestFocus();
			mResult = false;
		} else if (mEtLastName.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_last_name_blank));
			mEtLastName.requestFocus();
			mResult = false;
		} else if (!Utils.isValidName(mEtLastName.getText().toString())) {
			showToastMessage(getString(R.string.message_error_last_name));
			mEtLastName.requestFocus();
			mResult = false;
		} else if (mEtZip.getText().toString().length() > 0
				&& !Utils.isValidZip(mEtZip.getText().toString())) {
			showToastMessage(getString(R.string.message_error_zip));
			mEtZip.requestFocus();
			mResult = false;
		} else if (mEtEmailAddress.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_email_blank));
			mEtEmailAddress.requestFocus();
			mResult = false;
		} else if (!Utils.isValidEmail(mEtEmailAddress.getText().toString())) {
			showToastMessage(getString(R.string.message_error_email));
			mEtEmailAddress.requestFocus();
			mResult = false;
		} else if (mEtUserName.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_user_name_blank));
			mEtUserName.requestFocus();
			mResult = false;
		} else if (mEtUserName.getText().toString().length() < 3) {
			showToastMessage(getString(R.string.message_error_user_name));
			mEtUserName.requestFocus();
			mResult = false;
		} else if (!Utils.isValidUsername(mEtUserName.getText().toString())) {
			showToastMessage(getString(R.string.message_error_user_name_invalid));
			mEtUserName.requestFocus();
			mResult = false;

		} else if (mEtPassword.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_password_blank));
			mEtPassword.requestFocus();
			mResult = false;
		} else if (mEtPassword.getText().toString().length() < 5) {
			showToastMessage(getString(R.string.message_error_password));
			mEtPassword.requestFocus();
			mResult = false;
		} else if (!Utils.isValidPassword(mEtPassword.getText().toString())) {
			showToastMessage(getString(R.string.message_error_password_invalid));
			mEtPassword.requestFocus();
			mResult = false;

		} else if (mEtConfirmPassword.getText().toString().length() == 0) {
			showToastMessage(getString(R.string.message_error_confirm_password_blank));
			mEtConfirmPassword.requestFocus();
			mResult = false;
		} else if (!mEtConfirmPassword.getText().toString()
				.equals(mEtPassword.getText().toString())) {
			showToastMessage(getString(R.string.message_error_confirm_password));
			mEtConfirmPassword.requestFocus();
			mResult = false;
		}

		return mResult;
	}

	public class SignUpTask extends AsyncTask<String, Void, SignUpEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected SignUpEntity doInBackground(String... params) {
			try {
				InputStream source = null;

				switch (mTypeSocial) {
				case Constants.LOGIN_VIA_FACEBOOK:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL
									.getEntitySignUpSocial(UserObject
											.getInstance(SignUpActivity.this)
											.getUserID(),
											Constants.PROVIDER_FACEBOOK,
											mEtUserName.getText().toString()
													.trim(), mEtPassword
													.getText().toString()
													.trim(), mEtEmailAddress
													.getText().toString()
													.trim(), mEtFirstName
													.getText().toString()
													.trim(), mEtLastName
													.getText().toString()
													.trim(), "", mEtZip
													.getText().toString()
													.trim()));
					break;
				case Constants.LOGIN_VIA_GOOGLE:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL
									.getEntitySignUpSocial(UserObject
											.getInstance(SignUpActivity.this)
											.getUserID(),
											Constants.PROVIDER_GOOGLE,
											mEtUserName.getText().toString()
													.trim(), mEtPassword
													.getText().toString()
													.trim(), mEtEmailAddress
													.getText().toString()
													.trim(), mEtFirstName
													.getText().toString()
													.trim(), mEtLastName
													.getText().toString()
													.trim(), "", mEtZip
													.getText().toString()
													.trim()));
					break;
				case Constants.LOGIN_VIA_TWITTER:
					source = Server.requestPost(ServerURL.getUrlSignUpSocial(),
							ServerURL
									.getEntitySignUpSocial(UserObject
											.getInstance(SignUpActivity.this)
											.getUserID(),
											Constants.PROVIDER_TWITTER,
											mEtUserName.getText().toString()
													.trim(), mEtPassword
													.getText().toString()
													.trim(), mEtEmailAddress
													.getText().toString()
													.trim(), mEtFirstName
													.getText().toString()
													.trim(), mEtLastName
													.getText().toString()
													.trim(), "", mEtZip
													.getText().toString()
													.trim()));
					break;
				default:
					source = Server.requestPost(
							ServerURL.getUrlSignUp(),
							ServerURL.getEntitySignUp(mEtUserName.getText()
									.toString().trim(), mEtPassword.getText()
									.toString().trim(), mEtEmailAddress
									.getText().toString().trim(), mEtFirstName
									.getText().toString().trim(), mEtLastName
									.getText().toString().trim(), "", mEtZip
									.getText().toString().trim()));
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
					if (!Utils.isNetworkConnected(SignUpActivity.this)) {
						Toast.makeText(SignUpActivity.this,
								R.string.mess_error_network, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(SignUpActivity.this,
								R.string.mess_error_server, Toast.LENGTH_SHORT)
								.show();
					}
					dismissProgress();
					return;

				}
				if (mTypeSocial != -1) {
					if (result.mAccessToken != null
							&& result.mAccessToken.length() > 0) {
						new UserObject("", mEtUserName.getText().toString(),
								mEtEmailAddress.getText().toString(),
								mEtFirstName.getText().toString(), mEtLastName
										.getText().toString(), mEtZip.getText()
										.toString(), "", result.mCity,
								result.mState,
								mEtPassword.getText().toString(), 0,
								mTypeSocial, result.mDinnerStatus, "", 0,
								result.mAccessToken);

						mAppPreferences.setUser(UserObject
								.getInstance(SignUpActivity.this));
						if (mBm != null) {
							new ImageUploadTask().execute(result.mAccessToken);
						} else {
							new startActivityTask().execute();
						}
					} else {
						showToastMessage(SignUpActivity.this.getResources()
								.getString(R.string.mess_register_error));
						return;
					}
				} else {
					if (result.mStatus == null) {
						showToastMessage(result.mError.toString());
						return;
					}
					if (result.mStatus.equals(Constants.CONNECT_ERROR)) {
						if (result.mError.mEmail != null) {
							showToastMessage(SignUpActivity.this
									.getResources()
									.getString(
											R.string.message_error_email_existed));
						} else if (result.mError.mUserName != null) {
							showToastMessage(SignUpActivity.this
									.getResources()
									.getString(
											R.string.message_error_user_name_existed));
						}
						dismissProgress();
					} else {
						new UserObject(result.mId, mEtUserName.getText().toString(),
								mEtEmailAddress.getText().toString(),
								mEtFirstName.getText().toString(), mEtLastName
										.getText().toString(), mEtZip.getText()
										.toString(), "", result.mCity,
								result.mState,
								mEtPassword.getText().toString(), 0,
								Constants.LOGIN_VIA_DEFAULT,
								result.mDinnerStatus, "", 0,
								result.mAccessToken);
						mAppPreferences.setUser(UserObject
								.getInstance(SignUpActivity.this));

						if (mBm != null) {
							try {
								if (Utils
										.isNetworkConnected(SignUpActivity.this)) {
									new ImageUploadTask()
											.execute(result.mAccessToken);
								} else {
									showToastMessage(getString(R.string.mess_error_network));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							new startActivityTask().execute();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
			dismissProgress();
		}
	}

	private class startActivityTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// startActivity(new Intent(SignUpActivity.this,
			// MapViewActivity.class));
			setResult(RESULT_OK);
			finish();
		}

	}

	/**
	 * The class connects with server and uploads the photo
	 * 
	 * 
	 */

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
						+ UserObject.getInstance(SignUpActivity.this)
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
				if (!Utils.isNetworkConnected(SignUpActivity.this)) {
					Toast.makeText(SignUpActivity.this,
							R.string.mess_error_network, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(SignUpActivity.this,
							R.string.mess_error_avatar, Toast.LENGTH_SHORT)
							.show();
					new startActivityTask().execute();
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
			} else if (result.mError != null) {
				Toast.makeText(SignUpActivity.this, R.string.mess_error_avatar,
						Toast.LENGTH_SHORT).show();
				new startActivityTask().execute();
				dismissProgress();
			} else if (result.mAvatar != null) {
				UserObject.getInstance(SignUpActivity.this).setProfileAvatar(
						result.mAvatar.toString());
				mAppPreferences.setUser(UserObject
						.getInstance(SignUpActivity.this));
				new startActivityTask().execute();
			}
		}

	}

	public void logout() {
		showProgress(R.string.mess_waitting);
		LogoutSocialTask.execute(SignUpActivity.this, this, this);
	}

	private void doCrop() {
		try {
			final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setType("image/*");

			List<ResolveInfo> list = getPackageManager().queryIntentActivities(
					intent, 0);

			int size = list.size();

			if (size == 0) {
				Toast.makeText(this, "Can not find image crop app",
						Toast.LENGTH_SHORT).show();

				return;
			} else {
				intent.setData(selectedImageUri);

				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", true);

				if (size == 1) {
					Intent i = new Intent(intent);
					ResolveInfo res = list.get(0);

					i.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));

					startActivityForResult(i, CROP_FROM_CAMERA);
				} else {
					for (ResolveInfo res : list) {
						final CropOption co = new CropOption();

						co.title = getPackageManager().getApplicationLabel(
								res.activityInfo.applicationInfo);
						co.icon = getPackageManager().getApplicationIcon(
								res.activityInfo.applicationInfo);
						co.appIntent = new Intent(intent);

						co.appIntent.setComponent(new ComponentName(
								res.activityInfo.packageName,
								res.activityInfo.name));

						cropOptions.add(co);
					}

					CropOptionAdapter adapter = new CropOptionAdapter(
							getApplicationContext(), cropOptions);

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Choose Crop App");
					builder.setAdapter(adapter,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									startActivityForResult(
											cropOptions.get(item).appIntent,
											CROP_FROM_CAMERA);
								}
							});

					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {

							if (selectedImageUri != null) {
								getContentResolver().delete(selectedImageUri,
										null, null);
								selectedImageUri = null;
							}
						}
					});

					AlertDialog alert = builder.create();

					alert.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void LogoutFinish() {
		finish();

	}

	@Override
	public void LogoutError() {
		finish();
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		// TODO Auto-generated method stub

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
			mIvAvatar.setImageResource(R.drawable.ic_load_img_150);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("Async-Example", "onPostExecute Called");
			mIvAvatar.setImageBitmap(result);
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
					mIvAvatar.setImageResource(R.drawable.ic_default_avatar);
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
				mIvAvatar.setImageResource(R.drawable.ic_default_avatar);
			}

			return null;
		}
	}

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
				InputMethodManager imm = (InputMethodManager) this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

}

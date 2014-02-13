package com.magrabbit.intelligentmenu.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.CropOptionAdapter;
import com.magrabbit.intelligentmenu.customview.DialogChangePassword;
import com.magrabbit.intelligentmenu.customview.DialogChangeServer;
import com.magrabbit.intelligentmenu.customview.DialogChangeServer.ProcessDialogChangeServer;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.listener.LogoutListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.AvatarEntity;
import com.magrabbit.intelligentmenu.model.CropOption;
import com.magrabbit.intelligentmenu.model.MyPointGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserGlobalEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.task.LogoutTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Hung Hoang
 * @description Logout,Edit and view profile.
 */
public class AccountSettingActivity extends BaseActivity implements
		LogoutListener, OnAccessRevokedListener {
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private TextView mTvLoggin;
	private TextView mTvStatus;
	private TextView mTvPoints;
	private TextView mTvAddress;
	private TextView mTvEmailAddress;
	private TextView mTvFullname;
	private ImageView mIvAvatar;
	private Dialog mDlPhoto;
	// Layout Dialog Photo
	private Button mTvTakePhoto;
	private Button mTvChoosePhoto;
	private Button mBtCancel;
	private Bitmap mBm;
	private ImageLoader mImageLoader;
	private Uri mImageCaptureUri;
	private AppPreferences mAppPreferences;
	private ActivityInfo mActivityInfo;
	private ComponentName mCn;
	private ProgressBar mProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_setting);
		mAppPreferences = new AppPreferences(this);
		mImageLoader = new ImageLoader(this);
		mTvLoggin = (TextView) findViewById(R.id.account_setting_loggin);
		mTvStatus = (TextView) findViewById(R.id.account_setting_tv_dinner_status);
		mTvPoints = (TextView) findViewById(R.id.account_setting_tv_points);
		mTvAddress = (TextView) findViewById(R.id.account_setting_tv_address);
		mTvFullname = (TextView) findViewById(R.id.account_setting_tv_full_name);
		mTvEmailAddress = (TextView) findViewById(R.id.account_setting_tv_email_address);
		mIvAvatar = (ImageView) findViewById(R.id.account_setting_iv_avatar);
		mProcess = (ProgressBar) findViewById(R.id.account_setting_progres);
		mTvAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvStatus.setText("Status: ");
		mTvPoints.setText("Point: ");

		new ListGlobalPoints().execute();
		if (UserObject.getInstance(this) != null) {
			if (UserObject.getInstance(this).getZip() == null
					|| UserObject.getInstance(this).getZip().equals("")) {
				mTvAddress.setText("");
			} else {
				mTvAddress.setText(UserObject.getInstance(this).getCity()
						+ ", " + UserObject.getInstance(this).getState() + " "
						+ UserObject.getInstance(this).getZip());
			}
			mTvLoggin.setText(UserObject.getInstance(this).getUserName());
			mTvFullname.setText(UserObject.getInstance(this).getFirstName()
					+ " " + UserObject.getInstance(this).getLastName());
			mTvEmailAddress.setText(UserObject.getInstance(this).getEmail());
			try {

				if (UserObject.getInstance(this).getProfileAvatar() != null
						&& !UserObject.getInstance(this).getProfileAvatar()
								.equalsIgnoreCase("")) {
					mIvAvatar.setScaleType(ScaleType.FIT_XY);
					mImageLoader.DisplayImage(ServerURL.URL
							+ UserObject.getInstance(this).getProfileAvatar(),
							mIvAvatar);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageLoader.clearCache();
	}

	/**
	 * @param v
	 *            Edit Profile
	 */
	public void onClick_Edit(View v) {
		try {
			if (!Utils.isNetworkConnected(this)) {
				showToastMessage(getString(R.string.mess_error_network));
			} else {
				startActivityForResult(new Intent(AccountSettingActivity.this,
						EditInfoActivity.class),
						CodeRequest.CODE_REQUEST_ACCOUNTSETTING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Logout account.
	 */
	public void onClick_Logout(View v) {

		try {
			if (!Utils.isNetworkConnected(this)) {
				showToastMessage(getString(R.string.mess_error_network));
				return;
			}

			DialogConfirm dialog = new DialogConfirm(this,
					android.R.drawable.ic_dialog_alert, "Logout",
					getString(R.string.mess_logout),true,
					new ProcessDialogConfirm() {

						@Override
						public void click_Ok() {
							showProgress(R.string.mess_waitting);
							LogoutTask.execute(AccountSettingActivity.this,
									AccountSettingActivity.this,
									AccountSettingActivity.this);
							// LogoutErrorTask
							// .execute(AccountSettingActivity.this);
							// dismissProgress();
							// setResult(CodeRequest.CODE_RESULT_LOGOUT);
							// finish();
						}

						@Override
						public void click_Cancel() {
						}
					});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Change Password.
	 */
	public void onClick_ChangePassword(View v) {
		try {
			if (!Utils.isNetworkConnected(this)) {
				showToastMessage(getString(R.string.mess_error_network));
				return;
			} else {
				DialogChangePassword mDialog = new DialogChangePassword(this);
				mDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void LogoutFinish() {
		dismissProgress();
		setResult(CodeRequest.CODE_RESULT_LOGOUT);
		finish();
	}

	@Override
	public void LogoutError() {
		dismissProgress();
		showToastMessage(R.string.mess_logout_error);
	}

	/**
	 * @param v
	 *            Change server for app.
	 */
	public void onClick_ChangeServer(View v) {
		try {
			DialogChangeServer dialog = new DialogChangeServer(this,
					new ProcessDialogChangeServer() {

						@Override
						public void click_Save() {
							setResult(CodeRequest.CODE_RESULT_LOGOUT);
							finish();
						}
					});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Select avatar for user.
	 */
	public void onClick_Avatar(View v) {
		selectImage();
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {

	}

	/**
	 * Select photo from camera or library.
	 */
	private void selectImage() {
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
						// Intent intent = new Intent(
						// MediaStore.ACTION_IMAGE_CAPTURE);
						// File f = new File(android.os.Environment
						// .getExternalStorageDirectory(), "temp.jpg");
						// intent.putExtra(MediaStore.EXTRA_OUTPUT,
						// Uri.fromFile(f));
						// startActivityForResult(intent,
						// CodeRequest.CODE_REQUEST_CAMERA);
						/**
						 * To take a photo from camera, pass intent action
						 * ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera
						 * app.
						 */
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);

						/**
						 * Also specify the Uri to save the image on specified
						 * path and file name. Note that this Uri variable also
						 * used by gallery app to hold the selected image path.
						 */
						mImageCaptureUri = Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "tmp_avatar_"
								+ String.valueOf(System.currentTimeMillis())
								+ ".jpg"));

						intent.putExtra(
								android.provider.MediaStore.EXTRA_OUTPUT,
								mImageCaptureUri);

						try {
							intent.putExtra("return-data", true);

							startActivityForResult(intent, PICK_FROM_CAMERA);
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
					try {
						// pick from file
						/**
						 * To select an image from existing files, use
						 * Intent.createChooser to open image chooser. Android
						 * will automatically display a list of supported
						 * applications, such as image gallery or file manager.
						 */
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
							startActivityForResult(intent, PICK_FROM_FILE);
						} else {
							Intent intent1 = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							intent1.setType("image/*");
							startActivityForResult(Intent.createChooser(
									intent1, "Select File"), PICK_FROM_FILE);
						}
						mDlPhoto.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
		mDlPhoto.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == CodeRequest.CODE_REQUEST_ACCOUNTSETTING) {
				if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					finish();
				}
			}
			if (resultCode != RESULT_OK)
				return;

			switch (requestCode) {
			case PICK_FROM_CAMERA:
				/**
				 * After taking a picture, do the crop
				 */
				doCrop();

				break;

			case PICK_FROM_FILE:
				/**
				 * After selecting image from files, save the selected path
				 */
				mImageCaptureUri = data.getData();
				doCrop();

				break;

			case CROP_FROM_CAMERA:
				Bundle extras = data.getExtras();
				/**
				 * After cropping the image, get the bitmap of the cropped image
				 * and display it on imageview.
				 */
				if (extras != null) {
					mBm = extras.getParcelable("data");
					new ImageUploadTask().execute(UserObject.getInstance(this)
							.getAccess_token());
				}

				File f = new File(mImageCaptureUri.getPath());
				/**
				 * Delete the temporary image
				 */
				if (f.exists())
					f.delete();

				break;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The class connects with server and uploads the photo
	 */
	public class ImageUploadTask extends AsyncTask<String, Void, AvatarEntity> {

		@Override
		protected void onPreExecute() {
			mProcess.setVisibility(View.VISIBLE);
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
						+ UserObject.getInstance(AccountSettingActivity.this)
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
				if (!Utils.isNetworkConnected(AccountSettingActivity.this)) {
					showToastMessage(getString(R.string.mess_error_network));
				} else {
					showToastMessage(AccountSettingActivity.this.getResources()
							.getString(R.string.mess_error_avatar));
				}
				mProcess.setVisibility(View.GONE);
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
			if (result.mError != null) {
				mProcess.setVisibility(View.GONE);
				showToastMessage(AccountSettingActivity.this.getResources()
						.getString(R.string.mess_error_avatar));
			} else if (result.mAvatar != null) {
				mIvAvatar.setImageBitmap(mBm);
				mProcess.setVisibility(View.GONE);
				UserObject.getInstance(AccountSettingActivity.this)
						.setProfileAvatar(result.mAvatar.toString());
				mAppPreferences.setUser(UserObject
						.getInstance(AccountSettingActivity.this));
			}
		}
	}

	/**
	 * Crop image for avatar.
	 */
	private void doCrop() {
		try {
			final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
			/**
			 * Open image crop app by starting an intent
			 * ‘com.android.camera.action.CROP‘.
			 */
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setType("image/*");

			/**
			 * Check if there is image cropper app installed.
			 */
			List<ResolveInfo> list = getPackageManager().queryIntentActivities(
					intent, 0);

			int size = list.size();

			/**
			 * If there is no image cropper app, display warning message
			 */
			if (size == 0) {

				Toast.makeText(this, "Can not find image crop app.",
						Toast.LENGTH_SHORT).show();

				return;
			} else {
				/**
				 * Specify the image path, crop dimension and scale
				 */
				intent.setData(mImageCaptureUri);

				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", true);
				/**
				 * There is posibility when more than one image cropper app
				 * exist, so we have to check for it first. If there is only one
				 * app, open then app.
				 */

				if (size == 1) {
					Intent i = new Intent(intent);
					ResolveInfo res = list.get(0);

					i.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));

					startActivityForResult(i, CROP_FROM_CAMERA);
				} else {
					/**
					 * If there are several app exist, create a custom chooser
					 * to let user selects the app.
					 */
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

							if (mImageCaptureUri != null) {
								getContentResolver().delete(mImageCaptureUri,
										null, null);
								mImageCaptureUri = null;
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
	protected void onResume() {
		try {
			super.onResume();
			new ListGlobalPoints().execute();
			mTvLoggin.setText(UserObject.getInstance(
					AccountSettingActivity.this).getUserName());
			mTvFullname.setText(UserObject.getInstance(
					AccountSettingActivity.this).getFirstName()
					+ " "
					+ UserObject.getInstance(AccountSettingActivity.this)
							.getLastName());
			if (UserObject.getInstance(this).getZip() == null
					|| UserObject.getInstance(this).getZip().equals("")) {
				mTvAddress.setText("");
			} else {
				mTvAddress.setText(UserObject.getInstance(this).getCity()
						+ ", " + UserObject.getInstance(this).getState() + " "
						+ UserObject.getInstance(this).getZip());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            View address of user.
	 */
	public void onClick_Address(View v) {
		try {
			Intent intentMessage = new Intent(AccountSettingActivity.this,
					RestaurantMapView.class);
			// put the address of restaurant in Intent
			intentMessage.putExtra(StringExtraUtils.KEY_ADDRESS, mTvAddress
					.getText().toString());
			intentMessage.putExtra(
					StringExtraUtils.KEY_ADDRESS_FROM_RESTAURANT, false);
			// put the name of restaurant in Intent
			intentMessage.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
					mTvFullname.getText().toString() + " House");
			// Show MapView and finish the activity
			startActivity(intentMessage);
			// finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The class connects with server and uploads the global points.
	 *
	 */
	public class ListGlobalPoints extends
			AsyncTask<String, Void, MyPointGlobalEntity> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected MyPointGlobalEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL
						// "zDobFY81ysxZsyB8zVNq"
						.getUrlGlobalPoint(UserObject
								.getInstance(AccountSettingActivity.this).mAccess_token));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyPointGlobalEntity response = gson.fromJson(json,
						MyPointGlobalEntity.class);

				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyPointGlobalEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(AccountSettingActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(AccountSettingActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					}
					dismissProgress();
					return;

				} else {
					UserGlobalEntity mUserGlobal = new UserGlobalEntity();
					mUserGlobal = result.mUserGlobal;
					if (mUserGlobal.getPointNumber() <= 1) {
						mTvPoints.setText("Point: "
								+ Utils.formatPointNumbers(
										mUserGlobal.getPointNumber()));
					} else {
						mTvPoints.setText("Points: "
								+ Utils.formatPointNumbers(
										mUserGlobal.getPointNumber()));
					}

					mTvStatus.setText("Status: " + mUserGlobal.getStatus());
					dismissProgress();
				}

			} catch (Exception e) {
				e.toString();
			}
			dismissProgress();
		}

	}
}
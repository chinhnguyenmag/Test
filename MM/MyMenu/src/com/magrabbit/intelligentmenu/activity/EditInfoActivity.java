package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.EditUserEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Hung Hoang
 * @description Edit information profile
 */
public class EditInfoActivity extends BaseActivity {

	private EditText mEtFirstName;
	private EditText mEtLastName;
	private EditText mEtEmailAddress;
	private EditText mEtUserName;
	private EditText mEtZip;
	private AppPreferences mAppPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_profile);

		mAppPreferences = new AppPreferences(this);

		mEtEmailAddress = (EditText) findViewById(R.id.edit_profile_et_email);
		mEtFirstName = (EditText) findViewById(R.id.edit_profile_et_first_name);
		mEtLastName = (EditText) findViewById(R.id.edit_profile_et_last_name);
		mEtUserName = (EditText) findViewById(R.id.edit_profile_et_user_name);
		mEtZip = (EditText) findViewById(R.id.edit_profile_et_zip);
		mEtUserName.setText(UserObject.getInstance(EditInfoActivity.this)
				.getUserName());
		mEtEmailAddress.setText(UserObject.getInstance(EditInfoActivity.this)
				.getEmail());
		mEtFirstName.setText(UserObject.getInstance(EditInfoActivity.this)
				.getFirstName());
		mEtLastName.setText(UserObject.getInstance(EditInfoActivity.this)
				.getLastName());
		mEtZip.setText(UserObject.getInstance(EditInfoActivity.this).getZip());

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

	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mAppPreferences = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	public void onClick_Back(View v) {
		finish();
	}

	/**
	 * @param v
	 *            Pressed by consumer once username & password are entered to
	 *            enter into MyMenu.
	 */
	public void onClick_Save(View v) {
		if (validate() == true) {
			try {
				if (Utils.isNetworkConnected(this)) {
					new EditUserTask().execute();
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
		finish();
	}

	/**
	 * Validate all field for Edit Info.
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
		}
		return mResult;
	}

	public class EditUserTask extends AsyncTask<String, Void, EditUserEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected EditUserEntity doInBackground(String... params) {
			try {

				InputStream source = Server.requestPost(ServerURL
						.getUrlUpdateUser(), ServerURL.getEntityUpdateUser(
						EditInfoActivity.this, mEtFirstName.getText()
								.toString().trim(), mEtLastName.getText()
								.toString().trim(), mEtZip.getText().toString()
								.trim(), mEtUserName.getText().toString()
								.trim()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				EditUserEntity response = gson.fromJson(json,
						EditUserEntity.class);

				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(EditUserEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(EditInfoActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(EditInfoActivity.this.getResources()
								.getString(R.string.mess_error_server));
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
				}
				if (result.mStatus == null) {
					showToastMessage(result.mError.toString());
					dismissProgress();
					return;

				} else if (result.mStatus.equals(Constants.CONNECT_ERROR)) {
					if (result.mErrors.mEmail != null) {
						showToastMessage(EditInfoActivity.this
								.getResources()
								.getString(R.string.message_error_email_existed));
					} else if (result.mErrors.mUserName != null) {
						showToastMessage(EditInfoActivity.this
								.getResources()
								.getString(
										R.string.message_error_user_name_existed));
					}
					dismissProgress();
				} else {
					if (UserObject.getInstance(EditInfoActivity.this) != null) {
						UserObject
								.getInstance(EditInfoActivity.this)
								.setFirstName(mEtFirstName.getText().toString());
						UserObject.getInstance(EditInfoActivity.this)
								.setLastName(mEtLastName.getText().toString());
						UserObject.getInstance(EditInfoActivity.this).setZip(
								mEtZip.getText().toString());
						UserObject.getInstance(EditInfoActivity.this).setCity(
								result.mCity);
						UserObject.getInstance(EditInfoActivity.this).setState(
								result.mState);
						UserObject.getInstance(EditInfoActivity.this)
								.setUserName(mEtUserName.getText().toString());
						mAppPreferences.setUser(UserObject
								.getInstance(EditInfoActivity.this));
					}
					Toast.makeText(EditInfoActivity.this,
							R.string.message_update_account_success,
							Toast.LENGTH_SHORT).show();
					finish();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
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
package com.magrabbit.intelligentmenu.customview;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * Dialog Change password
 * 
 * @author Hung Hoang.
 * 
 */
public class DialogChangePassword extends BaseDialog implements OnClickListener {

	private Button mBtCancel;
	private Button mBtSave;
	private EditText mEdtNewPassword;
	private EditText mEdtConfirmPassword;
	private Activity mContext;
	private AppPreferences mAppPreferences;

	public DialogChangePassword(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_change_password);

		this.mContext =(Activity) context;
		mAppPreferences = new AppPreferences(mContext);
		this.mBtCancel = (Button) findViewById(R.id.dialog_change_password_bt_cancel);
		this.mBtSave = (Button) findViewById(R.id.dialog_change_password_bt_save);
		this.mEdtNewPassword = (EditText) findViewById(R.id.dialog_change_password_edt_password);
		this.mEdtConfirmPassword = (EditText) findViewById(R.id.dialog_change_password_edt_confirm_password);
		this.mEdtNewPassword
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
		this.mEdtConfirmPassword
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
		this.mBtCancel.setOnClickListener(this);
		this.mBtSave.setOnClickListener(this);

	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (v == this.mBtCancel) {
			this.dismiss();
		} else if (v == this.mBtSave) {
			if (validate() == true) {
				try {
					if (!Utils.isNetworkConnected(mContext)) {
						showErrorDialog(R.string.mess_error_network);
					} else {
						new ChangePasswordTask().execute();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class ChangePasswordTask extends
			AsyncTask<String, Void, EditUserEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected EditUserEntity doInBackground(String... params) {
			try {

				InputStream source = Server.requestPost(ServerURL
						.getUrlUpdateUser(), ServerURL.getEntityChangePassword(
						UserObject.getInstance(mContext).getAccess_token(),
						mEdtNewPassword.getText().toString().trim()));
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
					if (!Utils.isNetworkConnected(mContext)) {
						Toast.makeText(mContext, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, R.string.mess_error_server,
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					return;

				}
				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					mContext.setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(mContext,
							mContext.getResources().getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					mContext.finish();
					return;
				}
				if (result.mStatus == null) {
					Toast.makeText(mContext, result.mError.toString(),
							Toast.LENGTH_LONG).show();
					dismissProgress();
					return;

				} else if (result.mStatus.equals(Constants.CONNECT_ERROR)) {
					Toast.makeText(mContext, result.mError.toString(),
							Toast.LENGTH_LONG).show();
					dismissProgress();
				} else {
					if (UserObject.getInstance(mContext) != null) {
						UserObject.getInstance(mContext).setPassword(
								mEdtNewPassword.getText().toString());
						mAppPreferences.setUser(UserObject
								.getInstance(mContext));
					}
					Toast.makeText(mContext,
							R.string.message_change_password_successfully,
							Toast.LENGTH_SHORT).show();
					dismissProgress();
					dismiss();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static abstract class ProcessDialogForgotPassword {
		public abstract void click_Dialog(boolean result);
	}

	/**
	 * Validate all field for Edit Info.
	 */
	public boolean validate() {
		boolean mResult = true;
		try {
			if (mEdtNewPassword.getText().toString().length() == 0) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.message_error_password_blank),
						Toast.LENGTH_SHORT).show();
				mEdtNewPassword.requestFocus();
				mResult = false;
			} else if (mEdtNewPassword.getText().toString().length() < 5) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.message_error_password),
						Toast.LENGTH_SHORT).show();
				mEdtNewPassword.requestFocus();
				mResult = false;
			} else if (!Utils.isValidPassword(mEdtNewPassword.getText()
					.toString())) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.message_error_password_invalid),
						Toast.LENGTH_SHORT).show();
				mEdtNewPassword.requestFocus();
				mResult = false;
			} else if (mEdtConfirmPassword.getText().toString().length() == 0) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.message_error_confirm_password_blank),
						Toast.LENGTH_SHORT).show();
				mEdtConfirmPassword.requestFocus();
				mResult = false;
			} else if (!mEdtConfirmPassword.getText().toString()
					.equals(mEdtNewPassword.getText().toString())) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.message_error_confirm_password),
						Toast.LENGTH_SHORT).show();
				mEdtConfirmPassword.requestFocus();
				mResult = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
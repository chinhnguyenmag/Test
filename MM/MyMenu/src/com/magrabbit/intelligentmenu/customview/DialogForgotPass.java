package com.magrabbit.intelligentmenu.customview;

import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.MessageErrorEntity;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * Dialog forgot password
 * 
 * @author chinhnd
 * 
 */
public class DialogForgotPass extends BaseDialog implements OnClickListener {

	private ProcessDialogForgotPassword process;

	private Button mBtCancel;
	private Button mBtSave;
	private EditText mEdtEmail;
	private Context mContext;

	public DialogForgotPass(Context context, ProcessDialogForgotPassword pro) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_forgot_password);
		this.process = pro;
		this.mContext = context;
		this.mBtCancel = (Button) findViewById(R.id.dialog_forgot_password_bt_cancel);
		this.mBtSave = (Button) findViewById(R.id.dialog_forgot_password_bt_submit);
		this.mEdtEmail = (EditText) findViewById(R.id.dialog_forgot_password_edt_email);
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
			try {
				String email = "";
				email = mEdtEmail.getText().toString().trim();
				if (email.length() == 0) {
					Toast.makeText(mContext,
							mContext.getString(R.string.mess_input_email),
							Toast.LENGTH_SHORT).show();
				} else if (!Utils.isValidEmail(email)) {
					Toast.makeText(mContext,
							mContext.getString(R.string.login_error_email),
							Toast.LENGTH_SHORT).show();
				} else {
					new ForgotPassTask().execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.dismiss();
			}
		}
	}

	public static abstract class ProcessDialogForgotPassword {
		public abstract void click_Dialog(boolean result);
	}

	private class ForgotPassTask extends
			AsyncTask<Void, Void, MessageErrorEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected MessageErrorEntity doInBackground(Void... params) {
			try {
				InputStream source = Server.requestPost(
						ServerURL.getUrlForgotPassword(),
						ServerURL.getEntityForgotPass(mEdtEmail.getText()
								.toString().trim()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MessageErrorEntity response = gson.fromJson(json,
						MessageErrorEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MessageErrorEntity result) {
			super.onPostExecute(result);

			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(mContext)) {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(mContext,
								mContext.getString(R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {

					Toast.makeText(mContext, result.mError, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(mContext,
							mContext.getString(R.string.mess_forgot_susscess),
							Toast.LENGTH_LONG).show();
					process.click_Dialog(true);
					dismiss();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}
	}
}
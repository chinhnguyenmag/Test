package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * Dialog forgot password
 * 
 * @author chinhnd
 * 
 */
public class DialogChangeServer extends BaseDialog implements OnClickListener {

	private Button mBtCancel;
	private Button mBtSave;
	private EditText mEdtServerURL;
	private Context mContext;
	private AppPreferences mAppPreferences;
	private ProcessDialogChangeServer process;

	public DialogChangeServer(Context context, ProcessDialogChangeServer pro) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_change_server);
		this.mContext = context;
		this.process = pro;
		mAppPreferences = new AppPreferences(context);
		this.mBtCancel = (Button) findViewById(R.id.dialog_change_server_bt_cancel);
		this.mBtSave = (Button) findViewById(R.id.dialog_change_server_bt_submit);
		this.mEdtServerURL = (EditText) findViewById(R.id.dialog_change_server_edt_url);

		if (mAppPreferences.getServerURL().length() > 0) {
			mEdtServerURL.setText(mAppPreferences.getServerURL());
			ServerURL.URL = mAppPreferences.getServerURL();
		} else {
			mEdtServerURL.setText(ServerURL.URL);
			mAppPreferences.setServerURL(ServerURL.URL);
		}

		mEdtServerURL.setText(mAppPreferences.getServerURL());
		this.mBtCancel.setOnClickListener(this);
		this.mBtSave.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (v == this.mBtCancel) {
			this.dismiss();
		} else if (v == this.mBtSave) {
			try {
				String url = mEdtServerURL.getText().toString().trim();
				if (mAppPreferences.setServerURL(url)) {
					ServerURL.URL = url;
				}
				process.click_Save();
				this.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				this.dismiss();
			}
		}
	}

	public static abstract class ProcessDialogChangeServer {
		public abstract void click_Save();
	}

}
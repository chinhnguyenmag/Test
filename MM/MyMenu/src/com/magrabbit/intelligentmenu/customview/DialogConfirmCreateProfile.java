package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;

/**
 * Dialog for confirm creating new profile or not
 * 
 * @author baonguyen
 * 
 */
public class DialogConfirmCreateProfile extends BaseDialog implements OnClickListener {

	private ProcessDialogConfirm process;

	private Button mBtCancel;
	private Button mBtOk;
	private ImageView mIvIcon;
	private TextView mTvTitle;
	private TextView mTvContent;
	private LinearLayout mLayoutTitle;
	private Context mContext;

	/**
	 * 
	 * @param context
	 * @param icon
	 * @param title
	 * @param content
	 * @param pro
	 */
	public DialogConfirmCreateProfile(Context context, int icon, String title,
			String content, ProcessDialogConfirm pro) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_confirm_create_profile);
		this.process = pro;
		this.mContext = context;
		this.mBtCancel = (Button) findViewById(R.id.dialog_confirm_create_profile_bt_Cancel);
		this.mBtOk = (Button) findViewById(R.id.dialog_confirm_create_profile_bt_Ok);
		this.mIvIcon = (ImageView) findViewById(R.id.dialog_confirm_create_profile_iv_icon);
		this.mTvTitle = (TextView) findViewById(R.id.dialog_confirm_create_profile_tv_title);
		this.mTvContent = (TextView) findViewById(R.id.dialog_confirm_create_profile_tv_content);
		this.mLayoutTitle = (LinearLayout) findViewById(R.id.dialog_confirm_create_profile_layout_title);
		this.mBtCancel.setOnClickListener(this);
		this.mBtOk.setOnClickListener(this);

		setData(icon, title, content);
	}

	public void setData(int icon, String title, String content) {
		if (icon == -1) {
			this.mIvIcon.setVisibility(View.GONE);
		} else {
			this.mIvIcon.setVisibility(View.VISIBLE);
			this.mIvIcon.setBackgroundResource(icon);
		}

		if (title == null || title.length() == 0) {
			this.mLayoutTitle.setVisibility(View.GONE);
		} else {
			this.mLayoutTitle.setVisibility(View.VISIBLE);
			this.mTvTitle.setText(title);
		}

		this.mTvContent.setText(content);
	}

	@Override
	public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (v == this.mBtCancel) {
			process.click_Cancel();
			this.dismiss();
		} else if (v == this.mBtOk) {
			process.click_Create();
			this.dismiss();
		}
	}

	public static abstract class ProcessDialogConfirm {
		public abstract void click_Create();

		public abstract void click_Cancel();
	}

}
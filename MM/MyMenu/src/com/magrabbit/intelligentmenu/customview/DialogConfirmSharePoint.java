package com.magrabbit.intelligentmenu.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;

/**
 * 
 * 
 * @author baognguyen
 * @Description : Dialog for showing confirmation for users to accept or cancel
 *              sharing points
 */
public class DialogConfirmSharePoint extends Dialog implements OnClickListener {
	private ProcessDialogConfirm_SaveProfile mProcessDialogMove;
	private Button mBtnOk;
	private Button mBtnCancel;
	private Context mContext;

	// For showing the confirmation content
	private TextView mTvContent;
	private String mToUser;
	private int mPointNumber;
	private int mSendType;
	private boolean mPluralPoint = false;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogConfirmSharePoint(Context context, String to_user, int point,
			int sendType, ProcessDialogConfirm_SaveProfile processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// /** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_confirm_share_point);
		this.mContext = context;
		this.mToUser = to_user;
		this.mPointNumber = point;
		if (this.mPointNumber > 1) {
			mPluralPoint = true;
		}
		this.mSendType = sendType;
		mProcessDialogMove = processMove;

		// Set content for confirmation message
		mTvContent = (TextView) findViewById(R.id.dialog_confirm_share_point_tv_content);

		switch (mSendType) {
		case 0:// SMS
			StringBuilder builder1 = new StringBuilder();
			builder1.append("Do you want to share " + mPointNumber);
			if (mPluralPoint) {
				builder1.append(" points");
			} else {
				builder1.append(" point");
			}
			builder1.append(" to " + mToUser + " through SMS ?");
			mTvContent.setText(builder1.toString());
			break;
		case 1:// Email
			StringBuilder builder2 = new StringBuilder();
			builder2.append("Do you want to share " + mPointNumber);
			if (mPluralPoint) {
				builder2.append(" points");
			} else {
				builder2.append(" point");
			}
			builder2.append(" to " + mToUser + " through Email ?");
			mTvContent.setText(builder2.toString());
			break;
		case 2:// FriendList
			StringBuilder builder3 = new StringBuilder();
			builder3.append("Do you want to share " + mPointNumber);
			if (mPluralPoint) {
				builder3.append(" points");
			} else {
				builder3.append(" point");
			}
			builder3.append(" to " + mToUser + " in your friend list ?");
			mTvContent.setText(builder3.toString());
			break;

		default:
			break;
		}

		mBtnOk = (Button) findViewById(R.id.dialog_confirm_share_point_btn_ok);
		mBtnOk.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.dialog_confirm_share_point_btn_cancel);
		mBtnCancel.setOnClickListener(this);
	}

	/**
	 * @Description : Handle the click events
	 */
	@Override
	public void onClick(View v) {
		if (v == mBtnCancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtnOk) {
			mProcessDialogMove.click_Save();
			dismiss();
		}
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of text box
	 */
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
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	public static abstract class ProcessDialogConfirm_SaveProfile {
		public abstract void click_Save();

		public abstract void click_Cancel();
	}
}

package com.magrabbit.intelligentmenu.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;

/**
 * @author vule
 *
 */
public class DialogConFirmRating extends Dialog implements
		android.view.View.OnClickListener {

	private Button mBtPostIt;
	private Button mBtCancel;
	private TextView mTvGrade;
	private TextView mTvComment;
	private Activity mActivity;
	private ProcessDialogConfirmRating mProcessDialogMove;

	public DialogConFirmRating(Context context,
			ProcessDialogConfirmRating processMove, String grade, String comment) {
		super(context);
		mActivity = (Activity) context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.dialog_confirm_rating);
		mProcessDialogMove = processMove;
		mBtPostIt = (Button) findViewById(R.id.dialog_confirm_rating_bt_postit);
		mBtCancel = (Button) findViewById(R.id.dialog_confirm_rating_bt_cancel);
		mTvGrade = (TextView) findViewById(R.id.dialog_confirm_rating_tv_grade);
		mTvComment = (TextView) findViewById(R.id.dialog_confirm_rating_tv_comment);
		mBtPostIt.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);

		mTvGrade.setText("Grade: " + grade);
		mTvComment.setText("Comment: " + comment);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtCancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtPostIt) {
			mProcessDialogMove.click_PostIt();
			dismiss();
		}
	}

	/**
	 * @author Thanh Vu
	 * 
	 */
	public static abstract class ProcessDialogConfirmRating {
		public abstract void click_PostIt();

		public abstract void click_Cancel();
	}

}

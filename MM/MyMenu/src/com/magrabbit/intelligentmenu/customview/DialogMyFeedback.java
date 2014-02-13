package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.control.SeekBarRating;
import com.magrabbit.intelligentmenu.control.SeekBarRating.OnRangeSeekBarChangeListener;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * 
 */
public class DialogMyFeedback extends Dialog implements
		android.view.View.OnClickListener {

	private Button mBtn_save;
	private Button mBtn_cancel;
	private EditText mTv_comment;
	private Activity mActivity;
	private TextView mTvTitleDialog;
	private ProcessDialogFeedback mProcessDialogMove;
	private TextView mTvValueRate;
	private LinearLayout mLlRating;
	private SeekBarRating<Integer> mSeekBarRating;
	private int mMaxValueRating = 0;
	private List<String> mListValues;

	/**
	 * This value use to keep value when user running rating seekbar
	 */

	public DialogMyFeedback(Context context, boolean isShowSave,
			boolean isShowCancel, ProcessDialogFeedback processMove,
			int mValue, String title) {
		super(context);
		mActivity = (Activity) context;
		this.mMaxValueRating = mValue;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.dialog_myfeedback);
		mProcessDialogMove = processMove;
		mBtn_save = (Button) findViewById(R.id.btn_submit);
		mBtn_cancel = (Button) findViewById(R.id.btn_cancel);
		mTv_comment = (EditText) findViewById(R.id.tv_comment_rating);
		mBtn_save.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
		mTvValueRate = (TextView) findViewById(R.id.tv_value_rate);
		mLlRating = (LinearLayout) findViewById(R.id.dialog_custom_rating_ll);
		mSeekBarRating = new SeekBarRating<Integer>(0, 12, mActivity);
		mSeekBarRating.setDrawMax(false);
		setCanceledOnTouchOutside(true);
		mListValues = new ArrayList<String>();
		for (int i = 12; i >= 0; i--) {
			mListValues.add(Utils.exchangeRateGetExtra(i + 1) + "");
		}
		mSeekBarRating.setValue(mListValues);
		mSeekBarRating
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							SeekBarRating<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxValueRating = Utils.exchangeRatePost(minValue);
					}
				});

		LinearLayout layout5 = (LinearLayout) findViewById(R.id.dialog_custom_rating_ll);
		layout5.addView(mSeekBarRating);
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
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	/**
	 * This method user use to draw text on seekbar
	 */
	public void drawText(String value, int pos) {
		mTvValueRate.setPadding(pos, 0, 0, 0);
		mTvValueRate.setText(value);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtn_cancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtn_save) {
			mProcessDialogMove.click_Ok();
			dismiss();
		}
	}

	/**
	 * @return value when user rated
	 */
	public int rateScore() {
		return mMaxValueRating;
	}

	/**
	 * @return value when user commented
	 */
	public String rateComment() {

		return mTv_comment.getText().toString();
	}

	/**
	 * @return current datetime of system to get for feedback customer
	 */
	public String rateDateTime() {
		return Utils.getDateFormat();
	}

	/**
	 * @author Thanh Vu
	 * 
	 */
	public static abstract class ProcessDialogFeedback {
		public abstract void click_Ok();

		public abstract void click_Cancel();
	}
}

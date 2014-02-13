//package com.magrabbit.intelligentmenu.activity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//import com.flurry.android.FlurryAgent;
//import com.magrabbit.intelligentmenu.R;
//import com.magrabbit.intelligentmenu.utils.Constants;
//import com.magrabbit.intelligentmenu.utils.Utils;
//
///**
// * @author HungHoang Rating for mymenu app.
// * 
// */
//public class RatingMyMenuActivity extends BaseActivity {
//	private SeekBar mSeekBar;
//	private EditText mEtComent;
//	private TextView mTvValueRate;
//	private int mXPos;
//	private int mValueRate = 0;
//	/**
//	 * This value rate of customer.
//	 */
//	private int mValue = 0;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_rating_mymenu);
//
//		mSeekBar = (SeekBar) findViewById(R.id.rating_mymenu_seek_bar);
//		mEtComent = (EditText) findViewById(R.id.rating_mymenu_et_comment);
//		mTvValueRate = (TextView) findViewById(R.id.rating_mymenu_tv_value_rate);
//
//		mSeekBar.setMax(12);
//		mSeekBar.setProgress(mValue);
//		mXPos = ((mSeekBar.getRight() - mSeekBar.getLeft()) * mSeekBar
//				.getProgress()) / mSeekBar.getMax();
//		drawText(Utils.exchangeRating(mValue), Utils.xPos(mXPos, mValue));
//
//		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				mValueRate = seekBar.getProgress();
//				mXPos = ((mSeekBar.getRight() - mSeekBar.getLeft()) * mSeekBar
//						.getProgress()) / mSeekBar.getMax();
//				drawText(Utils.exchangeRating(mValueRate),
//						Utils.xPos(mXPos, mValueRate));
//			}
//		});
//	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
//	}
//
//	@Override
//	protected void onStop() {
//		FlurryAgent.onEndSession(this);
//		super.onStop();
//	}
//
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		mXPos = ((mSeekBar.getRight() - mSeekBar.getLeft()) * mSeekBar
//				.getProgress()) / mSeekBar.getMax();
//		drawText(Utils.exchangeRating(mValueRate),
//				Utils.xPos(mXPos, mValueRate));
//		super.onWindowFocusChanged(hasFocus);
//	}
//
//	/**
//	 * This method user use to draw text on seekbar
//	 */
//	public void drawText(String value, int pos) {
//		mTvValueRate.setPadding(pos, 0, 0, 0);
//		mTvValueRate.setText(value);
//	}
//
//	public void onClick_Submit(View v) {
//	}
//
//	public void onClick_Cancel(View v) {
//		finish();
//	}
//}

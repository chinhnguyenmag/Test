package com.magrabbit.intelligentmenu.customview;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.ServerCommentAdapter;
import com.magrabbit.intelligentmenu.adapter.ServerCommentAdapter.ProcessLoadRatingListServer;
import com.magrabbit.intelligentmenu.model.CommentsServerObject;
import com.magrabbit.intelligentmenu.model.ListCommentsServerEntity;
import com.magrabbit.intelligentmenu.model.ServerEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

public class DialogCustomerFeedbacksServer extends BaseDialog implements
		android.view.View.OnClickListener {

	private Button mBtDone;
	private RelativeLayout mLayoutReview;
	private Activity mActivity;
	private ListView mLvFeedback;
	private ProgressBar mPBLoadMore;
	private TextView mTvRateScore;
	private TextView mTvTotalReview;
	private LinearLayout mLlTitle;
	private ServerCommentAdapter mFeedbackAdapter;
	private List<CommentsServerObject> mListComments;
	private ServerEntity mServerEntity;
	private int mLimit = 10, mOffset = 0, mTotal = 0;
	private boolean mGetMore = false;
	private boolean mIsUpdateRestaurantRating = false;
	private float mRatingNumber;

	// For updating menu item rating
	private ProcessServerFeedBack mProcess;

	public DialogCustomerFeedbacksServer(Context context,
			ServerEntity serverEntity, boolean isUpdateRatingComment,
			float numberRating, ProcessServerFeedBack process) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.dialog_feedback_customer);
		mActivity = (Activity) context;
		this.mProcess = process;
		this.mServerEntity = serverEntity;
		this.mIsUpdateRestaurantRating = isUpdateRatingComment;
		this.mRatingNumber = numberRating;

		mBtDone = (Button) findViewById(R.id.dialog_feedback_customer_bt_done);
		mBtDone.setOnClickListener(this);
		mLayoutReview = (RelativeLayout) findViewById(R.id.dialog_feedback_customer_rl_review);
		mLayoutReview.setVisibility(View.GONE);
		mLvFeedback = (ListView) findViewById(R.id.dialog_feedback_customer_lv_feedback);
		mPBLoadMore = (ProgressBar) findViewById(R.id.dialog_feedback_customer_pb_loadmore);
		mTvRateScore = (TextView) findViewById(R.id.dialog_feedback_customer_tv_score);
		mTvTotalReview = (TextView) findViewById(R.id.dialog_feedback_customer_tv_reviews);
		mLlTitle = (LinearLayout) findViewById(R.id.dialog_feedback_customer_ll_title);

		Typeface type = Typeface.createFromAsset(mActivity.getAssets(),
				"fonts/amrtypen.ttf");
		mTvRateScore.setTypeface(type);
		mTvTotalReview.setTypeface(type);

		mLlTitle.setVisibility(View.VISIBLE);
		mTvTotalReview.setVisibility(View.INVISIBLE);

		mListComments = new ArrayList<CommentsServerObject>();
		mFeedbackAdapter = new ServerCommentAdapter(mContext,
				R.layout.dialog_feedback_customer, mListComments,
				new ProcessLoadRatingListServer() {

					@Override
					public void loadRatingListAgain(float ratingNumber) {
						mProcess.update_rating(ratingNumber);
						dismiss();
					}

					@Override
					public void process_lostAccessToken() {
						mActivity.setResult(CodeRequest.CODE_RESULT_LOGOUT);
						mActivity.finish();
					}
				});
		mLvFeedback.setAdapter(mFeedbackAdapter);
		mLvFeedback.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (mOffset >= mTotal
						|| scrollState == OnScrollListener.SCROLL_STATE_FLING) {
					mPBLoadMore.setVisibility(View.GONE);
				} else if (mGetMore
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (!mPBLoadMore.isShown()) {
						mPBLoadMore.setVisibility(View.VISIBLE);
						new ListCommentsRatingAsyncTask().execute();
					}
				} else if (!mGetMore) {
					mPBLoadMore.setVisibility(View.GONE);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mGetMore = false;
				if (mLvFeedback.getLastVisiblePosition() + 1 == totalItemCount) {
					mGetMore = true;
				}
			}
		});
	}

	@Override
	public void show() {
		super.show();
		try {
			showProgress(R.string.mess_waitting);
			new ListCommentsRatingAsyncTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtDone) {
			dismiss();
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetData() {
		mListComments.clear();
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of textBox
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

	public class ListCommentsRatingAsyncTask extends
			AsyncTask<String, Void, ListCommentsServerEntity> {

		@Override
		protected ListCommentsServerEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlListCommentsServer(
								UserObject.getInstance(mContext)
										.getAccess_token(), mServerEntity
										.getId(), mLimit, mOffset));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				ListCommentsServerEntity response = gson.fromJson(json,
						ListCommentsServerEntity.class);

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ListCommentsServerEntity result) {
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(mActivity)) {
						Toast.makeText(
								mActivity,
								mActivity.getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								mActivity,
								mActivity.getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					mActivity.setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					mActivity.finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.mess_error_server),
							Toast.LENGTH_LONG).show();
					dismissProgress();
				} else {
					mTotal = result.mRatingCount;
					// Set Rating Number
					if (mIsUpdateRestaurantRating) {
						// Update rating
						if (mRatingNumber >= 13) {
							mTvRateScore.setText("F");
						} else {
							mTvRateScore.setText(Utils
									.exchangeRateGetExtra(mRatingNumber));
						}

					} else {
						// Not update rating
						if (mRatingNumber >= 13) {
							mTvRateScore.setText("F");
						} else {
							mTvRateScore.setText(Utils
									.exchangeRateGetExtra(result.mRatings));
						}
					}

					for (CommentsServerObject commentsEntity : result.mListComments) {
						mListComments.add(commentsEntity);
					}
					// mListComments.addAll(result.mListComments);
					mFeedbackAdapter.notifyDataSetChanged();
					mOffset = mListComments.size();
				}
				mPBLoadMore.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}
	}

	public static abstract class ProcessServerFeedBack {
		public abstract void update_rating(float ratingNumber);
	}

}

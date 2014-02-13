package com.magrabbit.intelligentmenu.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyNotificationMessageAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMynotificationReplyMessage;
import com.magrabbit.intelligentmenu.customview.DialogMynotificationReplyMessage.ProcessDialogConfirmRelply;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener.OnSwipeCallback;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.DeleteNotificationByMessageEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationMessageEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationMessageListEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationMessageReplyEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationRestaurantListEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * @Description This class use to load all notification of message
 */
public class MyNotificationMessageActivity extends BaseActivity implements
		OptionMenuClickListener {

	private ListView mListview;
	private MyNotificationMessageAdapter mMessageAdapter;
	List<MyNotificationMessageListEntity> mListMessageObject;
	private MyNotificationRestaurantListEntity restaurantObject;
	private MyNotificationMessageListEntity messageObject;
	private ImageView mLogo;
	private TextView mTitle;
	private ImageView mLogoType;
	private String mBdLogo;
	private ImageLoader mImageLoader;
	private DialogMynotificationReplyMessage mDialogReplyMessage;
	private Button mBtReply;
	private boolean mIsShowTvComment = false;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mIsShowOnlyCheckBox = false;
	private boolean mIsFirstTimeToLoad = false;
	private DialogConfirm dialogConfirm;
	private boolean mIsInvalidComment = false;
	private int[] mReverseSortedPositions;
	private int mId;
	public static int mIntentMessageId;
	public static boolean mIntentStatus = false; 

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mynotification_message);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mListview = (ListView) findViewById(R.id.mynotification_message_listview);
		mLogo = (ImageView) findViewById(R.id.mynotification_message_iv_restaurant_logo);
		mTitle = (TextView) findViewById(R.id.mynotification_message_tv_msg_subject);
		mLogoType = (ImageView) findViewById(R.id.mynotification_message_iv_type_logo);
		mBtReply = (Button) findViewById(R.id.mynotification_message_bt_reply);
		mImageLoader = new ImageLoader(this);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		mIsFirstTimeToLoad = true;

		Bundle bGetGlobalỌbject = getIntent().getExtras();
		if (bGetGlobalỌbject != null) {
			restaurantObject = bGetGlobalỌbject
					.getParcelable(StringExtraUtils.KEY_MYNOTIFICATION_RESTAURANT);

			mBdLogo = bGetGlobalỌbject
					.getString(StringExtraUtils.KEY_MYNOTIFICATION_RESTAURANT_LOGO);
		}

		try {
			mImageLoader.DisplayImage(ServerURL.URL + mBdLogo, mLogo);
			mTitle.setText(restaurantObject.mMsgSubject);
			mImageLoader.DisplayImage(
					ServerURL.URL + restaurantObject.getAlertLogo(), mLogoType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		new GetListNotificationMessageTask().execute();

		SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(
				mListview, new OnSwipeCallback() {

					@Override
					public void onSwipeRight(ListView listView,
							int[] reverseSortedPositions) {
					}

					@Override
					public void onSwipeLeft(ListView listView,
							int[] reverseSortedPositions) {
						mReverseSortedPositions = reverseSortedPositions;
						try {
							DialogConfirm d = new DialogConfirm(
									MyNotificationMessageActivity.this, -1,
									"Delete Message",
									getString(R.string.delete_message), true,
									new ProcessDialogConfirm() {

										@Override
										public void click_Ok() {
											try {
												for (int position : mReverseSortedPositions) {
													mId = mMessageAdapter
															.getItem(position).mMsgId;
													mMessageAdapter
															.remove(mMessageAdapter
																	.getItem(position));

													// apply service delete !!!
													new DeleteNotificationByMessageAsyncTask()
															.execute(mId);
												}
												mMessageAdapter
														.notifyDataSetChanged();
											} catch (Exception e) {

											}
										}

										@Override
										public void click_Cancel() {
										}
									});

							d.show();
						} catch (Exception e) {
						}

					}
				}, false, false);
		mListview.setOnTouchListener(touchListener);
		mListview.setOnScrollListener(touchListener.makeScrollListener());

	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			if (mLayoutMenu != null) {
				if (mLayoutMenu.isShown()) {
					mLayoutMenu.setVisibility(View.GONE);
				} else {
					mLayoutMenu.setVisibility(View.VISIBLE);
					mLayoutMenu.startAnimation(mAnimationShow);

				}
			}

		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				MyNotificationMessageActivity.this, null, "", 0, true);
		dialog.show();
	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		} else {
			showToastMessage(getString(R.string.mess_error_network));
			return;
		}
	}

	@Override
	public void onThumbnailClickListener() {
		mLayoutMenu.setVisibility(View.GONE);

	}

	@Override
	public void onSearchClickListener() {
		mLayoutMenu.setVisibility(View.GONE);

	}

	@Override
	public void onFeedbackClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		myFeedback();
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(
					MyNotificationMessageActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyNotificationMessageActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyNotificationMessageActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyNotificationMessageActivity.this)) {
														new PostMyFeedBackAsynTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														showToastMessage(getResources()
																.getString(
																		R.string.mess_error_network));
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

											@Override
											public void click_Cancel() {
												mDialogConfirmRating.dismiss();
											}
										},
										Utils.exchangeRateGetExtra(mDialogMyFeedback
												.rateScore()),
										mDialogMyFeedback.rateComment());

								mDialogConfirmRating.show();

							} else {
								showToastMessage(getResources().getString(
										R.string.mess_error_network));
							}
						}

						@Override
						public void click_Cancel() {
							mDialogMyFeedback.dismiss();
						}
					}, 13, getResources().getString(
							R.string.custom_seebar_rate_tittle_menuitem));

		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialogMyFeedback.show();

	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYNOTIMESSAGE);
	}

	@Override
	public void onMyAccountClickListener() {
		startActivityForResult(new Intent(MyNotificationMessageActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYNOTIMESSAGE);
	}

	public void onClick_back(View v) {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CodeRequest.CODE_REQUEST_MYNOTIMESSAGE) {
			// Update Avatar after changing it from EditInfo
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
		}
	}

	/**
	 * This method use to reply message
	 */
	public void onClick_replyMessage(View v) {
		try {
			mDialogReplyMessage = new DialogMynotificationReplyMessage(
					MyNotificationMessageActivity.this,
					true,
					true,
					new ProcessDialogConfirmRelply() {

						@Override
						public void click_Ok(
								MyNotificationRestaurantListEntity restaurantObject,
								String mLogo,
								MyNotificationMessageListEntity messageObject) {

							if (!Utils
									.isNetworkConnected(MyNotificationMessageActivity.this)) {
								showToastMessage(getString(R.string.mess_error_network));
								return;
							} else {
								if (mIsShowTvComment) {
									if (!mDialogReplyMessage.message().equals(
											"")) {
										mIsInvalidComment = false;
										new ReplyMessageTask().execute();
									} else {
										mIsInvalidComment = true;
									}
								} else {
									// if (mDialogReplyMessage
									// .isValidateAcceptBox()) {
									new ReplyMessageTask().execute();
									// } else {
									// dialogConfirm = new DialogConfirm(
									// MyNotificationMessageActivity.this,
									// android.R.drawable.ic_dialog_alert,
									// "Accept Points",
									// getString(R.string.mess_error_validate_accept_point),
									// true,
									// new ProcessDialogConfirm() {
									//
									// @Override
									// public void click_Ok() {
									// dialogConfirm.dismiss();
									// }
									//
									// @Override
									// public void click_Cancel() {
									// dialogConfirm.dismiss();
									// }
									// });
									// dialogConfirm.show();
									// }
								}
							}

						}

						@Override
						public void click_Cancel() {
						}
					}, mBdLogo, messageObject, mIsShowTvComment,
					mIsShowOnlyCheckBox);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDialogReplyMessage.show();
	}

	public class GetListNotificationMessageTask extends
			AsyncTask<String, Void, MyNotificationMessageEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MyNotificationMessageEntity doInBackground(String... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlMyNotificationMessage(
								UserObject
										.getInstance(MyNotificationMessageActivity.this).mAccess_token,
								restaurantObject.mId));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyNotificationMessageEntity response = gson.fromJson(json,
						MyNotificationMessageEntity.class);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyNotificationMessageEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationMessageActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(getString(R.string.mess_error_server));
					}
					dismissProgress();
					finish();
					return;
				}

				if (Server.mStatus != 200) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MyNotificationMessageActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mListMessageObject == null) {
						mListMessageObject = new ArrayList<MyNotificationMessageListEntity>();
					}
					mListMessageObject.clear();
					mListMessageObject.addAll(result.mMessage);

					if (mListMessageObject.size() > 0) {
						messageObject = mListMessageObject.get(0);

						// TODO
						if (messageObject.mMsgType.toUpperCase().trim()
								.equals("SINGLE")) {
							mBtReply.setVisibility(View.GONE);
						} else {
							if (messageObject.mAlertType.trim().toUpperCase()
									.equals("SHARING POINTS")) {
								mIsShowOnlyCheckBox = false;
								if (messageObject.mReceived == 0) {
									mIsShowOnlyCheckBox = true;
									mBtReply.setVisibility(View.VISIBLE);
								} else {
									mIsShowOnlyCheckBox = false;
									mBtReply.setVisibility(View.GONE);
								}
							} else {
								mBtReply.setVisibility(View.VISIBLE);
								if (messageObject.mReceived == 0
										&& messageObject.mPoint > 0) {
									mIsShowOnlyCheckBox = true;
								} else {
									mIsShowOnlyCheckBox = false;
								}
								mIsShowTvComment = true;
							}
						}
						mMessageAdapter = new MyNotificationMessageAdapter(
								MyNotificationMessageActivity.this,
								R.id.mynotification_message_listview,
								mListMessageObject);
						mMessageAdapter.notifyDataSetChanged();
						mListview.setAdapter(mMessageAdapter);
						dismissProgress();
					} else {
						if (MyNotificationChainActivity.mCheckStatus) {
							mIntentMessageId = mId;
							mIntentStatus = true;
						}
						dismissProgress();
					}
				}

			} catch (Exception e) {
				dismissProgress();
				e.printStackTrace();
			}

		}
	}

	public class ReplyMessageTask extends
			AsyncTask<String, Void, MyNotificationMessageReplyEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyNotificationMessageReplyEntity doInBackground(
				String... params) {
			try {
				InputStream source = Server.requestPost(ServerURL
						.getUrlReplyMessage(), ServerURL.getEntityReplyMessage(
						UserObject.getInstance(
								MyNotificationMessageActivity.this)
								.getAccess_token(), mDialogReplyMessage
								.fromUser(),
						mDialogReplyMessage.restaurantId(), mDialogReplyMessage
								.messageId(), mDialogReplyMessage.accepted(),
						mDialogReplyMessage.points(), mDialogReplyMessage
								.message(), mDialogReplyMessage.alertType()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyNotificationMessageReplyEntity response = gson.fromJson(json,
						MyNotificationMessageReplyEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyNotificationMessageReplyEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationMessageActivity.this)) {
						showToastMessage(getString(R.string.mess_error_network));
					} else {
						showToastMessage(getString(R.string.mess_error_server));
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
					showToastMessage(getResources().getString(
							R.string.mess_invalid_accesstoken));
					finish();
					return;
				}

				if (result.mError != null) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					if (result.mStatus != null) {
						if (result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_SUCCESS)) {
							if (mDialogReplyMessage.isValidateAcceptBox()) {
								showToastMessage("Points has been received successfully.");
							} else {
								showToastMessage("Message has been sent successfully.");
							}
							new GetListNotificationMessageTask().execute();
							dismissProgress();
						} else if (result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
							showToastMessage(getResources().getString(
									R.string.mess_error_server));
							dismissProgress();
						} else {
							showToastMessage(result.mStatus);
							dismissProgress();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class DeleteNotificationByMessageAsyncTask extends
			AsyncTask<Integer, Void, DeleteNotificationByMessageEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected DeleteNotificationByMessageEntity doInBackground(
				Integer... params) {
			try {
				InputStream source = null;
				source = Server.requestPost(ServerURL
						.getUrlDeleteNotificationByMessage(), ServerURL
						.getEntityDeleteNotificationByMessage(
								UserObject.getInstance(
										MyNotificationMessageActivity.this)
										.getAccess_token(), params[0]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				DeleteNotificationByMessageEntity response = gson.fromJson(
						json, DeleteNotificationByMessageEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(DeleteNotificationByMessageEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationMessageActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
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

				if (result.mError != null) {
					Toast.makeText(MyNotificationMessageActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				}
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					showToastMessage("Message has been deleted successfully !");
					new GetListNotificationMessageTask().execute();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class PostMyFeedBackAsynTask extends
			AsyncTask<String, Void, MyFeedBackEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MyFeedBackEntity doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server.requestPost(ServerURL.getUrlMyFeedback(),
						ServerURL.getEntityMyFeedBack(
								UserObject.getInstance(
										MyNotificationMessageActivity.this)
										.getAccess_token(), mDialogMyFeedback
										.rateScore(), mDialogMyFeedback
										.rateComment()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFeedBackEntity response = gson.fromJson(json,
						MyFeedBackEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFeedBackEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils
							.isNetworkConnected(MyNotificationMessageActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
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
					showToastMessage(getResources().getString(
							R.string.mess_invalid_accesstoken));
					finish();
					return;
				}

				if (result.mError != null) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					if (result.mStatus != null) {
						if (result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
							showToastMessage(getResources().getString(
									R.string.mess_error_server));
							dismissProgress();
						} else {
							showToastMessage(getString(R.string.dialog_my_feedback_mess_success));
							dismissProgress();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

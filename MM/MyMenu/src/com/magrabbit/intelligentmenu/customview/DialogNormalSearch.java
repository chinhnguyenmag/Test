package com.magrabbit.intelligentmenu.customview;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConfirmCreateProfile.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogCreateNewProfile.ProcessDialogConfirm_CreateNewProfile;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.SearchProfile;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * 
 * @author baonguyen
 * @Description : Dialog for showing Search Normal Option
 */
public class DialogNormalSearch extends BaseDialog implements OnClickListener {
	private ProcessDialogConfirm_NormalSearch mProcessDialogMove;
	private Button mBtnSubmit;
	private Button mBtnCancel;
	private EditText mEdtKeywords;
	private CheckBox mCbxDefaultSearch;
	private Context context;

	private String mOldSearchKeywords;

	// For creating a new search profile
	private boolean mIsFirstProfile;
	private List<SearchProfile> mListSearchProfiles;
	private DialogCreateNewProfile dialog_create;
	private DialogConfirmCreateProfile dialog_confirm;
	AppPreferences mAppPreferences;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogNormalSearch(Context context, String restaurantName,
			ProcessDialogConfirm_NormalSearch processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// /** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_search_normal);
		this.context = context;
		mProcessDialogMove = processMove;

		mBtnSubmit = (Button) findViewById(R.id.dialog_search_normal_btn_submit);
		mBtnSubmit.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.dialog_search_normal_btn_cancel);
		mBtnCancel.setOnClickListener(this);
		mEdtKeywords = (EditText) findViewById(R.id.dialog_search_normal_edt_keywords);
		// Set the old keywords and inflate it into variable "oldSearchKeywords"
		mEdtKeywords.setText(restaurantName);
		mEdtKeywords.requestFocus();
		mOldSearchKeywords = restaurantName;
		mCbxDefaultSearch = (CheckBox) findViewById(R.id.dialog_search_normal_checkbox);
		mCbxDefaultSearch.setOnClickListener(this);
		mAppPreferences = new AppPreferences(mContext);
		mListSearchProfiles = new ArrayList<SearchProfile>();
	}

	/**
	 * @Description : Handle the click events on Buttons
	 */
	@Override
	public void onClick(View v) {
		if (v == mBtnCancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtnSubmit) {
			if (Utils.isNetworkConnected(mContext)) {
				if (mCbxDefaultSearch.isChecked()) {
					if (UserObject.getInstance(mContext).getDefaultProfileId() == 0) {
						dialog_confirm = new DialogConfirmCreateProfile(
								mContext,
								android.R.drawable.ic_dialog_alert,
								"Warning",
								mContext.getString(R.string.dialog_search_normal_create_new_profile_confirm),
								new ProcessDialogConfirm() {

									@Override
									public void click_Create() {
										mIsFirstProfile = true;
										dialog_create = new DialogCreateNewProfile(
												mContext,
												false,
												-1,
												mIsFirstProfile,
												mListSearchProfiles,
												new ProcessDialogConfirm_CreateNewProfile() {

													@Override
													public void click_Search(
															SearchProfile searchprofile) {
														// Push search profile
														// to MapViewActivity
														// for searching
														mProcessDialogMove
																.click_Search(
																		searchprofile,
																		false);
														dialog_create.dismiss();
														dialog_confirm
																.dismiss();

													}

													@Override
													public void click_Save(
															List<SearchProfile> listSearchProfiles,
															int indexOfAddedProfile) {
														if (Utils
																.isNetworkConnected(mContext)) {
															new getAllSearchProfilesTask()
																	.execute();
														} else {
															Toast.makeText(
																	mContext,
																	mContext.getResources()
																			.getString(
																					R.string.mess_error_network),
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}

													@Override
													public void click_ChangeButtonBackground() {
														// TODO Auto-generated
														// method stub

													}
												});
										dialog_create.show();
									}

									@Override
									public void click_Cancel() {
										dialog_confirm.dismiss();

									}
								});
						dialog_confirm.show();
					} else {
						// Add code to send url to request searching Default
						// Profile
						mProcessDialogMove.click_Submit_DefaultSearch();
						dismiss();
					}
				} else {
					String keywords = mEdtKeywords.getText().toString().trim();
					if (keywords != null && keywords.length() == 0)
						Toast.makeText(context,
								"Please enter keywords to search",
								Toast.LENGTH_SHORT).show();
					else {
						String alphaAndDigits = keywords.replaceAll(
								"[^a-zA-Z0-9' ]+", " ");
						// alphaAndDigits = alphaAndDigits.replaceAll(" ",
						// "%20");
						mProcessDialogMove.click_Submit_NormalSearch(
								alphaAndDigits, keywords);
						dismiss();
					}
				}
			} else {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.mess_error_network),
						Toast.LENGTH_SHORT).show();
			}
		} else if (v == mCbxDefaultSearch) {
			if (mCbxDefaultSearch.isChecked()) {
				// Disable Keyword textBox to prevent users from inputing and get the old text value
				mOldSearchKeywords = mEdtKeywords.getText().toString();
				mEdtKeywords.setEnabled(false);
				mEdtKeywords.setText("");
			} else {
				// Enable Keyword textBox for users to input
				mEdtKeywords.setEnabled(true);
				mEdtKeywords.setText(mOldSearchKeywords);
			}
		}
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Get all search profiles when going to MySearch screen
	 */
	public class getAllSearchProfilesTask extends
			AsyncTask<Void, Void, SearchProfile[]> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected SearchProfile[] doInBackground(Void... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlGetAllSearchProfile(mContext));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SearchProfile[] response = gson.fromJson(json,
						SearchProfile[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchProfile[] result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(mContext)) {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					mListSearchProfiles.clear();
					for (int i = 0; i < result.length; i++) {
						mListSearchProfiles.add(result[i]);
					}
					// Update default search profile id
					UserObject.getInstance(mContext).mDefaultProfileId = mListSearchProfiles
							.get(0).getId();

					mAppPreferences.setDefaultsearchprofile(mListSearchProfiles
							.get(0).getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
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

	public static abstract class ProcessDialogConfirm_NormalSearch {
		public abstract void click_Submit_NormalSearch(String keywordsFilled,
				String keywordsNormal);

		public abstract void click_Submit_DefaultSearch();

		public abstract void click_Cancel();

		// For searching advanced when creating a brand new search profile
		public abstract void click_Search(SearchProfile searchprofile,
				boolean isRunDefault);
	}
}

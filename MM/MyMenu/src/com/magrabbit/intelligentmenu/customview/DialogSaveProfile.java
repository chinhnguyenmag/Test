package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.SearchProfile;

/**
 * 
 * 
 * @author baognguyen
 * @Description : Dialog for showing Saving Profile Option
 */
public class DialogSaveProfile extends Dialog implements OnClickListener {
	private ProcessDialogConfirm_SaveProfile mProcessDialogMove;
	private Button mBtnSave;
	private Button mBtnCancel;
	private EditText mEdtProfileName;
	private Context context;

	// Edit Mode for profiles
	private boolean mIsEditMode;
	private int mEditedProfilePostion;
	private List<SearchProfile> mListSearchProfiles;
	private List<SearchProfile> mListNewSearchProfiles;

	// Max Search distance
	private int mMaxSearchDistance;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogSaveProfile(Context context, boolean isEditMode,
			int editedProfilePos, List<SearchProfile> listSearchProfiles,
			int maxSearchDistance, ProcessDialogConfirm_SaveProfile processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// /** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_save_profile);
		this.context = context;
		this.mProcessDialogMove = processMove;
		this.mIsEditMode = isEditMode;
		this.mEditedProfilePostion = editedProfilePos;
		this.mMaxSearchDistance = maxSearchDistance;
		this.mListNewSearchProfiles = new ArrayList<SearchProfile>();
		if (this.mListSearchProfiles == null) {
			this.mListSearchProfiles = new ArrayList<SearchProfile>();
		}
		this.mListSearchProfiles.clear();
		if (listSearchProfiles != null) {
			for (SearchProfile s : listSearchProfiles) {
				this.mListSearchProfiles.add(s.clone());
				this.mListNewSearchProfiles.add(s.clone());
			}
		}
		if (mIsEditMode) {
			// Get a new list of search profiles without edited profile
			this.mListNewSearchProfiles.remove(mEditedProfilePostion);
		}

		mBtnSave = (Button) findViewById(R.id.dialog_save_profile_btnSave);
		mBtnSave.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.dialog_save_profile_btnCancel);
		mBtnCancel.setOnClickListener(this);
		mEdtProfileName = (EditText) findViewById(R.id.dialog_save_profile_edtName);

		// Set name of profile if it is editMode
		if (mIsEditMode) {
			mEdtProfileName.setText(this.mListSearchProfiles.get(
					mEditedProfilePostion).getNameProfile());
			mEdtProfileName.requestFocus();
		}
	}

	/**
	 * @Description : Handle the click events
	 */
	@Override
	public void onClick(View v) {
		if (v == mBtnCancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtnSave) {
			String name = mEdtProfileName.getText().toString();
			if (name != null && name.length() == 0) {
				Toast.makeText(
						context,
						context.getString(R.string.dialog_save_profile_invalidate_blank_name),
						Toast.LENGTH_SHORT).show();
			} else if (checkNameExist(name) && (!mIsEditMode)) {
				// Check whether profile name is existing or not when Add
				Toast.makeText(
						context,
						context.getString(R.string.dialog_save_profile_invalidate_exist_name),
						Toast.LENGTH_SHORT).show();
			} else if ((mIsEditMode) && checkNameExistException(name)) {
				// Check whether profile name is existing or not when Edit
				Toast.makeText(
						context,
						context.getString(R.string.dialog_save_profile_invalidate_exist_name),
						Toast.LENGTH_SHORT).show();
			} else {
				mProcessDialogMove.click_Save(name);
				dismiss();
			}
		}
	}

	public boolean checkNameExist(String name) {
		if (mListSearchProfiles != null) {
			for (SearchProfile s : mListSearchProfiles) {
				if (name.equalsIgnoreCase(s.getNameProfile())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkNameExistException(String name) {
		if (mListNewSearchProfiles != null) {
			for (SearchProfile s : mListNewSearchProfiles) {
				if (name.equalsIgnoreCase(s.getNameProfile())) {
					return true;
				}
			}
		}
		return false;
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
		public abstract void click_Save(String name);

		public abstract void click_Cancel();
	}
}

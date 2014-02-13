package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;

/**
 * 
 * 
 * @author baonguyen
 * @Description : Dialog for showing Search Normal Option
 */
public class DialogSearchNormal extends BaseDialog implements OnClickListener {
	private ProcessDialogConfirm_NormalSearch mProcessDialogMove;
	private Button mBtnSubmit;
	private Button mBtnCancel;
	private EditText mEdtKeywords;
	private CheckBox mCbxDefaultSearch;
	private Context context;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogSearchNormal(Context context,
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
		mCbxDefaultSearch = (CheckBox) findViewById(R.id.dialog_search_normal_checkbox);
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
			if (mCbxDefaultSearch.isChecked()) {
				// Add code to send url to request searching Default Profile

			} else {
				String keywords = mEdtKeywords.getText().toString();
				if (keywords != null && keywords.length() == 0)
					Toast.makeText(context, "Please enter keywords to search",
							Toast.LENGTH_SHORT).show();
				else {
					mProcessDialogMove.click_Submit(keywords);
					dismiss();
				}
			}
		}
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of textbox
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
		public abstract void click_Submit(String keywords);

		public abstract void click_Cancel();
	}
}

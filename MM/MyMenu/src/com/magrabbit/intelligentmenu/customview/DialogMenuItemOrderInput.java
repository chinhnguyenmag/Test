package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.magrabbit.intelligentmenu.R;

public class DialogMenuItemOrderInput extends BaseDialog implements
		android.view.View.OnClickListener {

	private Button mBtCancel;
	private Button mBtSubmit;
	private EditText mEtNumberQuantity;
	private Context mContext;
	private ProcessDialogConfirmOrder mConfirmOrder;

	public DialogMenuItemOrderInput(Context context,
			ProcessDialogConfirmOrder pro, String quantity) {
		super(context);
		this.mContext = context;
		this.mConfirmOrder = pro;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_menu_item_order_textbox);
		this.mBtCancel = (Button) findViewById(R.id.dialog_menuitem_order_bt_cancel);
		this.mBtSubmit = (Button) findViewById(R.id.dialog_menuitem_order_bt_submit);
		this.mEtNumberQuantity = (EditText) findViewById(R.id.dialog_menuitem_order_et_number);
		this.mBtCancel.setOnClickListener(this);
		this.mBtSubmit.setOnClickListener(this);
		mEtNumberQuantity.setText(quantity);
		mEtNumberQuantity.setSelection(mEtNumberQuantity.getText().toString()
				.trim().length());
	}

	@Override
	public void onClick(View v) {
		if (v == this.mBtCancel) {
			mConfirmOrder.click_Cancel();
			this.dismiss();
		} else if (v == this.mBtSubmit) {
			mConfirmOrder.click_Ok();
			this.dismiss();
		}
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

	public String getQuantity() {
		if (mEtNumberQuantity.getText().length() != 0) {
			return mEtNumberQuantity.getText().toString().trim();
		} else
			return "0";
	}

	public static abstract class ProcessDialogConfirmOrder {
		public abstract void click_Ok();

		public abstract void click_Cancel();
	}

}

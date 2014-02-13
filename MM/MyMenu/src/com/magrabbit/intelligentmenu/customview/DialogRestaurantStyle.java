package com.magrabbit.intelligentmenu.customview;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MenuTypeAdapter;
import com.magrabbit.intelligentmenu.model.MenuTypeObject;

/**
 * 
 * @author baognguyen
 * @Description: Dialog for showing Restaurant Style option
 */
public class DialogRestaurantStyle extends Dialog implements OnClickListener {
	private ProcessDialogConfirm_RestaurantStyle mProcessDialogMove;
	private Button mBtnClose;
	private ListView mLvRestaurant;
	public List<MenuTypeObject> mListMenuType;
	private MenuTypeAdapter mAdapterMenuType;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @param mListCheckBoxValues
	 * @description : Create Restaurant Style dialog with passed
	 *              mListCheckBoxValues
	 */
	public DialogRestaurantStyle(Context context,
			List<Boolean> mListCheckBoxValues,
			ProcessDialogConfirm_RestaurantStyle processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_restaurant_style);
		mProcessDialogMove = processMove;

		mBtnClose = (Button) findViewById(R.id.dialog_restaurant_type_btClose);
		mBtnClose.setOnClickListener(this);
	}

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 */
	public DialogRestaurantStyle(Context context,
			ProcessDialogConfirm_RestaurantStyle processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_restaurant_style);
		mProcessDialogMove = processMove;

		mBtnClose = (Button) findViewById(R.id.dialog_restaurant_type_btClose);
		mBtnClose.setOnClickListener(this);

	}

	public static abstract class ProcessDialogConfirm_RestaurantStyle {

		public abstract void click_Close();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnClose) {
			mProcessDialogMove.click_Close();
			dismiss();
		}

	}
}

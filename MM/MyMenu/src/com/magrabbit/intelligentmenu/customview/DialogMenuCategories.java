package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.magrabbit.intelligentmenu.R;

/**
 * 
 * @author baognguyen
 * @Description: Dialog for showing Restaurant Style option
 */
public class DialogMenuCategories extends Dialog implements OnClickListener {
	private ProcessDialogConfirm_MenuCategories mProcessDialogMove;
	private Button mBtnClose;
	private CheckBox mCbxAppetizers;
	private CheckBox mCbxDrinks;
	private CheckBox mCbxSalads;
	private CheckBox mCbxSandwiches;
	private CheckBox mCbxDessert;
	private CheckBox mCbxSpecials;
	private CheckBox mCbxMainDishes;
	// List of checkBox values
	public static List<Boolean> mListCheckBoxResStyle;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @param mListCheckBoxValues
	 * @description : Create Restaurant Style dialog with passed
	 *              mListCheckBoxValues
	 */
	public DialogMenuCategories(Context context,
			List<Boolean> mListCheckBoxValues,
			ProcessDialogConfirm_MenuCategories processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_menu_categories);
		mProcessDialogMove = processMove;
		mListCheckBoxResStyle = new ArrayList<Boolean>();
		mListCheckBoxResStyle = mListCheckBoxValues;

		mBtnClose = (Button) findViewById(R.id.dialog_menu_categories_btClose);
		mBtnClose.setOnClickListener(this);
		
		// Inflate checkBox element from layout
		mCbxAppetizers = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx1);
		mCbxDrinks = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx2);
		mCbxSalads = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx3);
		mCbxSandwiches = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx4);
		mCbxDessert = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx5);
		mCbxSpecials = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx6);
		mCbxMainDishes = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx7);
		
		// Apply saved profile values into list of checkBox in MenuType dialog
		mCbxAppetizers.setChecked(mListCheckBoxResStyle.get(0));
		mCbxDrinks.setChecked(mListCheckBoxResStyle.get(1));
		mCbxSalads.setChecked(mListCheckBoxResStyle.get(2));
		mCbxSandwiches.setChecked(mListCheckBoxResStyle.get(3));
		mCbxDessert.setChecked(mListCheckBoxResStyle.get(4));
		mCbxSpecials.setChecked(mListCheckBoxResStyle.get(5));
		mCbxMainDishes.setChecked(mListCheckBoxResStyle.get(6));
	}

	/**
	 * 
	 * @param context
	 * @param processMove : Process events
	 */
	public DialogMenuCategories(Context context,
			ProcessDialogConfirm_MenuCategories processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_menu_categories);
		mProcessDialogMove = processMove;

		mListCheckBoxResStyle= new ArrayList<Boolean>(); 
		
		mBtnClose = (Button) findViewById(R.id.dialog_menu_categories_btClose);
		mBtnClose.setOnClickListener(this);
		
		// Inflate checkBox element from layout
		mCbxAppetizers = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx1);
		mCbxDrinks = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx2);
		mCbxSalads = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx3);
		mCbxSandwiches = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx4);
		mCbxDessert = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx5);
		mCbxSpecials = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx6);
		mCbxMainDishes = (CheckBox) findViewById(R.id.dialog_menu_categories_cbx7);
	}

	public static abstract class ProcessDialogConfirm_MenuCategories {

		public abstract void click_Close();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnClose) {			
			// Save checkBox values into list
			mListCheckBoxResStyle.clear();
			mListCheckBoxResStyle.add(mCbxAppetizers.isChecked());
			mListCheckBoxResStyle.add(mCbxDrinks.isChecked());
			mListCheckBoxResStyle.add(mCbxSalads.isChecked());
			mListCheckBoxResStyle.add(mCbxSandwiches.isChecked());
			mListCheckBoxResStyle.add(mCbxDessert.isChecked());
			mListCheckBoxResStyle.add(mCbxSpecials.isChecked());
			mListCheckBoxResStyle.add(mCbxMainDishes.isChecked());
			
			mProcessDialogMove.click_Close();
			dismiss();
		}

	}
}

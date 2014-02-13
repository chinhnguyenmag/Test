package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MenuTypeAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.model.MenuTypeObject;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * *
 * 
 * @author baonguyen
 * @Description: Dialog for showing Menu Type option
 */
public class DialogMenuType extends Dialog implements OnClickListener {
	private ProcessDialogConfirm_Menu_Type mProcessDialogMove;
	private Button mBtnClose;
	private Button mBtnSave;
	private ListView mLvMenuItem;
	private TextView mTvTitle;
	private Context mContext;
	// List of checkBox value to transfer to AdvancedSearchActivity.java
	private List<MenuTypeObject> mListMenuType;
	private MenuTypeAdapter mAdapterMenuType;

	/**
	 * 
	 * @param context
	 * @param mListCheckBoxValues
	 * @param processMove
	 *            : Process events
	 * @Description: Create MenuType dialog with mListCheckBoxValues which is
	 *               passed through
	 */
	public DialogMenuType(Context context, List<MenuTypeObject> listMenuType,
			String title, ProcessDialogConfirm_Menu_Type processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.mContext = context;
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_menu_type);

		mProcessDialogMove = processMove;
		mTvTitle = (TextView) findViewById(R.id.dialog_menu_type_title);
		mTvTitle.setText(title);
		mBtnClose = (Button) findViewById(R.id.dialog_menu_type_btClose);
		mBtnClose.setOnClickListener(this);
		mBtnSave = (Button) findViewById(R.id.dialog_menu_type_btn_save);
		mBtnSave.setOnClickListener(this);

		mLvMenuItem = (ListView) findViewById(R.id.dialog_menu_type_lv);
		this.mListMenuType = new ArrayList<MenuTypeObject>();
		for (MenuTypeObject menuTypeObject : listMenuType) {
			this.mListMenuType.add(menuTypeObject.clone());
		}
		mAdapterMenuType = new MenuTypeAdapter(context, this.mListMenuType);
		mLvMenuItem.setAdapter(mAdapterMenuType);
	}

	/**
	 * @Description : Handle the click events
	 */
	@Override
	public void onClick(View v) {
		if (v == mBtnClose) {
			dismiss();
		} else if (v == mBtnSave) {
			// Save checkBox value into list
			DialogConfirm dialog = new DialogConfirm(mContext,
					android.R.drawable.ic_dialog_alert, "Save",
					mContext.getString(R.string.dialog_menu_type_save_confirm),
					true, new ProcessDialogConfirm() {

						@Override
						public void click_Ok() {
							if (Utils.isNetworkConnected(mContext)) {
								mProcessDialogMove.click_Close(mListMenuType);
								dismiss();
							} else {
								Toast.makeText(
										mContext,
										mContext.getResources().getString(
												R.string.mess_error_network),
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void click_Cancel() {

						}
					});
			dialog.show();
		}
	}

	public static abstract class ProcessDialogConfirm_Menu_Type {

		public abstract void click_Close(List<MenuTypeObject> listMenuType);
	}
}

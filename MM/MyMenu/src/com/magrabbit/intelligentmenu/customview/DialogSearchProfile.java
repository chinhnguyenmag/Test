package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.magrabbit.intelligentmenu.R;

/**
 * 
 * 
 * @author baonguyen
 * @Description : Dialog for showing Search Profile Option
 */
public class DialogSearchProfile extends Dialog implements OnClickListener {

	public int mIndexOfItemSelected = -1;
	private List<String> mListNameProfiles;

	private ArrayAdapter<String> mProfileAdapter;
	private ProcessDialogConfirm_SearchProfile mProcessDialogMove;
	private Button btnAdd;
	private Button btnClose;
	private ListView lvProfile;

	/**
	 * 
	 * @param context
	 * @param processMove
	 *            : Process events
	 * @Description: Showing with customer's confirmation
	 */
	public DialogSearchProfile(Context context,
			ProcessDialogConfirm_SearchProfile processMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// /** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_profile);
		mProcessDialogMove = processMove;

		btnAdd = (Button) findViewById(R.id.dialog_profile_btnAdd);
		btnClose = (Button) findViewById(R.id.dialog_profile_btClose);
		btnAdd.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		lvProfile = (ListView) findViewById(R.id.dialog_profile_lvProfile);

		// Initialize list of profile names to add into layout
		mListNameProfiles = new ArrayList<String>();
//		for (SearchProfile s : AdvancedSearchActivity.mListSearchProfiles) {
//			mListNameProfiles.add(s.getNameProfile());
//		}
		// Initialize adapter
		mProfileAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, mListNameProfiles);
		lvProfile.setAdapter(mProfileAdapter);

		// Process event click on each profile
		lvProfile.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long id) {
				mIndexOfItemSelected = pos;
				mProcessDialogMove.click_ListView(mIndexOfItemSelected);
				dismiss();
			}
		});
	}

	/**
	 * @Description : Handle the click events
	 */
	@Override
	public void onClick(View v) {
		if (v == btnAdd) {
			mProcessDialogMove.click_Add();
			mIndexOfItemSelected = -1;
			dismiss();
		} else if (v == btnClose) {
			mProcessDialogMove.click_Close();
			dismiss();
		}
	}

	public static abstract class ProcessDialogConfirm_SearchProfile {
		public abstract void click_Add();

		public abstract void click_Close();

		public abstract void click_ListView(int pos);
	}
}

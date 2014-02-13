package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.PickContactAdapter;
import com.magrabbit.intelligentmenu.listener.AddContactListener;
import com.magrabbit.intelligentmenu.listener.PickContactListener;
import com.magrabbit.intelligentmenu.listener.PickContactShareListener;
import com.magrabbit.intelligentmenu.model.ContactObject;

/**
 * 
 * @author Hung Hoang.
 * 
 */
public class DialogPickPhoneContact extends BaseDialog implements
		OnClickListener, AddContactListener, PickContactShareListener {
	private ListView mLvContact;
	private List<ContactObject> mListContact;
	private List<ContactObject> mGetListContact;
	private List<String> mListIdContact;
	private PickContactAdapter mMyAdapter;
	private Activity mActivity;
	private Button mBtOk;
	private Button MBtCancel;
	private Button mBtNew;
	private EditText mEdtSearch;
	private PickContactListener mListener;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogPickPhoneContact(Context context,
			PickContactListener listener, List<String> idContact,
			List<ContactObject> listContact, String mFullName) {
		super(context);
		mActivity = (Activity) context;
		mListener = listener;
		this.mListContact = new ArrayList<ContactObject>();
		mGetListContact = new ArrayList<ContactObject>();
		this.mListContact.clear();
		this.mListContact.addAll(listContact);
		Collections.sort(mListContact, new Comparator<ContactObject>() {
			@Override
			public int compare(ContactObject lhs, ContactObject rhs) {
				if (lhs.getFullName()
						.toString()
						.trim()
						.toUpperCase(Locale.ENGLISH)
						.compareTo(
								rhs.getFullName().toString().trim()
										.toUpperCase(Locale.ENGLISH)) > 0)
					return 1;
				else if (lhs
						.getFullName()
						.toString()
						.trim()
						.toUpperCase(Locale.ENGLISH)
						.compareTo(
								rhs.getFullName().toString().trim()
										.toUpperCase(Locale.ENGLISH)) < 0)
					return -1;
				else
					return 0;
			}
		});
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_pick_contact);
		mLvContact = (ListView) findViewById(R.id.dialog_pick_contact_lv_contacts);
		mBtOk = (Button) findViewById(R.id.dialog_pick_contact_bt_ok);
		MBtCancel = (Button) findViewById(R.id.dialog_pick_contact_bt_cancel);
		mBtNew = (Button) findViewById(R.id.dialog_pick_contact_bt_new);
		mEdtSearch = (EditText) findViewById(R.id.dialog_pick_contact_edtSearch);
		mMyAdapter = new PickContactAdapter(mActivity, this.mListContact,
				false, this);
		mLvContact.setAdapter(mMyAdapter);
		mBtOk.setOnClickListener(this);
		MBtCancel.setOnClickListener(this);
		mBtNew.setOnClickListener(this);
		for (int i = 0; i < mListContact.size(); i++) {
			if (mListContact.get(i).getFullName().equals(mFullName)) {
				mLvContact.setSelection(i - 2);
				mLvContact.smoothScrollToPosition(i - 2);
				break;
			}
		}
		mLvContact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				DialogEditContact dialog = new DialogEditContact(mActivity,
						DialogPickPhoneContact.this, mListContact.get(position));
				dialog.show();
			}
		});
		searchWithFilter();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_pick_contact_bt_ok:
			mGetListContact.clear();
			for (int i = 0; i < ContactObject.getListContact().size(); i++) {
				if (ContactObject.getListContact().get(i).isSelected()) {
					mGetListContact.add(ContactObject.getListContact().get(i));
				}
			}
			if (mGetListContact != null && mGetListContact.size() != 0) {
				mListener.getListChooseContact(mGetListContact, true);
				dismiss();
			}
			break;
		case R.id.dialog_pick_contact_bt_cancel:
			dismiss();
			break;
		case R.id.dialog_pick_contact_bt_new:
			DialogAddContact dialog = new DialogAddContact(mActivity, this);
			dialog.show();
			break;
		default:
			break;
		}

	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mListContact != null) {
			mListContact.clear();
			mListContact = null;
		}
		if (mGetListContact != null) {
			mGetListContact.clear();
			mGetListContact = null;
		}
		if (mListIdContact != null) {
			mListIdContact.clear();
			mListIdContact = null;
		}
	}

	@Override
	public void upDateContact(boolean save, ContactObject co) {
		if (save) {
			mListener.isSaveAddContact(true, true, co);
			dismiss();
		}
	}

	/**
	 * This method use to filter when user change text from edit text
	 */
	public void searchWithFilter() {
		mEdtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				mMyAdapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
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

	@Override
	public void getListChooseContactShare(List<ContactObject> mListContact,
			boolean typeContact) {
	}

	@Override
	public void addContact(boolean save) {
		if (save) {
			mListener.isSaveAddContact(true, true, null);
			dismiss();
		}
	}
}

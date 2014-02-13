package com.magrabbit.intelligentmenu.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class ContactObject implements Comparable<ContactObject> {
	private String mIdContact;
	private String mFristName;
	private String mLastName;
	private String mFullName;
	private String mPhoneNumber;
	private String mEmail;
	private boolean mSelected;
	private boolean mEditmode;
	private Cursor mNameCursor;
	public static List<ContactObject> mListContact;
	public static final int LOADING = 0;
	public static final int LOAD_FINISHED = 1;
	public static final int LOAD_ERROR = 2;
	public static int mLoadStatus = LOAD_ERROR;

	public ContactObject() {
		super();

	}

	public ContactObject(String mIdContact, String mFristName,
			String mLastName, String mFullName, String mPhoneNumber,
			String mEmail, boolean mSelected) {
		super();
		this.mIdContact = mIdContact;
		this.mFristName = mFristName;
		this.mLastName = mLastName;
		this.mFullName = mFullName;
		this.mPhoneNumber = mPhoneNumber;
		this.mEmail = mEmail;
		this.mSelected = mSelected;
	}

	public ContactObject(String mIdContact, String mFullName,
			String mPhoneNumber, String mEmail, boolean mSelected) {
		super();
		this.mIdContact = mIdContact;
		this.mFullName = mFullName;
		this.mPhoneNumber = mPhoneNumber;
		this.mEmail = mEmail;
		this.mSelected = mSelected;
	}

	public String getIdContact() {
		return mIdContact;
	}

	public void setIdContact(String mIdContact) {
		this.mIdContact = mIdContact;
	}

	public String getFristName() {
		if (mFristName == null) {
			return "";
		}
		return mFristName;
	}

	public void setFristName(String mFristName) {
		this.mFristName = mFristName;
	}

	public String getLastName() {
		if (mLastName == null) {
			return "";
		}
		return mLastName;
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	public String getPhoneNumber() {
		if (mPhoneNumber == null) {
			return "";
		} else {
			return mPhoneNumber;
		}
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public String getEmail() {
		if (mEmail == null) {
			return "";
		}
		return mEmail;
	}

	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean mSelected) {
		this.mSelected = mSelected;
	}

	public String getFullName() {
		if (mFullName == null) {
			return "";
		}
		return mFullName;
	}

	public void setFullName(String mFullName) {
		this.mFullName = mFullName;
	}

	public ContactObject clone() {
		return new ContactObject(mIdContact, mFristName, mLastName, mFullName,
				mPhoneNumber, mEmail, mSelected);
	}

	public boolean isEditmode() {
		return mEditmode;
	}

	public void setEditmode(boolean mEditmode) {
		this.mEditmode = mEditmode;
	}

	public Cursor getNameCursor() {
		return mNameCursor;
	}

	public void setNameCursor(Cursor mNameCursor) {
		this.mNameCursor = mNameCursor;
	}

	@Override
	public int compareTo(ContactObject compareName) {

		return 0;
	}

	@Override
	public String toString() {
		return mFullName;
	}

	public static List<ContactObject> getListContact() {
		if (mListContact == null) {
			mListContact = new ArrayList<ContactObject>();
		}
		return mListContact;
	}

	public static void setListContact(List<ContactObject> mListContact) {
		if (mListContact != null) {
			ContactObject.getListContact().clear();
			ContactObject.mListContact.addAll(mListContact);
		}

	}

}

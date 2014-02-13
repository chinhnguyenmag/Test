package com.magrabbit.intelligentmenu.listener;

import java.util.List;

import com.magrabbit.intelligentmenu.model.ContactObject;

public interface PickContactListener {
	void getListChooseContact(List<ContactObject> mListContact,boolean typeContact);
	void isSaveAddContact(boolean save,boolean typeContact,ContactObject co);
}
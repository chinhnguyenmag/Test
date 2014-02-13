package com.magrabbit.intelligentmenu.listener;

import com.magrabbit.intelligentmenu.model.ContactObject;


public interface AddContactListener {
	void upDateContact(boolean save,ContactObject co);
	void addContact(boolean save);
}
package com.magrabbit.intelligentmenu.listener;

import java.util.List;

import com.magrabbit.intelligentmenu.model.ContactObject;

public interface ContactPhoneListener {
	void loadContactFinish(List<ContactObject> mListContact);

	void loadContactError();
}

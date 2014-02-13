package com.magrabbit.intelligentmenu.listener;

import java.util.List;

import com.magrabbit.intelligentmenu.model.ContactObject;

public interface ContactMailListener {
	void loadContactMailFinish(List<ContactObject> mListContact);

	void loadContactMailError();
}

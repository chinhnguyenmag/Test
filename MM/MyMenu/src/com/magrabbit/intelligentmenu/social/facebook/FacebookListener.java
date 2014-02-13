package com.magrabbit.intelligentmenu.social.facebook;

import com.magrabbit.intelligentmenu.model.FaceBookAccount;

public interface FacebookListener {
	void facebookLoginSuccess(FaceBookAccount facebookAccount);

	void facebookLoginError();

	void facebookLoginFail();
}

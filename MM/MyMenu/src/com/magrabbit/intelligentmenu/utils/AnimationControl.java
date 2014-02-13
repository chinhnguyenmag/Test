package com.magrabbit.intelligentmenu.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationControl {
	public void RunAnimations(Context context, View view, int idAnimate) {
		Animation a = AnimationUtils.loadAnimation(context, idAnimate);
		a.reset();
		view.clearAnimation();
		view.startAnimation(a);
	}

	public void RunGroupAnimations(Context context, Dialog view, int idAnimate) {
		Animation a = AnimationUtils.loadAnimation(context, idAnimate);
		a.reset();
		view.getWindow().getAttributes().windowAnimations = idAnimate;
	}
}

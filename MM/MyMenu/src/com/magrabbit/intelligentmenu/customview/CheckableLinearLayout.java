/**
 * 
 */
package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;

/**
 * Extension of a relative layout to provide a checkable behaviour
 * 
 * @author marvinlabs
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

	private boolean isChecked;
	private List<Checkable> checkableViews;
	private List<TextView> textView;
	private OnCheckedChangeListener onCheckedChangeListener;
	private  Context mContext;
	
	/**
	 * Interface definition for a callback to be invoked when the checked state of a CheckableRelativeLayout changed.
	 */
	public static interface OnCheckedChangeListener {
		public void onCheckedChanged(CheckableLinearLayout layout, boolean isChecked);
	}

	@SuppressLint("NewApi")
	public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialise(attrs);
		this.mContext =context;
	}
	
	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(attrs);
		this.mContext =context;
	}

	public CheckableLinearLayout(Context context, int checkableId) {
		super(context);
		initialise(null);
		this.mContext =context;
	}

	/*
	 * @see android.widget.Checkable#isChecked()
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/*
	 * @see android.widget.Checkable#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		for (Checkable c : checkableViews) {
			c.setChecked(isChecked);
			for (TextView tv : textView) {
				if(c.isChecked()) {
					tv.setTextColor(mContext.getResources().getColor(R.color.White));
				}
				else {
					tv.setTextColor(mContext.getResources().getColor(R.color.hint));
				}
			}
		}

		if (onCheckedChangeListener != null) {
			onCheckedChangeListener.onCheckedChanged(this, isChecked);
		}
	}

	/*
	 * @see android.widget.Checkable#toggle()
	 */
	public void toggle() {
		this.isChecked = !this.isChecked;
		for (Checkable c : checkableViews) {
			c.toggle();
		}
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		final int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			findCheckableChildren(this.getChildAt(i));
		}
	}

	/**
	 * Read the custom XML attributes
	 */
	private void initialise(AttributeSet attrs) {
		this.isChecked = false;
		this.checkableViews = new ArrayList<Checkable>(5);
		this.textView = new ArrayList<TextView>();
	}

	/**
	 * Add to our checkable list all the children of the view that implement the interface Checkable
	 */
	private void findCheckableChildren(View v) {
		if (v instanceof Checkable) {
			this.checkableViews.add((Checkable) v);
		}
		
		if (v instanceof TextView) {
			this.textView.add((TextView) v);
		}

		if (v instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup) v;
			final int childCount = vg.getChildCount();
			for (int i = 0; i < childCount; ++i) {
				findCheckableChildren(vg.getChildAt(i));
			}
		}
	}

}

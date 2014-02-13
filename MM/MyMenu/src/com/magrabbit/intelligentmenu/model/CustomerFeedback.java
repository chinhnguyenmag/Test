package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

/**
 * @author Thanh Vu
 * 
 */
public class CustomerFeedback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mUser_id;
	private String mComment;
	private String mDate;
	private int mRating;

	public CustomerFeedback(int mUser_id, String mComment, String mDate,
			int mRating) {
		super();
		this.mUser_id = mUser_id;
		this.mComment = mComment;
		this.mDate = mDate;
		this.mRating = mRating;
	}

	public CustomerFeedback() {
	}

	public int getmUser_id() {
		return mUser_id;
	}

	public void setmUser_id(int mUser_id) {
		this.mUser_id = mUser_id;
	}

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	public int getRating() {
		return mRating;
	}

	public void setRating(int rating) {
		this.mRating = rating;
	}

}

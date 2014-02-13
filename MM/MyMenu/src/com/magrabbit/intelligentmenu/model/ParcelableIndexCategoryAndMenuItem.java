package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableIndexCategoryAndMenuItem implements Parcelable {
	private final IndexCategoryAndMenuItem index;

	private ParcelableIndexCategoryAndMenuItem(Parcel parcel) {
		this.index = new IndexCategoryAndMenuItem(parcel.readInt(),
				parcel.readInt());
	}

	public ParcelableIndexCategoryAndMenuItem(IndexCategoryAndMenuItem index) {
		this.index = index;
	}

	public IndexCategoryAndMenuItem getIndexCategoryAndMenuItem() {
		return index;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(index.getIndexCategory());
		parcel.writeInt(index.getIndexMenuItem());
	}

	public static final Creator<ParcelableIndexCategoryAndMenuItem> CREATOR = new Creator<ParcelableIndexCategoryAndMenuItem>() {

		// And here you create a new instance from a parcel using the first
		// constructor
		@Override
		public ParcelableIndexCategoryAndMenuItem createFromParcel(Parcel parcel) {
			return new ParcelableIndexCategoryAndMenuItem(parcel);
		}

		@Override
		public ParcelableIndexCategoryAndMenuItem[] newArray(int size) {
			return new ParcelableIndexCategoryAndMenuItem[size];
		}

	};

}

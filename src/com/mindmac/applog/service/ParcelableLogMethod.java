package com.mindmac.applog.service;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableLogMethod implements Parcelable {
	public String className;
	public String methodName;

	public ParcelableLogMethod() {
	}

	public static final Parcelable.Creator<ParcelableLogMethod> CREATOR = new Parcelable.Creator<ParcelableLogMethod>() {
		public ParcelableLogMethod createFromParcel(Parcel in) {
			return new ParcelableLogMethod(in);
		}

		public ParcelableLogMethod[] newArray(int size) {
			return new ParcelableLogMethod[size];
		}
	};

	private ParcelableLogMethod(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(className);
		out.writeString(methodName);
	}

	public void readFromParcel(Parcel in) {
		className = in.readString();
		methodName = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}

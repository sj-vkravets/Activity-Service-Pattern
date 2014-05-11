package com.example.framework.processor;

import android.os.Parcelable;

public abstract class BaseProcessor implements Parcelable {

	public static final int PROCESS_SUCCESS = 0;

	public static final int PROCESS_FAILURE = 1;

	public static final int PROCESS_PROGRESS = 2;

	public abstract void executeProcess();

	public abstract void cancelProcess();

}

package com.example.framework.processor;

import android.os.Parcelable;

public abstract class BaseProcessor implements Parcelable {

	public abstract void executeProcess();

	public abstract void cancelProcess();

}

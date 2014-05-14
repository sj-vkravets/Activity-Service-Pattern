package com.example.framework.service;

import android.os.Binder;
import android.os.Bundle;
import android.util.SparseArray;

import static com.example.framework.util.LogUtils.*;

public class AppBinder extends Binder {

	private static final String TAG = makeLogTag("AppBinder");

	private SparseArray<ServiceListener> mServiceListeners = new SparseArray<ServiceListener>();

	public void addServiceListener(int taskKey, ServiceListener serviceListener) {
		mServiceListeners.put(taskKey, serviceListener);
		LOGD(TAG, "Listener with key " + Integer.toString(taskKey) + " added");
	}

	public void removeServiceListener(int taskKey) {
		if (mServiceListeners.get(taskKey) != null) {
			mServiceListeners.remove(taskKey);
			LOGD(TAG, "Listener with key " + Integer.toString(taskKey)
					+ " removed");
		}
	}

	public boolean notifyListeners(int taskKey, Bundle args) {
		ServiceListener listener = mServiceListeners.get(taskKey);
		if (listener != null) {
			listener.onTaskFinished(taskKey, args);
			mServiceListeners.remove(taskKey);
			LOGD(TAG, "Notify a listener with key " + Integer.toString(taskKey));
			return true;
		} else {
			return false;
		}
	}

}

package com.example.framework.app;

import android.app.Application;
import android.content.Context;

import com.example.framework.service.AppServiceHelper;
import com.example.framework.service.AppServiceHelperImpl;

public class App extends Application {

	public static final String PACKAGE = "com.example.framework";

	private AppServiceHelper mAppServiceHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppServiceHelper = new AppServiceHelperImpl(this);
	}

	public static App getApplication(Context context) {
		if (context instanceof App) {
			return (App) context;
		}
		return (App) context.getApplicationContext();
	}

	public AppServiceHelper getServiceHelper() {
		return mAppServiceHelper;
	}

}

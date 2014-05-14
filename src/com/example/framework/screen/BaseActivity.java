package com.example.framework.screen;

import com.example.framework.app.App;

import android.app.Activity;

public class BaseActivity extends Activity {

	public App getApp() {
		return (App) getApplication();
	}

}

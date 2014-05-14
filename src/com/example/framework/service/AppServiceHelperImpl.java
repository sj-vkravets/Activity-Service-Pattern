package com.example.framework.service;

import android.app.Application;
import android.content.Intent;

import com.example.framework.processor.BaseProcessor;
import com.example.framework.processor.ExampleProcessor;

public class AppServiceHelperImpl implements AppServiceHelper {

	private Application mApp;

	public AppServiceHelperImpl(Application app) {
		mApp = app;
	}

	@Override
	public int exampleProcess() {
		Intent intent = createExecutableServiceIntent(new ExampleProcessor(),
				EXAMPLE_PROCESS);
		startService(intent);
		return EXAMPLE_PROCESS;
	}

	private void startService(Intent intent) {
		mApp.startService(intent);
	}

	private Intent createCancelableServiceIntent(int processorId) {
		Intent intent = new Intent(mApp, AppService.class);
		intent.setAction(AppService.ACTION_CANCEL_PROCESS);
		intent.putExtra(AppService.EXTRA_PROCESSOR_ID, processorId);
		return intent;
	}

	private Intent createExecutableServiceIntent(BaseProcessor baseProcessor,
			int processorId) {
		Intent intent = new Intent(mApp, AppService.class);
		intent.setAction(AppService.ACTION_EXECUTE_PROCESS);
		intent.putExtra(AppService.EXTRA_PROCESSOR, baseProcessor);
		intent.putExtra(AppService.EXTRA_PROCESSOR_ID, processorId);
		return intent;
	}
}

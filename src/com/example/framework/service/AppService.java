package com.example.framework.service;

import static com.example.framework.util.LogUtils.LOGD;
import static com.example.framework.util.LogUtils.makeLogTag;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.SparseArray;

import com.example.framework.app.App;
import com.example.framework.processor.BaseProcessor;

public class AppService extends Service {

	private static final String TAG = makeLogTag("AppService");

	private static final int NUM_THREADS = 2;

	public static final String ACTION_EXECUTE_PROCESS = App.PACKAGE
			.concat(".ACTION_EXECUTE_PROCESS");

	public static final String ACTION_CANCEL_PROCESS = App.PACKAGE
			.concat(".ACTION_CANCEL_PROCESS");

	public static final String EXTRA_PROCESSOR = App.PACKAGE
			.concat(".EXTRA_PROCESSOR");

	public static final String EXTRA_PROCESSOR_ID = App.PACKAGE
			.concat(".EXTRA_PROCESSOR_ID");

	private AppBinder mBinder = new AppBinder();

	private ExecutorService mExecutor = Executors
			.newFixedThreadPool(NUM_THREADS);

	private SparseArray<AppService.ServiceThread> mServiceThreads = new SparseArray<AppService.ServiceThread>();

	@Override
	public IBinder onBind(Intent intent) {
		LOGD(TAG, "onBind");
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LOGD(TAG, "onStartCommand");
		String action = intent.getAction();
		int processorId = getProcessorId(intent.getExtras());

		if (!TextUtils.isEmpty(action) && ACTION_EXECUTE_PROCESS.equals(action)) {
			LOGD(TAG,
					"Execute the process id = " + Integer.toString(processorId));
			// TODO: Execute process

			ServiceThread serviceThread = new ServiceThread(intent);
			addServiceThread(processorId, serviceThread);
			mExecutor.submit(serviceThread);
		} else if (!TextUtils.isEmpty(action)
				&& ACTION_CANCEL_PROCESS.equals(action)) {
			LOGD(TAG,
					"Cancel the process id = " + Integer.toString(processorId));
			// TODO: Cancel process

			ServiceThread runningProcess = mServiceThreads.get(processorId);
			if (runningProcess != null) {
				runningProcess.cancel();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		LOGD(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LOGD(TAG, "onDestroy");
		mExecutor.shutdownNow();
	}

	@Override
	public void onRebind(Intent intent) {
		LOGD(TAG, "onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LOGD(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	private class ServiceThread implements Runnable {

		private Intent intent;
		private BaseProcessor processor;
		private int processorId;

		public ServiceThread(Intent intent) {
			this.intent = intent;
			processor = getProcessor(intent.getExtras());
			processorId = getProcessorId(intent.getExtras());
		}

		@Override
		public void run() {
			processor.executeProcess();
			onPostExecute();
		}

		public void cancel() {
			processor.cancelProcess();
		}

		public void onPostExecute() {
			mBinder.notifyListeners(processorId, intent.getExtras());
			synchronized (mServiceThreads) {
				mServiceThreads.remove(processorId);
				if (mServiceThreads.size() == 0) {
					stopSelf();
				}
			}
		}

	}

	private int getProcessorId(Bundle args) {
		if (args != null) {
			return args.getInt(EXTRA_PROCESSOR_ID, -1);
		}
		return -1;
	}

	private BaseProcessor getProcessor(Bundle args) {
		if (args != null) {
			return args.getParcelable(EXTRA_PROCESSOR);
		}
		return null;
	}

	private synchronized void addServiceThread(int processorId,
			ServiceThread serviceThread) {
		mServiceThreads.put(processorId, serviceThread);
	}

}

package com.example.framework.service;

import static com.example.framework.util.LogUtils.LOGD;
import static com.example.framework.util.LogUtils.makeLogTag;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.SparseArray;

import com.example.framework.app.App;
import com.example.framework.processor.BaseProcessor;

public class AppService extends Service {

	private static final String TAG = makeLogTag("AppService");

	private static final int NUM_THREADS = 5;

	public static final String ACTION_EXECUTE_PROCESS = App.PACKAGE
			.concat(".ACTION_EXECUTE_PROCESS");

	public static final String ACTION_CANCEL_PROCESS = App.PACKAGE
			.concat(".ACTION_CANCEL_PROCESS");

	public static final String EXTRA_PROCESSOR = App.PACKAGE
			.concat(".EXTRA_PROCESSOR");

	public static final String EXTRA_PROCESSOR_ID = App.PACKAGE
			.concat(".EXTRA_PROCESSOR_ID");

	private AppBinder mBinder = new AppBinder();

	private Handler mHandler;

	private ExecutorService mExecutor = Executors
			.newFixedThreadPool(NUM_THREADS);

	private SparseArray<AppService.ProcessThread> mProcessThreads = new SparseArray<AppService.ProcessThread>();

	@Override
	public IBinder onBind(Intent intent) {
		LOGD(TAG, "onBind");
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		LOGD(TAG, "onRebind");
		if (mProcessThreads != null && mProcessThreads.size() > 0) {
			int processId = 0;
			for (int i = 0; i < mProcessThreads.size(); i++) {
				processId = mProcessThreads.keyAt(i);
				ProcessThread process = mProcessThreads.get(processId);
				if (process.getThreadStatus() == ProcessThread.PROCESS_FINISHED) {
					// The process is finished, notify the listeners
					notifyListeners(process);
				} else {
					// The process is in progress, notify the listener
				}
			}
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LOGD(TAG, "onStartCommand");
		mHandler = new Handler();
		String action = intent.getAction();
		int processorId = getProcessorId(intent.getExtras());

		if (!TextUtils.isEmpty(action) && ACTION_EXECUTE_PROCESS.equals(action)) {
			int processStatus = getProcessStatus(processorId, intent);
			if (processStatus == ProcessThread.PROCESS_FINISHED) {
				// TODO: Notify the listeners.
				LOGD(TAG,
						"The process ["
								+ Integer.toString(processorId)
								+ "] is finished. The listeners have not been notified.");
			} else if (processStatus == ProcessThread.PROCESS_IN_PROGRESS) {
				// TODO: Notify the listeners.
				LOGD(TAG, "The process [" + Integer.toString(processorId)
						+ "] is in progress");
			} else {
				// Execute the process
				mExecutor.submit(mProcessThreads.get(processorId));
				LOGD(TAG,
						"Execute the process [" + Integer.toString(processorId)
								+ "]");
			}

		} else if (!TextUtils.isEmpty(action)
				&& ACTION_CANCEL_PROCESS.equals(action)) {
			LOGD(TAG,
					"Cancel the process id = " + Integer.toString(processorId));
			// TODO: Cancel the process

			ProcessThread runningProcess = mProcessThreads.get(processorId);
			if (runningProcess != null) {
				runningProcess.cancel();
			}
		}
		return START_NOT_STICKY;
	}

	private class ProcessThread implements Runnable {

		public static final int PROCESS_IN_PROGRESS = 0;
		public static final int PROCESS_FINISHED = 1;

		private int threadStatus;
		private int processorId;
		private Intent intent;
		private BaseProcessor processor;

		public ProcessThread(Intent intent) {
			this.intent = intent;
			processor = getProcessor(intent.getExtras());
			processorId = AppService.this.getProcessorId(intent.getExtras());
		}

		@Override
		public void run() {
			threadStatus = PROCESS_IN_PROGRESS;
			processor.executeProcess();
			onPostRun();

		}

		public void cancel() {
			processor.cancelProcess();
		}

		private void onPostRun() {
			threadStatus = PROCESS_FINISHED;
			notifyListeners(this);
		}

		public Intent getIntent() {
			return intent;
		}

		public int getThreadStatus() {
			return threadStatus;
		}

		public int getProcessorId() {
			return processorId;
		}

	}

	private void notifyListeners(final ProcessThread processThread) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (processThread.getThreadStatus() == ProcessThread.PROCESS_FINISHED) {
					LOGD(TAG, "Notify the listeners");
					boolean isNotified = mBinder.notifyListeners(processThread
							.getProcessorId(), processThread.getIntent()
							.getExtras());
					if (isNotified) {
						removeProcessFromList(processThread.getProcessorId());
					}
				}
			}
		});
	}

	private int getProcessStatus(int processorId, Intent intent) {
		ProcessThread thread = mProcessThreads.get(processorId);
		if (thread != null
				&& thread.getThreadStatus() == ProcessThread.PROCESS_IN_PROGRESS) {
			// Process is in process
			return ProcessThread.PROCESS_IN_PROGRESS;
		} else if (thread != null
				&& thread.getThreadStatus() == ProcessThread.PROCESS_FINISHED) {
			// Process is finished and listeners have not been notified.
			return ProcessThread.PROCESS_FINISHED;
		}
		// Add the new process
		ProcessThread processThread = new ProcessThread(intent);
		mProcessThreads.put(processorId, processThread);
		return -1;
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

	private void removeProcessFromList(int processorId) {
		synchronized (mProcessThreads) {
			mProcessThreads.remove(processorId);
			if (mProcessThreads.size() == 0) {
				LOGD(TAG, "Stop the SERVICE");
				stopSelf();
			}
		}
	}

	/**
	 * Lifecycle
	 */

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
	public boolean onUnbind(Intent intent) {
		LOGD(TAG, "onUnbind");
		return true;
	}

}

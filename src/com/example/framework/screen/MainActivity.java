package com.example.framework.screen;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.widget.TextView;

import com.example.framework.R;
import com.example.framework.app.App;
import com.example.framework.service.AppBinder;
import com.example.framework.service.AppService;
import com.example.framework.service.AppServiceHelper;
import com.example.framework.service.ServiceListener;
import static com.example.framework.util.LogUtils.*;

public class MainActivity extends Activity implements ServiceListener {

	private String TAG;

	private boolean mBound;
	private AppBinder mBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = makeLogTag("Act-" + Integer.toString(this.hashCode()));
		LOGD(TAG, "onCreate");
		setContentView(R.layout.activity_main);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				LOGD(TAG, "run the process");
				getApp().getServiceHelper().exampleProcess();
			}
		}, 5000);

	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, AppService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		LOGD(TAG, "onStop");
		unbindService(mConnection);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LOGD(TAG, "onResume");
		// if (mBinder != null) {
		// mBinder.addServiceListener(AppServiceHelper.EXAMPLE_PROCESS, this);
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		LOGD(TAG, "onPause");
		if (mBinder != null) {
			mBinder.removeServiceListener(AppServiceHelper.EXAMPLE_PROCESS);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LOGD(TAG, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			LOGD(TAG, "onServiceConnected");
			mBinder = (AppBinder) binder;
			mBound = true;
			mBinder.addServiceListener(AppServiceHelper.EXAMPLE_PROCESS,
					MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			LOGD(TAG, "onServiceDisconnected");
			mBound = false;
		}
	};

	@Override
	public void onTaskFinished(int taskKey, Bundle args) {
		LOGD(TAG, "Callback from activity");
		((TextView) findViewById(R.id.tv_lable))
				.setText("The process is finished id = "
						+ Integer.toString(taskKey));

	}

	private App getApp() {
		return (App) getApplication();
	}

}

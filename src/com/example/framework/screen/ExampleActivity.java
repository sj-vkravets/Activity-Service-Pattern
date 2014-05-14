package com.example.framework.screen;

import static com.example.framework.util.LogUtils.LOGD;

import com.example.framework.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ExampleActivity extends ServiceBasedActivity {

	private static final String TAG = "ExampleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_example);

		findViewById(R.id.btn_start_service).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						LOGD(TAG, mClassHash + "Start Load");
						int processId = getApp().getServiceHelper()
								.exampleProcess();
						addListenerToQueue(processId);
						mBinder.addServiceListener(processId,
								ExampleActivity.this);
					}
				});
	}

	@Override
	public void onTaskFinished(int taskKey, Bundle args) {
		LOGD(TAG, mClassHash + "onTaskFinished");
		((TextView) findViewById(R.id.tv_status)).setText("Process done id - "
				+ Integer.toString(taskKey));
	}
}

package com.example.framework.processor;

import static com.example.framework.util.LogUtils.LOGD;
import static com.example.framework.util.LogUtils.makeLogTag;

import java.util.concurrent.TimeUnit;
import android.os.Parcel;
import android.os.Parcelable;

public class ExampleProcessor extends BaseProcessor {

	private static final String TAG = makeLogTag("ExamplePro");

	public ExampleProcessor(Parcel in) {
		// TODO Auto-generated constructor stub
	}

	public ExampleProcessor() {
	}

	@Override
	public void executeProcess() {
		for (int i = 0; i < 20; i++) {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGD(TAG, "Pro - " + Integer.toString(i));
		}
	}

	@Override
	public void cancelProcess() {
		LOGD(TAG, "Cancel the process");
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	public static final Parcelable.Creator<ExampleProcessor> CREATOR = new Parcelable.Creator<ExampleProcessor>() {
		public ExampleProcessor createFromParcel(Parcel in) {
			return new ExampleProcessor(in);
		}

		public ExampleProcessor[] newArray(int size) {
			return new ExampleProcessor[size];
		}
	};

}

package net.sunthecourier.questpresence;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestUsageStatsPermission();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button mainBtn = findViewById(R.id.presence_btn);
		final TextView ipBox = findViewById(R.id.ipBox);
		ipBox.setText(Utils.getIPAddress(true));
		if (isMyServiceRunning(QuestService.class)) {
			mainBtn.setText(R.string.stop_btn);
		}
		else {
			mainBtn.setText(R.string.start_btn);
		}
	}

	public void onClick(View view) {
		Intent intent = new Intent(MainActivity.this, QuestService.class);
		final Button mainBtn = findViewById(R.id.presence_btn);
		if (!isMyServiceRunning(QuestService.class)) {
			startService(intent);
			mainBtn.setText(R.string.stop_btn);
		}
		else {
			stopService(intent);
			mainBtn.setText(R.string.start_btn);
		}
	}

	public void onExitClick(View view) {
		System.exit(0);
	}

	void requestUsageStatsPermission() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				&& !hasUsageStatsPermission(this)) {
			startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	boolean hasUsageStatsPermission(Context context) {
		AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow("android:get_usage_stats",
				android.os.Process.myUid(), context.getPackageName());
		return mode == AppOpsManager.MODE_ALLOWED;
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}

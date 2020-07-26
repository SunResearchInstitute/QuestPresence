package net.sunthecourier.questpresence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import com.rvalerio.fgchecker.AppChecker;
import lombok.SneakyThrows;

import java.io.IOException;

public class QuestService extends Service {
	private SocketSender sender;

	public Handler handler = null;
	public static Runnable runnable = null;

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	@SneakyThrows @Override
	public void onCreate() {
		try {
			sender = new SocketSender();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		handler = new Handler();
		runnable = () -> {
			Thread thread = null;
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if (pm.isInteractive()) {
				String str = Utils.getAppNameFromPkgName(getBaseContext(), getPkgName());
				thread = new Thread(() -> {
					try {
						sender.SendData(str);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				});
				thread.start();
			}

			Thread finalThread = thread;
			new Thread(() -> {
				if (finalThread != null) {
					try {
						finalThread.wait();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (runnable != null)
					handler.postDelayed(runnable, 1000);
			}).start();
		};

		handler.postDelayed(runnable, 1000);
	}

	private String getPkgName() {
		return new AppChecker().getForegroundApp(this.getBaseContext());
	}
}

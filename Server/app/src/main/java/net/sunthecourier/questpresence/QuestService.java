package net.sunthecourier.questpresence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import com.rvalerio.fgchecker.AppChecker;
import lombok.Getter;

import java.io.IOException;

public class QuestService extends Service {
	@Getter private static boolean isRunning = false;
	private Thread thread;
	SocketSender sender;

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		isRunning = true;
		try {
			sender = new SocketSender();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		thread = new Thread(() -> {
			while (true) {
				try {
					PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					if (pm.isInteractive()) {
						String str = getAppName();

						sender.SendData(str);
						System.out.println(str);
					}
					Thread.sleep(1000);
				}
				catch (InterruptedException | IOException e) {
					break;
				}
			}
		});
		thread.start();
	}

	@Override public void onDestroy() {
		isRunning = false;
		thread.interrupt();

		try {
			sender.clean();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getAppName() {
		return new AppChecker().getForegroundApp(this.getBaseContext());
	}
}

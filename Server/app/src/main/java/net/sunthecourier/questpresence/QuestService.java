package net.sunthecourier.questpresence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import com.rvalerio.fgchecker.AppChecker;
import lombok.SneakyThrows;

import java.io.IOException;

public class QuestService extends Service {
	private Thread thread;
	private SocketSender sender;

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

		thread = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					if (pm.isInteractive()) {
						String str = Utils.getAppNameFromPkgName(getBaseContext(), getPkgName());

						sender.SendData(str);
						System.out.println(str);
					}
					Thread.sleep(1000);
				}
				catch (InterruptedException | IOException e) {
					if (!Thread.interrupted())
						break;
				}
			}
		});
		thread.start();
	}

	@Override public void onDestroy() {
		thread.interrupt();

		try {
			sender.clean();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getPkgName() {
		return new AppChecker().getForegroundApp(this.getBaseContext());
	}
}

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
	//private Thread thread;
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
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if (pm.isInteractive()) {
				String str = Utils.getAppNameFromPkgName(getBaseContext(), getPkgName());
				Thread thread = new Thread(() -> {
					try {
						sender.SendData(str);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				});
				thread.start();

				System.out.println(str);
			}
			if (runnable != null)
				handler.postDelayed(runnable, 1000);
		};

		handler.postDelayed(runnable, 1000);
		/*
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
		 */
	}

	@Override public void onDestroy() {
		//thread.interrupt();

		try {
			sender.clean();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private String getPkgName() {
		return new AppChecker().getForegroundApp(this.getBaseContext());
	}
}

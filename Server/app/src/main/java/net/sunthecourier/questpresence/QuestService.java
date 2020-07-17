package net.sunthecourier.questpresence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rvalerio.fgchecker.AppChecker;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;

public class QuestService extends Service {
	private Thread thread;
	private SocketSender sender;
	private HashMap<String, String> appDictionary;


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
		Gson gson = new Gson();
		Thread tthread = new Thread(() -> {
			try {
				String content = Utils.readStringFromURL("https://raw.githubusercontent.com/Sun-Research-University/QuestPresence/master/Resource/Applications.json");
				appDictionary = gson.fromJson(content, new TypeToken<HashMap<String, String>>() {}.getType());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		tthread.start();
		tthread.join();

		thread = new Thread(() -> {
			while (true) {
				try {
					PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					if (pm.isInteractive()) {
						String str = getAppName();

						sender.SendData(str, appDictionary);
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

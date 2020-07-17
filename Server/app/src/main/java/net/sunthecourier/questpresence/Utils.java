package net.sunthecourier.questpresence;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {
	/**
	 * Get IP address from first non-localhost interface
	 *
	 * @param useIPv4 true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						//boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						boolean isIPv4 = sAddr.indexOf(':') < 0;

						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						}
						else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
								return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		}
		catch (Exception ignored) { } // for now eat exceptions
		return "null";
	}

	public static String getAppNameFromPkgName(Context context, String packageName) {
		try {
			final PackageManager packageManager = context.getPackageManager();
			ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			return (String) packageManager.getApplicationLabel(info);
		}
		catch (PackageManager.NameNotFoundException e) {
			return packageName.substring(packageName.lastIndexOf(".") + 1);
		}
	}
}

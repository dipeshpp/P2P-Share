package com.project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
import android.widget.Toast;

public class Wifi {
	String myip;
	MulticastLock ml;

	public WifiManager getWifiManager(Context c) {
		WifiManager wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		return wm;
	}

	public String getinfo(WifiManager wm) {
		return "Connected to " + wm.getConnectionInfo().getSSID() + " - "
				+ wm.getConnectionInfo().getLinkSpeed() + " "
				+ WifiInfo.LINK_SPEED_UNITS;
	}

	public String getmyip(WifiManager wm) {
		WifiInfo myWifiInfo = wm.getConnectionInfo();
		int ipAddress = myWifiInfo.getIpAddress();
		myip = android.text.format.Formatter.formatIpAddress(ipAddress);
		Log.d("test", "myip set");
		return myip;

	}

	public boolean checkwifi(Context c) {
		ConnectivityManager connManager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {

			return true;
		}

		else {
			Toast.makeText(c.getApplicationContext(),
					"Please connect to a Wi-Fi network", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	}

	public void acquirelock(WifiManager wm) {
		ml = wm.createMulticastLock("mylock");
		ml.setReferenceCounted(true);
		ml.acquire();

	}

	public void releaseLock() {
		if (ml.isHeld())
			ml.release();
	}

}

package com.project;

import java.net.InetAddress;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class FileAccept extends Service {

	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Bundle extras = intent.getExtras();

		String username = extras.getString("username");
		String sip = extras.getString("sip");
		//String macid = extras.getString("macid");
		String filename = extras.getString("filename");
		String path = extras.getString("path");
		String filesize = extras.getString("size");

		if (intent.getExtras().getInt("notifid") == 2)
			nm.cancel(2);

		Intent frs = new Intent(this, FileReceiveService.class);
		frs.putExtra("username", username);
		frs.putExtra("filename", filename);
		frs.putExtra("size", filesize);
		startService(frs);

		Send sendmsg = new Send();
		Wifi wifi = new Wifi();
		WifiManager wm = wifi.getWifiManager(this);

		String[] args = { getUsername(),
				wm.getConnectionInfo().getMacAddress(), filename, path,
				filesize };

		String result = "";
		for (String s : args)
			result = result + s + Constants.separator;

		String filemsg = "20" + result;

		try {
			sendmsg.send(InetAddress.getByName(sip), filemsg);
		} catch (Exception e) {
			Log.d("test", e.getMessage());
		}

		stopSelf();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}
}
package com.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class Notify {
	Context context;

	Notify(Context context) {
		this.context = context;
	}

	public void displaynotification(String username, String sip, String macid) {
		int notifid = 1;
		Intent i = new Intent(context, Messaging.class);
		i.putExtra("notifid", notifid);
		i.putExtra("username", username);
		i.putExtra("sip", sip);
		i.putExtra("macid", macid);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				notifid, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = new Notification(R.drawable.ic_launcher,
				"New Message", System.currentTimeMillis());
		CharSequence from = "P2P Share";
		CharSequence message = "New Message from " + username;
		notif.setLatestEventInfo(context, from, message, pendingIntent);

		// ---100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms---
		if (getVibrate())
			notif.vibrate = new long[] { 100, 250, 100, 500 };
		notif.sound = getRingtone();
		if (getLED())
			notif.flags |= Notification.FLAG_SHOW_LIGHTS;
		nm.notify(notifid, notif);
	}

	public void displayfilenotif(String username, String sip, String macid,
			String filename, String path, String size) {
		int notifid = 2;
		Intent i = new Intent(context, FileAccept.class);
		i.putExtra("notifid", notifid);
		i.putExtra("username", username);
		i.putExtra("sip", sip);
		i.putExtra("macid", macid);
		i.putExtra("filename", filename);
		i.putExtra("path", path);
		i.putExtra("size", size);
		PendingIntent pendingIntent = PendingIntent.getService(context,
				notifid, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = new Notification(R.drawable.ic_launcher,
				"Accept incoming file", System.currentTimeMillis());
		CharSequence from = "Accept file from " + username;
		CharSequence message = filename + ": "
				+ Round(Float.parseFloat(size) / 1024 / 1024, 2) + " MB";
		notif.setLatestEventInfo(context, from, message, pendingIntent);

		// ---100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms---
		if (getVibrate())
			notif.vibrate = new long[] { 100, 250, 100, 500 };
		notif.sound = getRingtone();
		if (getLED())
			notif.flags |= Notification.FLAG_SHOW_LIGHTS;

		nm.notify(notifid, notif);
	}

	public void displayfilenotif2(String username, String filename,
			Double size, int notifid) {

		Intent i = new Intent();
		PendingIntent pendingIntent = PendingIntent.getService(context,
				notifid, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = null;
		if (notifid == 12) {
			notif = new Notification(R.drawable.ic_launcher, "File sent!",
					System.currentTimeMillis());
			CharSequence from = "File sent to " + username;
			CharSequence message = filename + ": "
					+ Round((float) (size / 1024 / 1024), 2) + " MB";
			notif.setLatestEventInfo(context, from, message, pendingIntent);
		} else if (notifid == 13) {
			notif = new Notification(R.drawable.ic_launcher, "File received!",
					System.currentTimeMillis());
			CharSequence from = "File received from " + username;
			CharSequence message = filename + ": "
					+ Round((float) (size / 1024 / 1024), 2) + " MB";
			notif.setLatestEventInfo(context, from, message, pendingIntent);
		} else if (notifid == 14) {
			notif = new Notification(R.drawable.ic_launcher,
					"Transfer Failed!", System.currentTimeMillis());
			CharSequence from = "File transfer failed";
			CharSequence message = filename + ": "
					+ Round((float) (size / 1024 / 1024), 2) + " MB";
			notif.setLatestEventInfo(context, from, message, pendingIntent);
		} else if (notifid == 15) {
			notif = new Notification(R.drawable.ic_launcher,
					"Transfer Failed!", System.currentTimeMillis());
			CharSequence from = "Cannot write to SD card";
			CharSequence message = filename + ": "
					+ Round((float) (size / 1024 / 1024), 2) + " MB";
			notif.setLatestEventInfo(context, from, message, pendingIntent);
		}
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(notifid, notif);
	}

	public Uri getRingtone() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return Uri.parse(prefs
				.getString("ringtonePref", "DEFAULT_RINGTONE_URI"));
	}

	public boolean getVibrate() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean("vibratePref", true);
	}

	public boolean getLED() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean("ledPref", true);
	}

	public static float Round(float Rval, int Rpl) {
		float p = (float) Math.pow(10, Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float) tmp / p;
	}

}

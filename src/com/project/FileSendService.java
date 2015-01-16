package com.project;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

public class FileSendService extends IntentService {

	Notification notification;
	NotificationManager notificationManager;
	ProgressBar progressBar;
	private int progress = 0;
	Double size;
	double val;
	int readTotal;
	String username, filename, sip, path;
	Timer timer;

	public FileSendService() {
		super("FileSendService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("Service Example", "Service Started.. ");
		Intent intent = new Intent();
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		// configure the notification
		notification = new Notification(R.drawable.ic_launcher,
				"Sending file...", System.currentTimeMillis());
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.download_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon,
				R.drawable.ic_launcher);
		notification.contentView.setTextViewText(R.id.status_text,
				"Sending file...");
		notification.contentView.setProgressBar(R.id.status_progress, 100,
				progress, false);

		getApplicationContext();
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(10, notification);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Service Example", "Service Destroyed.. ");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

		Bundle extras = arg0.getExtras();
		username = extras.getString("username");
		filename = extras.getString("filename");
		sip = extras.getString("sip");
		path = extras.getString("path");
		size = Double.parseDouble(extras.getString("size"));

		notification.contentView.setTextViewText(R.id.status_text, "Sending "
				+ filename);

		final int BUFFER_SIZE = Constants.buffer_size;
		FileInputStream fileInputStream = null;
		OutputStream socketOutputStream = null;
		Socket socket = null;
		try {
			Thread.sleep(1000);
			socket = new Socket(sip, Constants.fileport);
			fileInputStream = new FileInputStream(path);
			socketOutputStream = socket.getOutputStream();
			long startTime = System.currentTimeMillis();
			byte[] buffer = new byte[BUFFER_SIZE];
			int read;
			readTotal = 0;
			val = 0;

			timer = new Timer();
			timer.schedule(new UpdateTask(), 1000, 1000);

			while ((read = fileInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
				socketOutputStream.write(buffer, 0, read);
				readTotal += read;
			}

			timer.cancel();
			notificationManager.cancel(10);
			Notify n = new Notify(this);
			n.displayfilenotif2(username, filename, size, 12);

			long endTime = System.currentTimeMillis();
			Log.d("test", readTotal + " bytes written in "
					+ (endTime - startTime) + " ms.");
		} catch (Exception e) {
			cancelnotif();
			Log.d("test", e.getMessage());
		} finally {
			try {
				socketOutputStream.close();
				fileInputStream.close();
				socket.close();
			} catch (IOException e) {
				Log.d("test", e.getMessage());
			}
		}

	}

	class UpdateTask extends TimerTask {
		int counter = 0;
		double temp;

		public void run() {
			val = ((readTotal / size) * 100);
			notification.contentView.setProgressBar(R.id.status_progress, 100,
					(int) val, false);
			notificationManager.notify(10, notification);
		}
	}

	public void cancelnotif() {
		Log.d("test", "cancel notif");
		timer.cancel();
		notification.contentView.setProgressBar(R.id.status_progress, 100,
				(int) val, true);
		notificationManager.cancel(10);
		Notify n = new Notify(getBaseContext());
		n.displayfilenotif2(username, filename, size, 14);
	}
}

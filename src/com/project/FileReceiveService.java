package com.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

public class FileReceiveService extends IntentService {

	Notification notification;
	NotificationManager notificationManager;
	ProgressBar progressBar;
	private int progress = 0;
	int readTotal;
	double val;
	Timer timer;
	Double size;
	String username, filename;
	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;

	public FileReceiveService() {
		super("FileReceiveService");
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
				"Receiving file...", System.currentTimeMillis());
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.download_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon,
				R.drawable.ic_launcher);
		notification.contentView.setTextViewText(R.id.status_text,
				"Receiving file...");
		notification.contentView.setProgressBar(R.id.status_progress, 100,
				progress, false);

		getApplicationContext();
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(11, notification);

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
		size = Double.parseDouble(extras.getString("size"));

		if (getState()) {

			notification.contentView.setTextViewText(R.id.status_text,
					"Receiving " + filename);

			final int BUFFER_SIZE = Constants.buffer_size;
			ServerSocket serverSocket = null;
			Socket clientSocket = null;
			InputStream clientInputStream = null;
			FileOutputStream fos = null;
			try {
				serverSocket = new ServerSocket(Constants.fileport);
				clientSocket = serverSocket.accept();
				clientInputStream = clientSocket.getInputStream();
				long startTime = System.currentTimeMillis();
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				readTotal = 0;
				val = 0;
				
				timer = new Timer();
				timer.schedule(new UpdateTask(), 1000, 1000);
				
				File root = Environment.getExternalStorageDirectory();
				File f = new File(root + getPath() + "/" + filename);
				fos = new FileOutputStream(f);

				while ((read = clientInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
					readTotal += read;
					fos.write(buffer, 0, read);
				}
				
				timer.cancel();
				notificationManager.cancel(11);
				Notify n = new Notify(this);
				n.displayfilenotif2(username, filename, size, 13);

				long endTime = System.currentTimeMillis();
				Log.d("test", readTotal + " bytes read in "
						+ (endTime - startTime) + " ms.");

			} catch (IOException e) {
				cancelnotif();
				Log.d("test", e.getMessage());
			} finally {
				try {
					clientInputStream.close();
					fos.close();
					clientSocket.close();
					serverSocket.close();
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
			}
		}
		
		
		else{
			Notify n = new Notify(getBaseContext());
			n.displayfilenotif2(username, filename, size, 15);
		}
	}
	
	class UpdateTask extends TimerTask {
		int counter = 0;
		double temp;
		public void run() {
			val = ((readTotal / size) * 100);
			notification.contentView.setProgressBar(R.id.status_progress, 100,
					(int) val, false);
			notificationManager.notify(11, notification);
		}
	}

	public void cancelnotif() {
		Log.d("test","cancel notif in FRS");
		timer.cancel();
		notification.contentView.setProgressBar(R.id.status_progress, 100,
				(int) val, true);
		notificationManager.cancel(11);
		Notify n = new Notify(getBaseContext());
		n.displayfilenotif2(username, filename, size, 14);
	}
	

	public String getPath() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("downloadDirectory", "/Download");
	}

	public boolean getState() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
			Log.d("test", "Can write to SD card");
			return true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
			Log.d("test", "Cannot write to SD card");
			return false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
			Log.d("test", "Cannot read nor write");
			return false;
		}
	}
}

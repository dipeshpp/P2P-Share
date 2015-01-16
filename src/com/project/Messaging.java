package com.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Messaging extends Activity {

	EditText chat;
	Button send;
	String sip, myname, macid, thisuser;
	Send sendmsg;
	ArrayList<HashMap<String, String>> list;
	Intent intent;
	IntentFilter intentFilter;
	HashMap<String, String> map;
	MessageAdapter adapter;
	InputMethodManager imm;
	int notificationID = 1;
	Wifi wifi;
	FileHandler fh;
	Notify notify;
	WifiManager wm;
	TextView label;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messaging);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			thisuser = extras.getString("username");
			sip = extras.getString("sip");
			macid = extras.getString("macid");
		}

		// sip=getIntent().getExtras().getString("sip");
		// Log.d("testip",sip);
		nm.cancel(getIntent().getExtras().getInt("notifid"));
		label = (TextView) findViewById(R.id.label);
		send = (Button) findViewById(R.id.send);
		chat = (EditText) findViewById(R.id.chat);
		label.setText(thisuser + " @ " + sip);
		// prefs = getSharedPreferences("myprefs", 0);
		// final HashMap<String, String> map;
		sendmsg = new Send();
		wifi = new Wifi();
		fh = new FileHandler(this);
		notify = new Notify(this);
		wm = wifi.getWifiManager(this);

		// intent = getIntent();
		// sip = intent.getExtras().getString("userip");
		intentFilter = new IntentFilter("p2pshare.newmessage");
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

		ListView chatlist = (ListView) findViewById(R.id.chatList);
		list = new ArrayList<HashMap<String, String>>();
		adapter = new MessageAdapter(this, list);
		chatlist.setAdapter(adapter);

		readFromFile();

	}

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}

	public void sendmsg(View v) {

		if (wifi.checkwifi(this)) {

			String tosend = chat.getText().toString();

			if (tosend.trim().length() > 0) {

				String msg = "11" + myname + Constants.separator
						+ wm.getConnectionInfo().getMacAddress()
						+ Constants.separator + tosend;

				try {
					sendmsg.send(InetAddress.getByName(sip), msg);
				} catch (Exception e) {
					Log.e("MYAPP", "exception", e);
				}

				// Log.d("test",sip);
				// Log.d("test",wifi.getmyip());

				if (!sip.equals(wifi.getmyip(wm))) {
					map = new HashMap<String, String>();
					map.put("line1", myname);
					map.put("line2", chat.getText().toString());
					list.add(map);
					adapter.notifyDataSetChanged();
					fh.writeToFile(macid, myname, chat.getText().toString(),
							false);
				}
				chat.setText("");
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			} else
				Toast.makeText(this, "Please enter a message",
						Toast.LENGTH_SHORT).show();
		}
	}

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			// Log.d("data","called");
			Bundle serviceData = intent.getExtras();
			String username = serviceData.getString("name");
			String tsip = serviceData.getString("ip");
			String macid = serviceData.getString("macid");

			if (tsip.equals(sip)) {
				String message = serviceData.getString("message");
				// Log.d("data", username);
				// Log.d("data", message);
				map = new HashMap<String, String>();
				map.put("line1", username);
				map.put("line2", message);
				list.add(map);
				adapter.notifyDataSetChanged();
			} else {
				notify.displaynotification(username, tsip, macid);
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		isInFront.setState(true);
		myname = getUsername();
		registerReceiver(intentReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		isInFront.setState(false);
		unregisterReceiver(intentReceiver);

	}

	public static class isInFront {
		private static boolean isInFront;

		public static boolean getState() {
			return isInFront;
		}

		public static void setState(boolean b) {
			isInFront = b;
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private void readFromFile() {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					openFileInput(macid)));
			String line;
			String temp = "";

			while ((line = input.readLine()) != null) {
				temp = temp + line;
				// Log.d("read", temp);
			}

			String s[] = split(temp);

			int i = 0;
			while (i < s.length) {
				map = new HashMap<String, String>();
				map.put("line1", s[i]);
				map.put("line2", s[i + 1]);
				list.add(map);
				adapter.notifyDataSetChanged();
				i = i + 2;
			}

			/*
			 * while ((line = input.readLine()) != null) { String temp = line;
			 * map = new HashMap<String, String>(); String[] s = split(temp);
			 * map.put("line1", thisuser); map.put("line2", s[1]);
			 * list.add(map);
			 * 
			 * // Log.d("read", temp); }
			 */

			// Log.d("read",buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String[] split(String s) {
		return s.split(Constants.separator);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.layout.menu2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sendfile:
			showFileChooser();
			break;

		case R.id.clearhistory:
			if (deleteFile(macid)) {
				Toast.makeText(this, "Deleted Successfully!",
						Toast.LENGTH_SHORT).show();
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
			break;
		}
		return true;
	}

	protected void onFileSelect(File file) {
		// Handle the file selected.
	}

	private static final int FILE_SELECT_CODE = 0;
	private static final String TAG = "MyActivity";

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				Uri uri = data.getData();
				Log.d(TAG, "File Uri: " + uri.toString());
				// Get the path
				String path = null;
				try {
					path = FileUtils.getPath(this, uri);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("test", "File Path: " + path);
				// Get the file instance
				// File file = new File(path);
				// Initiate the upload

				File f = new File(path);
				long filesize = f.length();
				String filename = f.getName();
				Log.d("test", Long.toString(filesize));

				String[] args = { getUsername(),
						wm.getConnectionInfo().getMacAddress(), filename, path,
						Long.toString(filesize) };
				String result = "";
				for (String s : args)
					result = result + s + Constants.separator;

				String filemsg = "02" + result;
				try {
					sendmsg.send(InetAddress.getByName(sip), filemsg);
				} catch (Exception e) {
					Log.d("test", e.getMessage());
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
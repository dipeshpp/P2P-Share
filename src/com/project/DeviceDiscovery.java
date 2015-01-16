package com.project;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceDiscovery extends Activity {

	Wifi wifi;
	WifiManager wm;
	String rec;
	Handler myHandler1;
	SharedPreferences prefs;
	ListView mylist;
	UserAdapter adapter;
	ArrayList<User> userlist;
	Button refresh;
	Send send;
	String sendto;
	IntentFilter intentFilter, wifi_intentFilter;
	Intent service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		wifi = new Wifi();
		service = new Intent(this, ReceiveService.class);
		intentFilter = new IntentFilter("p2pshare.userjoins");
		wifi_intentFilter = new IntentFilter();
		wifi_intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifi_intentFilter
				.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

		startService(service);

		refresh = (Button) findViewById(R.id.refresh);
		mylist = (ListView) findViewById(R.id.myList);

		userlist = new ArrayList<User>();
		adapter = new UserAdapter(this, userlist);

		mylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long index) {

				User u = adapter.getItem(position);
				String username = u.getname();
				String sendto = u.getip();
				String macid = u.getmacid();

				Intent i = new Intent("com.project.Messaging");
				i.putExtra("username", username);
				i.putExtra("sip", sendto);
				i.putExtra("macid", macid);
				startActivity(i);

			}
		});

		mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long index) {
				final User u = adapter.getItem(position);
				AlertDialog.Builder alert = new AlertDialog.Builder(view
						.getContext());

				alert.setTitle("User Details");
				alert.setMessage(u.UserInfo());

				alert.setPositiveButton("Send File",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendto = u.getip();
								showFileChooser();
							}

						});
				alert.setNegativeButton("Send Message",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String username = u.getname();
								String sendto = u.getip();
								String macid = u.getmacid();

								Intent i = new Intent("com.project.Messaging");
								i.putExtra("username", username);
								i.putExtra("sip", sendto);
								i.putExtra("macid", macid);
								startActivity(i);
							}
						});

				alert.show();
				return false;

			}

		});

		mylist.setAdapter(adapter);

	}

	public void refresh(View v) {
		adapter.clear();
		refresh.setText("Refresh | Online users: 0");

		if (wifi.checkwifi(this)) {
			wm = wifi.getWifiManager(this);
			send.multicast("00" + getString());

		}
	}

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("p2pshare.userjoins".equals(action)) {
				Bundle serviceData = intent.getExtras();
				String sip = serviceData.getString("sip");
				String username = serviceData.getString("username");
				String macid = serviceData.getString("macid");
				String device = serviceData.getString("device");
				String platform = serviceData.getString("platform");
				// Log.d("data", username);

				Boolean flag = false;
				if (!adapter.isEmpty()) {
					int index = adapter.checkItem(sip);
					if (!(index == 99999)) {
						User u = adapter.getItem(index);
						u.setname(username);
						adapter.notifyDataSetChanged();
						flag = true;
					}
				}

				if (flag == false) {
					userlist.add(new User(username, sip, macid, device,
							platform));
					adapter.notifyDataSetChanged();
				}

				refresh.setText("Refresh | Online users: " + adapter.getCount());

			}
		}
	};

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(WifiStateChangedReceiver, wifi_intentFilter);
		registerReceiver(intentReceiver, intentFilter);
		adapter.clear();
		refresh.setText("Refresh | Online users: 0");

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(intentReceiver);
		unregisterReceiver(WifiStateChangedReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private void exit() {
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.multicast:
			if (wifi.checkwifi(this))
				multicast();
			break;

		case R.id.search:
			if (wifi.checkwifi(this)) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				// alert.setTitle("Search");
				alert.setMessage("Enter File Search String:");

				// Set an EditText view to get user input
				final EditText input = new EditText(this);
				alert.setView(input);
				input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();

								if (value.matches(".*\\w.*")) {
									String[] args = { getUsername(), value };
									String result = "";
									for (String s : args)
										result = result + s
												+ Constants.separator;

									Intent i = new Intent(
											"com.project.SearchResults");
									i.putExtra("result", result);
									startActivity(i);
								} else
									Toast.makeText(getBaseContext(),
											"Please enter a valid string",
											Toast.LENGTH_SHORT).show();
								return;
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								return;
							}
						});
				alert.show();
			}
			break;

		case R.id.deviceinfo:
			Intent deviceInfo = new Intent("com.project.DeviceInfo");
			startActivity(deviceInfo);
			break;

		case R.id.history:
			Intent History = new Intent("com.project.History");
			startActivity(History);
			break;

		case R.id.settings:

			Intent settingsActivity = new Intent(this, Preferences.class);
			startActivity(settingsActivity);
			break;

		case R.id.exit:
			stopService(service);
			if (send != null)
				send.closeSocket();
			exit();
			break;
		}
		return true;
	}

	public String getString() {

		String[] args = { getUsername(),
				wm.getConnectionInfo().getMacAddress(), Constants.model,
				Constants.platform };
		String result = "";
		for (String s : args)
			result = result + s + Constants.separator;
		return result.toString();
	}

	private BroadcastReceiver WifiStateChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			final String action = intent.getAction();
			// Log.d("test", "wifi broadcast received");

			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

				NetworkInfo info = (NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

				if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
					// Log.d("test", "wifi connected");

					wm = wifi.getWifiManager(getBaseContext());
					Toast.makeText(getBaseContext(), wifi.getinfo(wm),
							Toast.LENGTH_SHORT).show();
					send = new Send(wm);
					new CountDownTimer(1000, 1000) {
						public void onTick(long millisUntilFinished) {
						}

						public void onFinish() {
							send.multicast("00" + getString());
						}
					}.start();

				} else if (info.getState().equals(
						NetworkInfo.State.DISCONNECTED)) {
					// Log.d("test", "wifi disconnected");
					adapter.clear();
					refresh.setText("Refresh | Online users: 0");
					Toast.makeText(getBaseContext(), "Wi-Fi Disconnected!",
							Toast.LENGTH_SHORT).show();
				}
			}

			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (!intent.getBooleanExtra(
						WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
					// Log.d("test", "wifi lost");
					adapter.clear();
					refresh.setText("Refresh | Online users: 0");
					Toast.makeText(getBaseContext(), "Wi-Fi Disconnected!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public void multicast() {
		final ArrayList<String> checked = adapter.getChecked();
		if (checked.size() < 2)
			Toast.makeText(this, "Please select multiple users.",
					Toast.LENGTH_SHORT).show();
		else {
			Log.d("test", "Users selected");

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Multicast");
			alert.setMessage("Type Message");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
			alert.setView(input);

			alert.setPositiveButton("Send",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String msg = "11" + getUsername()
									+ Constants.separator
									+ wm.getConnectionInfo().getMacAddress()
									+ Constants.separator
									+ input.getText().toString();
							for (String ip : checked) {
								try {
									send.send(InetAddress.getByName(ip), msg);
								} catch (UnknownHostException e) {
									Log.d("test", e.getMessage());
								} catch (Exception e) {
									Log.d("test", e.getMessage());
								}
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();
		}
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
					send.send(InetAddress.getByName(sendto), filemsg);
				} catch (Exception e) {
					Log.d("test", e.getMessage());
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}

package com.project;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResults extends Activity {

	Wifi wifi;
	WifiManager wm;
	IntentFilter intentFilter;
	Send send;
	Button search;
	ListView mylist;
	FileAdapter adapter;
	ArrayList<FileResult> filelist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		wifi = new Wifi();
		wm = wifi.getWifiManager(this);
		send = new Send(wm);
		Bundle extras = getIntent().getExtras();
		String result = extras.getString("result");
		send.broadcastSearch("03" + result);

		search = (Button) findViewById(R.id.search);
		mylist = (ListView) findViewById(R.id.myfileList);
		filelist = new ArrayList<FileResult>();
		adapter = new FileAdapter(this, filelist);

		intentFilter = new IntentFilter("p2pshare.matchfound");

		mylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long index) {

				final FileResult f = adapter.getItem(position);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SearchResults.this);
				builder.setMessage("Download " + f.getfilename() + " ?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										Intent i = new Intent(
												SearchResults.this,
												FileAccept.class);
										int notifid = 99;
										i.putExtra("notifid", notifid);
										i.putExtra("username", f.getusername());
										i.putExtra("sip", f.getsip());
										i.putExtra("macid", f.getmacid());
										i.putExtra("filename", f.getfilename());
										i.putExtra("path", f.getfilepath());
										i.putExtra("size", f.getfilesize());
										startService(i);

									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.show();

			}
		});

		mylist.setAdapter(adapter);

	}

	public void onResume() {
		super.onResume();
		registerReceiver(intentReceiver, intentFilter);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(intentReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("p2pshare.matchfound".equals(action)) {
				Bundle serviceData = intent.getExtras();
				String sip = serviceData.getString("sip");
				String username = serviceData.getString("username");
				String macid = serviceData.getString("macid");
				String filename = serviceData.getString("filename");
				String filepath = serviceData.getString("path");
				String filesize = serviceData.getString("size");
				Log.d("test", filename);

				filelist.add(new FileResult(filename, filepath, filesize,
						username, sip, macid));
				adapter.notifyDataSetChanged();

				search.setText("Search | Results Found: " + adapter.getCount());
			}
		}

	};

	public void search(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// alert.setTitle("Search");
		alert.setMessage("Enter File Search String:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String value = input.getText().toString();

				if (value.matches(".*\\w.*")) {
					adapter.clear();
					search.setText("Search | Results Found: 0");
					String[] args = { getUsername(), value };
					String result = "";
					for (String s : args)
						result = result + s + Constants.separator;
					send.broadcastSearch("03" + result);
				} else
					Toast.makeText(getBaseContext(),
							"Please enter a valid string", Toast.LENGTH_SHORT)
							.show();
				return;
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}
				});
		alert.show();
	}

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}
}
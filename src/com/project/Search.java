package com.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class Search extends IntentService {

	public Search() {
		super("Search");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		Wifi wifi = new Wifi();
		Send send = new Send();
		WifiManager wm = wifi.getWifiManager(this);

		Bundle extras = arg0.getExtras();
		String value = extras.getString("value");
		String sip = extras.getString("sip");
		// String remoteuser=extras.getString("username");

		ArrayList<String> list = readFromFile();
		ArrayList<String> keywords = getKeywords(value);
		ArrayList<String> found = new ArrayList<String>();

		for (String path : list) {

			File dir = new File(path);
			File[] filelist = dir.listFiles();

			for (File f : filelist) {

				if (!f.isDirectory()) {

					String filename = f.getName();

					for (String keyword : keywords) {

						// Log.d("test",keyword);
						if (filename.toLowerCase().contains(keyword)
								&& !found.contains(filename)) {

							found.add(filename);

							String[] args = { getUsername(),
									wm.getConnectionInfo().getMacAddress(),
									filename, f.getPath(),
									Long.toString(f.length()) };

							String result = "";
							for (String s : args)
								result = result + s + Constants.separator;

							String filemsg = "30" + result;

							try {
								send.send(InetAddress.getByName(sip), filemsg);
							} catch (Exception e) {
								Log.d("test", e.getMessage());
							}

						}

					}

				}

			}
		}
		stopSelf();

	}

	public ArrayList<String> getKeywords(String value) {
		ArrayList<String> keywords = new ArrayList<String>();
		String delims = "[ ]+";
		String[] tokens = value.split(delims);
		int y;
		if (value.charAt(0) == ' ')
			y = 1;
		else
			y = 0;

		for (int i = y; i < tokens.length; i++)
			keywords.add(tokens[i].toLowerCase());

		for (String keyword : keywords)
			Log.d("test", keyword);

		Log.d("test", Integer.toString(keywords.size()));

		return keywords;

	}

	private ArrayList<String> readFromFile() {
		BufferedReader input = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			input = new BufferedReader(new InputStreamReader(
					openFileInput("shared")));
			String line;

			while ((line = input.readLine()) != null) {
				if (new File(line).exists())
					list.add(line);
			}

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
		return list;

	}

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}

}

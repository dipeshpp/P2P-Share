package com.project;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ListView;

public class DeviceInfo extends Activity {
	InfoAdapter adapter;
	HashMap<String, String> map;
	private ArrayList<HashMap<String, String>> list;
	WifiManager wm;
	ConnectivityManager cm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceinfo);

		ListView lview = (ListView) findViewById(R.id.deviceinfo);
		list = new ArrayList<HashMap<String, String>>();
		adapter = new InfoAdapter(this, list);
		lview.setAdapter(adapter);
		populateList();
	}

	private void populateList() {

		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();

		Wifi w = new Wifi();
		wm = w.getWifiManager(this);
		WifiInfo info = wm.getConnectionInfo();

		map = new HashMap<String, String>();
		map.put("First Column", " ");
		map.put("Second Column", " ");
		list.add(map);

		
		map = new HashMap<String, String>();
		map.put("First Column", "Type");
		map.put("Second Column", ni.getTypeName());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "BSSID");
		map.put("Second Column", info.getBSSID());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "IP Address");
		map.put("Second Column", android.text.format.Formatter
				.formatIpAddress(info.getIpAddress()));
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Link Speed");
		map.put("Second Column", Integer.toString(info.getLinkSpeed()) + " "
				+ WifiInfo.LINK_SPEED_UNITS);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "MAC Address");
		map.put("Second Column", info.getMacAddress());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Netword ID");
		map.put("Second Column", Integer.toString(info.getNetworkId()));
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "RSSI");
		map.put("Second Column", String.valueOf(info.getRssi()));
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "SSID");
		map.put("Second Column", info.getSSID());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Status");
		map.put("Second Column", getState());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Supplicant");
		map.put("Second Column", info.getSupplicantState().name());
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", " ");
		map.put("Second Column", " ");
		list.add(map);
		
		map = new HashMap<String, String>();
		map.put("First Column", "Board");
		map.put("Second Column", Constants.board);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Bootloader");
		map.put("Second Column", Constants.bootloader);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Brand");
		map.put("Second Column", Constants.brand);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Build ID");
		map.put("Second Column", Constants.display);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Device");
		map.put("Second Column", Constants.device);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Fingerprint");
		map.put("Second Column", Constants.fingerprint);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Hardware");
		map.put("Second Column", Constants.hardware);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Host");
		map.put("Second Column", Constants.host);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Instruction Set");
		map.put("Second Column", Constants.cpu_abi);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Manufacturer");
		map.put("Second Column", Constants.manufacturer);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Model");
		map.put("Second Column", Constants.model);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Product");
		map.put("Second Column", Constants.product);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Radio");
		map.put("Second Column", Constants.radio);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Release");
		map.put("Second Column", Constants.build);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "SDK");
		map.put("Second Column", Constants.sdk);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Tags");
		map.put("Second Column", Constants.tags);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "Type");
		map.put("Second Column", Constants.type);
		list.add(map);

		map = new HashMap<String, String>();
		map.put("First Column", "User");
		map.put("Second Column", Constants.user);
		list.add(map);

	}

	public String getState() {
		NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected())
			return "Connected";
		else
			return "Disconnected";
	}
}
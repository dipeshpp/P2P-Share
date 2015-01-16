package com.project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class ReceiveService extends Service {
	private Send send;
	private String myip;
	private Intent userjoins,newmessage,matchfound;
	private Receive r;
	private ReceiveMulticast rm;
	private FileHandler fh;
	private Notify notify;
	private Wifi wifi;
	private WifiManager wm;
	private volatile boolean receive = false, receivemulti = false;
	private Handler myHandler1;
	private PowerManager pm;
	private PowerManager.WakeLock wl;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
		wl.acquire();

		try {
			fh = new FileHandler(this);
			notify = new Notify(this);
			wifi = new Wifi();

			IntentFilter wifi_intentFilter = new IntentFilter();
			wifi_intentFilter
					.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			wifi_intentFilter
					.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
			registerReceiver(WifiStateChangedReceiver, wifi_intentFilter);

		} catch (Exception e) {
			Log.d("test", "exception in oncreate");
		}

		myHandler1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				String rawrec = (String) msg.obj;
				String sip = rawrec.substring(0, rawrec.indexOf("+"));
				String rec = rawrec.substring(rawrec.indexOf("+") + 1);

				String head = "";
				int headint = 0;
				String temp = "";
				try {
					head = rec.substring(0, 2);
					headint = Integer.parseInt(head);
					temp = rec.substring(2);
				} catch (Exception e) {
					Log.d("test", "Invalid String" + e.getMessage());
				}

				// Log.d("test", temp);
				switch (headint) {

				case 00:
				case 01:

					String[] userinfo = split(temp);
					userjoins = new Intent("p2pshare.userjoins");
					userjoins.putExtra("sip", sip);
					userjoins.putExtra("username", userinfo[0]);
					userjoins.putExtra("macid", userinfo[1]);
					userjoins.putExtra("device", userinfo[2]);
					userjoins.putExtra("platform", userinfo[3]);
					sendBroadcast(userjoins);

					if (headint == 00 && !sip.equals(myip)) {

						String compose = "01" + getString();

						try {
							send.send(InetAddress.getByName(sip), compose);
						} catch (Exception e) {
							Log.d("test", e.getMessage());
						}
					}
					break;

				case 11:
					String[] s = split(temp);
					String username = s[0];
					String macid = s[1];
					String message = s[2];
					fh.writeToFile(macid, username, message, true);
					if (Messaging.isInFront.getState() == true) {
						newmessage = new Intent("p2pshare.newmessage");
						newmessage.putExtra("name", username);
						newmessage.putExtra("ip", sip);
						newmessage.putExtra("message", message);
						newmessage.putExtra("macid", macid);
						sendBroadcast(newmessage);
					} else
						notify.displaynotification(username, sip, macid);
					break;

				case 02:
					String[] fileinfo = split(temp);
					String name = fileinfo[0];
					String mac = fileinfo[1];
					String filename = fileinfo[2];
					String path = fileinfo[3];
					String size = fileinfo[4];

					notify.displayfilenotif(name, sip, mac, filename, path,
							size);
					break;

				case 20:
					String[] fileinfo2 = split(temp);
					String name2 = fileinfo2[0];
					//String mac2 = fileinfo2[1];
					String filename2 = fileinfo2[2];
					String path2 = fileinfo2[3];
					String size2 = fileinfo2[4];

					Intent fss = new Intent(getBaseContext(),
							FileSendService.class);
					fss.putExtra("username", name2);
					fss.putExtra("sip", sip);
					fss.putExtra("filename", filename2);
					fss.putExtra("path", path2);
					fss.putExtra("size", size2);
					startService(fss);
					break;
					
				case 03:
					String[] search = split(temp);
					String remoteuser=search[0];
					String value=search[1];
					Intent searchintent = new Intent(getBaseContext(), Search.class);
					searchintent.putExtra("value", value);
					searchintent.putExtra("sip",sip);
					searchintent.putExtra("username", remoteuser);
					startService(searchintent);
					break;
					
				case 30:
					String[] fileinfo3 = split(temp);
					String name3 = fileinfo3[0];
					String mac3 = fileinfo3[1];
					String filename3 = fileinfo3[2];
					String path3 = fileinfo3[3];
					String size3 = fileinfo3[4];
					
					matchfound = new Intent("p2pshare.matchfound");
					matchfound.putExtra("username", name3);
					matchfound.putExtra("sip", sip);
					matchfound.putExtra("macid", mac3);
					matchfound.putExtra("filename", filename3);
					matchfound.putExtra("path", path3);
					matchfound.putExtra("size", size3);
					sendBroadcast(matchfound);
					break;

				default:
					break;
				}

			}
		};

	}

	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return prefs.getString("usernamePref", Constants.model);
	}

	public void wifiInit() {
		wm = wifi.getWifiManager(this);
		myip = wifi.getmyip(wm);
		wifi.acquirelock(wm);
		send = new Send(wm);
	}

	public void receivemulti() {
		if (receivemulti == false) {
			rm = new ReceiveMulticast();
			rm.start();
		}
	}

	public void receive() {
		if (receive == false) {
			r = new Receive();
			r.start();
		}
	}

	public void stopThread() {

		if (receivemulti == true) {
			try {
				// rm.s.leaveGroup(rm.group);
				rm.s.close();
			} catch (Exception e) {
				Log.d("test", e.getMessage());
			}
		}

		if (receive == true) {
			try {
				r.ds1.close();
			} catch (Exception e) {
				Log.d("test", e.getMessage());
			}
		}
		
		//send.closeSocket();
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
					Log.d("test", "wifi connected");
					stopThread();
					wifiInit();

					new CountDownTimer(800, 800) {
						public void onTick(long millisUntilFinished) {
						}

						public void onFinish() {
							receive();
							receivemulti();
						}
					}.start();

				} else if (info.getState().equals(
						NetworkInfo.State.DISCONNECTED)) {
					Log.d("test", "wifi disconnected");
					stopThread();
				}
			}

			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (!intent.getBooleanExtra(
						WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
					Log.d("test", "wifi lost");
					stopThread();
				}
			}
		}
	};

	@Override
	public void onDestroy() {
		unregisterReceiver(WifiStateChangedReceiver);
		stopThread();
		wl.release();
		super.onDestroy();
	}

	private class Receive extends Thread {
		private DatagramSocket ds1;
		private DatagramPacket p;

		public void run() {

			Log.d("test", "receive on");
			try {
				ds1 = new DatagramSocket(Constants.port);
			} catch (SocketException e) {
				Log.d("test", "Exception in new datagram");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int buffer_size = 1024;
			byte buffer1[] = new byte[buffer_size];
			p = new DatagramPacket(buffer1, buffer1.length);
			receive = true;

			while (receive) {
				try {
					ds1.receive(p);
					String sip = p.getAddress().getHostAddress();
					String rec = new String(p.getData(), 0, p.getLength());
					Log.d("test", "received datagram " + rec);
					Message msg = myHandler1.obtainMessage();
					msg.obj = sip + "+" + rec;
					myHandler1.sendMessage(msg);
					
					Thread.sleep(100);
				} catch (Exception e) {
					receive = false;
					Log.d("test", "Receive Thread stopped" + e.getMessage());
				}
			}
		}

	}

	private class ReceiveMulticast extends Thread {
		private MulticastSocket s;
		private DatagramPacket mp;
		private InetAddress group;

		public void run() {

			Log.d("test", "receive multicast on");
			try {
				group = InetAddress.getByName(Constants.multicastip);
				s = new MulticastSocket(Constants.multicastport);
				s.joinGroup(group);
			} catch (UnknownHostException e1) {
				Log.d("test", e1.getMessage());
			} catch (IOException e) {
				Log.d("test", e.getMessage());
			}

			byte[] buffer2 = new byte[1024];
			mp = new DatagramPacket(buffer2, buffer2.length);
			receivemulti = true;

			while (receivemulti) {

				try {
					// Log.d("test", "blocked on rm");
					s.receive(mp);
					// Log.d("test", "after receive");
					String sip = mp.getAddress().getHostAddress();
					String rec = new String(mp.getData(), 0, mp.getLength());
					Log.d("test", "received multi" + rec);
					Message msg = myHandler1.obtainMessage();
					msg.obj = sip + "+" + rec;
					myHandler1.sendMessage(msg);

					Thread.sleep(100);
				} catch (Exception e) {
					receivemulti = false;
					Log.d("test",
							"ReceiveMulticast Thread stopped" + e.getMessage());
				}
			}
		}
	}

	public String getString() {

		String[] args = { getUsername(),
				wm.getConnectionInfo().getMacAddress(), Constants.model,
				Constants.platform };
		String result = "";
		for (String s : args)
			result = result + s + Constants.separator;
		return result;
	}

	public String[] split(String s) {
		return s.split(Constants.separator);
	}

}

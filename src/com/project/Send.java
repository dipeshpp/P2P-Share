package com.project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Send {
	private WifiManager wm;
	private static DatagramSocket ds2;
	private MulticastSocket s;

	Send() {
	};

	Send(WifiManager wm) {
		this.wm = wm;
	}

	public void send(final InetAddress address, final String send)
			throws Exception {

		try {
			int buffer_size = 1024;
			byte buffer3[] = new byte[buffer_size];
			ds2 = getInstance();
			buffer3 = send.getBytes();
			ds2.send(new DatagramPacket(buffer3, send.length(), address, 7777));
			// Log.d("msg", address.toString());
			// ds2.close();
		} catch (Exception e) {
			Log.d("test", "exception in send");
		}

	}

	public static DatagramSocket getInstance() {
		if (ds2 == null) {
			try {
				ds2 = new DatagramSocket(1555);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				Log.d("test", "Socket Exception: " + e.getMessage());
			}
		}
		return ds2;
	}

	public void closeSocket() {
		if (ds2!=null && ds2.isBound())
			ds2.close();
	}

	public void multicast(String msg) {

		try {
			InetAddress group = InetAddress.getByName(Constants.multicastip);
			s = new MulticastSocket(Constants.multicastport);
			s.setTimeToLive(16);
			s.joinGroup(group);
			DatagramPacket data = new DatagramPacket(msg.getBytes(),
					msg.length(), group, Constants.multicastport);
			s.send(data);

			s.leaveGroup(group);
			s.close();

			send(getBroadcastAddress(), msg);
			Log.d("test", "sent" + msg + "@" + getBroadcastAddress().toString());
		} catch (Exception e) {
			Log.d("test", "exception in send multicast" + e.getMessage());
		}

	}

	public void broadcastSearch(String value) {
		try {
			send(getBroadcastAddress(), value);
		} catch (Exception e) {
			Log.d("test", e.getMessage());
		}
	}

	public InetAddress getBroadcastAddress() throws IOException {
		// WifiManager wifi = (WifiManager)
		// context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wm.getDhcpInfo();
		// handle null somehow
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);

	}

}

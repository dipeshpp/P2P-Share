package com.project;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class FileHandler {
	static Context context;
	Wifi wifi = new Wifi();

	FileHandler(Context context) {
		FileHandler.context = context;
	}

	public void writeToFile(String macid, String username, String msg, boolean b) {
		// String eol = System.getProperty("line.separator");
		FileOutputStream fos = null;
		String write = username + Constants.separator + msg
				+ Constants.separator;
		try {
			fos = context.openFileOutput(macid, Context.MODE_APPEND);
			fos.write(write.getBytes());
		} catch (Exception e) {
			Log.d("test", e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (b){
				storename(macid, username);
				Log.d("test","stored");
			}
		}
	}
	
	public void writeToFile(String path){
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput("shared", Context.MODE_APPEND);
			fos.write(path.getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
		} catch (Exception e) {
			Log.d("test", e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public void storename(String macid, String username) {
		SharedPreferences prefs = context.getSharedPreferences("usermap", 0);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(macid, username);
		prefsEditor.commit();
	}

}

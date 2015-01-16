package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteMsg extends Activity {

	String name, macid;
	ArrayList<HashMap<String, String>> list;
	HashMap<String, String> map;
	MessageAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deletemsg);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			macid = extras.getString("macid");
			name = extras.getString("name");
		}

		TextView label = (TextView) findViewById(R.id.label);
		label.setText(name + " @ " + macid);

		ListView chatlist = (ListView) findViewById(R.id.msglist);
		list = new ArrayList<HashMap<String, String>>();
		adapter = new MessageAdapter(this, list);
		chatlist.setAdapter(adapter);

		readFromFile();

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
				map.put("line2", s[i+1]);
				list.add(map);
				adapter.notifyDataSetChanged();
				i = i + 2;
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
	}

	public String[] split(String s) {
		return s.split(Constants.separator);
	}

	public void deletemsg(View v) {
		if (deleteFile(macid)) {
			Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_SHORT)
					.show();
			adapter.clear();
			adapter.notifyDataSetChanged();
			Intent returnIntent = new Intent();
			returnIntent.putExtra("Deleted", name);
			setResult(RESULT_OK, returnIntent);
			finish();
		}

	}
}
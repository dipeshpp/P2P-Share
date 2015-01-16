package com.project;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class History extends Activity {
	ArrayAdapter<String> adapter;
	String[] filelist;
	ArrayList<String> name;
	ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		list = (ListView) findViewById(R.id.historylist);
		name = new ArrayList<String>();
		filelist = fileList();

		for (String macid : filelist) {
			if (!macid.equals("shared"))
				name.add(getName(macid));
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, name);

		// Assign adapter to ListView
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String macid = filelist[position];
				String name = adapter.getItem(position);
				// Log.d("test",macid);
				Intent i = new Intent("com.project.DeleteMsg");
				i.putExtra("macid", macid);
				i.putExtra("name", name);
				startActivityForResult(i, position);
			}

		});
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	private String getName(String macid) {
		SharedPreferences prefs = getSharedPreferences("usermap", 0);
		return prefs.getString(macid, "Unknown");

	}

	public void delete(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Are you sure you want to delete all the conversations?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								for (String macid : fileList()) {
									if (!macid.equals("shared"))
										deleteFile(macid);
								}
								adapter.clear();
								Toast.makeText(getBaseContext(),
										"Deleted Successfully!",
										Toast.LENGTH_SHORT).show();
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			String name = data.getStringExtra("Deleted");
			adapter.remove(name);

		}

	}
}
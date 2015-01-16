package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Shared extends Activity {
	ArrayAdapter<String> adapter;
	ArrayList<String> sharedlist;
	ListView list;
	FileHandler fh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shared);
		fh = new FileHandler(this);
		list = (ListView) findViewById(R.id.sharedlist);
		sharedlist = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, R.layout.row4, R.id.text1,
				sharedlist);

		// Assign adapter to ListView
		list.setAdapter(adapter);
		readFromFile();

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long index) {

				AlertDialog.Builder builder = new AlertDialog.Builder(Shared.this);
				builder.setMessage("Remove from shared directory list?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										sharedlist.remove(position);
										adapter.notifyDataSetChanged();
										save();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}
	
	public void save(){
		deleteFile("shared");
		for(String temp:sharedlist){
			fh.writeToFile(temp);
		}
	}

	private void readFromFile() {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					openFileInput("shared")));
			String line;
			// String temp = "";

			while ((line = input.readLine()) != null) {
				adapter.add(line);
				// Log.d("read", temp);
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

	public void addfolder(View v) {
		Intent intent = getIntent("com.estrongs.action.PICK_DIRECTORY",
				"Select a folder");
		// Assign a path.
		intent.setData(Uri.parse("file://" + "/sdcard"));

		try {
			startActivityForResult(intent, 1);
		} catch (ActivityNotFoundException e) {
			// No compatible file manager was found.
			Toast.makeText(this, "ES File Explorer not installed.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private Intent getIntent(String action, String title) {

		Intent intent = new Intent(action);

		if (title != null)
			intent.putExtra("com.estrongs.intent.extra.BUTTON_TITLE", title);

		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK || data == null)
			return;
		else if (requestCode == 1) {
			Uri uri = data.getData();
			String path = uri.getPath();
			Toast.makeText(this, "Shared "+path, Toast.LENGTH_SHORT).show();
			if (!sharedlist.contains(path)) {
				fh.writeToFile(uri.getPath());
				adapter.clear();
				readFromFile();
			}
		}
	}

}
package com.project;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {
	private Context context;
	private List<FileResult> filelist;

	public FileAdapter(Context context, List<FileResult> filelist) {
		this.context = context;
		this.filelist = filelist;
	}

	public boolean isEmpty() {
		if (filelist.isEmpty())
			return true;
		else
			return false;
	}

	public void clear() {
		filelist.clear();
		notifyDataSetChanged();
	}

	public int getCount() {
		return filelist.size();
	}

	public FileResult getItem(int position) {
		return filelist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		filelist.remove(position);
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {

		final FileResult entry = filelist.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row5, null);
		}

		TextView filename = (TextView) convertView.findViewById(R.id.filename);
		filename.setText(entry.getfilename());

		TextView filesize = (TextView) convertView.findViewById(R.id.fileinfo);
		filesize.setText(entry.getfilesizeinmb() + " - " + entry.getusername());

		/*
		 * TextView user = (TextView) convertView.findViewById(R.id.user);
		 * user.setText(entry.getusername());
		 */

		return convertView;
	}

}

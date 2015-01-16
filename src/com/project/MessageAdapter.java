package com.project;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> list;

	public MessageAdapter(Context context,
			ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
	}

	public boolean isEmpty() {
		if (list.isEmpty())
			return true;
		else
			return false;
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		list.remove(position);
	}

	private class ViewHolder {
		TextView username;
		TextView message;

	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
		ViewHolder holder;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row2, null);

			holder = new ViewHolder();
			holder.username = (TextView) convertView
					.findViewById(R.id.username);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, String> map = list.get(position);  
		holder.username.setText(map.get("line1"));  
		holder.message.setText(map.get("line2"));  
		  
		return convertView;
	}

}

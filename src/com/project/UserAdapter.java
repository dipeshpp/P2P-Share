package com.project;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter {
	private Context context;
	private List<User> userlist;

	public UserAdapter(Context context, List<User> userlist) {
		this.context = context;
		this.userlist = userlist;
	}

	public boolean isEmpty() {
		if (userlist.isEmpty())
			return true;
		else
			return false;
	}

	public void clear() {
		userlist.clear();
		notifyDataSetChanged();
	}

	public int getCount() {
		return userlist.size();
	}

	public User getItem(int position) {
		return userlist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void remove(int position){
		userlist.remove(position);
	}

	public int checkItem(String ip) {
		int count = this.getCount();
		int flag = 0, i;
		for (i = 0; i < count; i++) {
			User u = this.getItem(i);
			String ipaddr = u.getip();
			// Log.d("qwerty", n);

			if (ipaddr.equals(ip))
				flag = 1;

			if (flag == 1)
				break;
		}
		if (flag == 1)
			return i;
		else
			return 99999;
	}
	

	public ArrayList<String> getChecked(){
		ArrayList<String> checked = new ArrayList<String>();
		int count=this.getCount();
		for (int i = 0; i < count; i++){
			User u = this.getItem(i);
			if(u.isSelected())
				checked.add(u.getip());
		}
		return checked;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		final User entry = userlist.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row, null);
		}
		TextView username = (TextView) convertView.findViewById(R.id.username);
		username.setText(entry.getname());

		TextView ip = (TextView) convertView.findViewById(R.id.ip);
		ip.setText(entry.getip());
		
		CheckBox cb=(CheckBox)convertView.findViewById(R.id.checkbox);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				entry.setSelected(isChecked);
			}
		});
		
		
		return convertView;
	}
	

}

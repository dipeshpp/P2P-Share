package com.project;

public class User {
	private String name;
	private String ip;
	private String macid;
	private String device;
	private String platform;
	private boolean selected;

	// Constructor for the User class
	public User(String name, String ip, String macid, String device,
			String platform) {
		// super();
		this.name = name;
		this.ip = ip;
		this.macid = macid;
		this.device = device;
		this.platform = platform;
	}

	// Getter and setter methods for all the fields.

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getip() {
		return ip;
	}

	public void setip(String ip) {
		this.ip = ip;
	}

	public String getmacid() {
		return macid;
	}

	public void setmacid(String macid) {
		this.macid = macid;
	}

	public String getdevice() {
		return device;
	}

	public void setdevice(String device) {
		this.device = device;
	}

	public String getplatform() {
		return platform;
	}

	public void setplatform(String platform) {
		this.platform = platform;
	}

	public String UserInfo() {
		return "Username: " + name + "\n" + "IP Address: " + ip + "\n"
				+ "MAC Address: " + macid + "\n" + "Device: " + device + "\n"
				+ "Platform: " + platform + "\n";
	}
}
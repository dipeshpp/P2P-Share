package com.project;

public class FileResult {
	private String filename;
	private String filepath;
	private String filesize;
	private String username;
	private String sip;
	private String macid;

	public FileResult(String filename, String filepath, String filesize,
			String username, String sip, String macid) {
		// super();
		this.filename = filename;
		this.filepath = filepath;
		this.filesize = filesize;
		this.username = username;
		this.sip = sip;
		this.macid = macid;
		// Log.d("test","FileResult object created");
	}

	// Getter and setter methods for all the fields.

	public String getfilename() {
		return filename;
	}

	public void setfilename(String filename) {
		this.filename = filename;
	}

	public String getfilepath() {
		return filepath;
	}

	public void setfilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getfilesize() {
		return filesize;
	}

	public String getfilesizeinmb() {
		Double d = Double.valueOf(getfilesize());
		return Round((float) (d / 1024 / 1024), 2) + " MB";
	}

	public void setfilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getusername() {
		return username;
	}

	public void setusername(String username) {
		this.username = username;
	}

	public String getsip() {
		return sip;
	}

	public void setsip(String sip) {
		this.sip = sip;
	}

	public String getmacid() {
		return macid;
	}

	public void setmacid(String macid) {
		this.macid = macid;
	}

	public static float Round(float Rval, int Rpl) {
		float p = (float) Math.pow(10, Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float) tmp / p;
	}

}
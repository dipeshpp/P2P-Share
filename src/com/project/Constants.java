package com.project;

public final class Constants {

	private Constants() {
	}

	public static final String multicastip = "230.0.0.1";
	public static final int multicastport = 6789;
	public static final int port = 7777;
	public static final int buffer_size = 1048576;
	public static final int fileport = 12345;

	public static final String separator = "`~`";

	public static final String platform = "Android "
			+ android.os.Build.VERSION.RELEASE;

	public static final String board = android.os.Build.BOARD;
	public static final String bootloader = android.os.Build.BOOTLOADER;
	public static final String brand = android.os.Build.BRAND;
	public static final String build = android.os.Build.VERSION.RELEASE;
	public static final String cpu_abi = android.os.Build.CPU_ABI;
	public static final String cpu_abi2 = android.os.Build.CPU_ABI2;
	public static final String device = android.os.Build.DEVICE;
	public static final String display = android.os.Build.DISPLAY;
	public static final String fingerprint = android.os.Build.FINGERPRINT;
	public static final String hardware = android.os.Build.HARDWARE;
	public static final String host = android.os.Build.HOST;
	public static final String id = android.os.Build.ID;
	public static final String manufacturer = android.os.Build.MANUFACTURER;
	public static final String model = android.os.Build.MODEL;
	public static final String product = android.os.Build.PRODUCT;
	public static final String radio = android.os.Build.RADIO;
	public static final String tags = android.os.Build.TAGS;
	public static final String type = android.os.Build.TYPE;
	public static final String sdk = android.os.Build.VERSION.SDK;
	public static final String user = android.os.Build.USER;

}
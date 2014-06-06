package com.varicom.shadow.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {

	public static final String LOCAL_IP = "127.0.0.1";
	
	public static final String HOST_NAME = "127.0.0.1";
	
	/**
	 * 获取本机ip
	 * @return
	 */
	public static String getHostName()
	{
		try {
			InetAddress inetAddr = InetAddress.getLocalHost();
			return inetAddr.getHostName();
		} catch (UnknownHostException e) {
			return HOST_NAME;
		}
	}
	

	/**
	 * 获取本机hostname
	 * @return
	 */
	public static String getLocalIp()
	{
		String ipAddr = "";
		
		try {
			InetAddress inetAddr = InetAddress.getLocalHost();
			byte[] addr = inetAddr.getAddress();
			// Convert to dot representation
			
			for (int i = 0; i < addr.length; i++) {
				if (i > 0) {
					ipAddr += ".";
				}
				ipAddr += addr[i] & 0xFF;
			}
		} catch (UnknownHostException e) {
			return LOCAL_IP;
		}
		
		return ipAddr;
	}
	
	
	public static void main(String[] args) {
		System.out.println("IP Address: " + getLocalIp());
		System.out.println("Hostname: " + getHostName());
	}
}

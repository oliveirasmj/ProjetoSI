package com.main;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BibControlo {

	public BibControlo() {
	}

	public boolean temLicenca(boolean licenca) {
		return licenca;
	}

	public static void main(String[] args) throws UnknownHostException, SocketException {
		obterDadosSO();
	}

	public static void obterDadosSO() throws UnknownHostException, SocketException {

		// Operating system name
		System.out.println(System.getProperty("os.name"));

		// Operating system version
		System.out.println(System.getProperty("os.version"));

		// Path separator character used in java.class.path
		System.out.println(System.getProperty("path.separator"));

		// User working directory
		System.out.println(System.getProperty("user.dir"));

		// User home directory
		System.out.println(System.getProperty("user.home"));

		// User account name
		System.out.println(System.getProperty("user.name"));

		// Operating system architecture
		System.out.println(System.getProperty("os.arch"));

		// obter ip atual e mac do pc
		InetAddress ip;
		try {

			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			System.out.println(sb.toString());

		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (SocketException e) {

			e.printStackTrace();

		}

	}

}

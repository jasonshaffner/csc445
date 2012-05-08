package server;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import Database.*;

public class ShardServer {
	
	public static void main(String[] args) {
			new ServerThread().start();
	}

	static class ServerThread extends Thread {

		MulticastSocket socket;
		DatagramPacket packet;
		DatagramPacket update = new DatagramPacket(new byte[1024], 1024);
		InetAddress gee = InetAddress.getByName("gee");
		InetAddress rho = InetAddress.getByName("rho");
		InetAddress moxie = InetAddress.getByName("moxie");
		InetAddress lambda = InetAddress.getByName("lambda");
		HashMap<InetAddress, Long> shards = new HashMap<InetAddress, Long>();
		//shards.put(gee,0);
		//shards.put(rho,0);
		//shards.put(moxie,0);
		//shards.put(lambda,0);
		int port = 2372;
		byte[] b;
		byte[] c;
		boolean isRunning = false;
		Random random = new Random();
		Long timeStamp;

		ServerThread () {
			super("ServerThread");
			Date date = new Date();
			b = new byte[1];
			c = new byte[1];
			try {
				socket = new DatagramSocket(PORT);
			} catch (IOException ie) {
				System.out.println(ie);
			}
		}

		void updateTime() {
			for (InetAddress ia : shards.keySet()) {
				packet = new DatagramPacket(b, b.length, ia, PORT);
				socket.send(packet);
			}
			for (InetAddress ia : shards.keySet()) {
				socket.receive(update);
				Byte[] time = packet.getData();
				shards.put(packet.getAddress(),time.longValue());
				if (time > mostRecent) {
					mostRecent = time; 
					mostUpdated = packet.getAddress();
				}
			}
		}

		void updateData() {
			if (mostRecent == timeStamp) return;
			packet = new DatagramPacket(updateRequest,updateRequest.length, mostUpdated, PORT);
			socket.send(packet);
			while (magic & wonder) {
				socket.receive(update);
				db.add(update);
			}
		}
				
		public void run() {
			Database db = new Database();
			db.build();
			timeStamp = date.getTime();
			b = timeStamp.getBytes();
			mostRecent = timeStamp;
			isRunning = true;
			System.out.println("Shard Server thread running");
			for (;;) {
				try {
				updateTime();
				updateData();
				packet = new MulticastPacket(b, b.length, group, 2372);
				socket.receive(packet);
		//		System.out.println("Packet Received " + i);
				group = packet.getAddress();
				port = packet.getPort();
				packet = new DatagramPacket(c, c.length, address, port);
				socket.send(packet);
		//		System.out.println("Packet sent");
				} catch (IOException ie) {}
			}
				System.out.println("Shard Server thread ending");
				socket.close();
				isRunning = false;
		}
	}
}

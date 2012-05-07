package client;

import java.io.*;
import java.net.*;
import java.util.*;
import database.*;

public class Client {

	final  int port = 2372;
	final  String host = "localhost";

	public Client() {}

	public  boolean sendDataSet(DataSet d) {
		try {
			Byte reply;
			int i = 0;
			Socket socket = new Socket(host,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (socket.isConnected()) {
				try {
					System.out.println("Client sending data");
					oos.writeObject("dataset");
					oos.writeObject(d);
					System.out.println("Client data sent");
					reply = (Byte) ois.readObject();
					if (reply == 0)
						if (i++ < 10) continue;
						else return false;
					else {
						System.out.println("Client closing");
						oos.flush();
						ois.close();
						oos.close();
						socket.close();
						return true; 
					}
				} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
			}
		} catch (IOException io) { System.out.println(io); }
		return false;
	}

	public  boolean sendFile(File f) {
		try {
			Byte reply;
			int i = 0;
			Socket socket = new Socket(host,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (socket.isConnected()) {
				try {
					System.out.println("Client sending file");
					oos.writeObject("file");
					oos.writeObject(f);
					System.out.println("Client file sent");
					reply = (Byte) ois.readObject();
					if (reply == 0) 
						if (i++ < 10 ) continue;
						else return false;
					else {
						System.out.println("Client closing");
						oos.flush();
						ois.close();
						oos.close();
						socket.close();
						return true;
					}
				} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
			}
		} catch (IOException io) { System.out.println(io); }
		return false;
	}
	
	public  boolean removeRecord(String key) {
		try {
			Byte reply;
			int i = 0;
			Socket socket = new Socket(host,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (socket.isConnected()) {
				try {
					System.out.println("Client requesting removal");
					oos.writeObject("remove");
					oos.writeObject(key);
					System.out.println("Client sent request");
					reply = (Byte) ois.readObject();
					if (reply == 0){ 
						System.out.println("Not removed");
						if (i++ < 10 ) continue;
						else return false;
					} else {
						System.out.println("Client closing");
						oos.flush();
						ois.close();
						oos.close();
						socket.close();
						return true;
					}
				} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
			}
		} catch (IOException io) { System.out.println(io); }
		return false;
	}

	public String requestData(String query) {
		try {
			int i = 0;
			Socket socket = new Socket(host,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (socket.isConnected()) {
				try {
					System.out.println("Client requesting data");
					oos.writeObject("request");
					oos.writeObject(query);
					System.out.println("Client request sent");
					String d = (String) ois.readObject();
					System.out.println("Client closing");
					ois.close();
					oos.close();
					socket.close();
					return d;
				} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
			}
		} catch (IOException io) { System.out.println(io); }
		return null;
	}
}	
	

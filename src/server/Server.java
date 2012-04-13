package server;

import java.net.*;
import java.io.*;
import java.util.*;
import database.*;

public class Server {
	
	static final int PORT = 2372;
	static Scanner sc;

	public static void main(String[] args) throws FileNotFoundException {
		Database db = new Database();
		db.build();
		int i = 0;
		Byte yes = 1;
		Byte no = 0;
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("ServerSocket started");
			for (;;) {
				Socket client = serverSocket.accept();
				System.out.println("Client connected");
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				String cmd = (String) ois.readObject();
				if (cmd.equals("dataset"))
					if (db.add((DataSet) ois.readObject())) oos.writeObject(yes);
					else oos.writeObject((Byte) no);
				else if (cmd.equals("file"))
					if (db.add((File) ois.readObject())) oos.writeObject(yes);
					else oos.writeObject((Byte) no);
				else if (cmd.equals("remove"))
					if (db.remove((String) ois.readObject())) oos.writeObject(yes);
					else oos.writeObject((Byte) no);
				else if (cmd.equals("request"))
					oos.writeObject(db.request((String) ois.readObject()));
				
				oos.close();
				ois.close();
				client.close();
			}
		} catch (Exception ex) { ex.printStackTrace();
			System.exit(-1);
		}
	}
}

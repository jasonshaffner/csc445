package server;

import java.net.*;
import java.io.*;
import java.util.*;
import database.*;
import failover.Tester;

public class Server {
	
	static final int PORT = 2372;
	static Scanner sc;

	public static void main(String[] args) throws FileNotFoundException, UnknownHostException {
		Database db = new Database();
                InetAddress clientAdd;
		db.build();
		int i = 0;
		Byte yes = 1;
		Byte no = 0;
                Tester start = new Tester();
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("ServerSocket started");
                        
			for (;;) {
/////////////////////////////////////modified by Ben///////////////////////////////////////////////// 
//      This is where the client address gets set for all the nodes
//      whenever a client connects.                            
/////////////////////////////////////////////////////////////////////////////////////////////////////
				Socket client = serverSocket.accept();
				System.out.println("Client connected");
                                clientAdd = client.getInetAddress();
                                start.setClientLocation(client.getInetAddress());
/////////////////////////////////////////////////////////////////////////////////////////////////////////                                
                                System.out.println("client:  " + clientAdd.toString());
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				String cmd = (String) ois.readObject();
				if (cmd.equals("dataset"))
					if (db.add((DataSet) ois.readObject())) oos.writeObject(yes);
					else oos.writeObject((Byte) no);
				else if (cmd.equals("file"))
					if (db.add((File) ois.readObject())) oos.writeObject(yes);
					else oos.writeObject((Byte) no);
				//else if (cmd.equals("remove"))
				//	if (db.remove((String) ois.readObject())) oos.writeObject(yes);
				//	else oos.writeObject((Byte) no);
				else if (cmd.equals("request"))
					oos.writeObject(Query.search((String) ois.readObject(), db.toHashMap()));
				oos.flush();
				ois.close();
				oos.close();
				client.close();
			}
		} catch (Exception ex) { ex.printStackTrace();
			System.exit(-1);
		}
                
                
	}
        
}

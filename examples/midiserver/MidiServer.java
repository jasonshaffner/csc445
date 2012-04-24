package midiserver;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;

import org.jfugue.*;

public class MidiServer {
	
	final static int PORT = 2689;
	static ServerSocket serverSocket;
	static ConcurrentHashMap<Double,Note> notes = new ConcurrentHashMap<>();
	static MidiServer instance = null;
	static boolean isRunning = false;
	static RandomMidiNoteGenerator rmng = RandomMidiNoteGenerator.getInstance();
	static int accesses;

	public static MidiServer getInstance() {
		if (instance == null) return new MidiServer();
		else return instance;
	}

	private MidiServer() {}

	static void receiveNotes(ArrayList<Note> n) {
		for (Note note : n) {
			String s = note.getVerifyString();
			Double key = Double.parseDouble(s.substring(s.indexOf("=")+1,s.indexOf(","))) +
				Double.parseDouble(s.substring(s.indexOf("n")+2, s.indexOf("s")-2));
			notes.put(key,note);
			accesses++;
		}
	}

	static ArrayList<Note> sendNotes(int quantity) {
		ArrayList<Note> n = new ArrayList<>();
		for (Double d : notes.keySet())
			if (quantity >= 0) {
				n.add(notes.get(d));
				accesses++;
				quantity--;
			} else break;
		return n;
	}

	public static void run() {
		isRunning = true;
		System.out.println("Server running");
		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(500);
			ArrayList<MidiServerThread> threads = new ArrayList<>();
			for (int i = 0; i < 1000; i++) { 
				threads.add(new MidiServerThread(serverSocket.accept()));
				threads.get(i).start();
				System.out.println("Server thread started");
			}
			while (isRunning) {
				isRunning = false;
				for (MidiServerThread t : threads) {
					if (!t.isRunning) continue;
					else { 
						isRunning = true;
						break;
					}
				}
			}
			System.out.println("Server closing");
			serverSocket.close();
			isRunning = false;
		} catch (IOException io) {
			System.out.println("Socket error");
			isRunning = false;
		}
		
	}

	static class MidiServerThread extends Thread {

		Socket socket;
		ArrayList<Note> notes = new ArrayList<>();
		ObjectInputStream ois;
		ObjectOutputStream oos;
		String command;
		boolean isRunning = false;

		MidiServerThread (Socket socket) {
			super("MidiServerThread");
			this.socket = socket;
		}

		public void run() {
			isRunning = true;
			System.out.println("Server thread running");
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				while (!socket.isClosed()) {
					try {
						command = (String) ois.readObject();
						if (command.equals("Sending Data")) {
							System.out.println("Server receiving data");
							notes = (ArrayList<Note>) ois.readObject();
							MidiServer.receiveNotes(notes);
							oos.writeObject("Data Received");
							System.out.println("Server data received");
						} else if (command.startsWith("Waiting")) {
							Integer i = Integer.parseInt(command.substring(command.lastIndexOf(",")+1));
							if (MidiServer.notes != null) {
								notes = MidiServer.sendNotes(i);
								oos.writeObject("Data Sent");
								System.out.println("Server sending data");
								oos.writeObject(notes);
								String reply = (String)ois.readObject();
							} else {
								oos.writeObject("No data");
								System.out.println("No data");
							}
						}
					} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
				}
				System.out.println("Server thread ending");
				isRunning = false;
				ois.close();
				oos.close();
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException io) { 
				System.out.println(io);
		 		isRunning = false;	
			}
		}
	}
}


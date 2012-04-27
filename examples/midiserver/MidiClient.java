package midiserver;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jfugue.*;

public class MidiClient {

	ArrayList<Note> notes;
	int port;
	String host;
	RandomMidiNoteGenerator rmng;
	Socket socket;
	Random random = new Random();

	public MidiClient(String host, int port) {
		this.port = port;
		this.host = host;
		rmng = RandomMidiNoteGenerator.getInstance();
	}

	public void generateNotes() {
		notes = new ArrayList<Note>();
		int i = random.nextInt(100);
		do {
			notes.add(rmng.generateNote());
			i--;
		} while (i > 0);
	}

	public void modifyNotes(ArrayList<Note> notes) {
		for (Note n : notes) {
			rmng.editPitch(n);
			rmng.editVelocity(n);
			rmng.editDuration(n);
		}
	}

	public void run() {
		try {
			socket = new Socket(host,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (socket.isConnected()) {
				try {
					double d = Math.random() * 2;
					if (d > 1) { 
						System.out.println("Client sending data");
						oos.writeObject("Sending Data");
						generateNotes();
						oos.writeObject(notes);
						System.out.println("Client data sent");
						String reply = (String) ois.readObject();
					} else {
						System.out.println("Client waiting for data");
						oos.writeObject("Waiting," + random.nextInt(100));
						String command = (String) ois.readObject();
						System.out.println("Client attempting to receive data");
						notes = (ArrayList<Note>)ois.readObject();
						System.out.println("Client received data");
						oos.writeObject("Data received");
						System.out.println("Client modifying notes");
						oos.writeObject("Sending Data");
						modifyNotes(notes);
						oos.writeObject(notes);
						System.out.println("Client sent modified data");
						String reply = (String) ois.readObject();
					} 
					d = Math.random();
					if (d < .1) {
						System.out.println("Client closing");
						ois.close();
						oos.close();
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
					} 
				} catch (ClassNotFoundException cnf) { System.out.println(cnf); }
			}
		} catch (IOException io) { 
				System.out.println(io);
		}
	}
}	
	

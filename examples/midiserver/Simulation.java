package midiserver;

import java.util.*;
import org.jfugue.*;
import java.io.*;
import java.net.*;

public class Simulation {
	
	public static void main(String[] args) {
		long start = System.nanoTime();
		MidiServer.getInstance();
		System.out.println("Starting server");
		new Thread(new Runnable() {
			public void run() {
				MidiServer.run();
			}
		}).start();
		for (int i = 0; i <= 500; i++) {
			System.out.println("New Client");
			new Thread(new Runnable() {
				public void run() {								
					new MidiClient("localhost",2689).run();
				}
			}).start();
		}
		while (MidiServer.isRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {}
		}
	//	Pattern pattern = new Pattern();
	//	for (Double d : ms.notes.keySet()) {
	//		System.out.print(d + ", ");
	//		pattern.addElement(ms.notes.get(d));
	//	}
		long finish = System.nanoTime();
		long nanoseconds = finish - start;
		System.out.println("\nTotal accesses: " + MidiServer.accesses);
		System.out.println("\nTotal nanoseconds: " + (nanoseconds));
		System.out.println("\nTotal nanoseconds per access: " + (double)((double)nanoseconds/(double)MidiServer.accesses));
		System.out.println("\nTotal size: " + MidiServer.notes.size());
		System.exit(-1);
		//Player player = new Player();
		//player.play(pattern);
	}
}

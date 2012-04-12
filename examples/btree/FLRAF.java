package btree;

import java.io.*;
import java.util.*;

public class FLRAF extends RandomAccessFile {
	
	final int BLOCKSIZE;
	int index;
	String name;

	public FLRAF(int blocksize) throws FileNotFoundException {
		super(new File(".flraf"),"rw");
		BLOCKSIZE = blocksize;
		index = 0;
	}

	public void write(String s) {
		try {
			seek(index * BLOCKSIZE);
			super.writeBytes(s);
		} catch (IOException ie) {}
		index++;
	}

  String read(int i) {
		if (i >= index || i < 0) return null;
		byte[] b = new byte[BLOCKSIZE];
		try {
			seek(i * BLOCKSIZE);
			super.readFully(b);
		} catch (IOException ie) {}
		return new String(b).trim();
	}

	String[] read(int start, int finish) {
		String[] blocks = new String[finish+1-start];
		for (int i = start; i <= finish; i++)
			blocks[i-(start)] = read(i);
		return blocks;
	}


}

class FLRAFTester {

	static FLRAF flraf;
	static Scanner sc;
		
	String pad(String s) {
		if (s.length() < 28)
			for(int i = s.length(); i < 28; i++)
					s += " ";
		return s;
	}

	public static void main(String[] args) {
		try {
			flraf = new FLRAF(28);
			sc = new Scanner(new File("btree/words.txt"));
		} catch (FileNotFoundException f) {System.out.println(f);}
		while(sc.hasNext())
			flraf.write(sc.next());
		System.out.println("Block 0: " + flraf.read(0));
		System.out.println("Block 5643: " + flraf.read(5643));
		System.out.println("Block 45406: " + flraf.read(45406));
		sc = new Scanner(System.in);
		System.out.print("Another? > ");
		while (sc.nextLine().equalsIgnoreCase("y")) {
			System.out.print("Enter index to read > ");
			String s = sc.nextLine();
			if (s.contains(",")) {
				Integer j = Integer.parseInt(s.substring(0,s.lastIndexOf(",")));
				Integer k = Integer.parseInt(s.substring(s.lastIndexOf(",")+1));
				String[] st = flraf.read(j,k);
				System.out.println("Blocks : " + j + " - " + k + " : ");
				for (int i = 0; i < st.length; i++)
					if (st[i] != null) System.out.println(st[i]);
					else System.out.println("Index out of range");
			} else {
				Integer i = Integer.parseInt(s);
				s = flraf.read(i);
				if (s != null) System.out.println("Block " + i + ": " + s);
				else System.out.println("Index out of range");
			}
			System.out.print("Another?");
		}
	}
}


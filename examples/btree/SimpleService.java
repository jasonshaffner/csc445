package btree;

import java.net.*;
import java.io.*;
import java.util.*;

public class SimpleService {
	
	static final int PORT = 2372;
	static Scanner sc;
	static BTree bt = new BTree(8);

	static void build() throws FileNotFoundException {
		sc = new Scanner(new File("btree/words.txt"));
		while (sc.hasNext()) {
			bt.add(sc.nextLine());
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String reply;
		build();
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			for (;;) {
				Socket client = serverSocket.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String cmd = in.readLine();
				String s = cmd.substring(5,cmd.length()-9);
				StringBuilder sb = new StringBuilder("<html>\n<head><title>Testing</title></head>\n");
				if (s.equals("shutdown")) {
					out.close();
					in.close();
					client.close();
					System.exit(-1);
				} else if (s.equals("")) sb.append("<body><h1>Enter word to add, or \"-\"word to delete</h1></body></html>");
				else if (s.startsWith("-")) {
					String st = s.substring(1);
					if (st.startsWith("-")) {
						sc = new Scanner(new File("btree/words.txt"));
						while (sc.hasNext())
							bt.remove(sc.nextLine());
						sb.append("<body><h1>All words from file removed!</h1></body></html>");
					} else if (bt.remove(st)) sb.append("<body><h1>Word Removed!</h1></body>\nWord:<br>" + st + "\n</html>");
					else sb.append("<body><h1>Word not removed</h1></body>\nWord: <br>" + st + "\n</html>"); 
				} else if (bt.add(s)) sb.append("<body><h1>Word Added!</h1></body>\nWord:<br>\n " + s + "\n</html>");
				else sb.append("<body><h1>Word Already Indexed</h1></body>\nWord: <br>\n" + s +  "\n</html>\n");
				reply = sb.toString();
				int len = reply.length();
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Length: " + len);
				out.println("Content-Type: text/html\n");
				out.println(reply);
				out.close();
				in.close();
				client.close();
				
			}
		} catch (IOException ex) { ex.printStackTrace();
			System.exit(-1);
		}
	}
}

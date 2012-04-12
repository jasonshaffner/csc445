package btree;

import java.net.*;
import java.io.*;
import java.util.*;

public class SimpleFLRAFService {
	
	static final int PORT = 2372;
	static Scanner sc;
	static FLRAF flraf;

	static void build() throws FileNotFoundException {
		flraf = new FLRAF(28);
		sc = new Scanner(new File("btree/words.txt"));
		while (sc.hasNext()) {
			flraf.write(sc.nextLine());
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
			  StringBuilder sb = new StringBuilder("<html>\n<head><title>Testing</title></head>\n<body>"); 
				if (s.equals("")) sb.append("<h1>Enter index</h1></body></html>");
				else if (s.equals("shutdown")) {
					out.close();
					in.close();
					client.close();
					System.exit(-1);
				} else if (s.contains(",")) {
					try {
						Integer j = Integer.parseInt(s.substring(0,s.lastIndexOf(",")));
						Integer k = Integer.parseInt(s.substring(s.lastIndexOf(",")+1));
						String[] st = flraf.read(j,k);
						sb.append("<h1>Words at Indexes " + j + " - " + k + "</h1></body>");
						for (int i = 0; i < st.length; i++)
							if (st[i] != null) sb.append("<br>\n" + st[i]);
							else sb.append("<br>\nIndex out of range");
						sb.append("\n</html>\n");
					} catch (NumberFormatException n) { sb.append("<h1>Numbers Only Please!!!</h1></body></html>");}
					} else {
						try {
							Integer i = Integer.parseInt(s);
							String st = flraf.read(i);
							sb.append("<h1>Word At Index "+ i + "</h1>\n");
							if (st != null) sb.append("<br>\n" + st +  "\n</html>\n");
							else sb.append("<br>\nIndex out of range\n</html>");
						} catch (NumberFormatException n) { sb.append("<h1>Numbers Only Please!!!</h1></body></html>");}
					}
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

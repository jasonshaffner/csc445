import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	
	static final int PORT = 2372;
	static Scanner sc;
	static HashMap<String,DataSet> data = new HashMap<String,DataSet>();

	static void build() throws FileNotFoundException {
		final File[] files = new File("../../data").listFiles();
		DataSet d = new DataSet();
		for (int i = 0; i < files.length; i++) {
			System.out.println(i);
			sc = new Scanner(files[i]);
			System.out.println(files[i].getName().substring(0,8));
			sc.useDelimiter(",");
			for (int j = 0; j < 5; j++) sc.next();
			while (sc.hasNext()) {
					d.setCity(sc.next());
					System.out.println(d.city);
					d.setVisits(Integer.parseInt(sc.next()));
					System.out.println(d.visits);
					d.setPagesPerVisit(Double.parseDouble(sc.next()));
					System.out.println(d.pagesPerVisit);
					d.setAvgVisitDuration(sc.next());
					System.out.println(d.avgVisitDuration);
					d.setPercentNewVisits(Double.parseDouble(sc.next()));
					System.out.println(d.percentNewVisits);
					d.setBounceRate(Double.parseDouble(sc.next()));
					System.out.println(d.bounceRate);
					if (d.city.length() > 2) data.put(files[i].getName().substring(0,8) + d.city, d);
					else data.put(files[i].getName().substring(0,8), d);
			}
		}
	}

	static class DataSet {
		String city;
		int visits;
		double pagesPerVisit;
		int avgVisitDuration;
		double percentNewVisits;
		double bounceRate;

		DataSet() {}

		void setCity(String city) {
			this.city = city;
		}

		void setVisits(int visits) {
			this.visits = visits;
		}

		void setPagesPerVisit(double pagesPerVisit) {
			this.pagesPerVisit = pagesPerVisit;
		}

		void setAvgVisitDuration(String avgVisitDuration) {
			int secs = 60 * 60 * Integer.parseInt(avgVisitDuration.substring(0,2));
			secs += 60 * Integer.parseInt(avgVisitDuration.substring(3,5));
			secs += Integer.parseInt(avgVisitDuration.substring(6));
			this.avgVisitDuration = secs;
		}

		void setPercentNewVisits(double percentNewVisits) {
			this.percentNewVisits = percentNewVisits;
		}

		void setBounceRate(double bounceRate) {
			this.bounceRate = bounceRate;
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
				} else if (s.equals("")) { 
					sb.append("<h1>Enter Date (format YYYYMMDD)</h1></body></html>");
				} else { 
						sb.append("<h1>" + s + "</h1></body>\nVisits: " + data.get(s).visits + "<br></body></html>");
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

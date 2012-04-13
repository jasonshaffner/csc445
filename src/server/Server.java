import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	
	static final int PORT = 2372;
	static Scanner sc;
	static HashMap<String,DataSet> data = new HashMap<String,DataSet>();

	static void build() throws FileNotFoundException {
		final File[] files = new File("../../data").listFiles();
		for (int i = 0; i < files.length; i++) {
			System.out.println(i);
			sc = new Scanner(files[i]);
			System.out.println(files[i].getName().substring(0,8));
			sc.useDelimiter(",");
			while (sc.hasNext()) {
					DataSet d = new DataSet();
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
		for (String s : data.keySet())
			System.out.println(s + " " + data.get(s).visits);
	}

	public static void main(String[] args) throws FileNotFoundException {
		Database db = new Database();
		db.build();
		int i = 0;
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			for (;;) {
				ObjectOutputStream oos = new ObjectOutputStream(serverSocket.getOutputStream());
				ObjectInputStream oos = new ObjectInputStream(serverSocket.getInputStream());
				Socket client = serverSocket.accept();
				String cmd = (String) ois.readObject();
				if (cmd.equals("dataset"))
					if (db.add((DataSet) ois.readObject())) oos.writeObject((Byte) 1);
					else oos.writeObject((Byte) 0);
				else if (cmd.equals("file"))
					if (db.add((File) ois.readObject())) oos.writeObject((Byte) 1);
					else oos.writeObject((Byte) 0);
				else if (cmd.equals("remove"))
					if (db.remove((String) ois.readObject())) oos.writeObject((Byte) 1);
					else oos.writeObject((Byte) 0);
				else if (cmd.equals("request"))
					oos.writeObject(db.request((String) ois.readObject()));
				
				out.close();
				in.close();
				client.close();
			}
		} catch (IOException ex) { ex.printStackTrace();
			System.exit(-1);
		}
	}
}

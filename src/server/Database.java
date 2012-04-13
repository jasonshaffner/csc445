import java.net.*;
import java.io.*;
import java.util.*;

public class Database {
	
	public static void build() throws FileNotFoundException {
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

	public static boolean add(File f) throws FileNotFoundException {
			sc = new Scanner(f);
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
					if (d.city.length() > 2) data.put(f.getName().substring(0,8) + d.city, d);
					else data.put(f.getName().substring(0,8), d);
			}
			return true;
		}

	public static boolean add(DataSet d) {
		if (d.city.length() > 2) data.put(files[i].getName().substring(0,8) + d.city, d);
		else data.put(files[i].getName().substring(0,8), d);
		return true;	
	}

	public static boolean remove(int date) {
		data.remove(date);
		ArrayList<String> keys = new ArrayList<String>();
		for (String s : data.keySet()) if (s.startsWith(date)) keys.add(s);
		for (String s : keys) data.remove(s);
	}

	public static DataSet request(String key) {
		return data.get(key);
	}
}

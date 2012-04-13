package database;

import java.net.*;
import java.io.*;
import java.util.*;

public class Database {

	static HashMap<String,DataSet> data = new HashMap<String,DataSet>();
	static Scanner sc;
	
	public static void build() throws FileNotFoundException {
		final File[] files = new File("../data").listFiles();
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
		if (d.city.length() > 2) data.put(d.date.toString() + d.city, new DataSet(d.city,d.visits,d.pagesPerVisit,d.avgVisitDuration,d.percentNewVisits,d.bounceRate));
		else data.put(d.date.toString(), new DataSet(d.city,d.visits,d.pagesPerVisit,d.avgVisitDuration,d.percentNewVisits,d.bounceRate));
		return true;	
	}

	public static boolean remove(String key) {
		data.remove(key);
		ArrayList<String> keys = new ArrayList<String>();
		for (String s : data.keySet()) if (s.startsWith(key)) keys.add(s);
		for (String s : keys) data.remove(s);
		return true;
	}

	public static DataSet request(String key) {
		return data.get(key);
	}
}

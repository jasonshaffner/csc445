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
			sc = new Scanner(files[i]);
			System.out.println(files[i].getName());
			sc.useDelimiter(",");
			while (sc.hasNext()) {
				DataSet d = new DataSet();
				d.setCity(sc.next());
				d.setVisits(Integer.parseInt(sc.next()));
				d.setPagesPerVisit(Double.parseDouble(sc.next()));
				d.setAvgVisitDuration(sc.next());
				d.setPercentNewVisits(Double.parseDouble(sc.next()));
				d.setBounceRate(Double.parseDouble(sc.next()));
				if (d.city.length() > 2) data.put(files[i].getName().substring(0,8) + d.city, d);
				else data.put(files[i].getName().substring(0,8), d);
			}
		}
	}

	public static HashMap<String,DataSet> toHashMap() {
		return data;
	}

	public static boolean add(File f) throws FileNotFoundException {
		sc = new Scanner(f);
		sc.useDelimiter(",");
		DataSet d = new DataSet();
		String key = f.getName().substring(0,8);
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
			if (d.city.length() > 2) {
				key += d.city;
				data.put(key, d);
			} else data.put(key, d);
			saveToCSV(d,key);
		}
		return data.containsKey(key);
	}

	public static boolean add(DataSet d) {
		String key = d.date.toString();
		d.date = null;
		if (d.city.length() > 2) {
			key += d.city;
			data.put(key, d); 
		} else data.put(key, d);
		saveToCSV(d,key);
		return data.containsKey(key);	
	}

	//we're probably not going to have time to implement a functional remove function
/*	public static boolean remove(String key) {
		data.remove(key);
		ArrayList<String> keys = new ArrayList<String>();
		for (String s : data.keySet()) if (s.startsWith(key)) keys.add(s);
		for (String s : keys) data.remove(s);
		return true;
	}
*/
	public static DataSet[] requestData(String key) {
		DataSet[] d;
		ArrayList<DataSet> dl = new ArrayList<DataSet>();
		String startDate = key.substring(0,8);
		String endDate = key.substring(8,16);
		if (startDate.compareTo(endDate) == 0) {
			System.out.println("One date");
			d = new DataSet[1];
			if (key.length() > 15) {
				String city = key.substring(16);
				d[0] = data.get(startDate + city);
				d[0].date = Integer.parseInt(startDate);
				return d;
			} else {
				d[0] = data.get(startDate);
				return d;
			}
		} else if (key.length() > 16) {
			System.out.println("City included");
			String city = key.substring(16);
			if (city.equals("all")) {
				for (String s : data.keySet())
					if (s.length() > 7 && s.substring(0,8).compareTo(startDate) >= 0 && s.substring(0,8).compareTo(endDate) <= 0) {
						dl.add(data.get(s));
						dl.get(dl.size()-1).date = Integer.parseInt(s.substring(0,8));
					}
			} else
				for (String s : data.keySet())
					if (s.contains(city) && s.substring(0,8).compareTo(startDate) >= 0 && s.substring(0,8).compareTo(endDate) <= 0) {
						dl.add(data.get(s));
						dl.get(dl.size()-1).date = Integer.parseInt(s.substring(0,8));
					}
		} else {
			System.out.println("Multiple Dates");
			for (String s : data.keySet())
				if (s.length() == 8 && s.compareTo(startDate) >= 0 && s.compareTo(endDate) <= 0) {
					System.out.println(s);
					dl.add(data.get(s));
					dl.get(dl.size()-1).date = Integer.parseInt(s);
				}
		}
		d = new DataSet[dl.size()];
		dl.toArray(d);
		return d;
	}
	
	static void saveToCSV(DataSet d, String key) {
		boolean append = true;
		try {
			File f = new File("../data/" + key.substring(0,8) + ".csv");
			if (!f.exists()) {
				f.createNewFile();
				append = false;
			}
			FileOutputStream fos = new FileOutputStream(f,append);
			byte[] b = ",".getBytes();
			byte[] c = ":".getBytes();
			if (append) fos.write(b);
			fos.write(d.city.getBytes());
			fos.write(b);
			fos.write(String.valueOf(d.visits).getBytes());
			fos.write(b);
			fos.write(String.valueOf(d.pagesPerVisit).getBytes());
			fos.write(b);
			fos.write(String.valueOf(d.avgVisitDuration/3600).getBytes());
			fos.write(c);
			fos.write(String.valueOf((d.avgVisitDuration/60) % 60).getBytes());
			fos.write(c);
			fos.write(String.valueOf(d.avgVisitDuration % 60).getBytes());
			fos.write(b);
			fos.write(String.valueOf(d.percentNewVisits).getBytes());
			fos.write(b);
			fos.write(String.valueOf(d.bounceRate).getBytes());
			fos.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}

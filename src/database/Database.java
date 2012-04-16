package database;

import java.net.*;
import java.io.*;
import java.util.*;
import com.mongodb.*;

public class Database {

	static Mongo m;
	static DB db;
	static DBCollection data;
	static Scanner sc;

	public static void init() {
		try {
			m = new Mongo();
		} catch (Exception e) { e.printStackTrace(); }
		db = m.getDB("ga");
		data = db.getCollection("data");
		try {
			if (data.getCount() ==0) build();
		} catch (FileNotFoundException f) { f.printStackTrace(); }
	}
	
	public static void build() throws FileNotFoundException {
		final File[] files = new File("../data").listFiles();
		for (int i = 0; i < files.length; i++) {
			sc = new Scanner(files[i]);
			sc.useDelimiter(",");
			while (sc.hasNext()) {
				BasicDBObject d = new BasicDBObject();
				d.put("date",files[i].getName().substring(0,8));
				d.put("city",sc.next());
				d.put("visits",Integer.parseInt(sc.next()));
				d.put("pagesPerVisit",Double.parseDouble(sc.next()));
				d.put("avgVisitDuration",sc.next());
				d.put("percentNewVisits",Double.parseDouble(sc.next()));
				d.put("bounceRate",Double.parseDouble(sc.next()));
				data.insert(d);
			}
			System.out.println(data.getCount());
		}
	}

	public static boolean add(File f) throws FileNotFoundException {
			sc = new Scanner(f);
			sc.useDelimiter(",");
			while (sc.hasNext()) {
				BasicDBObject d = new BasicDBObject();
				d.put("date",f.getName().substring(0,8));
				d.put("city",sc.next());
				d.put("visits",Integer.parseInt(sc.next()));
				d.put("pagesPerVisit",Double.parseDouble(sc.next()));
				d.put("avgVisitDuration",sc.next());
				d.put("percentNewVisits",Double.parseDouble(sc.next()));
				d.put("bounceRate",Double.parseDouble(sc.next()));
				data.insert(d);
			}
		return true;
		}

	public static boolean add(DataSet ds) {
		BasicDBObject d = new BasicDBObject();
		d.put("date",ds.date);
		d.put("city",ds.city);
		d.put("visits",ds.visits);
		d.put("pagesPerVisit",ds.pagesPerVisit);
		d.put("avgVisitDuration",ds.avgVisitDuration);
		d.put("percentNewVisits",ds.percentNewVisits);
		d.put("bounceRate",ds.bounceRate);
		data.insert(d);
		return true;
	}

	public static boolean remove(String key) {
		BasicDBObject d = new BasicDBObject(key,key);
		data.remove(d);
		return true;
	}

	public static DataSet[] requestData(String key) {
		System.out.println("Data requested");
		ArrayList<DataSet> dl = new ArrayList<DataSet>();
		BasicDBObject query = new BasicDBObject();
		String startDate = key.substring(0,8);
		String endDate = key.substring(8,16);
		BasicDBObject q = new BasicDBObject("$gt",startDate).append("$lt",endDate);
		query.put("date",q);
		if (key.length() > 16) query.put("city",key.substring(16));
		DBCursor cursor = data.find(query);	
		System.out.println(cursor.size());
		while (cursor.hasNext()) dl.add(parse(cursor.next().toString()));
		System.out.println(dl.size());
		DataSet[] d = new DataSet[dl.size()];
		dl.toArray(d);
		return d;
	}

	static DataSet parse(String json) {
		DataSet d = new DataSet();
		StringTokenizer tokenizer = new StringTokenizer(json);
		tokenizer.nextToken(",");
		tokenizer.nextToken(":");
		String s = tokenizer.nextToken(",").substring(1).trim();
		d.setDate(Integer.parseInt(s.substring(1,s.length()-1)));
		tokenizer.nextToken(":");
		s = tokenizer.nextToken(",").substring(1).trim();
		d.setCity(s.substring(1,s.length()-1));
		tokenizer.nextToken(":");
		d.setVisits(Integer.parseInt(tokenizer.nextToken(",").substring(1).trim()));
		tokenizer.nextToken(":");
		d.setPagesPerVisit(Double.parseDouble(tokenizer.nextToken(",").substring(1).trim()));
		tokenizer.nextToken(":");
		s = tokenizer.nextToken(",").substring(1).trim();
		d.setAvgVisitDuration(s.substring(1,s.length()-1));
		tokenizer.nextToken(":");
		d.setPercentNewVisits(Double.parseDouble(tokenizer.nextToken(",").substring(1).trim()));
		tokenizer.nextToken(":");
		d.setBounceRate(Double.parseDouble(tokenizer.nextToken("}").substring(1).trim()));
		return d;
	}
}

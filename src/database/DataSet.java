package database;

import java.net.*;
import java.io.*;
import java.util.*;

public class DataSet {
	public String city;
	public Integer date;
	public int visits;
	public double pagesPerVisit;
	public int avgVisitDuration;
	public double percentNewVisits;
	public double bounceRate;

	public DataSet() {}

	public DataSet(String city, int visits, double pagesPerVisit, int avgVisitDuration, double percentNewVisits, double bounceRate) {
		this.city = city;
		this.visits = visits;
		this.pagesPerVisit = pagesPerVisit;
		this.avgVisitDuration = avgVisitDuration;
		this.percentNewVisits = percentNewVisits;
		this.bounceRate = bounceRate;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}

	public void setPagesPerVisit(double pagesPerVisit) {
		this.pagesPerVisit = pagesPerVisit;
	}
		
	public void setAvgVisitDuration(String avgVisitDuration) {
		int secs = 60 * 60 * Integer.parseInt(avgVisitDuration.substring(0,2));
		secs += 60 * Integer.parseInt(avgVisitDuration.substring(3,5));
		secs += Integer.parseInt(avgVisitDuration.substring(6));
		this.avgVisitDuration = secs;
	}

	public void setPercentNewVisits(double percentNewVisits) {
		this.percentNewVisits = percentNewVisits;
	}

	public void setBounceRate(double bounceRate) {
		this.bounceRate = bounceRate;
	}

}	

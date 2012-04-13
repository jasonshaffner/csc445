import java.net.*;
import java.io.*;
import java.util.*;

public class DataSet {
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

/****************************************************************************************
*	CLASS    : Query
*	AUTHOR   : Corey O'Neill
*	DIRECTIVE: To extract information from the database per the user's specifications
****************************************************************************************/
package database;

import java.util.*;
import java.text.*;

public class Query
{
	//Declare field sizes (column_width) *MIN_SIZE for column header length
	static int city_width  = 24; //MIN_SIZE 12
	static int visit_width = 12; //MIN_SIZE 12
	static int ppv_width   = 17; //MIN_SIZE 17
	static int avd_width   = 15; //MIN_SIZE 15
	static int pnv_width   = 22; //MIN_SIZE 22
	static int br_width    = 14; //MIN_SIZE 14
	/************************************************************************************
	*	METHOD    : search(String,HashMap<String,DataSet>)
	*	PARAMETERS: A String representing the query criteria and a HashMap (database)
	*	DIRECTIVE: 
	*		-This method will parse the query command for search criteria.
	*		-If there is a flaw in the search criteria or query format, an exception 
	*		will be thrown and the method will return a detailed error message.
	*		-The query result will be returned as one big formatted table String
	*		-A format index is used to determine which columns to add to the table result
	*			formatIndex[0] = city
	*			formatIndex[1] = visits
	*			formatIndex[2] = pagesPerVisit
	*			formatIndex[3] = avgVisitDuration
	*			formatIndex[4] = percentNewVisits
	*			formatIndex[5] = bounceRate
	************************************************************************************/
	public static String search(String qry, HashMap<String,DataSet> db)
	{
		//Get the database in an iterable format
		Collection<DataSet> c = db.values();
		Iterator<DataSet> i = c.iterator();
		
		//0=false, 1=true. Used to determine which columns used in table output
		int[] formatIndex = {0,0,0,0,0,0};
		String table = "<html>";	
		
		try
		{
			//Get first token in query
			qry = qry.trim();
			if(qry.equals("get *"))
			{
				//get * case means return all data as a table String
				for(int b = 0; b < formatIndex.length; b++)
					formatIndex[b] = 1;
				table += showHeader(formatIndex);
				while(i.hasNext())
				{
					DataSet d = i.next();
					String[] r = beautify(d);
					table += "\t";
					for(int num = 0; num < r.length; num++)
						table += r[num];
					table += "<br>";
				}
				table += showFooter(formatIndex);
				return table;
			}
			//Return error message if the first token is not the 'get' operator
			if(!qry.startsWith("get "))
				throw new FormatException();
			else
				qry = qry.substring(4); //Parse 'get' off of the query String
			
			//Sentinel value
			int quit = 0;
			do
			{
				//Trim white space
				qry = qry.trim();
				
				//Get all column case *
				if(quit == 0 && qry.startsWith("*"))
				{
					//Set format index to add all columns in table
					for(int b = 0; b < formatIndex.length; b++)
						formatIndex[b] = 1;
					qry = qry.substring(1);
					quit = -1;
				}
				
				//See method directive documentation above for formatIndex[]
				
				//If city argument is received
				else if(qry.startsWith("city"))
				{
					//Set city column and trim argument from query String
					if(formatIndex[0] == 1)
						throw new FormatException("'City' field used more than once!");
					qry = qry.substring(4);
					formatIndex[0] = 1;
				}
				//If visits argument is received
				else if(qry.startsWith("visits"))
				{
					//Set visits column and trim argument from query String
					if(formatIndex[1] == 1)
						throw new FormatException("'Visits' field used more than once!");
					qry = qry.substring(6);
					formatIndex[1] = 1;
				}
				//If pages per view argument is received
				else if(qry.startsWith("ppv"))
				{
					//Set pages per view column and trim argument from query String
					if(formatIndex[2] == 1)
						throw new FormatException("'PgPerVisit' field used more than once!");
					qry = qry.substring(3);
					formatIndex[2] = 1;
				}
				//If average visit duration argument is received
				else if(qry.startsWith("avd"))
				{
					//Set average visit duration column and trim argument from query String
					if(formatIndex[3] == 1)
						throw new FormatException("'AvgVisitDur' field used more than once!");
					qry = qry.substring(3);
					formatIndex[3] = 1;
				}
				//If percent of new visits argument is received
				else if(qry.startsWith("ponv"))
				{
					//Set percent of new visits column and trim argument from query String
					if(formatIndex[4] == 1)
						throw new FormatException("'% NewVisits' field used more than once!");
					qry = qry.substring(4);
					formatIndex[4] = 1;
				}
				//If bounce rate argument is received
				else if(qry.startsWith("br"))
				{
					//Set bounce rate column and trim argument from query String
					if(formatIndex[5] == 1)
						throw new FormatException("'BounceRate' field used more than once!");
					qry = qry.substring(2);
					formatIndex[5] = 1;
				}
				//If 'where' argument is received
				else if(qry.startsWith("where"))
				{
					//'get' clause is finished - set to quit loop
					quit = -1;
				}
				//All other cases = bad query request
				else
				{
					throw new FormatException("Unknown field in query!");
				}
				
				//Determine whether or not it is time to quit loop
				if(quit != -1) {quit++;}
				if(quit > 5 || qry.equals("")) {quit = -1;}
				
			//Continue loop while there are still tokens in 'get' clause
			}while(quit != -1);
			
			//Trim white space to prepare to potential 'where' clause
			qry = qry.trim();
			
			//If there is a 'where' clause
			if(qry.startsWith("where"))
			{
				//Discard 'where' from the query String
				qry = qry.substring(5);
				
				//The new first index[0] proceeding 'where' must be a space
				if(qry.indexOf(" ") != 0 || qry.indexOf(" ") == -1)
					throw new FormatException("Invalid 'where' clause.");
				else
					qry = qry.trim(); //Trim space off
					
				//Declare variables to determine map reduction
				String constraint = "";
				String comparison = "";
				String city_value = ""; //Used only when constraint = a city name
				Double value = null;    //Used for all numerical columns
				
				//Find the space separating constraint from comparison operator
				if(qry.indexOf(" ") == -1)
				{
					throw new FormatException("Invalid arguments in 'where' clause!");
				}
				else
				{
					//Get the constrain and trim it off the query String
					constraint = qry.substring(0,qry.indexOf(" "));
					if(!constraint.equals("city") && !constraint.equals("visits") && !constraint.equals("ppv") && !constraint.equals("avd") && !constraint.equals("ponv") && !constraint.equals("br"))
						throw new FormatException("Invalid constraint. Must be 'city', 'visits', 'ppv', 'avd', 'ponv', or 'br.'");
					qry = qry.substring(qry.indexOf(" ")).trim();	
				}
				
				//Find the space separating comparison operator from compared value
				if(qry.indexOf(" ") == -1)
				{
					throw new FormatException("Invalid arguments in 'where' clause!");
				}
				else
				{
					//Get the comparison operator and trim it off the query String
					comparison = qry.substring(0,qry.indexOf(" "));
					if(!comparison.equals(">") && !comparison.equals("=") && !comparison.equals("<"))
						throw new FormatException("Invalid comparison operator.");
					qry = qry.substring(qry.indexOf(" ")+1).trim();
				}
				
				//Get the comparison value. *Assumes comparison is a city if exception caught!
				try{value = Double.parseDouble(qry);}
				catch(NumberFormatException e){city_value = qry;}
				
				table += showHeader(formatIndex);
				
				//Iterate the database (ie For each recording in database)
				while(i.hasNext())
				{
					//Declare 'where' clause parsing variables
					boolean valid = false;
					boolean tab = false;
					DataSet d = i.next();
					String[] r = beautify(d);
					
					//For each field in record
					for(int num = 0; num < r.length; num++)
					{
						//If the num'th column should be in table
						if(formatIndex[num] == 1)
						{
							//If Constraint = CITY
							if(constraint.equals("city"))
							{
								//City only supports equals operator
								if(!comparison.equals("="))
									throw new FormatException("City cannot be > or <, only =");
								//If a city matching the query criteria is found
								if(r[0].equals(beautifyCity(city_value)))
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
							}
							//If Constraint = VISITS
							else if(constraint.equals("visits"))
							{
								//If Comparison is the equals operator & this record meets query criteria
								if(comparison.equals("=") && new Double(getRawValue(r[1])).intValue()==new Double(value).intValue())
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the greater than operator & this record meets query criteria
								else if(comparison.equals(">") && getRawValue(r[1])>value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the less than operator & this record meets query criteria
								else if(comparison.equals("<") && getRawValue(r[1])<value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
							}
							//If Constraint = PAGES PER VIST
							else if(constraint.equals("ppv"))
							{
								//If Comparison is the equals operator & this record meets query criteria
								if(comparison.equals("=") && new Double(getRawValue(r[2])).intValue()==new Double(value).intValue())
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the greater than operator & this record meets query criteria
								else if(comparison.equals(">") && getRawValue(r[2])>value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the less than operator & this record meets query criteria
								else if(comparison.equals("<") && getRawValue(r[2])<value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
							}
							//If Constraint = AVERAGE VISIT DURATION
							else if(constraint.equals("avd"))
							{
								//If Comparison is the equals operator & this record meets query criteria
								if(comparison.equals("=") && new Double(getRawValue(r[3])).intValue()==new Double(value).intValue())
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the greater than operator & this record meets query criteria
								else if(comparison.equals(">") && getRawValue(r[3])>value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the less than operator & this record meets query criteria
								else if(comparison.equals("<") && getRawValue(r[3])<value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
							}
							//If Constraint = PERCENT OF NEW VISITS
							else if(constraint.equals("ponv"))
							{
								//If Comparison is the equals operator & this record meets query criteria
								if(comparison.equals("=") && new Double(getRawValue(r[4])).intValue()==new Double(value).intValue())
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the greater than operator & this record meets query criteria
								else if(comparison.equals(">") && getRawValue(r[4])>value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the less than operator & this record meets query criteria
								else if(comparison.equals("<") && getRawValue(r[4])<value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
							}
							//If Constraint = BOUNCE RATE
							else if(constraint.equals("br"))
							{
								//If Comparison is the equals operator & this record meets query criteria
								if(comparison.equals("=") && new Double(getRawValue(r[5])).intValue()==new Double(value).intValue())
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the greater than operator & this record meets query criteria
								else if(comparison.equals(">") && getRawValue(r[5])>value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}
								//If Comparison is the less than operator & this record meets query criteria
								else if(comparison.equals("<") && getRawValue(r[5])<value)
								{
									//Add num'th field of this record to table
									if(!tab)
									{
										tab = true;
										table += "\t";
									}
									valid = true;
									table += r[num];
								}//END Comparison IF
							}//END Constraint IF
							else
							{
								throw new FormatException("Illegal comparison!");
							}
						}//END valid table column IF
					}//END field iteration in record FOR
					if(valid)
						table += "<br>";
				}//END iteration over database WHILE
				table += showFooter(formatIndex);
				return table;
			}
			//If query String does not have 'where' clause but there is more in String
			else if(!qry.equals(""))
			{
				//These are illegal - Send Error message
				throw new FormatException("Illegal syntax after 'get' clause!");
			}
			//IF no 'where' clause and String has rightfully terminated
			else
			{
				//Build table of all records with specified columns
				table += showHeader(formatIndex);
				while(i.hasNext())
				{
					DataSet d = i.next();
					String[] r = beautify(d);
					table += "\t";
					for(int num = 0; num < r.length; num++)
					{
						if(formatIndex[num] == 1)
							table += r[num];
					}
					table += "<br>";
				}
				table += showFooter(formatIndex);
				return table;	
			}//END if there is a 'where' clause IF
		}
		catch(FormatException e)
		{
			table = "<br>ERROR in query syntax: " + e.getM() + "<br>";
		}
		catch(NumberFormatException e)
		{
			table = "<br>ERROR in query: Invalid compare value! Must be a number.<br>";
		}
		
		//If this return is reached, table = an error message
		return table;
	}
	/************************************************************************************
	*	METHOD: beautify(DataSet)
	*	DIRECTIVE: 
	*		-Takes a record from the data base and formats the fields via padding or
	*		truncation to a specific length in order for output to appear columnized.
	*		-Each beautified token is stored in a string array as such:
	*			String[0] = city
	*			String[1] = visits
	*			String[2] = pagesPerVisit
	*			String[3] = avgVisitDuration
	*			String[4] = percentNewVisits
	*			String[5] = bounceRate
	*		This is a design strategy in order to keep the tokens separated in the event
	*		that a query specifies fewer attributes be pulled, PREVENTING the need to
	*		parse one long string of the specific attributes desired.
	*		-The length of the spaces and vertical dividers (|) are subtracted from the
	*		specific field width so that when these formatting devices are added to the
	*		String, it will be the appropriate length.
	************************************************************************************/
	public static String[] beautify(DataSet d)
	{
		//Account for length of formatting to be added to each String
		int cityWidth  = city_width;
		int visitWidth = visit_width;
		int ppvWidth   = ppv_width;
		int avdWidth   = avd_width;
		int pnvWidth   = pnv_width;
		int brWidth    = br_width;
		
		//Create a String array to hold the formatted attributes
		String[] s = new String[6];
		
		//PHASE 0: City
		String c = d.city;
		
		//If city length is too long, truncate the extra letters.
		if(c.length() > cityWidth){c = c.substring(0, cityWidth);}
		
		//If city length is too small, pad with spaces to desired length.
		if(c.length() < cityWidth){c = pad(c, cityWidth);}
		
		//Store formatted city into array at slot 0
		s[0] = "| " + c + " |";
		

		//PHASE 1: Visits
		String v = Integer.toString(d.visits);
		
		//If visits length is too long, truncate the extra digits.
		if(v.length() > visitWidth) v = v.substring(0, visitWidth);
		
		//If visits length is too small, pad with spaces to desired length.
		if(v.length() < visitWidth) v = pad(v, visitWidth);
		
		//Store formatted visits into array at slot 1
		s[1] = "| " + v + " |";
		
	
		//PHASE 2: Pages Per Visit
		String ppv = Double.toString(d.pagesPerVisit);
		
		//If pages_per_visit length is too long, truncate the extra digits.
		if(ppv.length() > ppvWidth) ppv = ppv.substring(0, ppvWidth);
		
		//If pages_per_visit length is too small, pad with spaces to desired length.
		if(ppv.length() < ppvWidth){ppv = pad(ppv, ppvWidth);}
		
		//Store formatted pages_per_visit into array at slot 2
		s[2] = "| " + ppv + " |";
		

		//PHASE 3: Average Visit Duration
		String a = Integer.toString(d.avgVisitDuration);
		
		//If average_visit_duration length is too long, truncate the extra digits.
		if(a.length() > avdWidth){a = a.substring(0, avdWidth);}
		
		//If average_visit_duration length is too small, pad with spaces to desired length.
		if(a.length() < avdWidth){a = pad(a, avdWidth);}
		
		//Store formatted average_visit_duration into array at slot 3
		s[3] = "| " + a + " |";
		
	
		//PHASE 4: Percent New Visits
		String pnv = Double.toString(d.percentNewVisits);
		
		//If percent_new_visits length is too long, truncate the extra digits.
		if(pnv.length() > pnvWidth) pnv = pnv.substring(0, pnvWidth);
		
		//If percent_new_visits length is too small, pad with spaces to desired length.
		if(pnv.length() < pnvWidth) pnv = pad(pnv, pnvWidth);
		
		//Store formatted percent_new_visits into array at slot 4
		s[4] = "| " + pnv + " |";
		
		//PHASE 5: Bounce Rate
		String b = Double.toString(d.bounceRate);
		
		//If visits length is too long, truncate the extra digits.
		if(b.length() > brWidth) b = b.substring(0, brWidth);
		
		//If visits length is too small, pad with spaces to desired length.
		if(b.length() < brWidth) b = pad(b, brWidth);
		
		//Store formatted visits into array at slot 5
		s[5] = "| " + b + " |";
		
		//Return the formatted String array
		return s;
	}
	/************************************************************************************
	*	METHOD: beautifyCity(String)
	*	DIRECTIVE: 
	*		-Beautifies a city String according to specifications for the table-formatted
	*		city in METHOD: beautify(DataSet).
	*		-This is for comparison purposes 'where' [constraint] = [some_city_name]
	************************************************************************************/
	private static String beautifyCity(String c)
	{
		int cityWidth  = city_width;
		//If city length is too long, truncate the extra letters.
		if(c.length() > cityWidth) c = c.substring(0, cityWidth);
		
		//If city length is too small, pad with spaces to desired length.
		if(c.length() < cityWidth) c = pad(c, cityWidth);
		
		//Store formatted city into array at slot 0
		return "| " + c + " |";
	}
	/************************************************************************************
	*	METHOD: getRawValue(String)
	*	DIRECTIVE: 
	*		-Converts a number String into a double for comparison purposes
	************************************************************************************/
	private static Double getRawValue(String s) throws NumberFormatException
	{
		s = s.substring(2);
		s = s.substring(0,s.indexOf("|"));
		s = s.trim();
		return Double.parseDouble(s);
	}
	/************************************************************************************
	*	METHOD: pad(String,int)
	*	DIRECTIVE: 
	*		-Adds a series of Strings to a String for table format purposes
	*		-This is NOT the same as METHOD: addSpaces(String, int)
	************************************************************************************/
	private static String pad(String s, int l)
	{
		while(s.length() < l)
		{
			s = s + " &nbsp; ";
		}
		return s;
	}
	/************************************************************************************
	*	METHOD: showHeader(int[])
	*	DIRECTIVE: 
	*		-Returns a String representing the table header
	*		-Takes an integer array as a parameter indicating which columns to be added
	************************************************************************************/
	private static String showHeader(int[] f)
	{
		//Declare horizontal rules and header)
		String top_hr = "";
		String header = "<br>";
		String bot_hr = "<br>";
		
		if(f[0] == 1)
		{
			top_hr += addDashes(city_width);
			header += addSpaces("| City", city_width);
			bot_hr += addDashes(city_width);
		}
		if(f[1] == 1)
		{
			top_hr += addDashes(visit_width);
			header += addSpaces("| Visits", visit_width);
			bot_hr += addDashes(visit_width);
		}
		if(f[2] == 1)
		{
			top_hr += addDashes(ppv_width);
			header += addSpaces("| PagesPerVisit", ppv_width);
			bot_hr += addDashes(ppv_width);
		}
		if(f[3] == 1)
		{
			top_hr += addDashes(avd_width);
			header += addSpaces("| AvgVisitDur", avd_width);
			bot_hr += addDashes(avd_width);
		}
		if(f[4] == 1)
		{
			top_hr += addDashes(pnv_width);
			header += addSpaces("| PercentOfNewVisits", pnv_width);
			bot_hr += addDashes(pnv_width);
		}
		if(f[5] == 1)
		{
			top_hr += addDashes(br_width);
			header += addSpaces("| BounceRate", br_width);
			bot_hr += addDashes(br_width);
		}
		
		return top_hr + header + bot_hr + "<br>";
	}
	/************************************************************************************
	*	METHOD: addDashes(int)
	*	DIRECTIVE: 
	*		-Adds a series of dashes to a String for table formatting purposes
	************************************************************************************/
	private static String addDashes(int w)
	{
		String result = "";
		int i = 0;
		while(i < w)
		{
			result += "-";
			i++;
		}
		return result;
	}
	/************************************************************************************
	*	METHOD: addSpaces (String, int)
	*	DIRECTIVE: 
	*		-Adds a series of spaces to a String for table formatting purposes
	************************************************************************************/
	private static String addSpaces(String s, int w)
	{
		int i = s.length();
		while(i < w-1)
		{
			s += " &nbsp ";
			i++;
		}
		s += "|";
		return s;
	}
	/************************************************************************************
	*	METHOD: showFooter(int[])
	*	DIRECTIVE: 
	*		-Prints a series of dashes to encapsulate the bottom of the output table
	************************************************************************************/
	private static String showFooter(int[] f)
	{
		String s = "";
		if(f[0] == 1)
			s += addDashes(city_width);
		
		if(f[1] == 1)
			s += addDashes(visit_width);

		if(f[2] == 1)
			s += addDashes(ppv_width);

		if(f[3] == 1)
			s += addDashes(avd_width);

		if(f[4] == 1)
			s += addDashes(pnv_width);
			
		if(f[5] == 1)
			s += addDashes(br_width);
			
		return s;
	}
	/************************************************************************************
	*	METHOD: createTestMap()
	*	DIRECTIVE: 
	*		-Creates a dummy hash map of data that can be used for testing purposes
	************************************************************************************/
	public static HashMap<String,DataSet> createTestMap()
	{
		//Declare variables for random data generation
		Random r = new Random();
		HashMap<String,DataSet> m = new HashMap<String,DataSet>();
		String[] cities = {"Philadelphia", "New York", "Albuquerque", "Dayton", "Syracuse", "Oswego", "Liverpool", "Clay", "Brewerton", "Topeka", "Santa Fe", "Anchorage", "Sacromento", "Delray Beach", "San Diego", "Mattydale", "Watertown", "Buffalo", "Rochester", "Neptune", "Juno", "Jersey City"};
		
		//Show how many cities added to test database
		System.out.println("\n" + cities.length + " cities added to database.");
		for(int i = 0; i < cities.length; i++)
		{
			//Create a new record and add record to database
			DataSet a = new DataSet(cities[i], r.nextInt(100), (r.nextDouble() * 20), r.nextInt(180), (r.nextDouble() * 100), (r.nextDouble() * 500));
			m.put(cities[i], a);
		}
		return m;
	}
	/************************************************************************************
	*	METHOD: main(String[])
	*	DIRECTIVE: 
	*		-Not intended for practical use on the overall project.
	*		-Serves as a temporary means to test a query
	************************************************************************************/
	public static void main(String[] args)
	{
		//Create a dummy hash map
		HashMap<String,DataSet> db = createTestMap();
		
		//Display instructions
		System.out.println("\nType 'quit' to exit program.\n\n"
		+ "Query Example: get city ppv where ppv > 30\n");
		String qry;
		do
		{
			//Get a query from the user
			System.out.print("ARGUMENT = TABLE_COLUMN\n"
			+ "  city   = City\n"
			+ " visits  = Visits\n"
			+ "  ppv    = PgPerVisit\n"
			+ "  avd    = AvgVisitDur\n"
			+ "  ponv   = PercentOfNewVisits\n"
			+ "  br     = BounceRate\n\n"
			+ "Enter Query: ");
			qry = new Scanner(System.in).nextLine();
			
			//Use query to extract specified information from the database
			if(!qry.equals("quit"))
				System.out.println(search(qry, db));
		}while(!qry.equals("quit"));
	}
}

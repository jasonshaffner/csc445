package database;

public class FormatException extends Exception
{
	public String m = "ERROR: Query Syntax!";
	public FormatException()
	{
		super();
	}
	public FormatException(String e)
	{
		m = e;
	}
	public String getM()
	{
		return m;
	}
}

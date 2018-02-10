package main;

import java.util.ArrayList;

public class CSV {
	ArrayList<String> list = new ArrayList<String>();
	public CSV(String s)
	{
		String[] parts = FileConverter.toWhiteSpaced(s).split(",");
		for(String ss : parts)
			list.add(ss);
	}
	public String toString(){return this.list.toString();}

}

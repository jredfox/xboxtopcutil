package main;

import java.util.ArrayList;

public class ColorEntry {
	
	public String name;
	public String color;
	public String arg;
	public String colorarg;
	
	public ColorEntry(String name,String color)
	{
		this.name = name;
		this.color = color;
		this.colorarg = "00";
		this.arg = "0B";
	}
	public ColorEntry(String name,String color,String arg)
	{
		this.name = name;
		this.color = color;
		this.colorarg = "00";
		this.arg = arg;
	}
	public ColorEntry(String name,String color, String arg,String colorarg)
	{
		this.name = name;
		this.color = color;
		this.arg = arg;
		this.colorarg = colorarg;
	}
	/**
	 * Convert Color Entry from file .COL byte array entry bytes[] 0:00 1:arg 2-end-5:name endlast3:color end-4:colorarg:
	 */
	public ColorEntry(byte[] by) 
	{
		arg = String.format("%02X", by[1]);
		String name = "";
		for(int i=2;i<by.length-4;i++)
		{
			byte b = by[i];
			name += (char)b;
		}
		this.name = name;
		this.colorarg = String.format("%02X", by[by.length-4]);
		this.color = String.format("%02X", by[by.length-3]) + String.format("%02X", by[by.length-2]) + String.format("%02X", by[by.length-1]);
	}
	public String getString(boolean csv)
	{
		if(!csv)
			return this.name + " = " + this.color;
		else
			return this.name + "," + this.color + "," + this.arg + "," +  this.colorarg;
	}
	@Override
	public String toString()
	{
		return this.name + " = " + this.color;
	}
	/**
	 * Checks if char is one of these characters "abcdefghijkmlnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_"
	 */
	public static boolean isValidChar(char c) 
	{
		int i = (int)c;
		return i >= 48 && i <=57 || i >= 65 && i <= 90 || i == 95 || i >= 97 && i <= 122;
	}

}

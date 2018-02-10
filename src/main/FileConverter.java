package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileConverter {
	
	/**
	 * Converts pc animation file .mcmeta to xbox .txt
	 */
	public static void animationToXbox(File f,boolean fancy){
	  try{
		JSONParser parser = new JSONParser();
		FileReader reader = new FileReader(f);
		JSONObject json = (JSONObject) parser.parse(reader);
		reader.close();
		
		JSONObject animation = (JSONObject) json.get("animation");
		Long frametime = (Long) animation.get("frametime");
		JSONArray arr = (JSONArray) animation.get("frames");
		Boolean interpolate = (Boolean) animation.get("interpolate");
		int ftime = 0;
		if(frametime != null)
			ftime = (int)((long)frametime);
		
		String str = "ANIM=";
		if(interpolate != null && interpolate == true)
			str += "#";
		if(fancy)
			str += "\r\n";
		
		for(int i=0;i<arr.size();i++)
		{
			Object obj = arr.get(i);
			if(obj instanceof Long)
			{
				if(ftime != 0)
					str += obj.toString() + "*" + ftime + ",";
				else
					str += obj.toString() + ",";
			}
			if(obj instanceof JSONObject)
			{
				JSONObject entry = (JSONObject)obj;
				int index = (int)((long) entry.get("index"));
				int time = (int)((long) entry.get("time"));
				str += index + "*" +  time + ",";
			}
			if(fancy)
				str += "\r\n";
		}
		File file = new File(f.getParentFile(),FileConverter.getFileDisplayName(f) + ".txt");
		file.createNewFile();
		ArrayList<String> list = new ArrayList<String>();
		list.add(str);
		Files.write(file.toPath(),list);
		System.out.print(str + "\n");
	  } catch (Exception e){App.printErr(e);}
	  
	}
	
	/**
	 * Converts xbox animation file .txt > .mcmeta
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static void animationToPc(File f,boolean fancy) {
		//"ANIM=" tag "#" = interpolate "*" is separator between frame and frametime
		try{
		List<String> list = Files.readAllLines(f.toPath());
		ArrayList<Point> entries = new ArrayList<Point>();
		boolean empty = isAnimationEmpty(list);
		boolean ip = doesAnimationIP(list);
		int frametime = 0;
		
		//Populate entry frame list
		if(!empty)
		for(int i=0;i<list.size();i++)
		{
			String str = list.get(i);
			String value = str;
			if(!FileConverter.doesCharHaveNum(str))
				continue;//Don't try to parse if doesn't have a number on that line
			if(str.contains("ANIM"))
			{
				String[] parts = str.split("=");
				if(!parts[1].contains("#"))
					value = parts[1];
				else
					value = toWhiteSpaced(parts[1]).substring(1,parts[1].length());	//interpolate support
			}
			if(value.contains(","))
			{
				String[] parts = value.split(",");
				for(String s : parts)
				{
					if(s.contains("*"))
					{
						String[] part2 = s.split("\\*");
						entries.add(new Point(Integer.parseInt(part2[0]), Integer.parseInt(part2[1]) ) );
					}
					else
						entries.add(new Point(Integer.parseInt(s), 0) );//Wiki says frametime defaults to one but, 0 represents nothing here
				}
			}
			else{
				String[] part2 = value.split("\\*");
				entries.add(new Point(Integer.parseInt(part2[0]), Integer.parseInt(part2[1]) ) );
			}
		}
		frametime = getFrameTime(entries);
		
		JSONObject json = new JSONObject();
		json.put("animation", new JSONObject());
		JSONObject animation = (JSONObject) json.get("animation");
		if(ip)
			animation.put("interpolate", true);
		if(frametime != 0)
			animation.put("frametime", frametime);
		JSONArray frames = new JSONArray();
		for(Point p : entries)
		{
			int frame = p.x;
			int time = p.y;
			if(frametime == time)
				frames.add(frame);
			else{
				//Custom Frames if not default frametime
				JSONObject cf = new JSONObject();
				cf.put("index", frame);
				cf.put("time", time);
				frames.add(cf);
			}
		}
		if(!frames.isEmpty())
			animation.put("frames", frames);
		File file = new File(f.getParentFile(),FileConverter.getFileDisplayName(f) + ".mcmeta");
		if(!file.exists())
			file.createNewFile();
		List<String> listjson = new ArrayList<String>();
		listjson.add(MainJava.toPrettyFormat( json.toString(),fancy ) );
		Files.write(file.toPath(), listjson);
		}catch(Exception e){App.printErr(e);}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int getFrameTime(List<Point> list)
	{
		HashMap<Integer,Integer> values = new HashMap();//frame,amount of points
		//counts up points
		for(Point p : list)
		{
			int frame = p.x;
			int time = p.y;
			if(values.containsKey(time))
				values.put(time, values.get(time) + 1);
			else
				values.put(time, 1);
		}
		//gets highest point value
		Point p = new Point(0,0);
		Iterator<Map.Entry<Integer,Integer>> it = values.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer,Integer> pair = it.next();
			int time = pair.getKey();
			int point = pair.getValue();
			if(point > p.y)
				p = new Point(time,point);
		}
		System.out.println(p);
		return p.x;
	}
	@SuppressWarnings("rawtypes")
	public static void printMap(Map map)
	{
		Iterator it = map.entrySet().iterator();
		int index = 0;
		System.out.print("[");
		while(it.hasNext())
		{
			Map.Entry pair = (Map.Entry) it.next();
			System.out.print(" Key:" + pair.getKey() + ",Value:" + pair.getValue());
			index++;
		}
		System.out.print("]\n");
	}

	public static boolean doesAnimationIP(List<String> list) 
	{
		for(int i=0;i<list.size();i++)
		{
			String s = toWhiteSpaced(list.get(i));
			if(s.contains("#"))
				return true;
		}
		return false;
	}
	
	public static boolean isAnimationEmpty(List<String> list) 
	{
		for(int i=0;i<list.size();i++)
		{
			String s = toWhiteSpaced(list.get(i));
			if(doesCharHaveNum(s) )
				return false;
		}
		return true;
	}

	public static boolean doesCharHaveNum(String s) {
		return s.contains("1") || s.contains("2") || s.contains("3") || s.contains("4") || s.contains("5") || s.contains("6") || s.contains("7") || s.contains("8") || s.contains("9") || s.contains("0");
	}

	/**
	 * Converts csv file back to .COL format after user has done some modifications to it
	 */
	public static void convertCSVFile(File f) {
		try{
		ArrayList<Byte> filebytes = new ArrayList<Byte>();
		List<String> list = Files.readAllLines(f.toPath());
		String header = toWhiteSpaced(list.get(1)).split("=")[1];
		for(int i=0;i<header.length();i+=2)
		{
			String hex = header.substring(i, i+2);
			filebytes.add(FileConverter.getByteFromHex(hex));
		}
		System.out.println("Header added starting entry conversion");
		
		for(int i=2;i<list.size();i++)
		{
			CSV csv = new CSV(toWhiteSpaced(list.get(i)));
			filebytes.add(FileConverter.getByteFromHex("00"));
			filebytes.add(FileConverter.getByteFromHex(csv.list.get(2)));//Arg
			String name = csv.list.get(0);
			char[] chars = name.toCharArray();
			for(char c : chars)
				filebytes.add((byte)c);//add the chars to name string to the file
			filebytes.add(FileConverter.getByteFromHex(csv.list.get(3)) );//Color arg
			
			//Add color back to file
			String color = toWhiteSpaced(csv.list.get(1));
			for(int j=0;j<color.length();j+=2)
			{
				String hex = color.substring(j, j+2);
				filebytes.add(FileConverter.getByteFromHex(hex));//add the color last arg for the color entry
			}
		}
		File file = new File(f.getParentFile(),FileConverter.getFileDisplayName(f) + ".col");
		if(file.exists())
			file.delete();
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file);
		byte[] byteArray = getByteArray(filebytes);
		stream.write(byteArray, 0, (int) byteArray.length);
		stream.close();
		
		}catch(Exception e){App.printErr(e);}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void convertColFile(File f,boolean csv) 
	{
	try {
		ArrayList<ColorEntry> entries = new ArrayList();
		FileInputStream input = new FileInputStream(f);
		byte[] bytes = IOUtils.toByteArray(input);
		input.close();
		String[] ubytes = FileConverter.getHexFromBytes(bytes);
		String header = "";
		for(int i=0;i<8;i++)
		{
			if(i != 0)
				header += " " + ubytes[i];
			else
				header += ubytes[i];
		}
		int index_start = -1;
		int index_end = -1;
		
		for(int i=8;i<ubytes.length;i++)
		{
			String hex = ubytes[i];
			byte b = bytes[i];
			char c = (char)b;
			boolean valid = ColorEntry.isValidChar(c);
			boolean start = index_start != -1;
			
			if(hex.equals("00") && !start)
				index_start = i;
			if(i-1 == index_start && start)
				continue;//is not color ignore starting point
			
			//if not valid character entry ends 4 indexes later add the color entry and clear stats
			if(!valid && start)
			{
				index_end = i+3;//from color arg 3 more hex till the end
				byte[] by = new byte[(index_end-index_start)+1];
				int index = 0;
				for(int j=index_start;j<=index_end;j++)
				{
					by[index] = bytes[j];
					index++;
				}
				entries.add(new ColorEntry(by));
				index_start = -1;
				index_end = -1;
				i+=3;
			}
		}
		String extension = ".csv";
		if(!csv)
			extension = ".txt";
		File converted = new File(f.getParentFile(),FileConverter.getFileDisplayName(f) + extension);
		if(!converted.exists())
			converted.createNewFile();
		FileWriter output = new FileWriter(converted);
		if(!csv)
			output.write("#Name = HexadecimalColor\r\n");
		else{
			output.write("#Name, Color, Arg, ColorArg\r\n");
			output.write("#Header = " + header + "\r\n");
		}
		
		entries = FileConverter.sortObjectArray(entries);
		for(ColorEntry e : entries)
			output.write(e.getString(csv) + "\r\n");
		output.close();
		
	}catch (Exception e) {AppCfg.printErr(e);}
    }
	/**
	 * Converts single hex back to byte
	 */
	public static byte getByteFromHex(String s)
	{
		return (byte) (Integer.parseInt(s,16) & 0xff);
	}
	public static String getFileDisplayName(File f) 
	{
		int last = findLastChar(f.getName(),'.');
		if(last == -1)
			last = f.getName().length();
		return f.getName().substring(0, last);
	}
	public static int findLastChar(String s, char c) {
		for(int i=s.length();i>0;i--)
		{
			String str = s.substring(i-1, i);
			if(str.equals("" + c))
				return i-1;
		}
		return -1;
	}
	public static String getHexFromByte(byte b)
	{
		return String.format("%02X", b);
	}
	public static String[] getHexFromBytes(byte[] bytes) 
	{
		String[] strings = new String[bytes.length];
		for(int i=0;i<bytes.length;i++)
			strings[i] = String.format("%02X", bytes[i]);
		return strings;
	}

	/**
	 * Ejects a string that is whitespaced
	 * @param s
	 * @return
	 */
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
	}
	
	public static byte[] getByteArray(ArrayList<Byte> filebytes) 
	{
		byte[] bytes = new byte[filebytes.size()];
		for(int i=0;i<filebytes.size();i++)
			bytes[i] = filebytes.get(i);
		return bytes;
	}
	
	/**
	 * Only works on object arrays with no Dupes and that all have proper toString()
	 * @param entries
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList sortObjectArray(ArrayList entries) 
	{
		HashMap<String,Object> map = new HashMap<String,Object>();
		for(Object obj : entries)
			map.put(obj.toString(), obj);
		TreeMap<String, Object> tree = new TreeMap<String,Object>(map);
		Iterator<Map.Entry<String,Object> > it = tree.entrySet().iterator();
		ArrayList<Object> list = new ArrayList<Object>();
		while(it.hasNext())
		{
			Map.Entry<String, Object> pair = it.next();
			list.add(pair.getValue());
		}
		return list;
	}
	public static void resizeImage(File f,int w,int h)
	{
		try{
		BufferedImage img = ImageIO.read(f);
		BufferedImage fimg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		stitchImage(img, fimg, 0, 0, img.getWidth(), img.getHeight());
		ImageIO.write(fimg, "png", f);
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Used for entities like beds and shulkers from xbox to pc. x,y is start then specify sub img height and the color to recolor grey img and then specify which img needs the color overlay
	 */
	public static void splitAndColorImage(File dirimg, int x1, int y1, int imgW, int imgH, int x2, int y2, int imgW2, int imgH2,String filename,Color color)
	{
		try{
		BufferedImage init = ImageIO.read(dirimg);
		BufferedImage grey = FileConverter.copyImage(init.getSubimage(x1, y1, imgW, imgH));
		BufferedImage overlay = FileConverter.copyImage(init.getSubimage(x2, y2, imgW2, imgH2));
		colorAndOverlayImg(dirimg.getParentFile(),filename,grey,overlay,color);
		}catch(Exception e){e.printStackTrace();}
	}
	public static void colorAndOverlayImg(File dir,String filename,BufferedImage grey, BufferedImage overlay, Color color) {
		try{
		File f2 = new File(dir,filename);
		f2.createNewFile();
		FileConverter.colorGreyImage(grey,color);
		FileConverter.overlayImage(grey,overlay,false);
		ImageIO.write(grey, "png", f2);
		}catch(Exception e){e.printStackTrace();}
	}

	public static BufferedImage copyImage(BufferedImage img) {
		BufferedImage image = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
		FileConverter.stitchImage(img, image, 0, 0, img.getWidth(), img.getHeight());
		return image;
	}

	public static void overlayImage(BufferedImage img1, BufferedImage img2,boolean transparent) 
	{
		for(int col=0;col<img2.getHeight();col++)
		{
			for(int row=0;row<img2.getWidth();row++)
			{
				Color c = new Color(img2.getRGB(row, col),true);
				if(c.getAlpha() != 0 || transparent)
					img1.setRGB(row, col, img2.getRGB(row, col));
			}
		}
	}
	public static void colorGreyImage(BufferedImage image, Color c) {
        // Converts color multiplier to separate RGB components, scaled from 0 to 1
        int colorMultiplier = c.getRGB();
        int width = image.getWidth();
        int height = image.getHeight();
        float redMultiplier = (float)(colorMultiplier >> 16 & 255) / 255.0F;
        float greenMultiplier = (float)(colorMultiplier >> 8 & 255) / 255.0F;
        float blueMultiplier = (float)(colorMultiplier & 255) / 255.0F;

        for (int x = 0; x < width; ++x)
        {
            for (int y = 0; y < height; ++y)
            {
                // Read pixel color value from grayscale image
                int int_color =  image.getRGB(x, y);
                Color tst = new Color(int_color,true);
                if(tst.getAlpha() == 0)
                    continue;//skip transparent pixels

                // Converts color to separate RGB components
                float red = (float)(int_color >> 16 & 255);
                float green = (float)(int_color >> 8 & 255);
                float blue = (float)(int_color & 255);

                // Calculate new color
                int r = (int)(red * redMultiplier);
                int g = (int)(green * greenMultiplier);
                int b = (int)(blue * blueMultiplier);
                int_color = r << 16 | g << 8 | b;
                Color pixel = new Color(int_color,true);
                pixel = new Color(pixel.getRed(),pixel.getGreen(),pixel.getBlue(),tst.getAlpha());//makes it set the right alpha
                // Set pixel to new color value
                image.setRGB(x, y, pixel.getRGB());
            }
        }
    }
	
	public static void stitchImages(File dir,BufferedImage init_img,int colums,CSVE blocks)
	{
		int rows = blocks.list.size()/colums;//gets dynamic rows based on colums and how many files are here
		int terrain_length = colums * init_img.getWidth();
		int terrain_height = rows * init_img.getHeight();
		stitchImages(dir,colums,rows,terrain_length,terrain_height,init_img.getWidth(),init_img.getHeight(),blocks );
	}
	
	public static void stitchImages(File dir,int colums,int rows,int Tlength,int Theight,int imgW,int imgH,CSVE blocks)
	{
		try{
	    ArrayList<File> files = new ArrayList<File>();
	    for(CSV c : blocks.list)
	    	files.add(new File(dir,c.list.get(0) + ".png"));
		
		BufferedImage terrain = new BufferedImage(Tlength, Theight, BufferedImage.TYPE_INT_ARGB);

		int col = 0;
		int row = 0;
		for(File f : files)
		{
			if(f.getName().equals("abc.png") || f.getName().equals("terrain.png"))
				continue;
			if(!f.exists())
			{
				f = new File(f.getParentFile(),"blanks/" + "blank " + row + "-" + col + ".png");
				if(!f.exists())
				{
					f = null;
					System.out.println("blank is null:" + row + "-" + col);
				}
			}
			BufferedImage img = null;
			if(f != null)
				img = ImageIO.read(f);
			else
				img = new BufferedImage(imgW, imgH,  BufferedImage.TYPE_INT_ARGB);
			
			stitchImage(img.getSubimage(0, 0, imgW, imgH),terrain,col,row,imgW,imgH );
			col++;
			if(col == colums)
			{
				col = 0;
				row++;
			}
		}
		String fname = FileConverter.getFileDisplayName(dir);
		if(fname.equals("blocks"))
			fname = "terrain";
		if(fname.equals("banners"))
			fname = "Banner_Atlas";
		File atlis = new File(dir.getParentFile(),fname + "_test.png");
		atlis.createNewFile();
		ImageIO.write(terrain, "png", atlis);
		
		}catch(Exception e){e.printStackTrace();}
		
	}
	public static void stitchImage(BufferedImage img, BufferedImage terrain, int col, int row, int imgW,int imgH) {
		int x = col * imgW;
		int y = row * imgH;
		terrain.setRGB(x, y, imgW, imgH, getImagePixels(img), 0, imgW);//int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
	}

	public static int[] getImagePixels(BufferedImage img) {
		int[] rgbArray = new int[0];
		rgbArray = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		return rgbArray;
	}
	public static void splitTerrain(int res,int colums,File dir,File terrain,File output,CSVE csve,boolean to_xbox)
	{
		splitTerrain(res,colums,dir,terrain,output,csve,null,to_xbox);
	}
	public static void splitTerrain(int res,int colums,File dir,File terrain,File output,CSVE csve,File filecolors,boolean to_xbox)
	{
		try{
		BufferedImage img = ImageIO.read(terrain);
		int rows = img.getHeight()/res;
		FileConverter.splitImage(terrain, colums,rows, output,csve);
		if(filecolors == null)
			return;
		doShulkerBoxes(dir,filecolors,output);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void doShulkerBoxes(File dir,File filecolors,File output) {
		try{
		//color shulker boxes
		String contains = "Shulker_Box_";
		List<String> colors = App.getColors(filecolors, contains);
		String actualname = "shulker_top_";
		BufferedImage box = ImageIO.read(new File(dir,"shulker_box.png"));
		BufferedImage overlay = ImageIO.read(new File(dir,"shulker_box_overlay.png"));
		for(int i=0;i<16;i++)
		{
			BufferedImage b = FileConverter.copyImage(box);
			String s = colors.get(i);
			Color c = Color.decode("#" + s.split("=")[1]);
			String name = colors.get(i).split("=")[0].substring(contains.length()).toLowerCase();
			if(name.equals("grey"))
				name = "gray";
			if(name.equals("light_green"))
				name = "lime";
			name = actualname + name;
			colorGreyImage(b, c);
			overlayImage(b, overlay, false);
			ImageIO.write(b, "png", new File(output,name + ".png"));
		  }
		}catch(Exception e){e.printStackTrace();}
	}

	public static void splitImage(File dirImage,int colums,int rows,File output)
	{
		splitImage(dirImage,colums,rows,output,null);
	}
	public static void splitImage(File dirImage,int colums,int rows,File output,CSVE csve)
	{
		try{
		BufferedImage image = ImageIO.read(dirImage);
		int imageX = image.getWidth();
		int imageY = image.getHeight();
		int imgW = imageX/colums;
		int imgH = imageY/rows;
		splitImage(dirImage,colums,rows,imgW,imgH,imgW,imgH,output,csve);
		}
		catch(Exception e){e.printStackTrace();}
	}
	public static void splitImage(File dirImage,int colums,int rows,int imgW,int imgH,int imgFW,int imgFH,File output,CSVE csve)
	{
		ArrayList<BufferedImage> images = new ArrayList();
		ArrayList<File> files = new ArrayList();
		try{
			BufferedImage image = ImageIO.read(dirImage);
			int x = 0;
			int y=0;
			int index_csv = 0;
			boolean blanksUsed = false;
			
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<colums;j++)
			{
				BufferedImage img = image.getSubimage(x, y, imgW, imgH);
				images.add(img);
				if(csve == null)
					files.add(new File(output, dirImage.getName().substring(0, dirImage.getName().length()-4 )+ " " + i + "-" + j + ".png"));
				else{
					String name = csve.list.get(index_csv).list.get(0);
					if(!name.equals("blank"))
						files.add(new File(output,name + ".png"));
					else{
						files.add(new File(output,"blanks/blank " + i + "-" + j + ".png"));
						blanksUsed = true;
					}
				}
				x+=imgW;
				index_csv++;
			}
			y += imgH;
			x = 0;
		}
		File blanks = new File(output,"blanks");
		if(!output.exists())
			output.mkdirs();
		if(!blanks.exists() && blanksUsed)
			blanks.mkdir();
		
		int index = 0;
		for(BufferedImage img : images)
		{
			File f =files.get(index);
			f.createNewFile();
			//For banners/zombies if not right canvas size for the image stitch it to 0,0 of the bigger res file
			if(imgW != imgFW || imgH != imgFH)
			{
				BufferedImage fImg = new BufferedImage(imgFW, imgFH, BufferedImage.TYPE_INT_ARGB);
				stitchImage(img, fImg, 0, 0, imgW, imgH);
				img = fImg;
			}
			ImageIO.write(img, "png", f);
			index++;
		}
		
		}
		catch(Exception e){e.printStackTrace();}
	}

	public static boolean isCharNum(String s) {
		return s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6") || s.equals("7") || s.equals("8") || s.equals("9");
	}

	public static String getFileExtension(File f) {
		return f.toString().substring(FileConverter.findLastChar(f.toString(), '.'), f.toString().length());
	}

}

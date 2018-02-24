package main;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainJava {
	public static App app;
	public static final int build = 21;
	public static final String version = "Build " + build;
	public static final File dir = new File(System.getProperty("user.dir"));
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static void main(String[] args)
	{
		/*
		try{
		BufferedImage tst = new BufferedImage(255,255,BufferedImage.TYPE_INT_ARGB);
		for(int x=0;x<tst.getWidth();x++)
		{
			for(int y=0;y<tst.getHeight();y++)
				tst.setRGB(x, y, Color.decode("#FFFFFF").getRGB());
		}
//		FileConverter.colorGreyImage(tst, Color.decode("#FCC724"));
		File dir = new File("C:/Users/jredfox/Desktop/color_test.png");
		dir.createNewFile();
		ImageIO.write(tst, "png", dir);
		}catch(Exception e){e.printStackTrace();}
		if(true)
			return;*/
		app = new App();
		System.out.println("App Starting");
	}
	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		  // The images must be the same size.
		  if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
		    int width = imgA.getWidth();
		    int height = imgA.getHeight();

		    // Loop over every pixel.
		    for (int y = 0; y < height; y++) {
		      for (int x = 0; x < width; x++) {
		        // Compare the pixels for equality.
		    	  Color c = new Color(imgA.getRGB(x, y),true);
		    	  Color c2 = new Color(imgB.getRGB(x, y),true);
		        if (imgA.getRGB(x, y) != imgB.getRGB(x, y) && !(c.getAlpha() == 0 && c2.getAlpha() == 0) ) {
		        	System.out.println("x:" + x + " y:" + y + " C:" + c);
		          return false;
		        }
		      }
		    }
		  } else {
		    return false;
		  }
		  
		return true;
	}
	public static void getAllFilesFromDir(File directory, ArrayList<File> files,String extension) {

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && !files.contains(file) && file.getName().endsWith(extension)) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	getAllFilesFromDir(file, files,extension);
	        }
	    }
	}
	
	public static void getAllFilesFromDir(File directory, ArrayList<File> files) {

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && !files.contains(file)) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	getAllFilesFromDir(file, files);
	        }
	    }
	}
	
	/**
	   * Convert a JSON string to pretty print version
	   * @param fancy is to allow for new file lines use if not printing and if you want it fancy
	   * @param jsonString
	   * @return
	   */
	  public static String toPrettyFormat(String jsonString, boolean fancy) 
	  {
		  if(!fancy)
			  return jsonString;
	      JsonParser parser = new JsonParser();
	      JsonObject json = parser.parse(jsonString).getAsJsonObject();

	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String prettyJson = gson.toJson(json);

	      return prettyJson.replaceAll("\n", "\r\n");
	  }

}

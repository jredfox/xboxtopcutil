package main;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
public class AppCfg extends JFrame{
	
	public int appX;
	public int appY;
	public int appW;
	public int appH;
	public File fc_lastdir = null;
	public boolean fc_details;
	public File cfg;
	public boolean FancyJson = true;
	public boolean FancyAnimation = true;
	
	public AppCfg(File f,File dir,String name,String version)
	{
		super(name + "-" + version);
		moveFileFromJar(this.getClass(),f.getName(), f,false);
		this.cfg = f;
		loadConfig();
		setApp();
		
		if(fc_lastdir == null || !fc_lastdir.exists())
			fc_lastdir = dir;
	}
	
	public void setApp() 
	{
		this.setLocation(appX, appY);
		this.setSize(appW, appH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Adds the saving function to the config file
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		     public void windowClosing(WindowEvent winEvt) {
		    	  saveConfig();
		      }
		 });
	}

	public List<String> loadConfig() 
	{
		try{
			List<String> list = Files.readAllLines(this.cfg.toPath());
		  for(String s : list)
		  {
			if(s == null || !s.contains("="))
				continue;
			String parts[] = s.split("=");
			String option = parts[0];
			String value = parts[1];
			if(option.equals("File"))
				this.fc_lastdir = new File(value);
			if(option.equals("FileDetails"))
				this.fc_details = Boolean.parseBoolean(value);
			if(option.equals("AppX"))
				this.appX = Integer.parseInt(value);
			if(option.equals("AppY"))
				this.appY = Integer.parseInt(value);
			if(option.equals("AppW"))
				this.appW = Integer.parseInt(value);
			if(option.equals("AppH"))
				this.appH = Integer.parseInt(value);
			if(option.equals("FancyJson"))
				this.FancyJson = Boolean.parseBoolean(value);
			if(option.equals("FancyAnimation"))
				this.FancyAnimation = Boolean.parseBoolean(value);
		   }
		  return list;
		}
		catch(Exception e){e.printStackTrace();}
		return null;
	}
	public List<String> saveConfig() {
		ArrayList<String> options = new ArrayList<String>();
		options.add("AppW=" + this.getWidth());
		options.add("AppH=" + this.getHeight());
		options.add("AppX=" + this.getX());
		options.add("AppY=" + this.getY());
		options.add("FileDetails=" + fc_details);
		if(fc_lastdir != null)
			options.add("File=" + fc_lastdir.toString());
		else
			options.add("File=C:/Documents");
		
		options.add("FancyJson=" + this.FancyJson);
		options.add("FancyAnimation=" + this.FancyAnimation);
		
		try{
			Files.write(this.cfg.toPath(),options);
		}catch(Exception e){App.printErr(e);}
		return options;
	}
	public static void printErr(Throwable e) 
	{
		 StringBuilder sb = new StringBuilder(e.toString());
		    for (StackTraceElement ste : e.getStackTrace()) {
		        sb.append("\n\tat ");
		        sb.append(ste);
		    }
		    String trace = sb.toString();
		AppCfg.printMsg(trace);
	
	}

	public static void printMsg(String trace) {
		JTextArea textArea = new JTextArea(trace);
		JScrollPane scrollPane = new JScrollPane(textArea);  
		textArea.setLineWrap(true);  
		textArea.setWrapStyleWord(true); 
		scrollPane.setPreferredSize( new Dimension(500, 300 ) );
		JOptionPane.showMessageDialog(null, scrollPane, "Error Occured", JOptionPane.YES_NO_OPTION);
		
	}

	@SuppressWarnings("rawtypes")
	public static void moveFileFromJar(Class clazz,String input,File output,boolean replace) {
		if(output.exists() && !replace)
			return;
		try {
			InputStream inputstream =  clazz.getResourceAsStream(input);
			FileOutputStream outputstream = new FileOutputStream(output);
			output.createNewFile();
			IOUtils.copy(inputstream,outputstream);
			inputstream.close();
			outputstream.close();
		} catch (Exception io) {io.printStackTrace();}
	}

}

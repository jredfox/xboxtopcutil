package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class App extends AppCfg{
	
	public JMenuBar mbar;//the menu bar all this stuff is put on
	public JMenu file;//only used to exit the program right now
	public JMenu convert;//Converting tab
	public JMenu options;//toggles certain aspects of the application
	public JMenu debug;
	
	//File
	public JMenuItem exit;
	//Convert
	public JMenuItem coltotxt;
	public JMenuItem coltocsv;
	public JMenuItem csvtocol;
	public JMenuItem animtopc;
	public JMenuItem animtoxbox;
	//Options
	public JMenuItem toggle_fancyJson;
	public JMenuItem toggle_fileDetails;
	public JMenuItem toggle_fancyAnimation;
	//Debug
	public JMenu renamers;
	public JMenu spliters;
	public JMenu unspliters;
	//renamers
	public JMenuItem renameBlocksToPc;
	public JMenuItem renameItemsToPc;
	public JMenuItem renameClock;
	public JMenuItem renameCompass;
	public JMenuItem renameBanners;
	//spliters
	public JMenuItem splitTerrain;
	public JMenuItem splitItems;
	public JMenuItem splitCompass;
	public JMenuItem splitClock;
	public JMenuItem splitBanners;
	//unspliters
	public JMenuItem unsplitBlocks;
	public JMenuItem unsplitItems;
	public JMenuItem unsplitCompass;
	public JMenuItem unsplitClock;
	public JMenuItem unsplitBanners;
	//entities
	public JMenuItem shulkers;
	public JMenuItem beds;
	public JMenuItem zombie_villager;
	public JMenuItem zombie;
	public JMenuItem boxes;

	
	//Mapping files
	public static final File mapsdir = new File(MainJava.dir,"mappings");
	public static final File index_clock = new File(mapsdir,"index_clock.csv");
	public static final File index_compass = new File(mapsdir,"index_compass.csv");
	public static final File index_entities = new File(mapsdir,"index_entities.csv");
	public static final File index_font = new File(mapsdir,"index_font.csv");
	public static final File index_rp = new File(mapsdir,"index_rp.csv");
	public static final File indexes_blocks = new File(mapsdir,"indexes_blocks.csv");
	public static final File indexes_items = new File(mapsdir,"indexes_items.csv");
	public static final File indexes_banners = new File(mapsdir,"indexes_banners.csv");
	
	//Comma Seperated Values Enhanced Format It is used here as mappings where the varabiles stand for the paths at the var section of csve
	public CSVE csv_clock;
	public CSVE csv_compass;
	public CSVE csv_entities;
	public CSVE csv_font;
	public CSVE csv_rp;
	public CSVE csv_blocks;
	public CSVE csv_items;
	public CSVE csv_banners;

	@SuppressWarnings("unused")
	public App() 
	{
		super(new File(MainJava.dir,"config.txt"),MainJava.dir,"XboxToPcUtil",MainJava.version);	
		setLayout(new FlowLayout());
		
		//Set up mapping for the main function of this application
		if(!mapsdir.exists())
			mapsdir.mkdir();
		AppCfg.moveFileFromJar(MainJava.class, "mappings/index_clock.csv", index_clock,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/index_compass.csv", index_compass,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/index_entities.csv", index_entities,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/index_font.csv", index_font,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/index_rp.csv", index_rp,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/indexes_blocks.csv", indexes_blocks,false);
		AppCfg.moveFileFromJar(MainJava.class, "mappings/indexes_items.csv", indexes_items,false);
		
		//Parse files into readable format for this program
		csv_clock = new CSVE(index_clock);
		csv_compass = new CSVE(index_compass);
		csv_entities = new CSVE(index_entities);
		csv_font = new CSVE(index_font);
		csv_rp = new CSVE(index_rp);
		csv_blocks = new CSVE(indexes_blocks);
		csv_items = new CSVE(indexes_items);
		csv_banners = new CSVE(indexes_banners);
		
		mbar = new JMenuBar();
		file = new JMenu("File");
		convert = new JMenu("Convert");
		options = new JMenu("Options");
		debug = new JMenu("Debug");
		renamers = new JMenu("File Renamers");
		
		options.setFont(new Font("Seric", Font.PLAIN,16));
		file.setFont(new Font("Seric", Font.PLAIN,16));
		convert.setFont(new Font("Seric", Font.PLAIN,16));
		debug.setFont(new Font("Seric", Font.PLAIN,16));
		
		//File
		exit = new JMenuItem("Exit");
		//Convert
		coltotxt = new JMenuItem(".col > .txt");
		coltocsv = new JMenuItem(".col > .csv");
		csvtocol = new JMenuItem(".csv > .col");
		animtopc = new JMenuItem("xbox animation > pc");
		animtoxbox = new JMenuItem("pc animation > xbox");
		//Options
		toggle_fancyJson = new JMenuItem("Toggle Fancy Json");
		toggle_fileDetails = new JMenuItem("Toggle File Details");
		toggle_fancyAnimation = new JMenuItem("Toggle Fancy Animation");
		//Debug
		spliters = new JMenu("Splitters");
		unspliters = new JMenu("UnSplitters");
		renameBlocksToPc = new JMenuItem("Rename Blocks > Pc");
		renameItemsToPc = new JMenuItem("Rename Items > Pc");
		renameClock = new JMenuItem("Rename Clock");
		renameCompass = new JMenuItem("Rename Compass");
		renameBanners = new JMenuItem("Rename Banners");
		splitTerrain = new JMenuItem("Split Terrain(Xbox Block Atlis)");
		splitItems = new JMenuItem("Split Items");
		splitCompass = new JMenuItem("Split Compass");
		splitClock = new JMenuItem("Split Clock");
		splitBanners = new JMenuItem("Split Banners");
		unsplitBlocks = new JMenuItem("UnSplit Blocks");
		unsplitItems = new JMenuItem("UnSplit Items");
		unsplitCompass = new JMenuItem("UnSplit Compass");
		unsplitClock = new JMenuItem("UnSplit Clock");
		unsplitBanners = new JMenuItem("UnSplit Banners");
		shulkers = new JMenuItem("Shulker > Pc");
		beds = new JMenuItem("Bed > Pc");
		zombie = new JMenuItem("Zombie > Pc");
		zombie_villager = new JMenuItem("Zombie Villager");
		boxes = new JMenuItem("boxes");
		
		setLocations();
		addItems();
		addFunctions();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setDefaultLookAndFeelDecorated(true);
			SwingUtilities.updateComponentTreeUI(this);
		} catch(Exception e){App.printErr(e);}
		this.setVisible(true);
	}
	public void setLocations() {
		
	}

	public void addFunctions() {
		//File
		exit.addActionListener(new BasicHandler());
		//Convert
		coltotxt.addActionListener(new ConverterHandler());
		coltocsv.addActionListener(new ConverterHandler());
		csvtocol.addActionListener(new ConverterHandler());
		animtopc.addActionListener(new ConverterHandler());
		animtoxbox.addActionListener(new ConverterHandler());
		//Debug
		renameBlocksToPc.addActionListener(new FileRenamer());
		renameItemsToPc.addActionListener(new FileRenamer());
		renameCompass.addActionListener(new FileRenamer());
		renameClock.addActionListener(new FileRenamer());
		renameBanners.addActionListener(new FileRenamer());
		
		splitTerrain.addActionListener(new FileRenamer() );
		splitItems.addActionListener(new FileRenamer() );
		splitCompass.addActionListener(new FileRenamer() );
		splitClock.addActionListener(new FileRenamer() );
		splitBanners.addActionListener(new FileRenamer() );
		
		unsplitBlocks.addActionListener(new FileRenamer() );
		unsplitItems.addActionListener(new FileRenamer() );
		unsplitCompass.addActionListener(new FileRenamer() );
		unsplitClock.addActionListener(new FileRenamer() );
		unsplitBanners.addActionListener(new FileRenamer() );
		
		shulkers.addActionListener(new FileRenamer() );
		beds.addActionListener(new FileRenamer() );
		zombie.addActionListener(new FileRenamer() );
		zombie_villager.addActionListener(new FileRenamer() );
		boxes.addActionListener(new FileRenamer() );
		
		//Options
		toggle_fancyJson.addActionListener(new BasicHandler());
		toggle_fileDetails.addActionListener(new BasicHandler());
		toggle_fancyAnimation.addActionListener(new BasicHandler() );
	}
	public class FileRenamer implements ActionListener
	{
		@SuppressWarnings("unused")
		@Override
		public void actionPerformed(ActionEvent e) {
			File[] ff = App.this.getSelectedFiles("",true);
			if(ff == null || ff.length == 0)
				return;
			
			boolean toxbox = false;
			//Rename Blocks
			if(e.getSource() == renameBlocksToPc)
				moveAndRenameFile(new File(ff[0],"blocks"),new File(ff[0],"output"),csv_blocks,toxbox,16);
			//Rename Items
			if(e.getSource() == renameItemsToPc)
				moveAndRenameFile(new File(ff[0],"items"),new File(ff[0],"output"),csv_items,toxbox,16);
			if(e.getSource() == renameCompass)
				moveAndRenameFile(new File(ff[0],"compass"),new File(ff[0],"compass"),csv_compass,toxbox,1);
			if(e.getSource() == renameClock)
				moveAndRenameFile(new File(ff[0],"clock"),new File(ff[0],"clock"),csv_clock,toxbox,1);
			if(e.getSource() == renameBanners)
				moveAndRenameFile(new File(ff[0],"banners"),new File(ff[0],"banners"),csv_banners,toxbox,6);
			
			if(e.getSource() == splitCompass)
				splitConvertCompass(new File(ff[0],"compass"),true);
			if(e.getSource() == splitClock)
				splitConvertCompass(new File(ff[0],"clock"),false);
			if(e.getSource() == splitTerrain)
				FileConverter.splitTerrain(16,16,new File(ff[0],"blocks"),new File(ff[0],"blocks/terrain.png"),new File(ff[0],"blocks"),csv_blocks,new File(ff[0],"colors.txt"),false);
			if(e.getSource() == splitItems)
				FileConverter.splitTerrain(16,16,new File(ff[0],"items"),new File(ff[0],"items/items.png"),new File(ff[0],"items"),csv_items,false);
			if(e.getSource() == splitBanners)
				FileConverter.splitImage(new File(ff[0],"banners/Banner_Atlas.png"), 6, 7, 42, 41,64,64, new File(ff[0],"banners"), csv_banners);
			try{
			if(e.getSource() == unsplitBlocks)
				FileConverter.stitchImages(new File(ff[0],"blocks"),ImageIO.read(new File(ff[0],"blocks/dirt.png") ), 16,csv_blocks);
			if(e.getSource() == unsplitItems)
				FileConverter.stitchImages(new File(ff[0],"items"),ImageIO.read(new File(ff[0],"items/apple.png") ), 16,csv_items);
			if(e.getSource() == unsplitCompass)
				FileConverter.stitchImages(new File(ff[0],"compass"),ImageIO.read(new File(ff[0],"compass/compass_00.png") ), 1,csv_compass);
			if(e.getSource() == unsplitClock)
				FileConverter.stitchImages(new File(ff[0],"clock"),ImageIO.read(new File(ff[0],"clock/clock_00.png") ), 1,csv_clock);
			if(e.getSource() == unsplitBanners)
				FileConverter.stitchImages(new File(ff[0],"banners"), 6, 7, 256, 288, 42, 41, csv_banners);
			if(e.getSource() == shulkers)
				splitColor(ff[0],new File(ff[0],"entities/endergolem.png"),"Shulker_Box_","shulker_");
			if(e.getSource() == beds)
				splitColor(ff[0],new File(ff[0],"entities/bed.png"),"Bed_","");
			if(e.getSource() == zombie)
				FileConverter.resizeImage(new File(ff[0],"entities/zombie.png"),64,64);
			if(e.getSource() == boxes)
				FileConverter.doShulkerBoxes(new File(ff[0],"blocks"), new File(ff[0],"colors.txt"), new File(ff[0],"blocks"));
			}catch(Exception ee){ee.printStackTrace();}
		}
	}
	public void splitColor(File dir,File shulker, String contains, String actualname) {
		List<String> colors = getColors(new File(dir,"colors.txt"),contains);
		for(int i=0;i<16;i++)
		{
			String s = colors.get(i).split("=")[0].substring(contains.length()).toLowerCase();
			if(s.equals("grey"))
				s = "gray";
			if(s.equals("light_green"))
				s = "lime";
			s = actualname + s;
			FileConverter.splitAndColorImage(shulker, 0, 0, 64, 64, 0, 64, 64, 64, s + ".png",Color.decode("#" + colors.get(i).split("=")[1]) );
		}
	}
	public static List<String> getColors(File file_colors,String contains) {
		if(!file_colors.exists())
		{
			AppCfg.printMsg("Invalid Input convert colors to text!");
			return null;
		}
		List<String> list = null;
		try {
			list = Files.readAllLines(file_colors.toPath());
		} catch (IOException e) {e.printStackTrace();}
		List<String> colors = new ArrayList();
		for(String s : list)
		{
			if(s.contains(contains))
				colors.add(FileConverter.toWhiteSpaced(s));
		}
		return colors;
	}
	public void splitConvertCompass(File dir,boolean isCompass) {
		File file = new File(dir,isCompass ? "compass.png" : "clock.png");
		int rows = isCompass ? 32 : 64;
		FileConverter.splitImage(file,1,rows,dir,isCompass ? csv_compass : csv_clock);
	    CSVE csve = isCompass ? csv_compass : csv_clock;
	}
	/**
	 * CSVE renamer supports two formats namepc,namexbox,path,path2 && name,path,path2
	 */
	public static void moveAndRenameFile(File dir,File output,CSVE csve,boolean to_xbox,int colMax)
	{
		ArrayList<File> files = new ArrayList();
		MainJava.getAllFilesFromDir(dir, files);
		int index_xbox = to_xbox ? 1 : 0;
		for(File f : files)
		{
			if(!FileConverter.getFileDisplayName(f).contains("-"))
	    		 continue;
			int index = getIndex(f,colMax);
			CSV csv = csve.list.get(index);
			if(csv.list.size() == 4 && to_xbox)
				index_xbox++;
			String name = csv.list.get(index_xbox);
			f.renameTo(new File(f.getParent(),name + ".png") );
		}
	}
	public static int getIndex(File f,int colMax) 
	{
			if(!f.toString().contains("-"))
				return -1;
			String[] parts = getTwoDimBase(f);
			int row = Integer.parseInt(parts[1]);
			int col = Integer.parseInt(parts[2]);
			int index = (row * colMax) + col;
			return index;
	}
		/**
		 * [0]=name,[1]=row,[2]=colum,[3]=file extension
		 */
		public static String[] getTwoDimBase(File f) {
			String s = FileConverter.getFileDisplayName(f);
			int index = 0;
			int index_dash = FileConverter.findLastChar(s,'-');
			
			for(int i=index_dash-1;i>=0;i--)
			{
				String str = s.substring(i-1,i);
				boolean isNum = FileConverter.isCharNum(str);
				if(!isNum)
				{
					index = i;
					break;
				}
			}
			String[] parts = new String[4];
			parts[0] = s.substring(0, index);//name
			parts[1] = s.substring(index, index_dash);//colum
			parts[2] = s.substring(index_dash+1, s.length() );//row
			parts[3] = FileConverter.getFileExtension(f);//extension
			return parts;
		}
		
	public void addItems() {
		
		//Tells the menu bar to add these menus with spaces
		mbar.add(file);
		mbar.add(convert);
		mbar.add(new JMSpace());
		mbar.add(debug);
		mbar.add(new JMSpace());
		mbar.add(options);
		
		//File
		file.add(exit);
		//Option
		options.add(toggle_fancyJson);
		options.addSeparator();
		options.add(toggle_fancyAnimation);
		options.addSeparator();
		options.add(toggle_fileDetails);
		//Debug
		debug.add(renamers);
		debug.add(spliters);
		debug.add(unspliters);
		renamers.add(renameBlocksToPc);
		renamers.add(renameItemsToPc);
		renamers.add(renameCompass);
		renamers.add(renameClock);
		renamers.add(renameBanners);

		spliters.add(splitTerrain);
		spliters.add(splitItems);
		spliters.add(splitCompass);
		spliters.add(splitClock);
		spliters.add(splitBanners);
		unspliters.add(unsplitBlocks);
		unspliters.add(unsplitItems);
		unspliters.add(unsplitCompass);
		unspliters.add(unsplitClock);
		unspliters.add(unsplitBanners);
		debug.add(shulkers);
		debug.add(beds);
		debug.add(zombie);
		debug.add(zombie_villager);
		debug.add(boxes);
		
		//Convert
		convert.add(coltotxt);
		convert.addSeparator();
		convert.add(coltocsv);
		convert.addSeparator();
		convert.add(csvtocol);
		convert.addSeparator();
		convert.add(animtopc);
		convert.addSeparator();
		convert.add(animtoxbox);
		
		add(mbar);
		this.setJMenuBar(mbar);
	}
	public class BasicHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == exit)
			{
				App.this.saveConfig();
				App.this.dispose();
			}
			if(e.getSource() == toggle_fancyJson)
			{
				App.this.FancyJson = !App.this.FancyJson;
				JOptionPane.showMessageDialog(null, "Json Fancy = " + App.this.FancyJson);
			}
			if(e.getSource() == toggle_fileDetails)
			{
				App.this.fc_details = !App.this.fc_details;
				JOptionPane.showMessageDialog(null, "File Details = " + App.this.fc_details);
			}
			if(e.getSource() == toggle_fancyAnimation)
			{
				App.this.FancyAnimation = !App.this.FancyAnimation;
				JOptionPane.showMessageDialog(null, "Animation Fancy = " + App.this.FancyAnimation);
			}
		}
	}
	public class ConverterHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == coltotxt || e.getSource() == coltocsv)
			{
				File[] files = getSelectedFiles("col",false);
				for(File f : files)
					if(f != null)
						if(f.exists())
							FileConverter.convertColFile(f, (e.getSource() == coltocsv));
			}
			if(e.getSource() == csvtocol)
			{
				File[] files = getSelectedFiles("csv",false);
				for(File f : files)
					if(f != null)
						if(f.exists())
							FileConverter.convertCSVFile(f);
			}
			if(e.getSource() == animtopc)
			{
				File[] files = getSelectedFiles("txt",false);
				for(File f : files)
					if(f != null)
						if(f.exists())
							FileConverter.animationToPc(f,App.this.FancyJson);
			}
			if(e.getSource() == animtoxbox)
			{
				File[] files = getSelectedFiles("mcmeta",false);
				for(File f : files)
					if(f != null)
						if(f.exists())
							FileConverter.animationToXbox(f,App.this.FancyAnimation);
			}
		}
		
	}
	public File[] getSelectedFiles(String extension,boolean directory) 
	{
		JFileChooser jc = new JFileChooser();
		jc.setCurrentDirectory(this.fc_lastdir);
		jc.setMultiSelectionEnabled(true);
		jc.setAcceptAllFileFilterUsed(false);
		if(!directory)
		{
			FileFilter filter = new FileNameExtensionFilter("*." + extension,extension);
			jc.setFileFilter(filter);
		}
		Action details = jc.getActionMap().get("viewTypeList");
		if(this.fc_details)
			details = jc.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		if(directory)
		{
			jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jc.setMultiSelectionEnabled(false);
		}
		if(jc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return new File[]{};
		
		File[] files = jc.getSelectedFiles();
		if(directory)
			files = new File[]{jc.getSelectedFile()};
		if(files == null || files.length == 0)
			return new File[]{};
		if(!directory)
			this.fc_lastdir = jc.getCurrentDirectory();
		else
			this.fc_lastdir = jc.getSelectedFile();
		
		return files;
	}

}

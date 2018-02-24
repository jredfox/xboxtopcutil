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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import org.json.simple.JSONObject;

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
	public JMenuItem bed_win10;
	public JMenuItem zombie_villager;
	public JMenuItem zombie;
	public JMenuItem boxes;
	public JMenuItem shulkertoxbox;
	public JMenuItem bedItem;
	public JMenuItem zombieToOld;
	public JMenuItem bedtoXbox;

	
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
		AppCfg.moveFileFromJar(MainJava.class, "mappings/indexes_banners.csv", indexes_banners,false);
		
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
		shulkertoxbox = new JMenuItem("Shulker > Xbox");
		beds = new JMenuItem("Bed > Pc");
		bed_win10 = new JMenuItem("Bed > Win10 From Xbox");//from xbox
		zombie = new JMenuItem("Zombie > Pc");
		zombie_villager = new JMenuItem("Zombie Villager > 1.9");
		boxes = new JMenuItem("boxes");
		bedItem = new JMenuItem("ItemBed > Pc");
		zombieToOld = new JMenuItem("Zombie Villager > 1.8");
		bedtoXbox = new JMenuItem("Bed > Xbox(from pc)");
		
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
		bed_win10.addActionListener(new FileRenamer() );
		shulkertoxbox.addActionListener(new FileRenamer() );
		bedItem.addActionListener(new FileRenamer() );
		zombieToOld.addActionListener(new FileRenamer() );
		bedtoXbox.addActionListener(new FileRenamer() );
		
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
				FileConverter.splitTerrain(16,16,new File(ff[0],"blocks"),new File(ff[0],"blocks/terrain.png"),new File(ff[0],"blocks"),csv_blocks,new File(ff[0],"colors.txt"),false,false,App.this.FancyJson);
			if(e.getSource() == splitItems)
				FileConverter.splitTerrain(16,16,new File(ff[0],"items"),new File(ff[0],"items/items.png"),new File(ff[0],"items"),csv_items,new File(ff[0],"colors.txt"),false,true,App.this.FancyJson);
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
				splitColor(ff[0],new File(ff[0],"entities/endergolem.png"),"Shulker_Box_","shulker_",true);
			if(e.getSource() == shulkertoxbox)
				convertShulkerToXbox(new File(ff[0],"entities"),true);
			if(e.getSource() == beds)
				convertBeds(ff[0],new File(ff[0],"entities/bed.png"),false);
			if(e.getSource() == bedItem)
				convertItemBeds(new File(ff[0],"items"),App.this.FancyJson);
			if(e.getSource() == bed_win10)
				convertBeds(ff[0],new File(ff[0],"entities/bed.png"),true);
			if(e.getSource() == bedtoXbox)
				convertBedToXbox(new File(ff[0],"entities"));
			if(e.getSource() == zombie)
				FileConverter.resizeImage(new File(ff[0],"entities/zombie.png"),64,64);
			if(e.getSource() == zombie_villager)
				convertZombieToPc(new File(ff[0],"entities"));
			if(e.getSource() == zombieToOld)
				convertZombieToOld(new File(ff[0],"entities"));
			if(e.getSource() == boxes)
				FileConverter.doShulkerBoxes(new File(ff[0],"blocks"), new File(ff[0],"colors.txt"), new File(ff[0],"blocks"));
			}catch(Exception ee){ee.printStackTrace();}
		}
	}
	public void convertBedToXbox(File dir) {
		try{
			BufferedImage bed = ImageIO.read(new File(dir,"bed.png"));
			BufferedImage img = new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
			moveImg(bed, 22, 22, 16, 6, 22, 0);//move thing up some
			moveImg(bed,0,28,44,16,0,22);//move bed up
			placeImage(img,bed,0,0);
			ImageIO.write(img, "png", new File(dir,"bed_test.png"));
		}catch(Exception e){App.printErr(e);}
	}
	public void convertZombieToOld(File dir) {
		try{
		BufferedImage origin = ImageIO.read(new File(dir,"zombie_villager.png"));
		BufferedImage zmb = new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
		BufferedImage head = origin.getSubimage(0, 0, 32,18);
		BufferedImage legs = origin.getSubimage(0, 22, 16,16);
		BufferedImage chest = origin.getSubimage(22, 26, 8, 12);
		BufferedImage back = origin.getSubimage(36,26, 8,12);
		BufferedImage arms = origin.getSubimage(44, 38,16,16);
		
		//Images to convert to 1.8
		BufferedImage lside = origin.getSubimage(16,26,6,12);
		BufferedImage rside = origin.getSubimage(30,26,6,12);
		BufferedImage neck = origin.getSubimage(22,20,8,6);
		BufferedImage but = origin.getSubimage(30,20,10,6);
		//place valid images
		placeImage(zmb, head, 0, 32);
		placeImage(zmb,legs,0,16);
		placeImage(zmb,chest,20,20);
		placeImage(zmb,back,32,20);
		placeImage(zmb,arms,40,16);
		
		//convert left side
		lside = getZombieSideOld(lside,1);
		rside = getZombieSideOld(rside,1);
		neck = getZombieNeckOld(neck,1);
		but = getZombieButOld(but,1);
		
		placeImage(zmb, lside, 16, 20);
		placeImage(zmb,rside,28,20);
		placeImage(zmb,neck,20,16);
		placeImage(zmb,but,28,16);
		
		ImageIO.write(zmb,"png",new File(dir,"zombie_villager1.8.png"));
		}catch(Exception e){App.printErr(e);}
	}
	public BufferedImage getZombieButOld(BufferedImage but, int i) {
		if(i == 1)
		{
			//delete generated rows from algorithm 1
			deleteColum(but,but.getWidth()-3);
			deleteColum(but,but.getWidth()-4);
			deleteRow(but,but.getHeight()-4);
			deleteRow(but,but.getHeight()-3);
			//move colums over
			moveColum(but,but.getWidth()-2,but.getWidth()-4);
			moveColum(but,but.getWidth()-1,but.getWidth()-3);
			//combine back rows to finish the formattting
			moveRow(but,but.getHeight()-2,but.getHeight()-4);
			moveRow(but,but.getHeight()-1,but.getHeight()-3);
			but = but.getSubimage(0, 0, 8,4);
			return but;
		}
		return null;
	}
	public BufferedImage getZombieNeckOld(BufferedImage neck, int i) {
		if(i == 1)
		{
			moveRow(neck,neck.getHeight()-2,neck.getHeight()-4);
			moveRow(neck,neck.getHeight()-1,neck.getHeight()-3);
			return neck.getSubimage(0, 0, 8, 4);
		}
		
		return null;
	}
	/**
	 * @return image of zombie side based on algorithm type
	 */
	public BufferedImage getZombieSideOld(BufferedImage side, int i) {
	  
	  if(i == 1)
	  {
		deleteColum(side,1);
		deleteColum(side,side.getWidth()-2);
		moveColum(side,0,1);
		moveColum(side,side.getWidth()-1,side.getWidth()-2);
		side = side.getSubimage(1, 0, side.getWidth()-2, side.getHeight());
		
		return side;
	  }
	  return null;
	}
	public void moveColum(BufferedImage img,int columfrom, int columto)
	{
		moveColum(img,columfrom,columto,true);
	}
	public void moveRow(BufferedImage img,int rowfrom, int rowto)
	{
		moveRow(img,rowfrom,rowto,true);
	}
	/**
	 * moves a colum over leaving a gap if boolean is true
	 * @param img
	 * @param offset
	 */
	public void moveColum(BufferedImage img,int columfrom, int columto, boolean delete)
	{
		moveImg(img, columfrom, 0, 1, img.getHeight(), columto, 0,delete);
	}
	/**
	 * moves a row of pixels based on offset
	 */
	public void moveRow(BufferedImage img,int rowfrom, int rowto, boolean delete)
	{
		moveImg(img, 0, rowfrom, img.getWidth(),1, 0, rowto,delete);
	}
	/**
	 * deletes a single colum of pixels
	 */
	public void deleteColum(BufferedImage img, int index) {
		deletePixles(img, index, 0, 1, img.getHeight());
	}
	/**
	 * deletes a single row of pixels
	 */
	public void deleteRow(BufferedImage img, int index) {
		deletePixles(img, 0, index, img.getWidth(), 1);
	}
	public void convertZombieToPc(File dir) {
		try{
		//Instantiate Initial Images
		BufferedImage origin = ImageIO.read(new File(dir,"zombie_villager.png") );
		BufferedImage zmb = new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
		BufferedImage zhead = origin.getSubimage(0, 32, 32, 18);
		BufferedImage zleg = origin.getSubimage(0, 16, 16,16);
		BufferedImage zarm = origin.getSubimage(40, 16, 16,16);
		BufferedImage zchest = origin.getSubimage(20, 20, 8, 12);
		BufferedImage zback = origin.getSubimage(32,20, 8,12);
		BufferedImage zlside = origin.getSubimage(16,20, 4,12);
		BufferedImage zrside = origin.getSubimage(28,20, 4,12);
		BufferedImage zneck = origin.getSubimage(20,16, 8,4);
		BufferedImage zbut = origin.getSubimage(28, 16, 8,4);
		
		//Place images
		placeImage(zmb, zhead, 0, 0);
		placeImage(zmb, zleg, 0,22);
		placeImage(zmb, zarm, 44,38);
		placeImage(zmb, zchest, 22,26);
		placeImage(zmb, zback, 36,26);
		
		//convert but
		zbut = getNewZombieBut(zbut,1);
		zneck = getNewZombieNeck(zneck,1);
		zlside = getZombieNewSide(zlside,1);
		zrside = getZombieNewSide(zrside,1);
		
		//place converted images
		placeImage(zmb, zlside, 16,26);
		placeImage(zmb, zrside, 30, 26);
		placeImage(zmb, zneck, 22,20);
		placeImage(zmb, zbut, 30,20);
		//Save Image
		ImageIO.write(zmb, "png", new File(dir,"zombie_villager_1.9.png"));
		}catch(Exception e){App.printErr(e);}
	}
	public BufferedImage getZombieNewSide(BufferedImage side, int i) {
		BufferedImage s = side;
		side = new BufferedImage(6,12,BufferedImage.TYPE_INT_ARGB);
		placeImage(side,s,1,0);//re-align image while keeping the canvis the right size
		if(i == 1)
		{
			moveColum(side,1,0);
			moveColum(side,side.getWidth()-2,side.getWidth()-1);
			//fill gaps
			moveColum(side,2,1,false);
			moveColum(side,side.getWidth()-3,side.getWidth()-2,false);
			return side;
		}
		return null;
	}
	public BufferedImage getNewZombieNeck(BufferedImage zneck, int i) {
		int w = zneck.getWidth();
		int h = zneck.getHeight();
		zneck = FileConverter.resizeImage(zneck, w, h+2);
		if(i == 1)
		{
			//move last two rows to end
			moveRow(zneck,h-1,zneck.getHeight()-1);
			moveRow(zneck,h-2,zneck.getHeight()-2);
			//fil in botom gaps
			moveRow(zneck,zneck.getHeight()-2,zneck.getHeight()-3,false);
			moveRow(zneck,h-3,h-2,false);
			return zneck;
		}
		return null;
	}
	/**
	 * Get on that
	 * @param zbut
	 * @param i
	 * @return
	 */
	public BufferedImage getNewZombieBut(BufferedImage zbut, int i) {
		int w = zbut.getWidth();
		int h = zbut.getHeight();
		zbut = FileConverter.resizeImage(zbut, 10, 6);
		
		if(i == 1)
		{
			//move colums to end
			moveColum(zbut,w-1,zbut.getWidth()-1);
			moveColum(zbut,w-2,zbut.getWidth()-2);
			//fill in gaps
			moveColum(zbut,w-3,zbut.getWidth()-4,false);
			moveColum(zbut,zbut.getWidth()-2,zbut.getWidth()-3,false);
			//move colums to end
			moveRow(zbut,h-1,zbut.getHeight()-1);
			moveRow(zbut,h-2,zbut.getHeight()-2);
			//fill in row gaps
			moveRow(zbut,zbut.getHeight()-2,zbut.getHeight()-3,false);
			
			moveRow(zbut,h-3,h-2,false);
			return zbut;
		}
		return null;
	}
	public static void convertItemBeds(File dir,boolean fancyjson)
	{
		try{
			BufferedImage o = ImageIO.read(new File(dir,"bed.png"));
			BufferedImage g = ImageIO.read(new File(dir,"bed_overlay.png"));
			List<String> list = getColors(new File(dir.getParentFile(),"colors.txt"), "Bed_");
			for(String str : list)
			{
				str = FileConverter.toWhiteSpaced(str);
				BufferedImage overlay = FileConverter.copyImage(o);
				BufferedImage grey = FileConverter.copyImage(g);
				String s = str.split("=")[0].substring("Bed_".length()).toLowerCase();
				if(s.equals("grey"))
					s = "gray";
				if(s.equals("light_green"))
					s = "lime";
				String color = s;
				s = "bed_" + s;
				FileConverter.colorGreyImage(grey,Color.decode("#" + str.split("=")[1]) );
				FileConverter.overlayImage(grey, overlay, false);
				File bed = new File(dir,s + ".png");
				ImageIO.write(grey, "png", bed);
				JSONObject json = new JSONObject();
				json.put("parent", "item/generated");
				JSONObject textures = new JSONObject();
				textures.put("layer0", "items/" + s);
				json.put("textures",textures);
				ArrayList<String> strlist = new ArrayList();
				strlist.add(MainJava.toPrettyFormat(json.toString(), fancyjson));
				File models = new File(dir,"models/item");
				models.mkdirs();
				if(color.equals("silver"))
					color = "light_gray";
				File jbed = new File(models,color + "_bed.json");
				jbed.createNewFile();
				Files.write(jbed.toPath(), strlist);
			}
		}catch(Exception e){App.printErr(e);}
	}
	public void convertShulkerToXbox(File dir,boolean msg) {
		try{
		BufferedImage terrain = new BufferedImage(64,128,BufferedImage.TYPE_INT_ARGB);
		BufferedImage white = ImageIO.read(new File(dir,"shulker_white.png"));
		BufferedImage shulker = white.getSubimage(0, 0, 64, 52);
		BufferedImage head = white.getSubimage(0, 52, 64, 12);
		placeImage(terrain, shulker, 0, 0);//place shulker
		placeImage(terrain,shulker, 0, 64);//place overlay
		placeImage(terrain,head,0,116);//place head
		ImageIO.write(terrain, "png", new File(dir,"endergolem_test.png"));
		if(msg)
			App.printMsg("Shulker Foramtted To Pc Sucessfully\nNeeds manual work remove the 2d shulker shell for what doesn't need to get overlayed without color!");
		}catch(Exception e){App.printErr(e);}
	}
	public void convertBeds(File dir, File dirbed,boolean win10) {
		HashMap<BufferedImage,String> beds = splitColor(dir,dirbed,"Bed_","",win10);//Converts beds to format of win 10 edition
		if(win10)
			return;//stop do nothing else already is in the proper format
		Iterator<Map.Entry<BufferedImage,String> > it = beds.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<BufferedImage,String> map = it.next();
			BufferedImage bed = map.getKey();
			File f = new File(dirbed.getParentFile(),map.getValue());
			try {f.createNewFile();}catch (IOException e) {e.printStackTrace();}
			
			convertBed(bed, f);//converts to shitty pc format
		}
		
	}
	/**
	 * From Win10 Edition to pc
	 */
	public void convertBed(BufferedImage bed,File dirbed) {
		moveImg(bed,0,38,24,12,0,38+6);//move leg so doesn't get trampled
		moveImg(bed,0,22,44,16,0,22+6);//move bed to match pc
		moveImg(bed,22,0,16,6,22,22);//move fragment to match pc
		
		//convert and generate legs
		BufferedImage legset = FileConverter.copyImage(bed.getSubimage(0, 44, 24, 12));
		deletePixles(bed, 0, 44, 24, 12);
		for(int i=0;i<4;i++)
		{
			BufferedImage leg = null;
			//bottom left leg
			if(i == 0)
				leg = legset.getSubimage(12*0, 6*1, 12, 6);
			//top left leg
			if(i == 1)
				leg = legset.getSubimage(12*0, 6*0, 12, 6);
			//bottom right leg
			if(i == 2)
				leg = legset.getSubimage(12*1, 6*1, 12, 6);
			//top right leg
			if(i == 3)
				leg = legset.getSubimage(12*1, 6*0, 12, 6);
			
			BufferedImage[] images = new BufferedImage[8];
			//null images to transparent images
			images[0] = new BufferedImage(3,3,BufferedImage.TYPE_INT_ARGB);
			images[3] = new BufferedImage(3,3,BufferedImage.TYPE_INT_ARGB);
			//get image 3x3's based on faces and based on leg id
			int dim = 3;
			images[getLegNorth(i)] = getLegPartNorth(leg,getXboxLegIndex(i));
			images[getLegSouth(i)] = getLegPartSouth(leg,getXboxLegIndex(i) );
			images[getLegEast(i)] = getLegPartEast(leg,getXboxLegIndex(i) );
			images[getLegWest(i)] = getLegPartWest(leg,getXboxLegIndex(i) );
			images[getLegTop(i)] = getLegPartTop(leg,getXboxLegIndex(i) );
			images[getLegBottom(i)] = getLegPartBottom(leg,getXboxLegIndex(i) );
			
			//create leg based on 3x3's by relative positioning 
			BufferedImage img = bed.getSubimage(50, 6*i,12,16);
			int y = 0;
			int index = 0;
			for(int j=0;j<2;j++)
			{
				for(int k=0;k<4;k++)
				{
					placeImage(img, images[index], k*3, y*3);
					index++;
				}
				y++;
			}
		}
		
		try{
		ImageIO.write(bed, "png", dirbed);
		}catch(Exception e){e.printStackTrace();}
	}
	public BufferedImage getLegPartNorth(BufferedImage leg, String type) {
		int dim = 3;
		BufferedImage img = null;
		int i = 1;
		if(type.equals("topleft") || type.equals("topright"))
			img = leg.getSubimage(dim*1, dim*0, dim, dim);
		if(type.equals("bottomleft") || type.equals("bottomright") ){
			img = leg.getSubimage(dim*2, dim*0, dim, dim);
			i = 2;
		}
		img = rotateXboxLeg(img,i);//miror or rotate based on xbox index one dim leggings
		
		return img;
	}
	public BufferedImage getLegPartSouth(BufferedImage leg, String type) {
		int dim = 3;
		BufferedImage img = null;
		int i = 2;
		if(type.equals("topleft") || type.equals("topright"))
			img = leg.getSubimage(dim*2, dim*0, dim, dim);
		if(type.equals("bottomleft") || type.equals("bottomright") ){
			img = leg.getSubimage(dim*1, dim*0, dim, dim);
			i = 1;
		}
		img = rotateXboxLeg(img,i);//miror or rotate based on xbox index one dim leggings
		
		return img;
	}
	/**
	 * 0 && 3 == null
	 * 1 = north for top and south for bottom
	 * 2 = south for top and north for bottom
	 * 4 = west for left side and east for right side
	 * 6 = east for left side and west for right side
	 * @return rotated or mirrored image based on xbox index note: doesn't rotate top and bottom based on pc leg index that's another method
	 */
	public BufferedImage rotateXboxLeg(BufferedImage img,int i) {
		if(i == 1)
			img = rotate180(img);
		if(i == 2)
			img = mirorVerticle(img);
		if(i == 4)
			img = rotate90Counter(img);
		if(i == 6)
			img = rotate90(img);
		return img;
	}
	public static BufferedImage mirorVerticle(BufferedImage image) {
		BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<img.getHeight();y++)
		{
			for(int x=0;x<img.getWidth();x++)
				img.setRGB(x, (img.getHeight()-1)-y, image.getRGB(x, y) );
		}
		return img;
	}
	public static BufferedImage mirorHorizontal(BufferedImage image){
		BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<img.getHeight();y++)
		{
			for(int x=0;x<img.getWidth();x++)
				img.setRGB( (image.getWidth()-1)-x, y, image.getRGB(x, y) );
		}
		return img;
	}
	public BufferedImage getLegPartTop(BufferedImage leg, String type) {
		int dim = 3;
		BufferedImage img = leg.getSubimage(dim*1, dim*1, dim,dim);//fixed point regardless of type
		img = roatePcTopAndBottom(img,type);
		return img;
	}
	public BufferedImage roatePcTopAndBottom(BufferedImage img, String type) {
		//do nothing for bottom left since rotation is accurate
		if(type.equals("topleft")){
			img = rotate90Counter(img);
		}
		if(type.equals("bottomright")){
			img = rotate90(img);
		}
		if(type.equals("topright")){
			img = rotate180(img);
		}
		return img;
	}
	
	public BufferedImage getLegPartBottom(BufferedImage leg, String type) {
		int dim = 3;
		BufferedImage img = leg.getSubimage(dim*3, dim*1, dim,dim);//fixed point regardless of type
		img = mirorHorizontal(img);
		img = roatePcTopAndBottom(img,type);
		return img;
	}
	public BufferedImage getLegPartWest(BufferedImage leg, String type) {
		int dim = 3;
		BufferedImage img = null;
		int i = 4;
		if(type.equals("topleft") || type.equals("bottomleft"))
			img = leg.getSubimage(dim*0, dim*1, dim, dim);
		if(type.equals("topright") || type.equals("bottomright") ){
			img = leg.getSubimage(dim*2, dim*1, dim, dim);
			i = 6;
		}
		img = rotateXboxLeg(img,i);//miror or rotate based on xbox index one dim leggings
		
		return img;
	}
	
	public BufferedImage getLegPartEast(BufferedImage leg, String type) 
	{	
		int dim = 3;
		BufferedImage img = null;
		int i = 6;
		if(type.equals("topleft") || type.equals("bottomleft"))
			img = leg.getSubimage(dim*2, dim*1, dim, dim);
		if(type.equals("topright") || type.equals("bottomright") ){
			img = leg.getSubimage(dim*0, dim*1, dim, dim);
			i = 4;
		}
		img = rotateXboxLeg(img,i);//miror or rotate based on xbox index one dim leggings
		
		return img;
	}
	public String getXboxLegIndex(int vindex) {
		if(vindex == 0)
			return "bottomleft";
		if(vindex == 1)
			return "topleft";
		if(vindex == 2)
			return "bottomright";
		if(vindex == 3)
			return "topright";
		return null;
	}
	public static BufferedImage rotate90Counter(BufferedImage img)
	{
		return rotate270(img);
	}
	public static BufferedImage rotate270(BufferedImage img)
	{
		return rotate90(rotate180(img));
	}
	public static BufferedImage rotate180(BufferedImage img)
	{
		return rotate90(rotate90(img) );
	}
	public static BufferedImage rotate90(BufferedImage img)
	{
	    int  width  = img.getWidth();
	    int  height = img.getHeight();
	    BufferedImage   newImage = new BufferedImage( height, width, img.getType() );
	 
	    for( int i=0 ; i < width ; i++ )
	        for( int j=0 ; j < height ; j++ )
	            newImage.setRGB( height-1-j, i, img.getRGB(i,j) );
	    return newImage;
	}
	/**
	 * gets bed's leg based on vanilla index
	 * black
	 */
	private int getLegNorth(int i) {
		if(i == 0)
			return 7;
		if(i == 1)
			return 4;
		if(i == 2)
			return 6;
		if(i == 3)
			return 5;
		return -1;
	}
	/**
	 * grey
	 */
	private int getLegSouth(int i) {
		if(i == 0)
			return 5;
		if(i == 1)
			return 6;
		if(i == 2)
			return 4;
		if(i == 3)
			return 7;
		return -1;
	}
	/**
	 * red
	 */
	private int getLegWest(int i){
	
		if(i == 0)
			return 4;
		if(i == 1)
			return 5;
		if(i == 2)
			return 7;
		if(i == 3)
			return 6;
		
		return -1;
	}
	/**
	 * yellow
	 */
	private int getLegEast(int i){
		
		if(i == 0)
			return 6;
		if(i == 1)
			return 7;
		if(i == 2)
			return 5;
		if(i == 3)
			return 4;
		
		return -1;
	}
	private int getLegTop(int unused){return 1;}
	private int getLegBottom(int unused){return 2;}
	
	public void moveImg(BufferedImage origin,int oldx,int oldy,int w,int h, int newX,int newY) {
		moveImg(origin, oldx, oldy, w, h, newX, newY,true);
	}
	
	public void moveImg(BufferedImage origin,int oldx,int oldy,int w,int h, int newX,int newY,boolean delete) {
		BufferedImage sub = FileConverter.copyImage(origin.getSubimage(oldx, oldy, w, h));//bed breakup
		if(delete)
			deletePixles(origin,oldx,oldy,w,h);
		placeImage(origin,sub,newX,newY);
	}
	public void deletePixles(BufferedImage origin, int x, int y, int w, int h) {
		BufferedImage dummy = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		int[] blacks = dummy.getRGB(0, 0, dummy.getWidth(), dummy.getHeight(), null, 0, dummy.getWidth());
		origin.setRGB(x, y, w, h, blacks, 0, dummy.getWidth());//sets current image to white
	}
	public static void placeImage(BufferedImage bed, BufferedImage bedEnd, int x, int y) {
		bed.setRGB(x, y, bedEnd.getWidth(), bedEnd.getHeight(), bedEnd.getRGB(0, 0, bedEnd.getWidth(), bedEnd.getHeight(), null, 0, bedEnd.getWidth() ), 0, bedEnd.getWidth() );
	}
	public HashMap<BufferedImage,String> splitColor(File dir,File shulker, String contains, String actualname,boolean write) {
		List<String> colors = getColors(new File(dir,"colors.txt"),contains);
		HashMap<BufferedImage,String> images = new HashMap();
		for(int i=0;i<16;i++)
		{
			String s = colors.get(i).split("=")[0].substring(contains.length()).toLowerCase();
			if(s.equals("grey"))
				s = "gray";
			if(s.equals("light_green"))
				s = "lime";
			s = actualname + s;
			images.put(FileConverter.splitAndColorImage(shulker, 0, 0, 64, 64, 0, 64, 64, 64, s + ".png",Color.decode("#" + colors.get(i).split("=")[1]),write ),s + ".png");
		}
		return images;
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
		debug.add(shulkertoxbox);
		debug.add(beds);
		debug.add(bedItem);
		debug.add(bed_win10);
		debug.add(zombie);
		debug.add(zombie_villager);
		debug.add(zombieToOld);
		debug.add(boxes);
		debug.add(bedtoXbox);
		
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

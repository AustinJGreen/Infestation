 package infestation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import gameStates.*;

public class Game extends Component implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener 
{
	/*
	 * Static variables
	 */
	private static final long serialVersionUID = 9195031636978305813L;
	
	private Container frame;
	
	private Graphics2D gfx;
	private Renderer renderer;
	private Thread gameThread;
	
	private GameRandom rnd;
	private ArrayList<GameResource> resources;
	private HashMap<String, Bitmap> entities;
	private HashMap<Integer, Pair<Bitmap, Bitmap>> towers;
	private HashMap<String, SoundEffect> soundEffects;
	private HashMap<String, GameState> gameStates;
	
	private HashMap<Integer, Bitmap> tileset;
	
	/*
	 * GameEngine variables
	 */
	private boolean gameRunning = true;
	private int framesPerSecond = 0;
	private int framesCount = 0;
	private long lastTimeStamp = 0;
	private long lastFPSStamp = 0;
	private long elapsed = 0;
	
	/*
	 * Logic oreintated
	 */
	private GameState current;		
	
	/*
	 * Adds the component to the parent frame
	 */
	public void add(Component component)
	{
		frame.add(component);
		frame.validate();
	}
	
	/*
	 * Removes the component from the parent frame
	 */
	public void remove(Component component)
	{
		frame.remove(component);
		frame.validate();
	}
	
	/*
	 * Shows a message box with the specified message
	 */
	public void showMessageBox(String message)
	{
		JOptionPane.showMessageDialog(frame, message);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 * Hooks and handles graphics object
	 */
	@Override
	public void paint(Graphics g)
	{
		this.gfx = (Graphics2D)g;
		
		//Set default rendering settings
		//Set antialiasing on  
		gfx.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Set text antialiasing on
		gfx.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		draw();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * Handles game threading and errors
	 */
	@Override
	public synchronized void run()
	{	
		lastTimeStamp = System.currentTimeMillis();
		lastFPSStamp = System.currentTimeMillis();
		while (gameRunning)
		{
			long currentTimeStamp = System.currentTimeMillis();
			long delta = currentTimeStamp - lastTimeStamp;
			
			int num = (int)Math.round(1000 / 90.0);
			if (delta > num)//FPS control
			{
				lastTimeStamp = currentTimeStamp;
				elapsed = currentTimeStamp - lastFPSStamp;
				if (elapsed >= 1000)
				{
					lastFPSStamp = currentTimeStamp;
					framesPerSecond = framesCount;
					framesCount = 0;
				}
				else
				{
					framesCount++;
				}
				
				update();
				repaint();
			}
		}	
	}
	
	/*
	 * Update game objects
	 */
	public void update()
	{
		current.update();
	}
	
	/*
	 * Draw game objects
	 */	
	public void draw()
	{
		if (gfx != null)
		{   		
			renderer.loadGraphics(gfx);
			
			//Draw drab background color
			Color bg = new Color(167, 181, 150);
			gfx.setColor(bg);
			gfx.fillRect(0, 0, Settings.WIDTH, Settings.HEIGHT);
		
			//Draw screen
			current.draw(renderer);
			renderer.render(gfx);
			
			//Draw debug
			if (Settings.DEBUG > 0)
			{
				gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				gfx.setColor(Color.WHITE);
				Font defualt = new Font("Consolas", Font.PLAIN, 12);
				gfx.setFont(defualt);
				gfx.drawString("FPS = " + framesPerSecond, 10, 10);
				gfx.drawString("S: " + current.getName(), 10, 22);	
				
				long megaBytes = Runtime.getRuntime().totalMemory() / 1048576;
				gfx.drawString("M: " + megaBytes + "mb used." , 10, 34);
				ArrayList<Character> list = Keyboard.getKeysDown();
				String keysDown = "KeysDown = { ";
				for (int i = 0; i < list.size(); i++)
				{
					if (i != list.size() - 1)
					{
						keysDown += list.get(i) + ", ";
					}
					else		
					{
						System.out.println((int)list.get(i));
						keysDown += list.get(i);
					}
				}
				keysDown += " }";
				gfx.drawString(keysDown, 10, 46);
				gfx.drawString("{ X: " + Mouse.x + ", Y: " + Mouse.y + " }", 10, 58);
			}	
		}	
	}
	
	/*
	 * Used for loading resources and initializing objects
	 */
	public void loadResources()
	{			
		//Load entities
		entities.put("cockroach", loadResource(new Bitmap(this, "cockroach.png", 8, 1, 1)));
		entities.put("crawler", loadResource(new Bitmap(this, "crawler.png", 8, 1, 1)));
		entities.put("loading", loadResource(new Bitmap(this, "LoadingCircle.png", 8, 1, 0.25)));
		entities.put("hearts", loadResource(new Bitmap(this, "heart.png")));
		
		//Loads towers
		Bitmap basicTower = loadResource(new Bitmap(this, "basicTower.png"));
		Bitmap basicGun = loadResource(new Bitmap(this, "basicGun.png"));		
		
		Bitmap empTower = loadResource(new Bitmap(this, "empTower.png"));
		Bitmap empTurret = loadResource(new Bitmap(this, "empTurret.png"));
		
		Bitmap flameTower = loadResource(new Bitmap(this, "flameTower.png"));
		Bitmap flameTurret = loadResource(new Bitmap(this, "flameTurret.png"));
		
		towers.put(0, new Pair<Bitmap, Bitmap>(basicTower, basicGun));
		towers.put(1, new Pair<Bitmap, Bitmap>(empTower, empTurret));
		towers.put(2, new Pair<Bitmap, Bitmap>(flameTower, flameTurret));
		
		//Load soundeffects
		soundEffects.put("click", loadResource(new SoundEffect("click.wav")));
		soundEffects.put("music", loadResource(new SoundEffect("8bit.wav")));
		
		for (int i = 0; i < 96; i++)
		{
			tileset.put(i, loadResource(new Bitmap(this, "Tileset0_" + i + ".png")));
		}
		
		//Construct menus after resources are loaded		
		GameState mainMenu = new MainMenu(this);
		GameState editor = new Editor(this);
		GameState levelSelection = new LevelSelection(this);
		GameState multiplayer = new Multiplayer(this);
		
		gameStates.put("MainMenu", mainMenu);
		gameStates.put("Editor", editor);
		gameStates.put("LevelSelection", levelSelection);
		gameStates.put("Multiplayer", multiplayer);
		
		mainMenu.initObjects();
		editor.initObjects();
		levelSelection.initObjects();
		multiplayer.initObjects();
		
		switchStates(mainMenu, null);
	}
	
	/*
	 * Starts the game
	 */
	public void start()
	{
		State state = gameThread.getState();
		if (state == State.NEW)
		{
			loadResources();
			gameThread.start();
		}
	}
	
	/*
	 * Stops the game
	 */
	public void abort()
	{
		if (current != null)
		{
			current.onExit();
		}
		
		unloadResources();
		if (gameThread.getState() == State.RUNNABLE)
		{		
			gameRunning = false;
			gameThread.interrupt();
		}
	}
	
	/*
	 * Loads a resource and adds the resource to an unload queue
	 */
	public <T extends GameResource> T loadResource(T resource) 
	{
		resources.add(resource);
	    resource.load();
	    
	    return resource;
	}
	
	/*
	 * Unloads and removes any objects loaded
	 */
	public void unloadResources()
	{
		while (resources.size() > 0)
		{
			GameResource res = resources.remove(0);
			res.unload();
		}  
	}
	
	/*
	 * Switches the current game state to a new one
	 */
	public void switchStates(GameState newState, GameState reset)
	{
		if (current != null)
		{
			current.onExit();
			if (reset != null)
			{
				reset.dumpObjects();
				reset.initObjects();
			}
		}
		
		current = newState;
		current.onStart();
	}
	
	/*
	 * Returns a randomizer
	 */
	public GameRandom getRandom()
	{
		return rnd;
	}
	
	/*
	 * Gets a map tile id and returns a Level tile
	 */
	public Bitmap getTile(int id)
	{		
		return tileset.get(id);
	}
	
	/*
	 * Gets a tower tile and tower gun specified by an ID
	 */
	public Pair<Bitmap, Bitmap> getTowerData(int towerId)
	{
		return towers.get(towerId);
	}
	
	/*
	 * Gets a entity resource
	 */
	public Bitmap getEntity(String name)
	{
		return entities.get(name);
	}
	
	/*
	 * Gets a sound effect resource
	 */
	public SoundEffect getSoundEffect(String name)
	{
		return soundEffects.get(name);
	}
	
	/*
	 * Gets a game states
	 */
	public GameState getGameState(String name)
	{
		return gameStates.get(name);
	}
	
	/*
	 * Gets the default tileset
	 */
	public HashMap<Integer, Bitmap> getTileset()
	{
		return tileset;
	}
	
	/*
	 * Checks if the tile exits
	 */
	public boolean validateTile(int tileId)
	{
		return tileset.containsKey(tileId);
	}
	
	/*
	 * Gets last elapsed time
	 */
	public double getElapsed()
	{
		return elapsed;
	}
	
	/*
	 * Gets the game time
	 */
	public long getGametime()
	{
		return lastTimeStamp;
	}
	
	/*
	 * Sets the parent frame
	 */
	public void setContainer(Container frame)
	{
		this.frame = frame;
	}
	
	/*
	 * Gets the current GameState
	 */
	public GameState getCurrent()
	{
		return current;
	}
	
	/*
	 * Gets number of maps saved locally
	 */
	public static int getMaps()
	{	
		String resourcePath = System.getProperty("user.dir");
		String filePath = resourcePath + "//src//res//";
		File directory = new File(filePath);
		String[] files = directory.list();
		int count = 0;
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].endsWith(".txt"))
			{
				count++;
			}
		}
		
		return count;
	}

	
	/*
	 * Game constructor
	 * codeBase - the applications code base
	 */
	public Game()
	{		
		this.rnd = new GameRandom();
		this.resources = new ArrayList<GameResource>();
		this.entities = new HashMap<String, Bitmap>();
		this.towers = new HashMap<Integer, Pair<Bitmap, Bitmap>>();
		this.soundEffects = new HashMap<String, SoundEffect>();
		this.gameStates = new HashMap<String, GameState>();
		this.tileset = new HashMap<Integer, Bitmap>();
		this.renderer = new Renderer();
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		gameThread = new Thread(this);
		gameThread.setName("Infestation");
	}

	/*
	 * Game engine mouse and keyboard handling
	 */
	@Override
	public void keyPressed(KeyEvent e) 
	{
		keyTyped(e);
	}
	@Override
	public void keyReleased(KeyEvent e) 
	{
		keyTyped(e);
	}
	@Override
	public void keyTyped(KeyEvent e) 
	{
		switch (e.getID())
		{
		case KeyEvent.KEY_PRESSED:
			Keyboard.setDown(e.getKeyChar());
			break;
		case KeyEvent.KEY_RELEASED:
			Keyboard.setUp(e.getKeyChar());
			break;
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		Mouse.handleEvent(e);
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		Mouse.handleEvent(e);
	}
	@Override
	public void mouseExited(MouseEvent e) {
		Mouse.handleEvent(e);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		Mouse.handleEvent(e);
		Mouse.setDown(true);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		Mouse.handleEvent(e);
		Mouse.setDown(false);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Mouse.handleEvent(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		Mouse.handleEvent(e);	
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Mouse.handleEvent(e);
	}
}

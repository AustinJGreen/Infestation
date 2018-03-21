package infestation;

import gameStates.GamePaused;
import gui.TileOverlay;
import gui.TowerScroller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import protocol.UpdatedTile;
import towers.*;

/*
 * Logical handing for the game
 */
public class Level implements GameObject
{	
	private GameState gameplayState;
	
	protected long start = -1;
	protected long elapsed = 0;
	protected long generatorSeed = -1;
	
	protected int lives = 200;
	protected int playerMoney = 2000;
	protected int balance = playerMoney;
	protected int selectedTower = -1;
	
	protected boolean playing = false;
	protected boolean paused = false;
	protected boolean Mdown = false;
	protected boolean Pdown = false;
	protected boolean built = false;    
	
	protected Map map;
	protected Game game;
	protected Wave[] waves;
	protected Tile[][] level;
	protected TileOverlay selection;
	protected TowerScroller scroller;
	protected LevelViewport viewport;
	protected ArrayList<Entity> entities;
	protected ArrayList<EntityPath> paths;
	
	/*
	 * Draws the level using the specified graphics
	 */
	public void draw(Renderer renderer)
	{		
		if (!built)
		{
			return;
		}
		
		for (int ty = 0; ty < getMapHeight(); ty++)
		{
			for (int tx = 0; tx < getMapWidth(); tx++)
			{			
				Tile tile = getTile(tx, ty);
				if (tile != null)
				{		
					tile.draw(renderer);
				}
			}
		}
		
		if (!paused && playing)
		{
			renderer.draw(selection);
			renderer.draw(scroller);
			if (waves != null)
			{
				for (int i = 0; i < waves.length; i++)
				{
					Wave wave = waves[i];
					if (wave.isRegistered())
					{
						wave.draw(renderer);
					}
				}
			}
			
			Graphics2D gfx = renderer.getGraphics();
			gfx.setColor(Color.BLACK);
			gfx.setFont(new Font("Copperplate Gothic Light", Font.BOLD, 24));
			gfx.drawString("$" + balance, 10, 24);
			
			Bitmap hearts = game.getEntity("hearts");
			hearts.draw(gfx, new Rectangle(10, 35, hearts.getWidth(), hearts.getHeight()), 0);
			String livesLabel = ((Integer)lives).toString();
			gfx.setColor(Color.RED);
			gfx.drawString(livesLabel, 60, 60);
		}
	}
	
	/*
	 * Builds the level and constructs the tiles
	 */
	public Level build()
	{
		built = true;
		for (int y = 0; y < getMapHeight(); y++)
		{
			for (int x = 0; x < getMapWidth(); x++)
			{
				MetaData data = map.getData(x, y);	
				
				Tile tile = new Tile(game, this, x, y);	
				if (data.hasTowerData()) //Create tower
				{
					data.TowerID = 1;
					ParticleTower tower = new ParticleTower(game, this, x, y);
					tile = tower;
				}					
			
				//Set tile
				level[y][x] = tile;
			}			
		}
		
		ArrayList<Tile> spawns = new ArrayList<Tile>();	
		for (int x = 0; x < getMapWidth(); x++)
		{
			for (int y = 0; y < getMapHeight(); y++)
			{
				Tile tile = getTile(x, y);
				MetaData cur = tile.getMetaData();
				if (cur != null)
				{
					if ((cur.TileModifiers & MetaData.TILE_SPAWN) != 0)
					{
						spawns.add(tile);
					}
				}
			}
		}
		
		ArrayList<Tile> marked = new ArrayList<Tile>();
		ArrayList<ArrayList<Tile>> checkpoints = getCheckpoints(spawns, marked);
		
		Random rnd = new Random(generatorSeed);
		
		int pathId = 0;
		paths = new ArrayList<EntityPath>();
		for (int i = 0; i < spawns.size(); i++, pathId++)
		{
			Tile spawn = spawns.get(i);
			ArrayList<Tile> checkpointSegments = new ArrayList<Tile>();
			for (int j = 0; j < checkpoints.size(); j++)
			{
				ArrayList<Tile> cur = checkpoints.get(j);
				Tile check = cur.get(rnd.nextInt(cur.size()));
				checkpointSegments.add(check);
			}
			
			Tile last = checkpointSegments.remove(0);
			ArrayList<Tile> previous = new ArrayList<Tile>();
			EntityPath original = constructPath(spawn, last, previous);		
			while (!checkpointSegments.isEmpty())
			{
				Tile next = checkpointSegments.remove(0);
				original.add(constructPath(last, next, previous));
				last = next;
			}		
			original.setId(pathId);		
			paths.add(original);
		}
		
		return this;
	}
	
	/*
	 * Gets illegal adjacent tiles
	 */
	private ArrayList<Tile> getAdjacent(Tile tile)
	{
		ArrayList<Tile> adjacent = new ArrayList<Tile>();
		adjacent.add(getTile(tile.getTileX() + 1, tile.getTileY()));
		adjacent.add(getTile(tile.getTileX() + 1, tile.getTileY() + 1));
		adjacent.add(getTile(tile.getTileX() + 1, tile.getTileY() - 1));
		adjacent.add(getTile(tile.getTileX() - 1, tile.getTileY()));
		adjacent.add(getTile(tile.getTileX() - 1, tile.getTileY() + 1));
		adjacent.add(getTile(tile.getTileX() - 1, tile.getTileY() - 1));
		adjacent.add(getTile(tile.getTileX(), tile.getTileY() + 1));
		adjacent.add(getTile(tile.getTileX(), tile.getTileY() - 1));
		return adjacent;
	}
	
	/*
	 * Searches an arraylist for a tile
	 */
	private Tile contains(ArrayList<Tile> traveled, Tile n)
	{
		for (Tile t : traveled)
		{
			if (t.getTileX() == n.getTileX() && t.getTileY() == n.getTileY())
			{
				return t;
			}
		}
		return null;
	}
	
	/*
	 * Gets the level's checkpoints checkpoints in occuring order (recursive)
	 */
	private ArrayList<ArrayList<Tile>> getCheckpoints(ArrayList<Tile> start, ArrayList<Tile> marked)
	{
		ArrayList<ArrayList<Tile>> levels = new ArrayList<ArrayList<Tile>>();
		
		ArrayList<Tile> checkpoints = new ArrayList<Tile>();	
		for (int i = 0; i < start.size(); i++)
		{		
			Tile curStart = start.get(i);
			
			ArrayList<Tile> path = new ArrayList<Tile>();
			path.add(curStart);
					
			while (!path.isEmpty())
			{
				Tile current = path.remove(0);
				for (Tile tile : getAdjacent(current))
				{
					if (tile != null)
					{
						MetaData tdata = tile.getMetaData();
						Tile matched = contains(marked, tile);
						if (matched == null)
						{
							if ((tdata.TileModifiers & MetaData.TILE_CHECKPOINT) == 0)
							{
								if ((tdata.TileModifiers & MetaData.TILE_WALKABLE) != 0)
								{
									path.add(tile);
									marked.add(tile);
								}
							}
							else
							{
								Tile existingCheckpoint = contains(checkpoints, tile);
								Tile existingCheckpoint2 = contains(start, tile);
								if (existingCheckpoint == null && existingCheckpoint2 == null)
								{
									checkpoints.add(tile);
								}
							}
						}
					}
				}
			}
		}
				
		if (!checkpoints.isEmpty())
		{
			levels.add(checkpoints);
			ArrayList<ArrayList<Tile>> recur = getCheckpoints(checkpoints, marked);
			for (ArrayList<Tile> collection : recur)
			{
				levels.add(collection);
			}
		}
		
		return levels;
	}
	
	/*
	 * Creates a entity path draft
	 */
	private EntityPath constructPath(Tile start, Tile end, ArrayList<Tile> previous)
	{	
		//Fill walkway and make a path with values indicating distance				
		//Next, fill the path
		ArrayList<Node> path = new ArrayList<Node>();
	
		ArrayList<Tile> marked = new ArrayList<Tile>();		
		
		int startdis = (int)Math.sqrt(Math.pow(start.getTileX() - end.getTileX(), 2) + Math.pow(start.getTileY() - end.getTileY(), 2));
		Node startNode = new Node(start, startdis);
		EntityPath entitypath = new EntityPath(game, startNode, end);
		path.add(startNode);
		marked.add(startNode.getTile());
		entitypath.add(startNode);
		
		while (!path.isEmpty())
		{			
			//Remove current node
			Node current = path.remove(0);	
			
			for (Tile tile : getAdjacent(current.getTile()))
			{
				if (tile != null)
				{
					MetaData tdata = tile.getMetaData();
					
					Tile matched = contains(marked, tile);
					Tile matched2 = contains(previous, tile);
					if (matched == null && matched2 == null && (tdata.TileModifiers & MetaData.TILE_WALKABLE) != 0)
					{					
						int dis = (int)Math.ceil(Math.sqrt(Math.pow(tile.getTileX() - end.getTileX(), 2) + Math.pow(tile.getTileY() - end.getTileY(), 2)));
						
						Node branch = new Node(tile, dis);
						
						//Add to queue
						path.add(branch);
						
						//Add marked
						marked.add(tile);	
						
						//Add to previous
						previous.add(tile);
						
						//Add to entity path
						entitypath.add(branch);
					}
				}
			}
		}
		
		entitypath.add(new Node(end, 0));
		return entitypath;
	}
	
	/*
	 * Gets a random path that was constructed
	 */
	public EntityPath getPath(Random rnd)
	{
		int i = rnd.nextInt(paths.size());
		return new EntityPath(paths.get(i));
	}
	
	/*
	 * Loads waves of enemies for the level using the map's wave data
	 */
	public void generateWaves()
	{		
		if (generatorSeed == -1)
		{	
			Random seedRnd = game.getRandom();	
			generatorSeed = seedRnd.nextInt();
		}
		
		System.out.println("Generating with seed: " + generatorSeed);
		
		Random rnd = new Random(generatorSeed);
		
		LevelWaveData dat = map.getLevelWaveData();
		int levelTime = dat.getTime();
		int count = dat.getCount();
		int[] mobs = dat.getMobs();
		double timeEach = levelTime / (double)count;
		double spread = mobs.length / (double)count;

		long lastTime = 0;
		waves = new Wave[count];
		for (int i = 0; i < waves.length; i++)
		{
			int mob = (int)(i * spread);
			int cnt = 1 + (int)Math.floor((1 + rnd.nextDouble()) * i) / (mob + 1);
			long time = (long)(lastTime + timeEach);
			long breakTime = 1000 + rnd.nextInt(10000);
			long startTime = time + breakTime;
			waves[i] = new Wave(this, mobs[mob], cnt, startTime, generatorSeed).create();
			lastTime = startTime;
			
		}
	}

	/*
	 * Gets the current entities
	 */
	public List<Entity> getEntities()
	{
		return entities;
	}
	
	/*
	 * Updates all logic in the level
	 */
	public void update()
	{
		if (!paused)
		{
			if (start == -1)
			{
				start = System.currentTimeMillis();
			}
			
			elapsed = System.currentTimeMillis() - start;	
			selection.update();
			scroller.update();
			
			if (balance > playerMoney)
			{			
				balance -= 5;
				if (balance < playerMoney)
					balance = playerMoney;
			}
			else if (balance < playerMoney)
			{
				balance += 5;
				if (balance > playerMoney)
					balance = playerMoney;
			}
			
			if (Mdown && !Mouse.isDown() && !paused && playing && !scroller.hasFocus())
			{
				onClick();				
			}
			
			for (int x = 0; x < getMapWidth(); x++)
			{
				for (int y = 0; y < getMapHeight(); y++)
				{	
					level[y][x].update();
				}			
			}		
			
			if (waves != null)
			{
				for (int i = 0; i < waves.length; i++)
				{
					Wave wave = waves[i];
					if (wave.isActive(elapsed))
					{
						if (!wave.isRegistered())
						{
							wave.register(entities);
						}
						
						wave.update();
					}
				}	
			}
		}
		
		if (Pdown && !Keyboard.isDown((char)27) && playing)
		{
			paused = !paused;
			if (paused)
			{
				gameplayState = game.getCurrent();
				
				GamePaused pauseScreen = new GamePaused(this);
				pauseScreen.initObjects();
				
				game.switchStates(pauseScreen, null);
			}
			else
			{
				game.switchStates(gameplayState, null);
			}
		}
		
		Mdown = Mouse.isDown();
		Pdown = Keyboard.isDown((char)27);
	}
	
	/*
	 * Handles anything that happens on a click
	 */
	protected void onClick()
	{ 
		if (selectedTower != -1)
		{
			TowerInfo info = Tower.getInfo(selectedTower);
			if (playerMoney >= info.getCost())
			{			
				int mx = Mouse.x - viewport.getCamX();
				int my = Mouse.y - viewport.getCamY();
				
				int tileWidth = viewport.getCamWidth() / getMapWidth();
				int tileHeight = viewport.getCamHeight() / getMapHeight();
				
				int tileX = mx / tileWidth;
				int tileY = my / tileHeight;
				
				Tile current = level[tileY][tileX];
				MetaData data = current.getMetaData();	
				if ((data.TileModifiers & MetaData.TILE_NORMAL) != 0 && !data.hasTowerData())
				{		
					playerMoney -= info.getCost();
					level[tileY][tileX] = Tower.fromID(game, this, tileX, tileY, selectedTower);
					level[tileY][tileX].setIsSynchronized(false);
				}
			}
		}
	}
	
	/*
	 * Gets the amount of tiles horizontally
	 */
	public int getMapWidth()
	{
		return map.getWidth();
	}
	
	/*
	 * Gets the amount of tiles vertically
	 */
	public int getMapHeight()
	{
		
		return map.getHeight();
	}

	/*
	 * Updates a tile in the level
	 */
	protected void updateTile(UpdatedTile tile)
	{
		int x = tile.tx;
		int y = tile.ty;
		MetaData data = tile.data;
		map.overrideData(x, y, data);
		if (data.hasTowerData())
		{
			Tower tower = Tower.fromID(game, this, x, y, data.TowerID);
			if (tower != null)
			{
				level[y][x] = tower;
			}
		}
		else
		{
			level[y][x] = new Tile(game, this, tile.tx, tile.ty);
		}
	}
	
	/*
	 * Returns whether the tile coordinate is within
	 * the bounds of the level
	 */
	public boolean isTileCoordinate(int tileX, int tileY)
	{
		return tileX >= 0 && tileX < getMapWidth() && tileY >= 0 && tileY < getMapHeight();
	}
	
	/*
	 * Gets the tile at a specified grid coordinate
	 */
 	public Tile getTile(int tileX, int tileY)
	{
		if (isTileCoordinate(tileX, tileY))
		{
			return level[tileY][tileX];
		}
		return null;
	}
	
	/*
	 * Gets the tile data at a specified grid coordinate
	 */
	public MetaData getTileData(int tileX, int tileY)
	{
		return map.getData(tileX, tileY);
	}
	
	/*
	 * Gets the tile ID at a specified grid coordinate
	 */
	public int getTileId(int tileX, int tileY)
	{
		MetaData data = map.getData(tileX, tileY);
		return (data == null) ? -1 : data.TileID;
	}
	
	/*
	 * Sets the selected tower
	 */
	public void setSelected(int id)
	{
		this.selectedTower = id;
	}
	
	/*
	 * Sets the level viewport
	 */
	public void setViewport(LevelViewport viewport)
	{
		this.viewport = viewport;
	}
	
	/*
	 * Gets the level viewport
	 */
	public LevelViewport getViewport()
	{
		return viewport;
	}

	/*
	 * Sets whether the user is playing the level
	 */
	public void setPlaying(boolean playing)
	{
		this.playing = playing;
	}
	
	/*
	 * Subtracts a life
	 */
	public void subtractLife()
	{
		lives--;
	}
	
	/*
	 * Gives the player money
	 */
	public void awardMoney(int amount)
	{
		playerMoney += amount;
	}
	
	/*
	 * Gets the level's map filename-
	 */
	public String getName()
	{
		return map.getName();
	}
	
	/*
	 * Returns the referenced game object
	 */
	public Game getGame()
	{
		return this.game;
	}
	
	/*
	 * Constructs a game
	 * map - the map file to use for tiles
	 * game - the game engine
	 */
	public Level(Game game, Map map, LevelViewport viewport)
	{
		this.level = new Tile[map.getHeight()][map.getWidth()];
		this.game = game;
		this.map = map;
		this.viewport = viewport;
		
		selection = new TileOverlay(this);
		scroller = new TowerScroller(this, 30);
		entities = new ArrayList<Entity>();
	}
	
	protected Level(Game game, LevelViewport viewport)
	{
		this.game = game;
		this.viewport = viewport;
		
		selection = new TileOverlay(this);
		scroller = new TowerScroller(this, 30);
		entities = new ArrayList<Entity>();
	}
}

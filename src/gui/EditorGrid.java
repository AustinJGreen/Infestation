package gui;

import infestation.Bitmap;
import infestation.Game;
import infestation.Keyboard;
import infestation.Map;
import infestation.MetaData;
import infestation.Mouse;
import infestation.Renderable;
import infestation.Renderer;
import infestation.Settings;
import infestation.Updateable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class EditorGrid implements Renderable, Updateable {

	private int x, y;
	private int width, height;
	private int mapWidth, mapHeight;
	private int tileIDSelected = 0;
	
	private HashMap<Integer, Bitmap> tileset;
	
	private String existingMap;
	private boolean editingExisting = false;
	
	private boolean Sdown;	//Saves
	private boolean Qdown;  //Toggles walkmap mode
	private boolean Tdown;	//Toggles walkmap path setting
	private boolean Bdown;  //Toggles monster beginning path
	private boolean Edown;  //Toggles monster end path
	private boolean Ndown;  //Toggles non-constuctable areas (water)
	private boolean Fdown;  //If held down clicks will fill area
	private boolean Cdown;  //Toggles checkpoints
	
	private boolean walkmapMode = false;
	private int currentTileModifier = MetaData.TILE_WALKABLE;
	
	private MetaData[] map;
	
	@Override
	public void draw(Graphics2D gfx) 
	{			
		//Draw bounds
		final int thickness = 5;
		
		gfx.setStroke(new BasicStroke(thickness));
		gfx.setColor(Color.BLACK);
		gfx.drawRect(x - (thickness / 2), y - (thickness / 2), width + (thickness / 2) + 1, height + (thickness / 2) + 1);
		
		//Draw tiles
		int tileWidth = width / mapWidth;
		int tileHeight = height / mapHeight;
		
		gfx.setStroke(new BasicStroke(0.5f));
		for (int tx = 0; tx < mapWidth; tx++)
		{
			for (int ty = 0; ty < mapHeight; ty++)
			{
				int tileIndex = (ty * mapWidth) + tx;
				MetaData meta = map[tileIndex];
				int tileId = meta.TileID;
				if (tileId == -1)
				{
					gfx.setColor(Color.BLACK);
					gfx.drawRect(x + (tx * tileWidth), y + (ty * tileHeight), tileWidth, tileHeight);
				}
				else
				{
					Bitmap tile = tileset.get(tileId);
					if (tile != null)
					{
						tile.draw(gfx, new Rectangle(x + (tx * tileWidth), y + (ty * tileHeight), tileWidth, tileHeight), 0);
					}
					else
					{
						System.out.println(tileId);
					}
				}
				

				if ((meta.TileModifiers & MetaData.TILE_SPAWN) != 0)
				{
					Color spawn = new Color(0, 255, 0, 100);
					gfx.setColor(spawn);
				}
				else if ((meta.TileModifiers & MetaData.TILE_END) != 0)
				{
					Color end = new Color(255, 0, 0, 100);
					gfx.setColor(end);
				}
				else if ((meta.TileModifiers & MetaData.TILE_CHECKPOINT) != 0)
				{
					Color checkpoint = new Color(0, 100, 255, 100);
					gfx.setColor(checkpoint);
				}
				else if ((meta.TileModifiers & MetaData.TILE_WALKABLE) != 0)
				{
					Color walkable = new Color(255, 255, 100, 100);
					gfx.setColor(walkable);
				}
				else if ((meta.TileModifiers & MetaData.TILE_NO_CONSTRUCTION) != 0)
				{
					Color nonconstuct = new Color(255, 100, 0, 100);
					gfx.setColor(nonconstuct);
				}
				else
				{
					Color notwalkable = new Color(100, 0, 255, 100);
					gfx.setColor(notwalkable);			
				}
				
				if (walkmapMode)
				{
					//if (tx == 49 && ty == 9)
					//	gfx.setColor(Color.BLACK);
					gfx.fillRect(x + (tx * tileWidth), y + (ty * tileHeight), tileWidth, tileHeight);
				}
			}
		}	
	}

	@Override
	public int getRenderMode() 
	{
		return Renderer.QUEUE_NORMAL;
	}
	
	@Override
	public void update() 
	{	
		//If middle click, sample tile from map
		if (Mouse.button == 2)
		{
			int index = getTileIndex();
			if (index >= 0 && index < map.length)
			{
				setTileSelected(map[index].TileID);
			}
		}
		else if (Mouse.isDown() && mouseInGrid())
		{
			int index = getTileIndex();
			if (index >= 0 && index < map.length)
			{
				if (walkmapMode)
				{
					if (Fdown)
					{
						int[] fill = getFill();
						for (int i = 0; i < fill.length; i++)
						{
							map[fill[i]].TileModifiers = currentTileModifier;
						}
					}
					else
					{
						map[index].TileModifiers = currentTileModifier;
					}
				}
				else
				{
					if (Fdown)
					{
						int[] fill = getFill();
						for (int i = 0; i < fill.length; i++)
						{
							map[fill[i]].TileID = tileIDSelected;
						}
					}
					else
					{
						//Standard drawing
						map[index].TileID = tileIDSelected;
					}
				}
			}
		}	
		
		if (Sdown && !Keyboard.isDown('s'))
		{
			if (!Settings.ISAPPLET)
			{
				if (editingExisting)
				{
					System.out.println("Saving...");
					saveAs(existingMap);
					System.out.println("Saved.");
				}
				else
				{
					//Change to message box eventually		
					System.out.println("Saving...");
					int mapCreated = Game.getMaps() + 1;
					saveAs("EditorMap_" + mapCreated + ".txt");
					System.out.println("Saved.");
				}
			}
			else
			{			
				System.out.println("Copying to clipboard...");
				StringBuilder data = new StringBuilder();
			    data.append(mapWidth + "," + mapHeight);
			    data.append("\n");
			    for (int i = 0; i < map.length; i++)
			    {
			    	int rem = i % mapWidth;
			    	if (rem != mapWidth - 1)
			    	{
			    		data.append(map[i].dataFormat() + ",");
			    	}
			    	else
			    	{
			    		data.append(map[i].dataFormat() + "\n");
			    	}
			    }
			    
			    StringSelection stringSelection = new StringSelection(data.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				System.out.println("Copied to clipboard.");
			}
		}
		
		if(Qdown && !Keyboard.isDown('q'))
		{
			walkmapMode = !walkmapMode;
		}
		
		if (Tdown && !Keyboard.isDown('t'))
		{
			currentTileModifier = (currentTileModifier == MetaData.TILE_NORMAL) ? MetaData.TILE_WALKABLE : MetaData.TILE_NORMAL;
		}
		
		if (Bdown && !Keyboard.isDown('b'))
		{
			currentTileModifier = MetaData.TILE_SPAWN | MetaData.TILE_WALKABLE;		
		}
		
		if (Edown && !Keyboard.isDown('e'))
		{
			currentTileModifier = MetaData.TILE_END | MetaData.TILE_CHECKPOINT | MetaData.TILE_WALKABLE;
		}
		
		if (Ndown && !Keyboard.isDown('n'))
		{
			currentTileModifier = MetaData.TILE_NO_CONSTRUCTION;
		}
		
		if (Cdown && !Keyboard.isDown('c'))
		{
			currentTileModifier = MetaData.TILE_CHECKPOINT;
		}
		
		Sdown = Keyboard.isDown('s');
		Qdown = Keyboard.isDown('q');
		Tdown = Keyboard.isDown('t');
		Bdown = Keyboard.isDown('b');
		Edown = Keyboard.isDown('e');
		Ndown = Keyboard.isDown('n');
		Fdown = Keyboard.isDown('f');
		Cdown = Keyboard.isDown('c');
	}
	
	private int[] getFill()
	{
		//4-way fill
		int index = getTileIndex();
		int id = map[index].TileID;
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		ArrayList<Integer> fill = new ArrayList<Integer>();

		nodes.add(index);
		while (!nodes.isEmpty())
		{
			int cur = nodes.remove(0);
			fill.add(cur);
			
			int left = cur - 1;
			int right = cur + 1;
			int up = cur - mapWidth;
			int down = cur + mapWidth;
			
			if (left >= 0 && left < map.length && !fill.contains(left) && !nodes.contains(left))
			{
				if (map[left].TileID == id)
				{
					nodes.add(left);
				}
			}
			
			if (right >= 0 && right < map.length && !fill.contains(right) && !nodes.contains(right))
			{
				if (map[right].TileID == id)
				{
					nodes.add(right);
				}
			}
			
			if (up >= 0 && up < map.length && !fill.contains(up) && !nodes.contains(up))
			{
				if (map[up].TileID == id)
				{
					nodes.add(up);
				}
			}
			
			if (down >= 0 && down < map.length && !fill.contains(down) && !nodes.contains(down))
			{
				if (map[down].TileID == id)
				{
					nodes.add(down);
				}
			}
		}
		
		int[] fillArea = new int[fill.size()];
		for (int i = 0; i < fill.size(); i++)
		{
			fillArea[i] = fill.get(i);
		}
		
		return fillArea;
	}
	
	public void setTileSelected(int tileID)
	{
		tileIDSelected = tileID;
	}
	
	public int getTileSelected()
	{
		return tileIDSelected;
	}
	
	public HashMap<Integer, Bitmap> getTileset()
	{
		return tileset;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	private int getTileIndex()
	{
		int mx = Mouse.x - x;
		int my = Mouse.y - y;
		
		int tileWidth = width / mapWidth;
		int tileHeight = height / mapHeight;
		
		int tileX = mx / tileWidth;
		int tileY = my / tileHeight;
		
		return (tileY * mapWidth) + tileX;		
	}
	
	private boolean mouseInGrid()  
	{
		return Mouse.x >= x && Mouse.x < x + width && Mouse.y >= y && Mouse.y < y + height;
	}
	
	public void addTileset(HashMap<Integer, Bitmap> tileset)
	{
		this.tileset = tileset;
	}
	
	public void saveAs(String filename)
	{
		String resourcePath = System.getProperty("user.dir");
		String filePath = resourcePath + "//src//res//" + filename;
		
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(filePath, "UTF-8");
		    writer.println(mapWidth + "," + mapHeight + ",300,55,{0:1}");
		    for (int i = 0; i < map.length; i++)
		    {
		    	int rem = i % mapWidth;
		    	if (rem != mapWidth - 1)
		    	{
		    		writer.write(map[i].dataFormat() + ",");
		    	}
		    	else
		    	{
		    		writer.write(map[i].dataFormat());
		    		writer.println();
		    	}
		    }

		} 
		catch (Exception e) 
		{  
			e.printStackTrace();
		}
		finally
		{
		    try
		    {
		        if (writer != null)
		        {
		        	writer.close();
		        }
		    }
		    catch (Exception ex)
		    {
		    	ex.printStackTrace();
		    }
		}
	}
	
	public EditorGrid(int marginX, int marginY, int mapWidth, int mapHeight, String template, boolean editing)
	{
		this.x = marginX;
		this.y = marginY;
		this.width = Settings.WIDTH - (marginX * 2);
		this.height = Settings.HEIGHT - (marginY * 2);
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		Map mapdata = new Map(template);
		mapdata.load();
		this.map = new MetaData[mapWidth * mapHeight];
		for (int i = 0; i < map.length; i++)
		{
			int x = i % 50;
			int y = i / 50;
			map[i] = mapdata.getData(x, y);
		}
		
		this.existingMap = template;
		editingExisting = editing;
	}
}

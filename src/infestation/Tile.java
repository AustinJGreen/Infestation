package infestation;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/*
 * Base class for individual tile bitmap
 */
public class Tile implements GameObject
{
	/*
	 * Private fields to prevent uncontrolled handling in other Tile classes
	 */	
	protected int tileX, tileY;
	protected float rotation;
	protected boolean isSynchronized = true;
	
	protected Game game;
	protected Level level;
	protected Bitmap texture;
	protected MetaData data;
		
	/*
	 * Get the tile's meta data
	 */
	public MetaData getMetaData()
	{
		return data;
	}
	
	/*
	 * Sets the tile's meta data
	 */
	public void setMetaData(MetaData data)
	{
		this.data = data;
	}
	
	/*
	 * Sets if the tile is synchronized with clients
	 */
	public void setIsSynchronized(boolean value)
	{
		isSynchronized = value;
	}
	
	/*
	 * Gets if the tile is synchronized with clients
	 */
	public boolean isSynchronized()
	{
		return isSynchronized;
	}
	
	/*
	 * Sets the tiles id
	 */
	protected void setTileId(int id)
	{
		this.texture = game.getTile(id);
		this.data.TileID = id;
	}
	
	/*
	 * Gets the tile's buffer
	 */
	protected Rectangle2D getBuffer()
	{
		LevelViewport viewport = level.getViewport();
		
		double tileWidth = viewport.getCamWidth() / (double)level.getMapWidth();
		double tileHeight = viewport.getCamHeight() / (double)level.getMapHeight();
		
		return new Rectangle2D.Double(viewport.getCamX() + (tileX * tileWidth), viewport.getCamY() + (tileY * tileHeight), tileWidth, tileHeight);
	}
	
	/*
	 * Draw the tile at the tile's coordinate using the 
	 * specified graphics
	 */
	public void draw(Renderer renderer)
	{
		if (texture != null)
		{
			Graphics2D gfx = renderer.getGraphics();
			
			Rectangle2D tileBuffer = getBuffer();
			texture.draw(gfx, tileBuffer, rotation);
		}
	}
	
	/*
	 * Updates the tiles data and bitmap
	 */
	public void update()
	{
		this.texture.update();
	}
	
	/*
	 * Set the amount of 90 degree rotations around the
	 * center of the tile
	 */
	protected void setRotation(int dir)
	{
		dir %= 4;
		rotation = dir * 90;
	}
	
	/*
	 * Get the tile's X coordinate
	 */
	public int getTileX()
	{
		return tileX;
	}
	
	/*
	 * Get the tile's Y coordinate
	 */
	public int getTileY()
	{
		return tileY;
	}
	
	/*
	 * Virtual function for extended tiles
	 */
	public void construct(Level level) {}
	
	
	public boolean isSameAs(Tile t)
	{
		return tileX == t.getTileX() && tileY == t.getTileY();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "(" + tileX + ", " + tileY + ")";
	}
	
	/*
	 * Tile constructor
	 * texture - The bitmap to be used as the background
	 * id - the tile id
	 * tileX - the tile coordinate on the x axis
	 * tileY - the tile coordinate on the y axis
	 * resolution - the resolution in pixels to draw the tile
	 */
	public Tile(Game game, Level level, int tileX, int tileY) 
	{
		this.game = game;
		this.level = level;
		this.data = level.getTileData(tileX, tileY);
		this.texture = game.getTile(data.TileID);
		this.tileX = tileX;
		this.tileY = tileY;
	}
}

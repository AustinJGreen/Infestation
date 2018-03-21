package gui;

import java.awt.Color;
import java.awt.Graphics2D;

import infestation.Level;
import infestation.LevelViewport;
import infestation.MetaData;
import infestation.Mouse;
import infestation.Renderable;
import infestation.Renderer;
import infestation.Updateable;

public class TileOverlay implements Renderable, Updateable {

	private final Color valid = new Color(200, 200, 200, 100);
	private final Color invalid = new Color(200, 0, 0, 100);
	
	private Color overlay;
	private Level level;
	
	private int tileX, tileY;
	
	@Override
	public void update() {		
		LevelViewport viewport = level.getViewport();
		
		int mx = Mouse.x - viewport.getCamX();
		int my = Mouse.y - viewport.getCamY();
		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		tileX = mx / tileWidth;
		tileY = my / tileHeight;
		
		MetaData data = level.getTileData(tileX, tileY);
		if (data == null)
			return;
		overlay = ((data.TileModifiers & MetaData.TILE_NORMAL) != 0) ? valid : invalid;
	}

	@Override
	public void draw(Graphics2D gfx) {
		LevelViewport viewport = level.getViewport();
		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		gfx.setColor(overlay);
		gfx.fillRect(tileX * tileWidth, tileY * tileHeight, tileWidth, tileHeight);
	}

	@Override
	public int getRenderMode() {
		return Renderer.QUEUE_NORMAL;
	}
	
	public TileOverlay(Level level)
	{
		this.level = level;
		
		overlay = valid;
	}
}

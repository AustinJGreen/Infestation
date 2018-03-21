package gui;

import infestation.Bitmap;
import infestation.Keyboard;
import infestation.Mouse;
import infestation.Renderable;
import infestation.Renderer;
import infestation.Updateable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

public class EditorHotbar implements Renderable, Updateable
{
	private EditorGrid grid;
	private int[] hotbar;
	
	private boolean Hdown;
	private boolean Mdown;
	
	@Override
	public void draw(Graphics2D gfx) 
	{
		HashMap<Integer, Bitmap> tileset = grid.getTileset();
		int x = grid.getX();
		int y = grid.getY() + grid.getHeight() + 10;
		int width = grid.getWidth();
		
		final int boxwidth = 50;
		final int boxheight = 50;
		int sx = x + width - (hotbar.length * boxwidth) + 2;
		
		gfx.setColor(Color.BLACK);
		gfx.setStroke(new BasicStroke(3));
		for (int i = 0; i < hotbar.length; i++)
		{
			int tileId = hotbar[i];
			gfx.drawRect(sx + (i * boxwidth), y, boxwidth, boxheight);
			if (tileId != -1)
			{
				Bitmap tile = tileset.get(tileId);
				tile.draw(gfx, new Rectangle(sx + (i * boxwidth), y, boxwidth, boxheight), 0);
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
		if (Mdown && !Mouse.isDown() && mouseInHotbar())
		{
			int index = getHotbarIndex();
			if (index >= 0 && index < hotbar.length)
			{
				int tile = hotbar[index];
				if (tile != -1)
				{
					grid.setTileSelected(tile);
				}
			}
		}
		
		if (Hdown && !Keyboard.isDown('h'))
		{
			addTileToHotbar();
		}	
		
		Hdown = Keyboard.isDown('h');
		Mdown = Mouse.isDown();
	}
	
	private void addTileToHotbar()
	{
		for (int i = hotbar.length - 1; i > 0; i--)
		{
			int tile = hotbar[i - 1];
			hotbar[i] = tile;
		}
		
		hotbar[0] = grid.getTileSelected();
	}
	
	private int getHotbarIndex()
	{
		int x = grid.getX();
		int width = grid.getHeight();
		
		int sx = x + width - (hotbar.length * 50) + 2;
		
		int boxWidth = 50;
		
		int tileX = (Mouse.x - sx)  / boxWidth;
		
		return tileX;
	}
	
	private boolean mouseInHotbar()
	{
		int x = grid.getX();
		int y = grid.getY();
		int width = grid.getWidth();
		int height = grid.getHeight();
		
		int sx = x + width - (hotbar.length * 50) + 2;
		int sy = y + height + 10;
		
		return Mouse.x > sx && Mouse.x < x + width && Mouse.y > sy && Mouse.y < sy + 50;
	}
	
	public EditorHotbar(EditorGrid grid, int length)
	{
		this.grid = grid;
		this.hotbar = new int[length];
		for (int i = 0; i < hotbar.length; i++)
		{
			hotbar[i] = -1;
		}
	}

}

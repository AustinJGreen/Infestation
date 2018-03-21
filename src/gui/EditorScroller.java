package gui;

import infestation.Bitmap;
import infestation.Mouse;
import infestation.Renderable;
import infestation.Renderer;
import infestation.Updateable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

public class EditorScroller implements Renderable, Updateable 
{
	private EditorGrid grid;
	
	@Override
	public void draw(Graphics2D gfx) 
	{		
		int tileIDSelected = grid.getTileSelected();
		HashMap<Integer, Bitmap> tileset = grid.getTileset();
		int x = grid.getX();
		int y = grid.getY();
		int height = grid.getHeight();
		
		Bitmap selectedTile = tileset.get(tileIDSelected);
		int offset = selectedTile.getHeight();
		
		gfx.setColor(Color.BLACK);
		gfx.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 24));
		
		String text = "Selected (" + tileIDSelected + "): ";
		gfx.drawString(text, x, y + height + offset + 24);
		
		int width = gfx.getFontMetrics().stringWidth(text);
		selectedTile.draw(gfx, new Rectangle(x + width, y + height + offset, 32, 32), 0);		
	}

	@Override
	public int getRenderMode() 
	{
		return Renderer.QUEUE_NORMAL;
	}
	
	@Override
	public void update() 
	{
		HashMap<Integer, Bitmap> tileset = grid.getTileset();
		
		int rot = (int)(Mouse.getWheelRotation() * -1);
		int current = grid.getTileSelected();
		current += rot;
		if(current < 0)
		{
			current = tileset.size() - 1;
		}
		else if (current > tileset.size() - 1)
		{
			current = 0;
		}	
		
		grid.setTileSelected(current);	
	}
	
	public EditorScroller(EditorGrid grid)
	{
		this.grid = grid;		
	}

}

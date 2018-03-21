package infestation;

import java.awt.Graphics2D;

public interface Renderable 
{
	public void draw(Graphics2D gfx);
	
	public int getRenderMode();
}

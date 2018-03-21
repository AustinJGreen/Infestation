package infestation;

import java.awt.Graphics2D;
import java.util.LinkedList;

public class Renderer 
{
	public static final int QUEUE_FIRST = 0;
	public static final int QUEUE_NORMAL = 1;
	public static final int QUEUE_LAST = 2;
	
	private LinkedList<Renderable> first;
	private LinkedList<Renderable> normal;
	private LinkedList<Renderable> last;
	
	private Graphics2D gfx;
	
	public void draw(Renderable object)
	{
		switch (object.getRenderMode())
		{
		case QUEUE_FIRST:
			first.add(object);
			break;
		case QUEUE_NORMAL:
			normal.add(object);
			break;
		case QUEUE_LAST:
			last.addFirst(object);
			break;
		}
	}
	
	public void render(Graphics2D gfx)
	{		
		for (Renderable obj : first) { obj.draw(gfx); }		
		for (Renderable obj : normal) { obj.draw(gfx); }
		for (Renderable obj : last) { obj.draw(gfx); }
		
		first.clear();
		normal.clear();
		last.clear();
	}
	
	public void loadGraphics(Graphics2D gfx)
	{
		this.gfx = gfx;
	}
	
	public Graphics2D getGraphics()
	{
		return gfx;
	}
	
	public Renderer()
	{
		first = new LinkedList<Renderable>();
		normal  = new LinkedList<Renderable>();
		last = new LinkedList<Renderable>();
	}
}

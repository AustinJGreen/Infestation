package effects;

import infestation.Renderable;
import infestation.Renderer;
import infestation.Settings;
import infestation.Updateable;
import infestation.Vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Particle implements Renderable, Updateable
{	
	public static final int GUN = 0;
	public static final int FLAME = 1;
	
	protected int scaleX = 1;
	protected int scaleY = 1;
	
	protected int lastX;
	protected int lastY;
	
	protected int posX;
	protected int posY;
	
	protected long timestamp = -1;
	protected double starttime, startlife, life;
	
	protected Vector<Double> vel;
	protected Color color;
	
	protected double clamp(double num, double min, double max)
	{
		if (num < min)
			return min;
		if (num > max)
			return max;
		return num;
	}
	
	protected double lerp(double value1, double value2, double amount)
	{
		return value1 + (value2 - value1) * amount;
	}
	
	public boolean dead()
	{
		return life <= 0;
	}
		
	public void draw(Graphics2D gfx)
	{
		gfx.setColor(color);
		gfx.fillOval(posX, posY, getScaleX(), getScaleY());
		
		if (Settings.DEBUG > 1)
		{
			gfx.setColor(new Color(255, 0, 0, 100));
			gfx.drawLine(lastX, lastY, posX, posY);
		}
	}
	
	@Override
	public int getRenderMode() 
	{
		return Renderer.QUEUE_LAST;
	}
	
	public void update()
	{
		if (timestamp == -1)
		{
			timestamp = System.currentTimeMillis();
		}
		
		if (System.currentTimeMillis() - timestamp > starttime)
		{
			life = startlife - (System.currentTimeMillis() - timestamp) - starttime;
			
			if (life < 0)
			{
				life = 0;
			}
			
			if (life > 0) //active
			{
				double per = (startlife - life - starttime) / (double)startlife;
				
				lastX = posX;
				lastY = posY;
				posX += vel.getX();
				posY += vel.getY();
				
				double frictionX = vel.getX();
				double frictionY = vel.getY();
				
				vel.setX(frictionX);
				vel.setY(frictionY);
				
				int alphaDim = (int)clamp(lerp(color.getAlpha(), 0, per), 0, 255);
				int redDim = (int)clamp(lerp(color.getRed(), 0, per), 0, 255);
				int greenDim = (int)clamp(lerp(color.getGreen(), 0, per), 0, 255);
				int blueDim = (int)clamp(lerp(color.getBlue(), 0, per), 0, 255);
				
				color = new Color(redDim, greenDim, blueDim, alphaDim);
			}
		}
	}
	
	public boolean interects(Rectangle2D bounds)
	{
		Line2D ray = new Line2D.Double(lastX, lastY, posX, posY);
		return ray.intersects(bounds);
	}
	
	public void destroy()
	{
		life = 0;
	}
	
	public int getScaleX() {
		return scaleX;
	}

	public void setScaleX(int scaleX) {
		this.scaleX = scaleX;
	}

	public int getScaleY() {
		return scaleY;
	}

	public void setScaleY(int scaleY) {
		this.scaleY = scaleY;
	}

	public Particle(
			int posX,
			int posY,
			int scaleX,
			int scaleY,
			Vector<Double> vel,
			Color color,
			double starttime,
			double life)
	{
		this.posX = posX;
		this.posY = posY;
		this.lastX = posX;
		this.lastY = posY;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.vel = vel;
		this.color = color;
		this.starttime = starttime;
		this.startlife = life;
		this.life = life;
	}
}

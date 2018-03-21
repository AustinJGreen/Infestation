package gui;

import infestation.Renderable;
import infestation.Renderer;

import java.awt.Color;
import java.awt.Graphics2D;

public class WifiSignal implements Renderable {

	private final int minHeight = 5;
	private final int margin = 1;
	private final int[] strength = new int[] { 3000, 1000, 250, 100, 50};
	
	private long clientConnectionMS;
	
	private int x, y, width, height;	
	
	
	@Override
	public void draw(Graphics2D gfx) 
	{	
		int barWidth = (width - (margin * strength.length)) / strength.length;
		int barInc = height / strength.length;
		
		int dx = x;
		int barHeight = barInc;
		
		gfx.setColor(Color.WHITE);
		
		for (int i = 0; i < strength.length; i++)
		{
			if (clientConnectionMS <= strength[i])
			{
				gfx.fillRect(dx, y - barHeight, barWidth, barHeight);
				barHeight += barInc;
			}
			else
			{
				gfx.fillRect(dx, y - minHeight, barWidth, minHeight);
			}
			
			dx += (barWidth + margin);
		}
	}

	@Override
	public int getRenderMode() {
		return Renderer.QUEUE_LAST;
	}
	
	public long getClientConnectionMS() {
		return clientConnectionMS;
	}

	public void setClientConnectionMS(long clientConnectionMS) {
		
		this.clientConnectionMS = clientConnectionMS;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public WifiSignal(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}

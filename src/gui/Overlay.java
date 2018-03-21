package gui;

import java.awt.Color;
import java.awt.Graphics2D;

import infestation.Renderer;
import infestation.Settings;

public class Overlay implements Control {
	
	private boolean visible;;
	private int x, y, width, height;
	
	@Override
	public void draw(Graphics2D gfx) {
		if (visible)
		{
			//Draw overlay
			Color overlay = new Color(75, 75, 75, 150);
			gfx.setColor(overlay);
			gfx.fillRect(x, y, width, height);
		}
	}

	@Override
	public int getRenderMode() {
		return Renderer.QUEUE_NORMAL;
	}

	@Override
	public void update() {
		
	}

	@Override
	public void setWindowX(int x) {
		this.x = x;
		
	}

	@Override
	public void setWindowY(int y) {
		this.y = y;
		
	}

	@Override
	public void setWindowWidth(int width) {
		this.width = width;	
	}

	@Override
	public void setWindowHeight(int height) {
		this.height = height;
		
	}

	@Override
	public void setVisibility(boolean value) {
		this.visible = value;
	}
	
	public Overlay()
	{
		this.x = 0;
		this.y = 0;
		this.width = Settings.WIDTH;
		this.height = Settings.HEIGHT;
		this.visible = true;
	}
}

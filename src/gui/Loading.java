package gui;

import infestation.Bitmap;
import infestation.Game;
import infestation.Renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Loading implements Control {

	private int x, y;
	private int windowX, windowY;
	private boolean visible = true;
	
	private Bitmap animation;
	
	@Override
	public void draw(Graphics2D gfx) {
		if (visible)
		{
			int w = animation.getFrameWidth();
			int h = animation.getFrameHeight();
			Rectangle buffer = new Rectangle(windowX + x - (w / 2), windowY + y - (h / 2), w, h);
			animation.draw(gfx, buffer, 0);	
		}
	}

	@Override
	public void update() {
		if (visible)
		{
			animation.update();	
		}
	}
	
	@Override
	public int getRenderMode() {
		return Renderer.QUEUE_NORMAL;
	}

	@Override
	public void setWindowX(int x) {
		this.windowX = x;
	}

	@Override
	public void setWindowY(int y) {
		this.windowY = y;
	}

	@Override
	public void setWindowWidth(int width) {}

	@Override
	public void setWindowHeight(int height) {}

	@Override
	public void setVisibility(boolean value) {
		this.visible = value;		
	}
	
	public Loading(Game game, int x, int y)
	{
		this.animation = game.getEntity("loading");
		
		this.x = x;
		this.y = y;
	}

}

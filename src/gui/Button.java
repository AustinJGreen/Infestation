package gui;

import infestation.Action;
import infestation.Game;
import infestation.Mouse;
import infestation.Renderer;
import infestation.SoundEffect;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Button implements Control
{
	protected Game game;
	
	protected Action action;
	
	protected String text;
	
	protected Font font;
	
	protected FontMetrics metrics;
	
	protected Color foreGround;
	
	protected Color highlighted;
	
	protected SoundEffect click;
	
	protected float origX, windowX, x;

	protected float origY, windowY, y;
	
	protected boolean centerX, centerY;
	
	protected boolean initialized = false;
	
	protected boolean isSelected = false;
	
	protected boolean mouseDown = false;
	
	protected boolean visible = true;
	
	protected boolean clicked = false;
	
	@Override
	public void draw(Graphics2D gfx)
	{		
		if (visible)
		{
			//Set font first to validate metrics
			gfx.setFont(font);
			if (!initialized)
			{
				updateMetrics(gfx);
			}
				
			if (isSelected)
			{
				gfx.setColor(highlighted);
			}
			else
			{
				gfx.setColor(foreGround);
			}	
			
			gfx.drawString(text, windowX + x, windowY + y);	
		}
	}

	public int getRenderMode()
	{
		return Renderer.QUEUE_NORMAL;
	}
	
	@Override
	public void update()
	{
		if (visible)
		{
			if (metrics != null)
			{		
				int width = metrics.stringWidth(text);
				int height = metrics.getHeight();			
	
				boolean sel = Mouse.x > windowX + x && Mouse.x < (windowX + x) + width && Mouse.y > (windowY + y) - height && Mouse.y < windowY + y;
				isSelected = sel;
				
				if (sel && mouseDown && !Mouse.isDown() && !clicked)
				{				
					clicked = true;
					if (action != null)
					{
						click.playAsync(false);
						action.execute(game);
					}
				}
				else
				{
					clicked = false;
				}
				
				if (sel)
				{
					mouseDown = Mouse.isDown();
				}
				else				
				{
					mouseDown = false;
				}
			}
		}
	}
	
	protected void updateMetrics(Graphics2D gfx)
	{	
		if (!initialized)
		{
			metrics = gfx.getFontMetrics();
			if (centerX)
			{
				float width = metrics.stringWidth(text);
				x = origX - (width / 2f);	
				
			}
			
			if (centerY)
			{
				float height = metrics.getHeight();
				y = origY - (height / 2f);
				
			}
			initialized = true;
		}
	}
	
	public boolean isClicked()
	{
		return clicked;
	}
	
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		initialized = false;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		initialized = false;
	}

	public Color getForeGround() {
		return foreGround;
	}

	public void setForeGround(Color foreGround) {
		this.foreGround = foreGround;
	}

	public Color getHighlighted() {
		return highlighted;
	}

	public void setHighlighted(Color highlighted) {
		this.highlighted = highlighted;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		initialized = false;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		initialized = false;
	}	
	
	@Override
	public void setWindowX(int x) {
		this.windowX = x;	
	}

	public void setWindowY(int y) {
		this.windowY = y;
	}
	
	public void setWindowWidth(int width) {}
	
	public void setWindowHeight(int height) {}

	@Override
	public void setVisibility(boolean value) {
		this.visible = value;		
	}
	
	public Button(Game game,
				  String text,
		      	  Font font,
			      Color foreGround,
			      Color highlighted,
			      float x,
			      float y,
			      boolean centerX,
			      boolean centerY,
			      Action action)
	{
		this.click = game.getSoundEffect("click");
		
		this.game = game;
		this.text = text;
		this.font = font;
		this.foreGround = foreGround;
		this.highlighted = highlighted;
		this.x = x;
		this.origX = x;
		this.y = y;
		this.origY = y;
		this.centerX = centerX;
		this.centerY = centerY;
		this.action = action;
	}
}

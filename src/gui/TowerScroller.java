package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import infestation.Bitmap;
import infestation.Game;
import infestation.Level;
import infestation.LevelViewport;
import infestation.Mouse;
import infestation.Pair;
import infestation.Renderable;
import infestation.Renderer;
import infestation.SoundEffect;
import infestation.Tower;
import infestation.TowerInfo;
import infestation.Updateable;
import infestation.Vector;

public class TowerScroller implements Renderable, Updateable {

	private final int height = 100;
	
	private int y;
	
	private int margin;
	private double angle;
	private double rotation;
	
	
	private Arc arc;
	private Rectangle[] towerBoxes;
	private Level level;
	
	private boolean Mdown;
	
	private boolean hasFocus = false;
	
	private Rectangle getBox(int id, double angle)
	{
		angle = arc.getEndAngle() + (angle % 180);
			
		Vector<Integer> pt = getPointOnArc(arc, angle);
		LevelViewport viewport = level.getViewport();
						
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		double scale = 5 - (0.05 * Math.abs((angle - 180) - 90));
		return new Rectangle(pt.getX() - ((int)(tileWidth * scale) / 2), pt.getY() - ((int)(tileHeight * scale) / 2) + y, (int)(tileWidth * scale), (int)(tileHeight * scale));
	}
	
	private void drawTower(Graphics2D gfx, int id, Rectangle buffer)
	{	
		Game game = level.getGame();
		Pair<Bitmap, Bitmap> tower = game.getTowerData(id);
		Bitmap base = tower.getANode();
		Bitmap turret = tower.getBNode();
		if (base != null && tower != null)
		{
			base.draw(gfx, buffer, 0);
			turret.draw(gfx, buffer, 0);
		}
	}
	
	private Vector<Integer> getPointOnArc(Arc arc, double angle)
	{		
		double originX = arc.getX() + (arc.getWidth() / 2);
		double originY = arc.getY() + (arc.getHeight() / 2);
		
		double r1 = arc.getWidth() / 2;
		double r2 = arc.getHeight() / 2;

		double x = originX + r1 * Math.cos(Math.toRadians(angle));
		double y = originY + r2 * Math.sin(Math.toRadians(angle));
		
		return new Vector<Integer>((int)x, (int)y);
	}
	
	@Override
	public void update() {
		LevelViewport viewport = level.getViewport();
		arc = new Arc(margin, viewport.getCamHeight() - (height / 2), viewport.getCamWidth() - (margin * 2), height, 0, 180);	
		
		double rot = Mouse.getWheelRotation() * -2;
		if (rot != 0)
		{
			if (Math.abs(rotation) < 10)
			{
				rotation += rot;
			}
		}
		else if (rotation != 0)
		{
			angle += rotation;
			
			double momentum = 0.2;			
			if (rotation > 0)
			{
				rotation -= momentum;
			}
			else
			{
				rotation += momentum;
			}
			
			if (momentum > Math.abs(rotation))
				rotation = 0;
		}
		
		angle = arc.getEndAngle() + (angle % 180);
		
		int[] towers = Tower.getAllTowers();
		int spacing = 180 / towers.length;
		double a = angle - (spacing * (towers.length / 2));
		
		Game game = level.getGame();
		towerBoxes = new Rectangle[towers.length];
		
		hasFocus = false;
		for (int i = 0; i < towerBoxes.length; i++, a += spacing)
		{
			towerBoxes[i] = getBox(towers[i], a);
			if (towerBoxes[i].contains(Mouse.x, Mouse.y))
			{
				hasFocus = true;
				if (Mdown && !Mouse.isDown())
				{
					SoundEffect click = game.getSoundEffect("click");
					click.playAsync(false);
					
					level.setSelected(towers[i]);
				}
			}
		}
		
		if (Mouse.y < viewport.getCamHeight() - height)
		{
			if (y < height)
				y++;
		}
		else if (y > 0)
		{
			y--;
		}
		
		Mdown = Mouse.isDown();
	}

	@Override
	public void draw(Graphics2D gfx) {
		gfx.setColor(Color.BLACK);		
		
		gfx.setStroke(new BasicStroke(5));
		gfx.drawArc(arc.getX(), arc.getY() + y, arc.getWidth(), arc.getHeight(), arc.getStartAngle(), arc.getEndAngle());
			
		if (towerBoxes != null)
		{
			int[] towers = Tower.getAllTowers();	
			for (int i = 0; i < towers.length; i++)
			{	
				Rectangle buffer = towerBoxes[i];
				if (buffer != null)
				{
					drawTower(gfx, towers[i], buffer);
					if (buffer.contains(Mouse.x, Mouse.y))
					{
						gfx.setStroke(new BasicStroke(2));
						
						TowerInfo info = Tower.getInfo(towers[i]);
						
						gfx.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 18));
						FontMetrics metrics = gfx.getFontMetrics();
						String nameLabel = info.getName();
						String costLabel = "$" + info.getCost();
							
						int nameWidth = metrics.stringWidth(nameLabel);
						int costWidth = metrics.stringWidth(costLabel);
						
						gfx.setFont(new Font("Copperplate Gothic Light", Font.BOLD, 18));
						gfx.drawString(nameLabel, buffer.x + (buffer.width / 2) - (nameWidth / 2), buffer.y - metrics.getHeight() - 10);
						gfx.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 18));
						gfx.drawString(costLabel, buffer.x + (buffer.width / 2) - (costWidth / 2), buffer.y - metrics.getHeight() + 10);
						
						gfx.drawRect(buffer.x, buffer.y, buffer.width, buffer.height);
					}
				}
			}
		}
	}

	@Override
	public int getRenderMode() {
		return Renderer.QUEUE_LAST;
	}
	
	public boolean hasFocus()
	{
		return hasFocus;
	}
	
	public TowerScroller(Level level, int margin)
	{
		this.level = level;
		this.margin = margin;
		
		LevelViewport viewport = level.getViewport();
		arc = new Arc(margin, viewport.getCamHeight() - (height / 2), viewport.getCamWidth() - (margin * 2), height, 0, 180);	
	}
}

package infestation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;

public  class Entity implements Renderable, Updateable
{
	protected int entitiyId = 0;
	
	protected int rot = 0;
	protected int health = 1;
	protected int maxHealth;
	protected int x, y;
	
	protected long lifeTime = 0;
	protected boolean reachedGoal = false;
	
	protected double speed = 1;
	protected double distance = 0;
	protected double distanceTogo = 0;
	
	protected LinkedList<Node> traveled;
	protected EntityPath entityPath;
	protected Random gen;
	
	protected Tile on;
	protected Node target;
	
	protected Level level;
	protected Bitmap sprite;
	
	protected double getDistanceTraveled()
	{
		LevelViewport viewport = level.getViewport();
		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		int x1 = on.getTileX() * tileWidth;
		int y1 = on.getTileY() * tileHeight;
		
		return Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2));
	}
	
	protected double getDistanceToGo()
	{
		LevelViewport viewport = level.getViewport();
		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		Tile targetTile = target.getTile();
		
		int x1 = on.getTileX() * tileWidth;
		int y1 = on.getTileY() * tileHeight;
		int x2 = targetTile.getTileX() * tileWidth;
		int y2 = targetTile.getTileY() * tileHeight;
		
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public void draw(Graphics2D gfx)
	{
		if (!isDead())
		{
			LevelViewport viewport = level.getViewport();
			
			int tileWidth = viewport.getCamWidth() / level.getMapWidth();
			int tileHeight = viewport.getCamHeight() / level.getMapHeight();
			Rectangle pos = new Rectangle(viewport.getCamX() + x, viewport.getCamY() + y, tileWidth, tileHeight);
			
			sprite.draw(gfx, pos, rot);			
			int centerX = viewport.getCamX() + x + (tileWidth / 2);
			
			final int barwidth = 50;
			final int barheight = 5;
			final int barmargin = 10;
			
			int width = barwidth;
			
			float percentHealth = health / (float)maxHealth;	
			width *= percentHealth;			
			int damageWidth = barwidth - width;

			Color health = new Color(0, 255, 0, 100);
			Color damage = new Color(255, 0, 0, 100);
			
			gfx.setColor(health);
			gfx.fillRect(centerX - (barwidth / 2), y - 10, width, 5);
			
			gfx.setColor(damage);
			gfx.fillRect(centerX - (barwidth / 2) + width, y - barmargin, damageWidth, barheight);		
			
			if (Settings.DEBUG > 2)
			{
				Tile targetTile = target.getTile();
				
				gfx.setColor(new Color(255, 0, 0, 100));
				gfx.fillRect(on.getTileX() * tileWidth, on.getTileY() * tileWidth, tileWidth, tileHeight);
				
				gfx.setColor(new Color(0, 255, 0, 100));
				gfx.fillRect(targetTile.getTileX() * tileWidth, targetTile.getTileY() * tileWidth, tileWidth, tileHeight);
			}
		
			
		}
	}
	
	@Override
	public int getRenderMode() 
	{
		return Renderer.QUEUE_NORMAL;
	}
	
	public void update()
	{	
		if (!isDead())
		{			
			sprite.update();
			
			LevelViewport viewport = level.getViewport();
			
			int tileWidth = viewport.getCamWidth() / level.getMapWidth();
			int tileHeight = viewport.getCamHeight() / level.getMapHeight();			
			
			if (distance > distanceTogo)
			{
				on = target.getTile();
				traveled.add(target);
				target = entityPath.getNext(gen, traveled);
				if (target == null)
				{
					if (!reachedGoal)
					{
						level.subtractLife();
					}
					
					reachedGoal = true;				
					return;
				}
				distanceTogo = getDistanceToGo();
				distance = 0;
			}	
			
			Tile targetTile = target.getTile();
			
			distance += speed;
			rot = 90 + (int)(Math.toDegrees(Math.atan2(targetTile.getTileY() - on.getTileY(), targetTile.getTileX() - on.getTileX())));
			
			x = (int)(on.getTileX() * tileWidth + distance * Math.cos(Math.toRadians(rot - 90)));
			y = (int)(on.getTileY() * tileHeight + distance * Math.sin(Math.toRadians(rot - 90)));
		}
	}
	
	public boolean isDead()
	{
		return health <= 0 || reachedGoal;
	}

	public void inflictDamage(int amount)
	{
		health -= amount;
		if (health <= 0)
			level.awardMoney(50);
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Bitmap getSprite() {
		return sprite;
	}

	public void setSprite(Bitmap sprite) {
		this.sprite = sprite;
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
	
	public long getTilesTraveled()
	{
		return traveled.size();
	}
	
	public Rectangle2D getBounds()
	{
		LevelViewport viewport = level.getViewport();		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		return new Rectangle2D.Double(x, y, tileWidth, tileHeight);
	}
	
	public int getId()
	{
		return entitiyId;
	}
	
	public String getData()
	{
		StringBuilder data = new StringBuilder();
		data.append("{");
		data.append("x:");
		data.append(x);
		data.append(",y:");
		data.append(y);
		data.append(",eid:");
		data.append(entitiyId);
		data.append(",pid:");
		data.append(entityPath.getId());
		data.append("}");
		return data.toString();
	}
	
	protected Entity(Level level, int id, Random gen)
	{		
		this.level = level;
		this.entitiyId = id;
		this.gen = gen;
		this.entityPath = level.getPath(gen);
		this.on = entityPath.getStart().getTile();
		
		traveled = new LinkedList<Node>();
		traveled.add(entityPath.getStart());
		
		this.target = entityPath.getNext(gen, traveled);	
		this.distanceTogo = getDistanceToGo();
		
		LevelViewport viewport = level.getViewport();
		
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		this.x = on.getTileX() * tileWidth;
		this.y = on.getTileY() * tileHeight;
	}
}

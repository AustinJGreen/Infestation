package infestation;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import effects.Particle;
import effects.ParticleEmitter;
import towers.*;

public abstract class Tower extends Tile 
{
	public static final int PARTICLE_TOWER = 0;
	public static final int EMP_TOWER = 1;
	public static final int FLAME_TOWER = 2;
	
	public static final int TARGET_CLOSEST = 0;
	public static final int TARGET_FURTHEST = 1;
	public static final int TARGET_OLDEST = 2;
	public static final int TARGET_NEWEST = 3;
	
	protected ParticleEmitter emitter;
	protected Bitmap tower;  
	protected Bitmap gun;
	protected int towerId;
	protected float rot;
	
	protected int cost = 0;	
	protected int range = 100;
	protected int damage = 1;
	protected int firerate = 10;
	protected int firepower = 10;
	protected int reloadTime = 100;	
	protected int particleSize = 5;
	protected int magazineSize = 10;
	protected long lastFireTime = -1;
	protected double particleSpread = 1.0;
	protected int targetMode = TARGET_OLDEST;
	
	public Entity getTarget()
	{		
		LevelViewport viewport = level.getViewport();
		int tileWidth = viewport.getCamWidth() / level.getMapWidth();
		int tileHeight = viewport.getCamHeight() / level.getMapHeight();
		
		double record = -1;
		Entity mostValid = null;
		List<Entity> entities = level.getEntities();
			
		for (int i = 0; i < entities.size(); i++)
		{
			Entity current = entities.get(i);
			if (!current.isDead())
			{
				int towerX = tileX * tileWidth;
				int towerY = tileY * tileHeight;
				
				double dis = Math.sqrt(Math.pow(current.getX() - towerX, 2) + Math.pow(current.getY() - towerY, 2));
				long lifetime = current.getTilesTraveled();
				if (dis < range)
				{
					switch (targetMode)
					{
					case TARGET_CLOSEST: default:
						if (dis < record || record == -1)
						{
							record = dis;
							mostValid = current;
						}
						break;
					case TARGET_FURTHEST:
						if (dis > record || record == -1)
						{
							record = dis;
							mostValid = current;
						}
						break;
					case TARGET_OLDEST:
						if (lifetime > record || record == -1)
						{
							record = lifetime;
							mostValid = current;
						}
						break;
					case TARGET_NEWEST:
						if (lifetime < record || record == -1)
						{
							record = lifetime;
							mostValid = current;
						}
						break;
					}
				}
			}
		}

		return mostValid;
	}
	
	@Override
	public void draw(Renderer renderer)
	{
		//Draw tile
		super.draw(renderer);
		if (tower != null)
		{
			Graphics2D gfx = renderer.getGraphics();
			
			Rectangle2D tileBuffer = getBuffer();
			
			tower.draw(gfx, tileBuffer, 0);
			gun.draw(gfx, tileBuffer, rot);
			emitter.draw(renderer);
		}
	}
	
	@Override
	public void update()  
	{
		Entity target = getTarget();
		if (target != null)
		{			

			for (Particle p : emitter.getParticles())
			{
				if (!p.dead())
				{
					for (Entity e : level.getEntities())
					{
						if (!e.isDead() && p.interects(e.getBounds()))
						{
							e.inflictDamage(damage);
							p.destroy();
							break;
						}
					}
				}
			}
			
			LevelViewport viewport = level.getViewport();
			int tileWidth = viewport.getCamWidth() / level.getMapWidth();
			int tileHeight = viewport.getCamHeight() / level.getMapHeight();
			
			int towerX = tileX * tileWidth;
			int towerY = tileY * tileHeight;
			
			rot = 90 + (float)Math.toDegrees(Math.atan2(target.getY() - towerY, target.getX() - towerX));
			
			long reloadedTime = game.getGametime() - lastFireTime;
			if (reloadedTime > reloadTime)
			{
				double velX = target.getX() - towerX;
				double velY = target.getY() - towerY;

				double num = firepower / (float)Math.sqrt((double)(velX * velX + velY * velY));	
				velX *= num;
				velY *= num;
				
				emitter.emit(magazineSize, 
							 firerate,
							 particleSize,
							 new Vector<Integer>(towerX + (tileWidth / 2) - (particleSize / 2), towerY + (tileHeight / 2) - (particleSize / 2)), 
							 new Vector<Double>(velX, velY), 
							 new Vector<Double>(particleSpread, particleSpread)					 
							 );
				
				lastFireTime = game.getGametime();
			}
		}
		
		emitter.update();
	}
	
	public int getTowerId()
	{
		return towerId;
	} 
	
	public void setTowerId(int id)
	{
		this.towerId = id;
		this.data.TowerID = id;
		
		Pair<Bitmap, Bitmap> data = game.getTowerData(id);
		this.tower = data.getANode();
		this.gun = data.getBNode();
	}
	
	public int getTargetMode()
	{
		return targetMode;
	}
	
	public void setTargetMode(int mode)
	{
		this.targetMode = mode;
	}
	
	protected Tower(Game game, Level level, int tileX, int tileY, int towerId)
	{
		super(game, level, tileX, tileY);
		this.setTowerId(towerId);
	}
	
	public static Tower fromID(Game game, Level level, int tileX, int tileY, int towerId)
	{
		switch (towerId)
		{
		case PARTICLE_TOWER:
			return new ParticleTower(game, level, tileX, tileY);
		case EMP_TOWER:
			return new EmpTower(game, level, tileX, tileY);
		case FLAME_TOWER:
			return new FlameTower(game, level, tileX, tileY);
		default:
			return null;
		}
	}
	
	public static TowerInfo getInfo(int towerId)
	{
		switch (towerId)
		{
		case PARTICLE_TOWER:
			return ParticleTower.INFO;
		case EMP_TOWER:
			return EmpTower.INFO;
		case FLAME_TOWER:
			return FlameTower.INFO;
		default:
			return null;
		}
	}
	
	public static int[] getAllTowers()
	{
		return new int[] { PARTICLE_TOWER, EMP_TOWER, FLAME_TOWER };
	}
	
}

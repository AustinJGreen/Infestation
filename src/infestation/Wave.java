package infestation;

import java.util.ArrayList;
import java.util.Random;

import entities.*;

public class Wave implements GameObject
{
	public static final int COCKROACH = 0;
	public static final int CRAWLER = 1;
	
	private Random generator;
	private int type, count;
	private long time;

	private boolean registered = false;
	
	private Level level;
	private Entity[] entities;
	
	private boolean dead = false;
	
	public void draw(Renderer renderer)
	{
		for (int i = 0; i < entities.length; i++)
		{
			renderer.draw(entities[i]);
		}
	}
	
	public void update()
	{	
		boolean fin = true;
		for (int i = 0; i < entities.length; i++)
		{
			Entity cur = entities[i];
			cur.update();
			if (!cur.isDead())
			{
				fin = false;
			}
		}
		
		dead = fin;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public long getSpawnTime()
	{
		return time;
	}
	
	public Wave create()
	{
		for (int i = 0; i < entities.length; i++)
		{
			entities[i] = getEntity(type);
		}
		
		return this;
	}
	
	public boolean isActive(long elapsed)
	{		
		return elapsed > time;
	}
	
	public void register(ArrayList<Entity> level)
	{
		for (int i = 0; i < entities.length; i++)
		{
			level.add(entities[i]);
		}
		registered = true;
	}
	
	public boolean isRegistered()
	{
		return registered;
	}
	
	private Entity getEntity(int type)
	{
		switch (type)
		{
		case COCKROACH:
			return new Cockroach(level, generator);
		case CRAWLER:
			return new Crawler(level, generator);
		default:
			return null;
		}
	}
	
	public String getConstructor()
	{
		return String.format("{%i:%i:%i}", type, count, time);
	}
	
	public Wave(Level level, int type, int count, long time, long seed)
	{	
		this.level = level;
		this.type= type;
		this.count = count;
		this.time = time;
		
		this.generator = new Random(seed);	
		this.entities = new Entity[count];
	}
	
	public Wave(String constructor)
	{
		constructor = constructor.substring(1, constructor.length() - 1);
		String[] properties = constructor.split(",");
		type = Integer.parseInt(properties[0]);
		count = Integer.parseInt(properties[1]);
		time = Long.parseLong(properties[2]);
		
		entities = new Entity[count];
	}
}

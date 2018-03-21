package entities;

import java.util.Random;

import infestation.Entity;
import infestation.Level;
import infestation.Wave;

public class Crawler extends Entity 
{			
	public Crawler(Level level, Random gen)
	{
		super(level, Wave.CRAWLER, gen);
		super.health = 325;	
		super.maxHealth = health;
		super.speed = 1.0;
		super.sprite = level.getGame().getEntity("crawler");
		sprite.setRate(speed);
	}
}

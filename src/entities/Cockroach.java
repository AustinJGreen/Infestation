package entities;

import java.util.Random;

import infestation.Entity;
import infestation.Level;
import infestation.Wave;

public class Cockroach extends Entity {

	public Cockroach(Level level, Random gen) 
	{
		super(level, Wave.COCKROACH, gen);
		super.health = 400;	
		super.maxHealth = health;
		super.speed = 0.75;
		super.sprite = level.getGame().getEntity("cockroach");
		sprite.setRate(speed);		
	}
}

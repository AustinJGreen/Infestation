package effects;

import infestation.Game;
import infestation.GameObject;
import infestation.GameRandom;
import infestation.Renderer;
import infestation.Vector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ParticleEmitter implements GameObject {

	private Game game;	
	private Color color;
	private ArrayList<Particle> particles;
	
	public void emit(int amount, int speed, int size, Vector<Integer> vector, Vector<Double> vel, Vector<Double> spread)
	{
		GameRandom rnd = game.getRandom();
		for (int j = 0; j < amount; j++)
		{
			Particle generated = new Particle(
					  vector.getX() + (int)rnd.nextDouble(-spread.getX(), spread.getX()), 
					  vector.getY() + (int)rnd.nextDouble(-spread.getY(), spread.getY()), 
					  size, 
					  size,
					  new Vector<Double>(vel.getX() + rnd.nextDouble(-spread.getX(), spread.getX()), 
									     vel.getY() + rnd.nextDouble(-spread.getY(), spread.getY())),
					  color,
					  speed * j,
					  rnd.nextInt(500, 1000));
			
			particles.add(generated);
		}
	}
	
	public void draw(Renderer renderer)
	{
		for (int j = 0; j < particles.size(); j++)
		{
			Particle current = particles.get(j);
			renderer.draw(current);
		}
	}
	
	public void update()
	{
		int removed = 0;
		for (int j = 0; j < particles.size(); j++)
		{
			Particle current = particles.get(j - removed);
			current.update();
			if (current.dead())
			{
				particles.remove(j);
				removed++;
			}
		}
	}
	
	public List<Particle> getParticles()
	{
		return particles;
	}
	
	public ParticleEmitter(Game game, Color color)
	{
		this.particles = new ArrayList<Particle>();
		this.color = color;
		this.game = game;
	}
}

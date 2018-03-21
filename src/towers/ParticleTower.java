package towers;

import java.awt.Color;

import effects.ParticleEmitter;
import infestation.Game;
import infestation.Level;
import infestation.Tower;
import infestation.TowerInfo;

public class ParticleTower extends Tower
{
	public static final TowerInfo INFO = new TowerInfo("Shooter", 750);
	
	public ParticleTower(Game game, Level level, int tileX, int tileY) 
	{
		super(game, level, tileX, tileY, Tower.PARTICLE_TOWER);
		
		this.emitter = new ParticleEmitter(game, Color.BLACK);
		this.cost = INFO.getCost();
	}
}

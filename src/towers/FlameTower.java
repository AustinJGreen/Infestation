package towers;

import java.awt.Color;

import effects.ParticleEmitter;
import infestation.Game;
import infestation.Level;
import infestation.Tower;
import infestation.TowerInfo;

public class FlameTower extends Tower {
	
	public static final TowerInfo INFO = new TowerInfo("Inferno", 1500);

	public FlameTower(Game game, Level level, int tileX, int tileY) 
	{
		super(game, level, tileX, tileY, Tower.FLAME_TOWER);
			
		this.cost = INFO.getCost();
		
		super.damage = 1;
		super.range = 75;
		super.firerate = 1;
		super.firepower = 30;
		super.magazineSize = 300;
		super.reloadTime = 1000;
		super.particleSpread = 0;
		
		super.emitter = new ParticleEmitter(game, Color.RED);
	}

}

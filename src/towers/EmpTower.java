package towers;

import java.awt.Color;

import effects.ParticleEmitter;
import infestation.Game;
import infestation.Level;
import infestation.Tower;
import infestation.TowerInfo;

public class EmpTower extends Tower {

	public static final TowerInfo INFO = new TowerInfo("Emp Tower", 1000);
	
	public EmpTower(Game game, Level level, int tileX, int tileY) {
		super(game, level, tileX, tileY, Tower.EMP_TOWER);
		
		this.cost = INFO.getCost();
		
		super.damage = 5;
		super.firerate = 5;
		super.range = 150;
		super.particleSize = 10;
		super.magazineSize = 10;
		super.reloadTime = 250;
		
		this.emitter = new ParticleEmitter(game, Color.GREEN);
	}

}

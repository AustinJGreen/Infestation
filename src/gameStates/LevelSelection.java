package gameStates;

import java.awt.Color;
import java.awt.Font;

import actions.SwitchState;
import gui.Button;
import gui.LevelGrid;
import infestation.Game;
import infestation.GameState;
import infestation.Level;
import infestation.LevelViewport;
import infestation.Map;
import infestation.Settings;

public class LevelSelection extends GameState {

	private Game game;
	
	@Override
	public void initObjects() 
	{		
		Map backmap = new Map("mainMenu.txt");
		backmap.load();
		
		LevelViewport view = new LevelViewport(0, 0, Settings.WIDTH, Settings.HEIGHT);
		Level bg = new Level(game, backmap, view).build();		
		backmap.unload();

		addObject(bg);
		  
		Map first = new Map("EditorMap_2.txt");		
		Map second = new Map("EditorMap_3.txt");
		Map third = new Map("EditorMap_4.txt");
		LevelGrid grid = new LevelGrid(game);
		grid.loadGrid(new Map[] { first, second, third }, new String[] { "The Beginning", "The Plains", "Twin Lakes" });
		addObject(grid);
		
		SwitchState backExec = new SwitchState(game.getGameState("MainMenu"), null);
		Button back = new Button(game, "Go back", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, Settings.HEIGHT, true, true, backExec);
		addDrawObject(back);
		addUpdateObject(back);	
	}

	@Override
	public String getName() {
		return "LevelSelection";
	}

	public LevelSelection(Game game)
	{
		this.game = game;
	}
}

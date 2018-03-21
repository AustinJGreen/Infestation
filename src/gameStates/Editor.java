package gameStates;

import java.awt.Color;
import java.awt.Font;

import actions.SwitchState;
import gui.Button;
import gui.EditorGrid;
import gui.EditorHotbar;
import gui.EditorScroller;
import infestation.*;

public class Editor extends GameState {
	
	private Game game;
	
	@Override
	public void onStart() 
	{
		SoundEffect music = game.getSoundEffect("music");
		music.stop();			
	}
	
	@Override
	public void initObjects() 
	{	
		EditorGrid grid = new EditorGrid(75, 75, 50, 50, "EditorMap_3.txt", false);
		grid.addTileset(game.getTileset());
		addDrawObject(grid);
		addUpdateObject(grid);
		
		EditorScroller scroller = new EditorScroller(grid);
		addDrawObject(scroller);
		addUpdateObject(scroller);
		
		EditorHotbar hotbar = new EditorHotbar(grid, 7);
		addDrawObject(hotbar);	
		addUpdateObject(hotbar);
		
		SwitchState backExec = new SwitchState(game.getGameState("MainMenu"), null);
		Button back = new Button(game, "Go back", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, 75, true, true, backExec);
		addDrawObject(back);
		addUpdateObject(back);
	}
	
	@Override
	public String getName() 
	{
		return "Editor";
	}
	
	public Editor(Game game)
	{
		this.game = game;
	}
}

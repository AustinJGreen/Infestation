package gameStates;

import java.awt.Color;
import java.awt.Font;

import actions.*;
import gui.Button;
import infestation.*;

public class MainMenu extends GameState {
	
	private Game game;
	
	@Override
	public void onStart() 
	{
		SoundEffect music = game.getSoundEffect("music");
		music.playAsync(true);
	}
	
	public void initObjects()
	{
		int menuY = 300;
		int spacing = 70;
		
		Map backmap = new Map("mainMenu.txt");
		backmap.load();
		
		Level bg = new Level(game, backmap, LevelViewport.SCREEN).build();		
		backmap.unload();		
		addObject(bg);
		
		Button title = new Button(game, "Infestation", new Font("Copperplate Gothic Light", Font.PLAIN, 72), Color.BLACK, Color.BLACK, Settings.WIDTH / 2, menuY, true, true, null);
		addDrawObject(title);
		addUpdateObject(title);
		menuY += spacing;
		
		SwitchState levelSelection = new SwitchState(game.getGameState("LevelSelection"), null);
		Button play = new Button(game, "Play", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, menuY, true, true, levelSelection);
		addDrawObject(play);
		addUpdateObject(play);
		menuY += spacing;
		
		if (!Settings.ISAPPLET)
		{
			SwitchState multiplayerExec = new SwitchState(game.getGameState("Multiplayer"), null);
			Button multiplayer = new Button(game, "Multiplayer", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, menuY, true, true, multiplayerExec);
			addDrawObject(multiplayer);
			addUpdateObject(multiplayer);
			menuY += spacing;
		}
				   
		SwitchState editorExec = new SwitchState(game.getGameState("Editor"), null);
		Button editor = new Button(game, "Editor", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, menuY, true, true, editorExec);
		addDrawObject(editor);
		addUpdateObject(editor);
		menuY += spacing;
		
		Button about = new Button(game, "About", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, menuY, true, true, null);
		addDrawObject(about);
		addUpdateObject(about);
	}
	
	@Override
	public String getName() 
	{
		return "MainMenu";
	}
	
	public MainMenu(Game game)
	{
		this.game = game;
	}
}

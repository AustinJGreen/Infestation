package gameStates;

import java.awt.Color;
import java.awt.Font;

import multiplayer.MultiplayerLevel;
import actions.SwitchState;
import gui.Button;
import gui.Overlay;
import infestation.Game;
import infestation.GameState;
import infestation.Level;
import infestation.Map;
import infestation.Settings;

public class GamePaused extends GameState {

	private Game game;
	private Level playing;
	
	@Override
	public void initObjects() 
	{
		addObject(playing);
		
		Overlay overlay = new Overlay();
		addDrawObject(overlay);
		
		Button title = new Button(game, "Game paused", new Font("Copperplate Gothic Light", Font.PLAIN, 72), Color.BLACK, Color.BLACK, Settings.WIDTH / 2, 280, true, false, null);
		addDrawObject(title);
		addUpdateObject(title);
		
		int y = 400;
		int margin = 70;
		
		if (!Settings.ISAPPLET)
		{
			Map original = new Map(playing.getName());
			original.load();
			
			MultiplayerLevel server = new MultiplayerLevel(game, original, playing.getViewport());
			original.unload();
			
			MultiplayerGameplay gameplay = new MultiplayerGameplay(game, server, true);
			gameplay.initObjects();
			
			SwitchState multiplayerExec = new SwitchState(gameplay, null);
			Button hostLevel = new Button(game, "Host Level", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, 400, true, false, multiplayerExec);
			addDrawObject(hostLevel);
			addUpdateObject(hostLevel);	
			y += margin;
		}
		
		SwitchState quitExecutor = new SwitchState(game.getGameState("MainMenu"), game.getGameState("LevelSelection"));
		Button mainMenu = new Button(game, "Quit", new Font("Copperplate Gothic Light", Font.PLAIN, 42), Color.BLACK, Color.DARK_GRAY, Settings.WIDTH / 2, y, true, false, quitExecutor);
		addDrawObject(mainMenu);
		addUpdateObject(mainMenu);
	}

	@Override
	public String getName() {
		return "GamePause";
	}

	
	public GamePaused(Level playing)
	{
		this.game = playing.getGame();
		this.playing = playing;
	}
}

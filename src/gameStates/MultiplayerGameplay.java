package gameStates;

import java.awt.Color;
import java.awt.Font;

import actions.ChangeTextIf;
import actions.SetVisibilityIf;
import multiplayer.Client;
import multiplayer.MultiplayerLevel;
import multiplayer.Server;
import gui.Button;
import gui.Loading;
import gui.Overlay;
import infestation.Game;
import infestation.GameState;
import infestation.Settings;

public class MultiplayerGameplay extends GameState {

	private boolean hosting;
	
	private Game game;
	private MultiplayerLevel level;
	
	@Override
	public void onStart()
	{
		if (level != null && hosting)
		{
			level.build();
			level.host();
		}
	}
	
	@Override
	public void onExit() 
	{
		if (level != null)
		{
			level.dispose();
		}
	}
	
	@Override
	public void initObjects() {		
		addObject(level);
		
		Overlay overlay = new Overlay();
		addDrawObject(overlay);
		
		String statusMessage = (hosting) ? "Waiting for players..." : "Loading level...";
		Button status = new Button(game, statusMessage, new Font("Pristina", Font.PLAIN, 32), Color.BLACK, Color.BLACK, Settings.WIDTH / 2, 400, true, false, null);
		addDrawObject(status);
		addUpdateObject(status);
		
		Loading loadingObject = new Loading(game, Settings.WIDTH / 2, 500);
		addDrawObject(loadingObject);
		addUpdateObject(loadingObject);
		if (hosting)
		{			
			//Add messages
			level.addEvent(new ChangeTextIf(status, "Loading level...", Client.PING));
			level.addEvent(new SetVisibilityIf(status, false, Client.READY));
			level.addEvent(new SetVisibilityIf(overlay, false, Client.READY));
			level.addEvent(new SetVisibilityIf(loadingObject, false, Client.READY));
		}
		else
		{
			level.addEvent(new SetVisibilityIf(status, false, Server.INITEND));
			level.addEvent(new SetVisibilityIf(overlay, false, Server.INITEND));	
			level.addEvent(new SetVisibilityIf(loadingObject, false, Server.INITEND));
		}
	}

	@Override
	public String getName() {
		return "MultiplayerGameplay";
	}
	
	public MultiplayerGameplay(Game game, MultiplayerLevel level, boolean hosting)
	{
		this.game = game;
		this.level = level;
		this.hosting = hosting;		
	}
}

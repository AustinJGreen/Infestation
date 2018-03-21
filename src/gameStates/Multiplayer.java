package gameStates;

import gui.ServerList;
import infestation.Game;
import infestation.GameState;
import infestation.SoundEffect;

public class Multiplayer extends GameState{

	private Game game;
	private ServerList serverList;
	
	@Override
	public void onStart() {
		
		SoundEffect music = game.getSoundEffect("music");
		music.stop();
			
		serverList.attach();		
	}
	
	@Override
	public void onExit() {
		serverList.dispose();
		game.requestFocus();
	}
	
	@Override
	public void initObjects() {		
		serverList = new ServerList(game);
		addUpdateObject(serverList);
	}

	@Override
	public String getName() {
		return "Multiplayer";
	}
	
	public Multiplayer(Game game)
	{
		this.game = game;
	}
}

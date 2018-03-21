package gameStates;

import infestation.Game;
import infestation.GameState;
import infestation.Level;
import infestation.SoundEffect;

public class Gameplay extends GameState {

	private Game game;
	private Level playing;
	
	@Override
	public void onStart() 
	{
		SoundEffect music = game.getSoundEffect("music");
		music.stop();
	}
	
	@Override
	public void initObjects()  
	{		
		addObject(playing);		
	}

	@Override
	public String getName() 
	{	
		return "Gameplay";
	}
	
	public Gameplay(Game game, Level playing)
	{
		this.game = game;
		this.playing = playing;
	}

}

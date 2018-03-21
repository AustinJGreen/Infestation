package actions;

import infestation.Action;
import infestation.Game;
import infestation.GameState;

public class SwitchState implements Action {

	private GameState reset;
	private GameState state;
	
	@Override
	public void execute(Game game) {
		game.switchStates(state, reset);		
	}
	
	public SwitchState(GameState state, GameState reset)
	{
		this.state = state;
		this.reset = reset;
	}
}

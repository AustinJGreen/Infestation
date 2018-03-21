package actions;

import infestation.Game;
import infestation.GameState;
import multiplayer.MultiplayerEvent;

public class SwitchStateIf implements MultiplayerEvent {

	private String condition;
	private GameState newState, reset;
	
	@Override
	public void execute(Game game) {
		game.switchStates(newState, reset);
	}

	@Override
	public String getEventName() {
		return condition;
	}
	
	public SwitchStateIf(GameState newState, GameState reset, String condition)
	{
		this.newState = newState;
		this.reset = reset;
		this.condition = condition;
	}
}

package actions;

import infestation.Game;
import multiplayer.MultiplayerEvent;
import multiplayer.MultiplayerLevel;

public class SendMessage implements MultiplayerEvent {

	private MultiplayerLevel level;
	private String message;
	private String condition;
	
	@Override
	public void execute(Game game) {
		level.send(message);	
	}

	@Override
	public String getEventName() {
		return condition;
	}
	
	public SendMessage(MultiplayerLevel level, String message, String condition)
	{
		this.level = level;
		this.message = message;
		this.condition = condition;
	}
}

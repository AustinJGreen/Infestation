package actions;

import gui.Button;
import infestation.Game;
import multiplayer.MultiplayerEvent;

public class ChangeTextIf implements MultiplayerEvent {
	private Button btn;
	private String newText;
	private String condition;
	
	@Override
	public void execute(Game game) {
		btn.setText(newText);	
	}
	
	@Override
	public String getEventName() {
		return condition;
	}
	
	public ChangeTextIf(Button btn, String newText, String condition)
	{
		this.btn = btn;
		this.newText = newText;
		this.condition = condition;
	}
}

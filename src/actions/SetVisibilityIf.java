package actions;

import gui.Control;
import infestation.Game;
import multiplayer.MultiplayerEvent;

public class SetVisibilityIf implements MultiplayerEvent {

	private boolean visibility;
	private Control control;
	private String condition;
	
	@Override
	public void execute(Game game) {
		control.setVisibility(visibility);
		
	}

	@Override
	public String getEventName() {
		return condition;
	}
	
	public SetVisibilityIf(Control control, boolean value, String condition)
	{
		this.control = control;
		this.visibility = value;
		this.condition = condition;
	}
}

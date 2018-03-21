package actions;

import gui.Control;
import infestation.Action;
import infestation.Game;

public class SetVisibility implements Action
{
	private Control control;
	private boolean visibility;
	
	@Override
	public void execute(Game game) 
	{	
		control.setVisibility(visibility);
		return;
	}
	
	public SetVisibility(Control control, boolean visibility)
	{
		this.control = control;
		this.visibility = visibility;
	}
}

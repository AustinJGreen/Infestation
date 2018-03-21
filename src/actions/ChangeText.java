package actions;

import gui.Button;
import infestation.Action;
import infestation.Game;

public class ChangeText implements Action {

	private Button btn;
	private String newText;
	
	@Override
	public void execute(Game game) {
		btn.setText(newText);	
	}
	
	public ChangeText(Button btn, String newText)
	{
		this.btn = btn;
		this.newText = newText;
	}
}

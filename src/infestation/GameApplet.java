package infestation;

import javax.swing.JApplet;

public class GameApplet extends JApplet {

	private static final long serialVersionUID = -4674718441396802329L;
	
	private Game game;
	
	public void init()
	{
		Settings.ISAPPLET = true;
		
		super.setSize(Settings.WIDTH, Settings.HEIGHT);
		game = new Game();
		game.setContainer(this);
		super.add(game);
		game.start();
	}
	
	public void destroy()
	{
		if (game != null)
		{
			game.abort();
		}
	}
}

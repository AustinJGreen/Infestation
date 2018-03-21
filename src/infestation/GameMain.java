package infestation;

public class GameMain {
	
	public static void main(String[] args)
	{
		Settings.ISAPPLET = false;
		
		Game game = new Game();
	    GameFrame frame = new GameFrame(game);
	    frame.start();		
	}
}

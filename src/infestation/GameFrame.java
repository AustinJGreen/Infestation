package infestation;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GameFrame extends JFrame implements WindowListener {
	
	private static final long serialVersionUID = -1639907898224924674L;

	private Game game;	
	
	public void start()
	{
		game.start();
		setVisible(true);
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		game.abort();		
	}
	
	public GameFrame(Game game)
	{		
		this.game = game;
		this.addWindowListener(this);
		
	    setLayout(new CardLayout());
	    setResizable(false);
	
		game.setContainer(this);
		game.setPreferredSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
			
	    setTitle("Infestation by Austin Green");
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    
	    add(game);
	    pack();
	    
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(((int)dim.getWidth() / 2) - (this.getWidth() / 2), ((int)dim.getHeight() / 2) - (this.getHeight() / 2));     
	}
}

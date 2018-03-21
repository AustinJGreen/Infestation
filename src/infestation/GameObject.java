package infestation;

public interface GameObject {

	/*
	 * Draws the game object using 2D graphics
	 */
	void draw(Renderer renderer);
	
	/*
	 * Updates the game object
	 */
	void update();	
}

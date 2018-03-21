package infestation;

/*
 * Infrastructure for basic memory handling
 */
public interface GameResource {

	/*
	 * Loads the resource
	 */
	void load();
	
	/*
	 * Unloads the resource
	 */
	void unload();
}

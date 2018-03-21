package infestation;

public class LevelViewport
{
	private int camX, camY, camWidth, camHeight;
	
	public int getCamX() {
		return camX;
	}

	public void setCamX(int camX) {
		this.camX = camX;
	}

	public int getCamY() {
		return camY;
	}

	public void setCamY(int camY) {
		this.camY = camY;
	}

	public int getCamWidth() {
		return camWidth;
	}

	public void setCamWidth(int camWidth) {
		this.camWidth = camWidth;
	}

	public int getCamHeight() {
		return camHeight;
	}

	public void setCamHeight(int camHeight) {
		this.camHeight = camHeight;
	}

	public LevelViewport(int camX, int camY, int camWidth, int camHeight)
	{
		this.camX = camX;
		this.camY = camY;
		this.camWidth = camWidth;
		this.camHeight = camHeight;
	}
	
	public static final LevelViewport SCREEN = new LevelViewport(0, 0, Settings.WIDTH, Settings.HEIGHT);
}

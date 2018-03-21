package infestation;

public class Vector<V> {

	private V x, y;
	
	public V getX() {
		return x;
	}

	public void setX(V x) {
		this.x = x;
	}

	public V getY() {
		return y;
	}

	public void setY(V y) {
		this.y = y;
	}
	
	public Vector(V x, V y)
	{
		this.x = x;
		this.y = y;
	}
}

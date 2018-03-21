package infestation;

public class Node 
{
	private Tile tile;
	private int value;
	
	public Tile getTile()
	{
		return tile;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void add(int num)
	{
		value += num;
	}
	
	public Node(Tile tile, int value)
	{
		this.tile = tile;
		this.value = value;
	}
}

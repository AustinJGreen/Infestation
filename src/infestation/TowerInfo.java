package infestation;

public class TowerInfo {

	private int cost;
	private String name;
	
	public int getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public TowerInfo(String name, int cost)
	{
		this.name = name;
		this.cost = cost;
	}
	
}

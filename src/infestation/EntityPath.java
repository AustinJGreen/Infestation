package infestation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class EntityPath 
{	
	private int id;
	
	private Game game;
	private Node start;
	private Tile end;
	private ArrayList<Node> current;
	private ArrayList<EntityPath> joints;
	
	public Game getGame()
	{
		return game;
	}
	
	public int getSize()
	{
		return current.size();
	}
	
	public void printChart(Level l)
	{
		for (int y = 0; y < 50; y++)
		{
			for (int x = 0; x < 50; x++)
			{
				int val = 0;
				for (Node n : current)
				{
					Tile t = n.getTile();
					if (t.getTileX() == x && t.getTileY() == y)
					{
						val = n.getValue();
						break;
					}
				}
				if (val < 10)
					System.out.print("00");
				else if (val < 100)
					System.out.print("0");
				System.out.print(val + " ");
			}
			System.out.println();
		}
	}
	
	private boolean isAdjacent(Tile origin, Tile tile)
	{
		int xDif = Math.abs(origin.tileX - tile.getTileX());
		int yDif = Math.abs(origin.tileY - tile.getTileY());
		return  (xDif == 0 || xDif == 1) && (yDif == 0 || yDif == 1);
	}
	
	private void nextJoint()
	{
		if (joints.isEmpty())
		{
			return;
		}
		
		EntityPath joint = joints.remove(0);
		start = joint.getStart();
		end = joint.getEnd();
		current = joint.getNodes();
		
	}
	
	public Node getNext(Random random, LinkedList<Node> history)
	{			
		Node last = history.getLast();
		if (last.getTile().isSameAs(end))
		{
			return null;
		}
	
		ArrayList<Node> best = new ArrayList<Node>();
		int record = Integer.MAX_VALUE;
		for (Node node : current)
		{
			boolean traveled = history.contains(node);
			if (isAdjacent(node.getTile(), last.getTile()) && !traveled)
			{
				if (node.getTile().isSameAs(end))
				{
					best.clear();
					best.add(node);
					nextJoint();
					break;
				}
				
				int val = node.getValue();	
				if (val < record)
				{
					record = val;
					best.clear();
					best.add(node);
				}
				else if (val == record)
				{
					best.add(node);
				}
			}
		}
	
		
		if (best.isEmpty())
		{		
			return null;
		}
		
		int size = best.size();
		int choice = random.nextInt(size);
		return best.get(choice);
	}
	
	public void add(Node node)
	{
		current.add(node);
	}
	
	public void add(EntityPath joint)
	{
		joints.add(joint);
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public ArrayList<Node> getNodes()
	{
		return current;
	}
	
	public ArrayList<EntityPath> getJoints()
	{
		return joints;
	}
	
	public Node getStart()
	{
		return start;
	}
	
	public Tile getEnd()
	{
		return end;
	}
	
	public int getId()
	{
		return id;
	}
	
	public EntityPath(Game game, Node start, Tile end)
	{
		this.game = game;
		this.current = new ArrayList<Node>();
		this.joints = new ArrayList<EntityPath>();
		
		this.start = start;
		this.end = end;
	}
	
	public EntityPath(EntityPath id)
	{
		this.game = id.getGame();
		this.current = new ArrayList<Node>();
		for (Node tile : id.getNodes())
			current.add(tile);
		
		this.joints = new ArrayList<EntityPath>();
		for (EntityPath joint : id.getJoints())
			joints.add(joint);
		
		this.start = id.getStart();
		this.end = id.getEnd();
	}
}

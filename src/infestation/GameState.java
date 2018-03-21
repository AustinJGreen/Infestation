package infestation;

import java.util.ArrayList;

public abstract class GameState implements GameObject {

	protected ArrayList<Renderable> drawable = new ArrayList<Renderable>();
	protected ArrayList<Updateable> updateable = new ArrayList<Updateable>();
	protected ArrayList<GameObject> objects = new ArrayList<GameObject>();
	
	public abstract void initObjects();
	
	public void onStart() { }
	
	public void onExit() { }
	
	public abstract String getName();
	
	public void dumpObjects()
	{
		drawable.clear();
		updateable.clear();
		objects.clear();
	}
	
	public void addObject(GameObject obj)
	{		
		objects.add(obj);
	}
	
	public void addDrawObject(Renderable obj)
	{
		drawable.add(obj);
	}
	
	public void addUpdateObject(Updateable obj)
	{
		updateable.add(obj);
	}
	
	public void draw(Renderer renderer)
	{
		for (int i = 0; i < drawable.size(); i++)
		{
			Renderable obj = drawable.get(i);
			renderer.draw(obj);
		}
		
		for (int j = 0; j < objects.size(); j++)
		{
			GameObject obj = objects.get(j);
			obj.draw(renderer);
		}
	}
	
	public void update()
	{
		for (int i = 0; i < updateable.size(); i++)
		{
			Updateable obj = updateable.get(i);
			obj.update();
		}
		
		for (int j = 0; j < objects.size(); j++)
		{
			GameObject obj = objects.get(j);
			obj.update();
		}
	}
}

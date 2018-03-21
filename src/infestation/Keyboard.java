package infestation;

import java.util.ArrayList;

public final class Keyboard {
	
	public static boolean[] keyboard = new boolean[255];	
	
	public static boolean isDown(char key)
	{
		if (key < 0 || key >= keyboard.length)
			return false;
		return keyboard[key];
	}
	
	public static boolean isUp(char key)
	{
		if (key < 0 || key >= keyboard.length)
			return false;
		return keyboard[key];
	}
	
	public static void setDown(char key)
	{
		if (key < 0 || key >= keyboard.length)
			return;
		keyboard[key] = true;
	}
	
	public static void setUp(char key)
	{
		if (key < 0 || key >= keyboard.length)
			return;
		keyboard[key] = false;
	}
	
	public static ArrayList<Character> getKeysDown()
	{
		ArrayList<Character> list = new ArrayList<Character>();
		for (int i = 0; i < keyboard.length; i++)
		{
			if (keyboard[i])
			{
				list.add((char)i);
			}		
		}
		return list;
	}
	
	private Keyboard() throws Exception
	{
		throw new Exception("\"Keyboard\" cannot be initialized, it is a static class.");
	}	
}

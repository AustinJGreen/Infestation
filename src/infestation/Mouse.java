package infestation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public final class Mouse {

	public static int x, y, button, mod;
	private static double wheelRotation;
	private static boolean isDown = false;
	
	public static void handleEvent(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
		button = e.getButton();
	}
	
	public static void handleEvent(MouseWheelEvent e)
	{
		wheelRotation = e.getPreciseWheelRotation();
	}
	
	public static double getWheelRotation()
	{
		double wheel = wheelRotation;
		wheelRotation = 0;
		return wheel;
	}
	
	public static void setDown(boolean val)
	{
		isDown = val;
	}
	
	public static boolean isDown()
	{
		return isDown;
	}
	
	private Mouse() throws Exception
	{
		throw new Exception("\"Mouse\" cannot be initialized, it is a static class.");
	}
}

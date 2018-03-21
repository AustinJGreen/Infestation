package infestation;

import java.util.Random;

public class GameRandom extends Random {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7894056466361895473L;
	
	public double nextDouble(double min, double max)
	{
		if (min == max)
			return min;
		double difference = max - min;
		return min + (difference * nextDouble()) % (min - max);
	}
	
	public int nextInt(int min, int max)
	{
		return min + nextInt(max - min + 1);
	}
	
	public double clamp(double value, double min, double max)
	{
		if (value < min)
			value = min;
		if (value > max)
			value = max;
		
		return value;
	}

}

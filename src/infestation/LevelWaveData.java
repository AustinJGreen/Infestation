package infestation;

public class LevelWaveData {

	private int count, time;
	private int[] mobs;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int[] getMobs() {
		return mobs;
	}

	public void setMobs(int[] mobs) {
		this.mobs = mobs;
	}

	public LevelWaveData(int time, int count, int[] mobs)
	{
		this.time = time;
		this.count = count;
		this.mobs = mobs;
	}
}
